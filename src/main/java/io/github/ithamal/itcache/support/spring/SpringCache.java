package io.github.ithamal.itcache.support.spring;

import io.github.ithamal.itcache.core.Cache;
import io.github.ithamal.itcache.core.CacheManager;
import lombok.val;

import java.util.concurrent.Callable;

/**
 * @author: ken.lin
 * @since: 2023-09-27 09:04
 */
public class SpringCache implements org.springframework.cache.Cache {

    private final String name;

    private final CacheManager cacheManager;

    public SpringCache(String name, CacheManager cacheManager){
        this.name = name;
        this.cacheManager = cacheManager;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Cache getNativeCache() {
        return cacheManager.getCache(name);
    }

    @Override
    public ValueWrapper get(Object key) {
        val valueWrapper = getNativeCache().get(key);
        if (valueWrapper == null) {
            return null;
        }
        return new SpringValueWrapper(valueWrapper.getValue());
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        return getNativeCache().get(key, type);
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        return getNativeCache().get(key, valueLoader);
    }

    @Override
    public void put(Object key, Object value) {
        getNativeCache().put(key, value);
    }

    @Override
    public void evict(Object key) {
        getNativeCache().evict(key);
    }

    @Override
    public void clear() {
        getNativeCache().clear();
    }
}
