package io.github.ithamal.itcache.service;

import java.util.List;

/**
 * @author: ken.lin
 * @since: 2023-09-26 09:46
 */
public abstract class ListCacheService<K, EK, ET> extends CacheService<K, List<EK>> {

    protected abstract EntityCacheService<EK, ET> getEntityCacheService();

    public List<ET> loadList(K key) {
        List<EK> entityKeys = load(key);
        return getEntityCacheService().batchLoad(entityKeys);
    }
}
