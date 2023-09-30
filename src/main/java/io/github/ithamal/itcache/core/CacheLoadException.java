package io.github.ithamal.itcache.core;

import lombok.*;

/**
 * @author: ken.lin
 * @since: 2023-09-26 11:54
 */
public class CacheLoadException extends RuntimeException{

    public CacheLoadException(Exception e) {
        super(e);
    }
}
