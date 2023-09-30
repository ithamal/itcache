package io.github.ithamal.itcache.lock;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author: ken.lin
 * @since: 2023-09-26 16:30
 */
public class SimpleLockManager implements LockManager {

    private final ConcurrentHashMap<Object, Lock> readLockHashMap = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<Object, Lock> writeLockHashMap = new ConcurrentHashMap<>();

    @Override
    public Lock getReadLock(Object key) {
        return readLockHashMap.computeIfAbsent(key, _key -> {
            return new LockImpl(key, readLockHashMap);
        });
    }

    @Override
    public Lock getWriteLock(Object key) {
        return writeLockHashMap.computeIfAbsent(key, _key -> {
            return new LockImpl(key, writeLockHashMap);
        });
    }

    private static class LockImpl extends ReentrantLock {

        private final Object key;

        private final ConcurrentHashMap<Object, Lock> hashMap;

        public LockImpl(Object key, ConcurrentHashMap<Object, Lock> hashMap) {
            this.key = key;
            this.hashMap = hashMap;
        }

        @Override
        public void unlock() {
            super.unlock();
            if (super.tryLock()) {
                try {
                    hashMap.remove(key);
                } finally {
                    super.unlock();
                }
            }
        }
    }
}
