package io.github.ithamal.itcache.test.domain;

import io.github.ithamal.itcache.service.EntityCacheService;
import io.github.ithamal.itcache.service.ListCacheService;

import java.util.*;

/**
 * @author: ken.lin
 * @since: 2023-09-26 09:45
 */
public class UserListCacheService extends ListCacheService<Integer, Integer, User> {

    @Override
    protected String getRegion() {
        return "users.list";
    }

    @Override
    protected String buildKey(Integer key) {
        return String.valueOf(key);
    }

    @Override
    protected List<Integer> loadFromDb(Integer key) {
        return Arrays.asList(1, 2, 3,4);
    }

    @Override
    protected EntityCacheService<Integer, User> getEntityCacheService() {
        UserEntityCacheService service = new UserEntityCacheService();
        service.setCacheManager(this.getCacheManager());
        return service;
    }
}
