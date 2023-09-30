package io.github.ithamal.itcache.core.impl;

import io.github.ithamal.itcache.core.AbstractCache;
import io.github.ithamal.itcache.core.ValueWrapper;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;

/**
 * @author: ken.lin
 * @since: 2023-09-26 11:52
 */
public class SimpleCache extends AbstractCache {

    private final ConcurrentHashMap<Object, ValueWrapper> memory = new ConcurrentHashMap<>();

    public SimpleCache(String name) {
        super(name);
    }


    @Override
    public void put(Object key, Object value) {
        Lock lock = getKeyLockManager().getWriteLock(key);
        try {
            lock.lock();
            memory.put(key, new ValueWrapper(value));
        } finally {
            lock.unlock();
        }
    }

    @Override
    public ValueWrapper get(Object key) {
        return memory.get(key);
    }

    @Override
    public boolean evict(Object key) {
        return memory.remove(key) != null;
    }

    @Override
    public void clear() {
        memory.clear();
    }
}
