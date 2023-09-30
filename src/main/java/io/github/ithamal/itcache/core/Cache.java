package io.github.ithamal.itcache.core;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Function;

/**
 * @author: ken.lin
 * @since: 2023-09-26 09:51
 */
public interface Cache {

    String getName();

    void put(Object key, Object value);

    void batchPut(Map<Object, Object> kvMap);

    ValueWrapper get(Object key);

    <T> T get(Object key, Class<T> type);

    <T> T get(Object key, Callable<T> valueLoader);

    Map<Object, ValueWrapper> batchGet(Collection<Object> keys);

    <T> Map<Object, T> batchGet(Collection<Object> keys, Function<Collection<Object>, Map<Object, T>> valueLoader);

    boolean evict(Object key);

    void clear();
}
