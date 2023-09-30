package io.github.ithamal.itcache.support.hutool;

import io.github.ithamal.itcache.config.CacheConfig;
import io.github.ithamal.itcache.config.CacheSetting;
import io.github.ithamal.itcache.core.Cache;

/**
 * @author: ken.lin
 * @since: 2023-09-26 14:56
 */
@SuppressWarnings("unchecked")
public class HutoolCacheFactory {

    private final CacheConfig config;

    public HutoolCacheFactory(CacheConfig config) {
        this.config = config;
    }

    public Cache createCache(String name) {
        CacheSetting setting = config.getCacheSetting(name);
        if ("hutoolFIFO".equals(setting.getImplClass())) {
            return new HutoolFIFOCache(name, setting);
        } else if ("hutoolLRU".equals(setting.getImplClass())) {
            return new HutoolLRUCache(name, setting);
        } else if ("hutoolLFU".equals(setting.getImplClass())) {
            return new HutoolLFUCache(name, setting);
        } else {
            return new HutoolLRUCache(name, setting);
        }
    }
}
