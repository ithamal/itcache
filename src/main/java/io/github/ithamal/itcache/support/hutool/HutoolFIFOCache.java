package io.github.ithamal.itcache.support.hutool;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import io.github.ithamal.itcache.config.CacheSetting;
import io.github.ithamal.itcache.core.AbstractCache;
import io.github.ithamal.itcache.core.ValueWrapper;

/**
 * @author: ken.lin
 * @since: 2023-09-27 15:09
 */
public class HutoolFIFOCache extends HutoolAbstractCache {

    public HutoolFIFOCache(String name, CacheSetting setting) {
        super(name, setting);
    }

    @Override
    protected Cache<Object, Object> createNativeCache(int capacity, int timeout) {
        return CacheUtil.newFIFOCache(capacity, timeout);
    }
}
