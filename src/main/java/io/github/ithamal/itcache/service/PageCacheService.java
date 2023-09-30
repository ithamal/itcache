package io.github.ithamal.itcache.service;

import java.util.Collection;
import java.util.List;

/**
 * @author: ken.lin
 * @since: 2023-09-26 09:46
 */
public abstract class PageCacheService<K, EK, ET> extends CacheService<K, Page<EK>> {

    protected abstract EntityCacheService<EK, ET> getEntityCacheService();

    public Page<ET> loadPage(K key) {
        Page<EK> page = load(key);
        Collection<EK> keys = page.getItems();
        List<ET> items = getEntityCacheService().batchLoad(keys);
        return Page.<ET>of(page.getCurrent(), page.getSize()).total(page.getTotal()).items(items);
    }
}
