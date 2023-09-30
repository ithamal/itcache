package io.github.ithamal.itcache.util;


import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * @author: ken.lin
 * @since: 2023-09-20 09:49
 */
public class SingleList<E> implements List<E> {

    private E element;

    public SingleList(){}

    public SingleList(E element){
        this.element = element;
    }

    @Override
    public int size() {
        return element != null ? 1 : 0;
    }

    @Override
    public boolean isEmpty() {
        return element == null;
    }

    @Override
    public boolean contains(Object o) {
        return element == o;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {

            private boolean hasNext = true;

            @Override
            public boolean hasNext() {
                return hasNext;
            }

            @Override
            public E next() {
                hasNext = false;
                return element;
            }
        };
    }

    @Override
    public Object[] toArray() {
        return new Object[]{element};
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new RuntimeException();
    }

    @Override
    public boolean add(E e) {
        this.element = e;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        if (this.element == o) {
            this.element = null;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new RuntimeException("NotImplementedException");
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        throw new RuntimeException("NotImplementedException");
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        throw new RuntimeException("NotImplementedException");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new RuntimeException("NotImplementedException");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new RuntimeException("NotImplementedException");
    }

    @Override
    public void clear() {
        element = null;
    }

    @Override
    public E get(int index) {
        if (index > 0) {
            throw new IndexOutOfBoundsException();
        }
        return element;
    }

    @Override
    public E set(int index, E element) {
        this.element = element;
        return element;
    }

    @Override
    public void add(int index, E element) {
        this.element = element;
    }

    @Override
    public E remove(int index) {
        E oldValue = this.element;
        this.element = null;
        return oldValue;
    }

    @Override
    public int indexOf(Object o) {
        return 0;
    }

    @Override
    public int lastIndexOf(Object o) {
        return 0;
    }

    @Override
    public ListIterator<E> listIterator() {
        throw new RuntimeException("NotImplementedException");
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        throw new RuntimeException("NotImplementedException");
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        throw new RuntimeException("NotImplementedException");
    }

}
