package io.github.ithamal.itcache.core;

/**
 * @author: ken.lin
 * @since: 2023-09-26 09:53
 */
public interface CacheManager {

    Cache getCache(String name);
}
