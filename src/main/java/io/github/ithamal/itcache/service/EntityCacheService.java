package io.github.ithamal.itcache.service;

import io.github.ithamal.itcache.core.Cache;
import io.github.ithamal.itcache.core.CacheManager;
import io.github.ithamal.itcache.util.SingleList;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Function;

/**
 * @author: ken.lin
 * @since: 2023-09-26 09:46
 */
public abstract class EntityCacheService<K, V> implements ICacheService<K, V> {

    @Getter
    @Setter
    private CacheManager cacheManager;

    private volatile Cache cache;

    protected abstract String getRegion();

    protected abstract String buildKey(K key);

    protected abstract Map<K, V> loadFromDb(Collection<K> keys);

    protected Cache getCache() {
        if (cache == null) {
            synchronized (this) {
                if (cache == null) {
                    cache = cacheManager.getCache(getRegion());
                }
            }
        }
        return cache;
    }

    public V load(K key) {
        return getCache().get(buildKey(key), () -> {
            Map<K, V> resultMap = loadFromDb(new SingleList<>(key));
            return resultMap.isEmpty() ? null : resultMap.values().iterator().next();
        });
    }

    public List<V> batchLoad(Collection<K> keys) {
        return batchLoadIfAbsent(keys, this::loadFromDb);
    }

    public V loadIfAbsent(K key, Callable<V> valueLoader) {
        return getCache().get(buildKey(key), valueLoader);
    }

    public List<V> batchLoadIfAbsent(Collection<K> keys, Function<Collection<K>, Map<K, V>> valueLoader) {
        List<Object> buildKeyList = new ArrayList<>(keys.size());
        Map<Object, K> buildKeyMap = new HashMap<>(keys.size());
        for (K key : keys) {
            String buildKey = buildKey(key);
            buildKeyList.add(buildKey);
            buildKeyMap.put(buildKey, key);
        }
        Map<Object, V> resultMap = getCache().batchGet(buildKeyList, (buildKeySubList) -> {
            List<K> subKeyList = new ArrayList<>(buildKeySubList.size());
            for (Object buildKey : buildKeySubList) {
                subKeyList.add(buildKeyMap.get(buildKey));
            }
            Map<K, V> resultDBMap = loadFromDb(subKeyList);
            Map<Object, V> resultSubMap = new HashMap<>(resultDBMap.size());
            int index = 0;
            for (Object buildKey : buildKeySubList) {
                K subKey = subKeyList.get(index++);
                V value = resultDBMap.get(subKey);
                resultSubMap.put(buildKey, value);
            }
            return resultSubMap;
        });
        List<V> resultList = new ArrayList<>();
        for (Object buildKey : buildKeyList) {
            V value = resultMap.get(buildKey);
            if (value != null) {
                resultList.add(value);
            }
        }
        return resultList;
    }

    public boolean evict(K key) {
        return getCache().evict(buildKey(key));
    }

    public void clear() {
        getCache().clear();
    }
}
