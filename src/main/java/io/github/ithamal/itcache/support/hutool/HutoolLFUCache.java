package io.github.ithamal.itcache.support.hutool;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import io.github.ithamal.itcache.config.CacheSetting;

/**
 * @author: ken.lin
 * @since: 2023-09-27 15:09
 */
public class HutoolLFUCache extends HutoolAbstractCache {

    public HutoolLFUCache(String name, CacheSetting setting) {
        super(name, setting);
    }

    @Override
    protected Cache<Object, Object> createNativeCache(int capacity, int timeout) {
        return CacheUtil.newLFUCache(capacity, timeout);
    }
}
