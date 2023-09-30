package io.github.ithamal.itcache.support.spring;

import io.github.ithamal.itcache.config.CacheConfig;
import io.github.ithamal.itcache.config.CacheSetting;
import io.github.ithamal.itcache.core.CacheManager;
import lombok.*;
import org.springframework.cache.Cache;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @author: ken.lin
 * @since: 2023-09-27 09:28
 */
public class SpringCacheManager implements org.springframework.cache.CacheManager {

    public final CacheConfig config;

    private final CacheManager nativeCacheManager;

    public SpringCacheManager(CacheConfig config, CacheManager cacheManager) {
        this.config = config;
        this.nativeCacheManager = cacheManager;
    }


    @Override
    public Cache getCache(String name) {
        return new SpringCache(name, nativeCacheManager);
    }

    @Override
    public Collection<String> getCacheNames() {
        return config.getCacheNames();
    }
}
