package io.github.ithamal.itcache.core.impl;

import io.github.ithamal.itcache.core.Cache;
import io.github.ithamal.itcache.core.CacheManager;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: ken.lin
 * @since: 2023-09-26 11:51
 */
@SuppressWarnings("unchecked")
public class SimpleCacheManager implements CacheManager {

    private final ConcurrentHashMap<String, Cache> hashMap = new ConcurrentHashMap<>();

    @Override
    public Cache getCache(String name) {
        return hashMap.computeIfAbsent(name, k -> new SimpleCache(name));
    }
}
