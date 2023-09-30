package io.github.ithamal.itcache.lock;

import java.util.concurrent.locks.Lock;

/**
 * @author: ken.lin
 * @since: 2023-09-26 16:17
 */
public interface LockManager {

    Lock getReadLock(Object key);

    Lock getWriteLock(Object key);
}
