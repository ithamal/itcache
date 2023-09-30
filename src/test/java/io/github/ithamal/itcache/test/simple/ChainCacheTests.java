package io.github.ithamal.itcache.test.simple;

import io.github.ithamal.itcache.core.Cache;
import io.github.ithamal.itcache.core.ChainCache;
import io.github.ithamal.itcache.core.impl.SimpleCacheManager;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * @author: ken.lin
 * @since: 2023-09-27 11:57
 */
public class ChainCacheTests {

    @Test
    public void testGet(){
        SimpleCacheManager simpleCacheManager = new SimpleCacheManager();
        Cache cache1 = simpleCacheManager.getCache("L1");
        Cache cache2 = simpleCacheManager.getCache("L2");
        cache2.put(1, "张三");
        cache2.put(2, "李四");
        cache2.put(3 , "王五");
        ChainCache chainCache = new ChainCache("Chain", Arrays.asList(cache1, cache2));
        System.out.println(chainCache.get(1));
        System.out.println(cache1.get(1));
        System.out.println(cache1.get(2));
        System.out.println(cache1.get(4));
        System.out.println();
    }


    @Test
    public void testBatchGet(){
        SimpleCacheManager simpleCacheManager = new SimpleCacheManager();
        Cache cache1 = simpleCacheManager.getCache("L1");
        Cache cache2 = simpleCacheManager.getCache("L2");
        cache2.put(1, "张三");
        cache2.put(2, "李四");
        cache2.put(3 , "王五");
        ChainCache chainCache = new ChainCache("Chain", Arrays.asList(cache1, cache2));
        System.out.println(chainCache.batchGet(Arrays.asList(1, 2, 4)));
        System.out.println(cache1.get(1));
        System.out.println(cache1.get(2));
        System.out.println();
    }
}
