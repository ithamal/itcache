package io.github.ithamal.itcache.util;

import io.github.ithamal.itcache.service.ICacheService;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author: ken.lin
 * @since: 2023-09-26 11:29
 */
public class EvictChain implements Runnable {

    private final List<Object> keyList = new ArrayList<>();

    private final List<ICacheService<?, ?>> serviceList = new ArrayList<>();

    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    public EvictChain add(ICacheService<?, ?> service, Object key) {
        serviceList.add(service);
        keyList.add(key);
        return this;
    }

    @SneakyThrows
    public <R> R call(Callable<R> valueLoader) {
        this.run();
        R result = valueLoader.call();
        executorService.schedule(this, 5, TimeUnit.SECONDS);
        return result;
    }

    public void call(Runnable runnable) {
        this.run();
        runnable.run();
        executorService.schedule(this, 5, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        for (int i = 0; i < serviceList.size(); i++) {
            ICacheService service = serviceList.get(i);
            service.evict(keyList.get(i));
        }
    }
}
