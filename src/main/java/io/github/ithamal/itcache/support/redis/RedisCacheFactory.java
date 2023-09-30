package io.github.ithamal.itcache.support.redis;

import io.github.ithamal.itcache.config.CacheConfig;
import io.github.ithamal.itcache.config.CacheSetting;
import io.github.ithamal.itcache.core.Cache;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * @author: ken.lin
 * @since: 2023-09-26 14:56
 */
@SuppressWarnings("unchecked")
public class RedisCacheFactory {

    private final RedisConnectionFactory connectionFactory;

    private final CacheConfig config;

    public RedisCacheFactory(CacheConfig config, RedisConnectionFactory connectionFactory) {
        this.config = config;
        this.connectionFactory = connectionFactory;
    }

    public RedisConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public Cache createCache(String name) {
        CacheSetting setting = config.getCacheSetting(name);
        if ("redisKV".equals(setting.getImplClass())) {
            return new RedisKVCache(name, setting, this);
        } else  if ("redisHash".equals(setting.getImplClass())) {
            return new RedisHashCache(name, setting, this);
        }  else {
            return new RedisKVCache(name, setting, this);
        }
    }
}
