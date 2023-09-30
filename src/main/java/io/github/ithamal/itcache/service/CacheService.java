package io.github.ithamal.itcache.service;

import io.github.ithamal.itcache.core.Cache;
import io.github.ithamal.itcache.core.CacheManager;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.Callable;

/**
 * @author: ken.lin
 * @since: 2023-09-26 09:46
 */
public abstract class CacheService<K, V> implements ICacheService<K, V> {

    @Getter
    @Setter
    private CacheManager cacheManager;

    private volatile Cache cache;

    protected abstract String getRegion();

    protected abstract String buildKey(K key);

    protected abstract V loadFromDb(K key);

    protected Cache getCache() {
        if (cache == null) {
            synchronized (this) {
                if (cache == null) {
                    cache = cacheManager.getCache(getRegion());
                }
            }
        }
        return cache;
    }

    public V load(K key) {
        return getCache().get(buildKey(key), () -> loadFromDb(key));
    }

    public V loadIfAbsent(K key, Callable<V> valueLoader) {
        return getCache().get(buildKey(key), valueLoader);
    }

    public boolean evict(K key) {
        return getCache().evict(buildKey(key));
    }

    public void clear() {
        getCache().clear();
    }
}
