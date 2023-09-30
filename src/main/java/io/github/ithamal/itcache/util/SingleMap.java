package io.github.ithamal.itcache.util;

import java.util.*;

/**
 * @author: ken.lin
 * @since: 2023-09-20 09:49
 */
public class SingleMap<K,V> implements Map<K,V> {

    private K key;

    private V value;

    public SingleMap(){}

    public SingleMap(K key, V value){
        this.put(key, value);
    }

    @Override
    public int size() {
        return value != null ? 1: 0;
    }

    @Override
    public boolean isEmpty() {
        return value == null;
    }

    @Override
    public boolean containsKey(Object key) {
        return Objects.equals(this.key, key);
    }

    @Override
    public boolean containsValue(Object value) {
        return Objects.equals(this.value, value);
    }

    @Override
    public V get(Object key) {
        return containsKey(key) ? value : null;
    }

    @Override
    public V put(K key, V value) {
        this.key = key;
        this.value = value;
        return this.value;
    }

    @Override
    public V remove(Object key) {
        this.key = null;
        this.value = null;
        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        throw new RuntimeException("NotImplementedException");
    }

    @Override
    public void clear() {
        this.key = null;
        this.value = null;
    }

    @Override
    public Set<K> keySet() {
        Set<K> keys = new HashSet<>(1);
        if(key != null){
            keys.add(key);
        }
        return keys;
    }

    @Override
    public Collection<V> values() {
        Set<V> values = new HashSet<>(1);
        if(value != null){
            values.add(value);
        }
        return values;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        throw new RuntimeException("NotImplementedException");
    }

    public V getValue() {
        return value;
    }
}
