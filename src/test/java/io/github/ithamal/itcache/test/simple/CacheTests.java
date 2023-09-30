package io.github.ithamal.itcache.test.simple;

import io.github.ithamal.itcache.core.impl.SimpleCacheManager;
import io.github.ithamal.itcache.test.domain.QueryDto;
import io.github.ithamal.itcache.test.domain.UserEntityCacheService;
import io.github.ithamal.itcache.test.domain.UserListCacheService;
import io.github.ithamal.itcache.test.domain.UserPageCacheService;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * @author: ken.lin
 * @since: 2023-09-26 11:49
 */
public class CacheTests {


    @Test
    public void testEntityCache(){
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        UserEntityCacheService userCacheService = new UserEntityCacheService();
        userCacheService.setCacheManager(cacheManager);
        System.out.println(userCacheService.load(1));
        System.out.println(userCacheService.batchLoad(Arrays.asList(1, 2, 3 ,4)));
        System.out.println(userCacheService.batchLoad(Arrays.asList(1, 2, 3 ,4)));
    }

    @Test
    public void testListCache(){
        SimpleCacheManager cacheManager = new SimpleCacheManager();
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
        SimpleCacheManager cacheManager = new SimpleCacheManager();
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
}
