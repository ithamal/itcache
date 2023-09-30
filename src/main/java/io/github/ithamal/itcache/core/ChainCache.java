package io.github.ithamal.itcache.core;

import io.github.ithamal.itcache.service.Page;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;

/**
 * @author: ken.lin
 * @since: 2023-09-27 11:12
 */
@SuppressWarnings("unchecked")
public final class ChainCache implements Cache {

    private final String name;

    private final List<Cache> preCaches;

    private final Cache finalCache;

    @Getter
    private final List<Cache> allCaches;

    public ChainCache(String name, List<Cache> caches) {
        this.name = name;
        this.allCaches = caches;
        this.preCaches = new ArrayList<>();
        this.finalCache = caches.get(caches.size() - 1);
        for (Cache cache : caches) {
            if (cache != this.finalCache) {
                this.preCaches.add(cache);
            }
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void put(Object key, Object value) {
        finalCache.put(key, value);
        for (Cache cache : preCaches) {
            cache.put(key, value);
        }
    }

    @Override
    public void batchPut(Map<Object, Object> kvMap) {
        finalCache.batchPut(kvMap);
        for (Cache cache : preCaches) {
            cache.batchPut(kvMap);
        }
    }

    @Override
    @SneakyThrows
    public ValueWrapper get(Object key) {
        Queue<Cache> cacheQueue = new LinkedBlockingQueue<>(allCaches);
        GetRecursiveCall<Object> recursiveCall = new GetRecursiveCall<>(key, cacheQueue, null);
        return recursiveCall.call();
    }

    @Override
    @SneakyThrows
    public <T> T get(Object key, Class<T> type) {
        Queue<Cache> cacheQueue = new LinkedBlockingQueue<>(allCaches);
        GetRecursiveCall<T> recursiveCall = new GetRecursiveCall<>(key, cacheQueue, null);
        ValueWrapper valueWrapper = recursiveCall.call();
        return valueWrapper == null ? null : (T) valueWrapper.getValue();
    }

    @Override
    @SneakyThrows
    public <T> T get(Object key, Callable<T> valueLoader) {
        Queue<Cache> cacheQueue = new LinkedBlockingQueue<>(allCaches);
        GetRecursiveCall<T> recursiveCall = new GetRecursiveCall<>(key, cacheQueue, valueLoader);
        ValueWrapper valueWrapper = recursiveCall.call();
        return valueWrapper == null ? null : (T) valueWrapper.getValue();
    }

    @Override
    public Map<Object, ValueWrapper> batchGet(Collection<Object> keys) {
        Queue<Cache> cacheQueue = new LinkedBlockingQueue<>(allCaches);
        BatchGetRecursiveCall1 recursiveCall = new BatchGetRecursiveCall1(cacheQueue);
        return recursiveCall.apply(keys);
    }

    @Override
    public <T> Map<Object, T> batchGet(Collection<Object> keys, Function<Collection<Object>, Map<Object, T>> valueLoader) {
        Queue<Cache> cacheQueue = new LinkedBlockingQueue<>(allCaches);
        BatchGetRecursiveCall2<T> recursiveCall = new BatchGetRecursiveCall2<>(cacheQueue, valueLoader);
        return recursiveCall.apply(keys);
    }

    @Override
    public boolean evict(Object key) {
        boolean result = false;
        for (Cache cache : preCaches) {
            result = result || cache.evict(key);
        }
        result = result || finalCache.evict(key);
        return result;
    }

    @Override
    public void clear() {
        for (Cache cache : preCaches) {
            cache.clear();
        }
        finalCache.clear();
    }


    private static class BatchGetRecursiveCall1 implements Function<Collection<Object>, Map<Object, ValueWrapper>> {

        private final Queue<Cache> cacheQueue;

        public BatchGetRecursiveCall1(Queue<Cache> cacheQueue) {
            this.cacheQueue = cacheQueue;
        }

        @Override
        public Map<Object, ValueWrapper> apply(Collection<Object> keys) {
            Cache cache = cacheQueue.poll();
            assert cache != null;
            if (cacheQueue.isEmpty()) {
                return cache.batchGet(keys);
            } else {
                return cache.batchGet(keys, this);
            }
        }
    }


    private static class BatchGetRecursiveCall2<T> implements Function<Collection<Object>, Map<Object, T>> {

        private final Queue<Cache> cacheQueue;

        private final Function<Collection<Object>, Map<Object, T>> finalCall;

        public BatchGetRecursiveCall2(Queue<Cache> cacheQueue, Function<Collection<Object>, Map<Object, T>> finalCall) {
            this.cacheQueue = cacheQueue;
            this.finalCall = finalCall;
        }

        @Override
        public Map<Object, T> apply(Collection<Object> keys) {
            Cache cache = cacheQueue.poll();
            assert cache != null;
            if (cacheQueue.isEmpty()) {
                return cache.batchGet(keys, finalCall);
            } else {
                return cache.batchGet(keys, this);
            }
        }
    }

    private static class GetRecursiveCall<T> implements Callable<ValueWrapper> {

        private final Object key;

        private final Queue<Cache> cacheQueue;

        private final Callable<T> finalCall;

        public GetRecursiveCall(Object key, Queue<Cache> cacheQueue, Callable<T> finalCall) {
            this.key = key;
            this.cacheQueue = cacheQueue;
            this.finalCall = finalCall;
        }


        @Override
        public ValueWrapper call() throws Exception {
            Cache cache = cacheQueue.poll();
            assert cache != null;
            if (cacheQueue.isEmpty()) {
                if (finalCall == null) {
                    return cache.get(key);
                } else {
                    T value = cache.get(key, finalCall);
                    return new ValueWrapper(value);
                }
            } else {
                ValueWrapper valueWrapper = cache.get(key);
                if (valueWrapper == null) {
                    valueWrapper = this.call();
                    if (valueWrapper != null) {
                        cache.put(key, valueWrapper.getValue());
                    }
                }
                return valueWrapper;
            }
        }
    }
}
