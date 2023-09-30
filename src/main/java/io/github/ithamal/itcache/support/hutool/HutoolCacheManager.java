package io.github.ithamal.itcache.support.hutool;

import io.github.ithamal.itcache.config.CacheConfig;
import io.github.ithamal.itcache.core.Cache;
import io.github.ithamal.itcache.core.CacheManager;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: ken.lin
 * @since: 2023-09-26 17:12
 */
@SuppressWarnings("unchecked")
public class HutoolCacheManager implements CacheManager {

    private final ConcurrentHashMap<String, Cache> hashMap = new ConcurrentHashMap<>();

    private final HutoolCacheFactory cacheFactory;

    public HutoolCacheManager(CacheConfig config) {
        this.cacheFactory = new HutoolCacheFactory(config);
    }

    @Override
    public Cache getCache(String name) {
        return hashMap.computeIfAbsent(name, cacheFactory::createCache);
    }
}
