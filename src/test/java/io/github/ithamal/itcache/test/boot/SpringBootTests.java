package io.github.ithamal.itcache.test.boot;

import io.github.ithamal.itcache.core.ChainCache;
import io.github.ithamal.itcache.test.SpringTestApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author: ken.lin
 * @since: 2023-09-19 17:28
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringTestApplication.class)
public class SpringBootTests {

    @Resource
    private CacheManager cacheManager;

    @Test
    public void test() throws Exception {
        Cache cache = cacheManager.getCache("test2");
        cache.put(1, "张三");
        System.out.println(((ChainCache)cache.getNativeCache()).getAllCaches().get(0).get(1)); // 张三
        Thread.sleep(10);
        System.out.println(((ChainCache)cache.getNativeCache()).getAllCaches().get(0).get(1));  // null
        System.out.println(cache.get(1)); // 张三
        System.out.println(cache.get(2));  // null
    }
}
