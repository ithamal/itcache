package io.github.ithamal.itcache.service;

import lombok.*;

/**
 * @author: ken.lin
 * @since: 2023-09-26 10:51
 */
public interface ICacheService<K, V> {

    boolean evict(K key);

    void clear();
}
