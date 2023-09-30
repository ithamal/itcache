package io.github.ithamal.itcache.service;

import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import javax.xml.transform.Result;
import java.util.Collection;

/**
 * @author: ken.lin
 * @since: 2023-09-26 14:27
 */
@Getter
@ToString
public class Page<T> {

    private int total;

    private int current;

    private int size;

    private Collection<T> items;

    public static <T> Page<T> of(int current, int size) {
        Page<T> page = new Page<>();
        page.current = current;
        page.size = size;
        return page;
    }

    public Page<T> items(Collection<T> items){
        this.items = items;
        return this;
    }

    public Page<T> total(int total){
        this.total = total;
        return this;
    }
}
