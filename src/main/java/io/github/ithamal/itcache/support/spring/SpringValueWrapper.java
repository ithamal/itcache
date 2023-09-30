package io.github.ithamal.itcache.support.spring;

import lombok.*;
import org.springframework.cache.Cache;

/**
 * @author: ken.lin
 * @since: 2023-09-27 09:39
 */
public class SpringValueWrapper implements Cache.ValueWrapper {

    public Object value;

    public SpringValueWrapper(Object value){
        this.value = value;
    }

    @Override
    public Object get() {
        return value;
    }

    @Override
    public String toString() {
        return value == null ? null : value.toString();
    }
}
