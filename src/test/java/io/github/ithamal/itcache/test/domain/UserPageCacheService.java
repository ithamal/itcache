package io.github.ithamal.itcache.test.domain;

import io.github.ithamal.itcache.service.EntityCacheService;
import io.github.ithamal.itcache.service.Page;
import io.github.ithamal.itcache.service.PageCacheService;

import java.util.Arrays;

/**
 * @author: ken.lin
 * @since: 2023-09-26 09:45
 */
public class UserPageCacheService extends PageCacheService<QueryDto, Integer, User> {

    @Override
    protected String getRegion() {
        return "users.page";
    }

    @Override
    protected String buildKey(QueryDto key) {
        return key.toString();
    }

    @Override
    protected Page<Integer> loadFromDb(QueryDto key) {
        return Page.<Integer>of(key.getPageNumber(), key.getPageSize()).items(Arrays.asList(1, 2, 3));
    }

    @Override
    protected EntityCacheService<Integer, User> getEntityCacheService() {
        UserEntityCacheService service = new UserEntityCacheService();
        service.setCacheManager(this.getCacheManager());
        return service;
    }
}
