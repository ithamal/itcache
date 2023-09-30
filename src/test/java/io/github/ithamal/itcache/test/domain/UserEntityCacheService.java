package io.github.ithamal.itcache.test.domain;

import io.github.ithamal.itcache.service.EntityCacheService;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: ken.lin
 * @since: 2023-09-26 09:45
 */
public class UserEntityCacheService extends EntityCacheService<Integer, User> {

    @Override
    protected String getRegion() {
        return "users";
    }

    @Override
    protected String buildKey(Integer key) {
        return String.valueOf(key);
    }

    @Override
    protected Map<Integer, User> loadFromDb(Collection<Integer> keys) {
        HashMap<Integer, User> map = new HashMap<>();
        map.put(1, new User(1, "张三"));
        map.put(2, new User(2, "李四"));
        return map;
    }
}
