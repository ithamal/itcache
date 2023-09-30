package io.github.ithamal.itcache.test.simple;

import io.github.ithamal.itcache.config.CacheConfig;
import io.github.ithamal.itcache.config.CacheSetting;
import io.github.ithamal.itcache.core.CacheManager;
import io.github.ithamal.itcache.core.impl.SimpleCacheManager;
import io.github.ithamal.itcache.support.spring.SpringCacheManager;
import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;

/**
 * @author: ken.lin
 * @since: 2023-09-26 11:49
 */
public class SpringCacheTests {

    public SpringCacheManager getCacheManager() {
        CacheConfig config = new CacheConfig();
        config.putTemplate("default", new CacheSetting());
        CacheManager cacheManager = new SimpleCacheManager();
        return new SpringCacheManager(config, cacheManager);
    }

    @Test
    public void test() {
        SpringCacheManager cacheManager = getCacheManager();
        Cache cache = cacheManager.getCache("test");
        cache.put(1, "张三");
        System.out.println(cache.get(1));
        System.out.println(cache.get(2));
    }
}
