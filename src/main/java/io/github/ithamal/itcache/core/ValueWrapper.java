package io.github.ithamal.itcache.core;

import lombok.*;

/**
 * @author: ken.lin
 * @since: 2023-09-26 09:55
 */
public class ValueWrapper{

    private final Object value;

    public ValueWrapper(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value == null ? null : value.toString();
    }
}
