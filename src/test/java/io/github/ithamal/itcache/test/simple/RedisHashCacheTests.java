package io.github.ithamal.itcache.test.simple;

import io.github.ithamal.itcache.config.CacheConfig;
import io.github.ithamal.itcache.config.CacheSetting;
import io.github.ithamal.itcache.core.Cache;
import io.github.ithamal.itcache.core.CacheManager;
import io.github.ithamal.itcache.core.ValueWrapper;
import io.github.ithamal.itcache.support.redis.RedisCacheFactory;
import io.github.ithamal.itcache.support.redis.RedisCacheManager;
import io.github.ithamal.itcache.test.domain.QueryDto;
import io.github.ithamal.itcache.test.domain.UserEntityCacheService;
import io.github.ithamal.itcache.test.domain.UserListCacheService;
import io.github.ithamal.itcache.test.domain.UserPageCacheService;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: ken.lin
 * @since: 2023-09-26 11:49
 */
public class RedisHashCacheTests {

    public RedisCacheManager getCacheManager(){
        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory();
        connectionFactory.setHostName("localhost");
        connectionFactory.setPort(6379);
        connectionFactory.setPassword("");
        connectionFactory.setDatabase(4);
        connectionFactory.afterPropertiesSet();
        CacheConfig config = new CacheConfig();
        CacheSetting setting = new CacheSetting();
        setting.setImplClass("redisHash");
        config.putTemplate("default", setting);
        RedisCacheFactory redisCacheFactory = new RedisCacheFactory(config, connectionFactory);
        return new RedisCacheManager(redisCacheFactory);
    }


    @Test
    public void testEntityCache(){
        CacheManager cacheManager = getCacheManager();
        UserEntityCacheService userCacheService = new UserEntityCacheService();
        userCacheService.setCacheManager(cacheManager);
        System.out.println(userCacheService.load(1));
        System.out.println(userCacheService.batchLoad(Arrays.asList(1, 2, 3 ,4)));
        System.out.println(userCacheService.batchLoad(Arrays.asList(1, 2, 3 ,4)));
    }

    @Test
    public void testListCache(){
        CacheManager cacheManager = getCacheManager();
        UserListCacheService userListCacheService = new UserListCacheService();
        userListCacheService.setCacheManager(cacheManager);
        System.out.println(userListCacheService.loadIfAbsent(1, ()->{
            return Arrays.asList(2);
        }));
        System.out.println(userListCacheService.loadList(1));
        System.out.println(userListCacheService.loadList(2));
    }

    @Test
    public void testPageCache(){
        CacheManager cacheManager = getCacheManager();
        UserPageCacheService userPageCacheService = new UserPageCacheService();
        userPageCacheService.setCacheManager(cacheManager);
        QueryDto queryDto = new QueryDto();
        queryDto.setPageNumber(1);
        queryDto.setPageSize(20);
//        userPageCacheService.loadIfAbsent(queryDto, ()->{
//            return Page.<Integer>of(queryDto.getPageNumber(), queryDto.getPageSize()).items(Arrays.asList(1));
//        });
        System.out.println(userPageCacheService.loadPage(queryDto));
    }

    @Test
    public void testBatch(){
        CacheManager cacheManager = getCacheManager();
        Cache cache = cacheManager.getCache("test");
        HashMap<Object, Object> kvMap = new HashMap<>();
        kvMap.put(1, "张三");
        kvMap.put(2, "李四");
        cache.batchPut(kvMap);
        Map<Object, ValueWrapper> map = cache.batchGet(Arrays.asList(1, 2, 3));
        System.out.println(map);
    }
}
