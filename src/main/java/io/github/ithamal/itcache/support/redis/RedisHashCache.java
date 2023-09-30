package io.github.ithamal.itcache.support.redis;

import io.github.ithamal.itcache.config.CacheSetting;
import io.github.ithamal.itcache.core.AbstractCache;
import io.github.ithamal.itcache.core.ValueWrapper;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.util.*;

/**
 * @author: ken.lin
 * @since: 2023-09-26 14:55
 */
@SuppressWarnings("unchecked")
public class RedisHashCache extends AbstractCache {

    private final CacheSetting setting;

    private final RedisCacheFactory redisCacheFactory;

    public RedisHashCache(String name, CacheSetting setting, RedisCacheFactory redisCacheFactory) {
        super(name);
        this.setting = setting;
        this.redisCacheFactory = redisCacheFactory;
    }

    @Override
    public void put(Object key, Object value) {
        byte[] storeKey = this.buildStoreKey();
        byte[] keyBytes = this.serializeKey(key.toString());
        assert keyBytes != null;
        byte[] valueBytes = this.serializeValue(value);
        RedisConnectionFactory connectionFactory = redisCacheFactory.getConnectionFactory();
        try (RedisConnection connection = connectionFactory.getConnection()) {
            if (value == null && !setting.getAllowNullValues()) {
                connection.hDel(storeKey, keyBytes);
            } else {
                connection.hSet(storeKey, keyBytes, valueBytes);
            }
        }
    }

    @Override
    public void batchPut(Map<Object, Object> kvMap) {
        byte[] storeKey = this.buildStoreKey();
        Map<byte[], byte[]> hashes = new HashMap<>();
        kvMap.forEach((key, value) -> {
            byte[] keyBytes = this.serializeKey(key.toString());
            byte[] valueBytes = this.serializeValue(value);
            if (value != null || setting.getAllowNullValues()) {
                hashes.put(keyBytes, valueBytes);
            }
        });
        RedisConnectionFactory connectionFactory = redisCacheFactory.getConnectionFactory();
        try (RedisConnection connection = connectionFactory.getConnection()) {
            connection.hMSet(storeKey, hashes);
        }
    }

    @Override
    public ValueWrapper get(Object key) {
        byte[] storeKey = buildStoreKey();
        byte[] keyBytes = this.serializeKey(key.toString());
        assert keyBytes != null;
        byte[] valueBytes;
        RedisConnectionFactory connectionFactory = redisCacheFactory.getConnectionFactory();
        try (RedisConnection connection = connectionFactory.getConnection()) {
            valueBytes = connection.hGet(storeKey, keyBytes);
            if (valueBytes != null && setting.getUpdateLiveAfterAccess()) {
                int timeToLiveSeconds = setting.getTimeToLiveSeconds();
                connection.expire(keyBytes, timeToLiveSeconds);
            }
        }
        return deserializeValue(valueBytes);
    }

    @Override
    public Map<Object, ValueWrapper> batchGet(Collection<Object> keys) {
        byte[] storeKey = this.buildStoreKey();
        byte[][] hashKeys = new byte[keys.size()][];
        int index = 0;
        for (Object key : keys) {
            hashKeys[index ++] = this.serializeKey(key.toString());
        }
        List<byte[]> values;
        RedisConnectionFactory connectionFactory = redisCacheFactory.getConnectionFactory();
        try (RedisConnection connection = connectionFactory.getConnection()) {
            values = connection.hMGet(storeKey, hashKeys);
        }
        Map<Object, ValueWrapper> resultMap = new HashMap<>();
        for (int i = 0; i < hashKeys.length; i++) {
            byte[] keyBytes = hashKeys[i];
            Object key = deserializeKey(keyBytes);
            assert values != null;
            byte[] valBytes = values.get(i);
            if(valBytes != null) {
                ValueWrapper value = deserializeValue(valBytes);
                resultMap.put(key, value);
            }
        }
        return resultMap;
    }

    @Override
    public boolean evict(Object key) {
        byte[] storeKey = buildStoreKey();
        byte[] keyBytes = RedisSerializerFactory.getSerializer(setting.getKeySerializer()).serialize(storeKey);
        assert keyBytes != null;
        RedisConnectionFactory connectionFactory = redisCacheFactory.getConnectionFactory();
        try (RedisConnection connection = connectionFactory.getConnection()) {
            Long del = connection.hDel(storeKey, keyBytes);
            return del != null && del > 0;
        }
    }

    @Override
    public void clear() {
        byte[] storeKey = buildStoreKey();
        RedisConnectionFactory connectionFactory = redisCacheFactory.getConnectionFactory();
        try (RedisConnection connection = connectionFactory.getConnection()) {
            connection.del(storeKey);
        }
    }

    private byte[] buildStoreKey() {
        return (setting.getPrefix() + getName()).getBytes();
    }

    private byte[] serializeKey(String key) {
        return RedisSerializerFactory.getSerializer(setting.getKeySerializer()).serialize(key);
    }

    private Object deserializeKey(byte[] keyBytes) {
        return RedisSerializerFactory.getSerializer(setting.getKeySerializer()).deserialize(keyBytes);
    }

    private byte[] serializeValue(Object value) {
        if (value == null) {
            return new byte[0];
        } else {
            return RedisSerializerFactory.getSerializer(setting.getValueSerializer()).serialize(value);
        }
    }

    private ValueWrapper deserializeValue(byte[] bytes) {
        if (bytes == null) return null;
        if (bytes.length == 0) {
            return new ValueWrapper(null);
        } else {
            Object value = RedisSerializerFactory.getSerializer(setting.getValueSerializer()).deserialize(bytes);
            return new ValueWrapper(value);
        }
    }

}
