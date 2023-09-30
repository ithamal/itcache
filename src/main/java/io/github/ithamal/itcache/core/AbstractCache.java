package io.github.ithamal.itcache.core;

import io.github.ithamal.itcache.lock.LockManager;
import io.github.ithamal.itcache.lock.SimpleLockManager;
import lombok.SneakyThrows;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.function.Function;

/**
 * @author: ken.lin
 * @since: 2023-09-26 15:49
 */
@SuppressWarnings("unchecked")
public abstract class AbstractCache implements Cache {

    private String name;

    public AbstractCache(String name){
        this.name = name;
    }

    private volatile LockManager lockManager;

    public void setKeyLockManager(LockManager lockManager) {
        this.lockManager = lockManager;
    }

    protected LockManager getKeyLockManager() {
        if (lockManager != null) {
            return lockManager;
        }
        synchronized (this) {
            if (lockManager == null) {
                lockManager = new SimpleLockManager();
            }
        }
        return lockManager;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void batchPut(Map<Object, Object> kvMap) {
        kvMap.forEach(this::put);
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        ValueWrapper valueWrapper = get(key);
        if (type == ValueWrapper.class) {
            return (T) valueWrapper;
        }
        return valueWrapper == null ? null : (T) valueWrapper.getValue();
    }

    @Override
    @SneakyThrows
    public <T> T get(Object key, Callable<T> valueLoader) {
        ValueWrapper valueWrapper = get(key);
        if (valueWrapper != null) {
            return (T) valueWrapper.getValue();
        }
        Lock lock = getKeyLockManager().getWriteLock(key);
        lock.lock();
        try {
            valueWrapper = get(key);
            if (valueWrapper != null) {
                return (T) valueWrapper.getValue();
            }
            T value = valueLoader.call();
            put(key, value);
            return value;
        } finally {
            lock.unlock();
        }
    }


    @Override
    public Map<Object, ValueWrapper> batchGet(Collection<Object> keys) {
        HashMap<Object, ValueWrapper> resultMap = new HashMap<>(keys.size());
        for (Object key : keys) {
            ValueWrapper valueWrapper = get(key);
            if (valueWrapper != null) {
                resultMap.put(key, valueWrapper);
            }
        }
        return resultMap;
    }

    @Override
    public <T> Map<Object, T> batchGet(Collection<Object> keys, Function<Collection<Object>, Map<Object, T>> valueLoader) {
        HashMap<Object, T> resultMap = new HashMap<>();
        Map<Object, ValueWrapper> valuesMap = batchGet(keys);
        List<Object> absentKeys = new ArrayList<>(keys.size() - valuesMap.size());
        for (Object key : keys) {
            ValueWrapper valueWrapper = valuesMap.get(key);
            if (valueWrapper == null) {
                absentKeys.add(key);
            } else {
                resultMap.put(key, (T) valueWrapper.getValue());
            }
        }
        if (!absentKeys.isEmpty()) {
            Map<Object, Object> subResultMap = (Map<Object, Object>) valueLoader.apply(absentKeys);
            if (!subResultMap.isEmpty()) {
                batchPut(subResultMap);
                subResultMap.forEach((k, v) -> {
                    resultMap.put(k, (T) v);
                });
            }
        }
        return resultMap;
    }
}
