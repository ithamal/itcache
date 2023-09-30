package io.github.ithamal.itcache.support.hutool;

import cn.hutool.cache.Cache;
import io.github.ithamal.itcache.config.CacheSetting;
import io.github.ithamal.itcache.core.AbstractCache;
import io.github.ithamal.itcache.core.ValueWrapper;

/**
 * @author: ken.lin
 * @since: 2023-09-27 15:09
 */
public abstract class HutoolAbstractCache extends AbstractCache {

    private final CacheSetting setting;

    private final Cache<Object, Object> nativeCache;

    protected abstract Cache<Object, Object> createNativeCache(int capacity, int timeout);

    public HutoolAbstractCache(String name, CacheSetting setting) {
        super(name);
        this.setting = setting;
        int capacity = setting.getMaxElements();
        capacity = capacity > 0 ? capacity : Integer.MAX_VALUE;
        int timeout = setting.getTimeToLiveSeconds();
        timeout = Math.max(timeout, 0);
//        this.nativeCache = CacheUtil.newLFUCache(capacity, timeout);
        this.nativeCache = this.createNativeCache(capacity, timeout);
    }

    @Override
    public void put(Object key, Object value) {
        nativeCache.put(key, value);
    }

    @Override
    public ValueWrapper get(Object key) {
        if (!nativeCache.containsKey(key)) {
            return null;
        }
        boolean isUpdateLastAccess = setting.getUpdateLiveAfterAccess();
        Object value = nativeCache.get(key, isUpdateLastAccess);
        return new ValueWrapper(value);
    }

    @Override
    public boolean evict(Object key) {
        if (!nativeCache.containsKey(key)) {
            return false;
        }
        nativeCache.remove(key);
        return true;
    }

    @Override
    public void clear() {
        nativeCache.clear();
    }
}
