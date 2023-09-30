package io.github.ithamal.itcache.boot;

import io.github.ithamal.itcache.config.CacheConfig;
import io.github.ithamal.itcache.config.CacheSetting;
import io.github.ithamal.itcache.core.Cache;
import io.github.ithamal.itcache.core.CacheManager;
import io.github.ithamal.itcache.core.ChainCache;
import io.github.ithamal.itcache.core.impl.SimpleCacheManager;
import io.github.ithamal.itcache.support.hutool.HutoolCacheManager;
import io.github.ithamal.itcache.support.redis.RedisCacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextException;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: ken.lin
 * @since: 2023-09-27 10:12
 */

public class AutoCacheManager implements CacheManager {

    private final CacheConfig config;

    private final SimpleCacheManager simpleCacheManager = new SimpleCacheManager();

    @Resource
    private ApplicationContext applicationContext;

    public AutoCacheManager(CacheConfig config) {
        this.config = config;
    }

    @Override
    public Cache getCache(String name) {
        CacheSetting setting = config.getCacheSetting(name);
        Cache cache = getCache(name, setting);
        if (setting.getPreCaches() != null && setting.getPreCaches().length > 0) {
            List<Cache> caches = selectPreCaches(name, setting.getPreCaches());
            caches.add(cache);
            cache = new ChainCache(name, caches);
        }
        return cache;
    }

    private List<Cache> selectPreCaches(String name, String[] preCaches) {
        return Arrays.stream(preCaches).map(it -> {
            if (it.startsWith("$$")) {
                String templateName = it.substring(2);
                return new String[]{ name + "$$" + templateName, templateName};
            } else {
                return new String[]{ name, "default"};
            }
        }).map(it -> {
            String finalName = it[0];
            String templateName = it[1];
            CacheSetting setting = config.getCacheSetting(finalName, templateName);
            return getCache(finalName, setting);
        }).collect(Collectors.toList());
    }


    private Cache getCache(String name, CacheSetting setting) {
        Cache cache = null;
        String impl = setting.getImplClass();
        if (impl == null) {
            cache = simpleCacheManager.getCache(name);
        } else if (impl.startsWith("redis")) {
            cache = applicationContext.getBean(RedisCacheManager.class).getCache(name);
        } else if (impl.startsWith("hutool")) {
            cache = applicationContext.getBean(HutoolCacheManager.class).getCache(name);
        }
        if (cache == null) {
            throw new ApplicationContextException("不支持的缓存实现方式:" + impl);
        }
        return cache;
    }
}
