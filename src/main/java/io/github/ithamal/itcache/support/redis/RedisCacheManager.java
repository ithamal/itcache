package io.github.ithamal.itcache.support.redis;

import io.github.ithamal.itcache.core.Cache;
import io.github.ithamal.itcache.core.CacheManager;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: ken.lin
 * @since: 2023-09-26 17:12
 */
@SuppressWarnings("unchecked")
public class RedisCacheManager implements CacheManager {

    private final ConcurrentHashMap<String, Cache> hashMap = new ConcurrentHashMap<>();

    private final RedisCacheFactory cacheFactory;

    public RedisCacheManager(RedisCacheFactory cacheFactory) {
        this.cacheFactory = cacheFactory;
    }

    @Override
    public Cache getCache(String name) {
        return hashMap.computeIfAbsent(name, cacheFactory::createCache);
    }
}
