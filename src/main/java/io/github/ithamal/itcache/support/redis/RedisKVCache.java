package io.github.ithamal.itcache.support.redis;

import io.github.ithamal.itcache.config.CacheSetting;
import io.github.ithamal.itcache.core.AbstractCache;
import io.github.ithamal.itcache.core.ValueWrapper;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: ken.lin
 * @since: 2023-09-26 14:55
 */
@SuppressWarnings("unchecked")
public class RedisKVCache extends AbstractCache {

    private final CacheSetting setting;

    private final RedisCacheFactory redisCacheFactory;

    public RedisKVCache(String name, CacheSetting setting, RedisCacheFactory redisCacheFactory) {
        super(name);
        this.setting = setting;
        this.redisCacheFactory = redisCacheFactory;
    }


    @Override
    public void put(Object key, Object value) {
        RedisConnectionFactory connectionFactory = redisCacheFactory.getConnectionFactory();
        String storeKey = this.buildStoreKey(key.toString());
        byte[] keyBytes = this.serializeKey(storeKey);
        assert keyBytes != null;
        byte[] valueBytes = this.serializeValue(value);
        try (RedisConnection connection = connectionFactory.getConnection()) {
            if (value == null && !setting.getAllowNullValues()) {
                connection.del(keyBytes);
            } else {
                int timeToLiveSeconds = setting.getTimeToLiveSeconds();
                if (timeToLiveSeconds > 0) {
                    connection.setEx(keyBytes, timeToLiveSeconds, valueBytes);
                } else {
                    connection.set(keyBytes, valueBytes);
                }
            }
        }
    }

    @Override
    public void batchPut(Map<Object, Object> kvMap) {
        Map<byte[], byte[]> tuple = new HashMap<>();
        kvMap.forEach((key, value) -> {
            String storeKey = this.buildStoreKey(key.toString());
            byte[] keyBytes = this.serializeKey(storeKey);
            byte[] valueBytes = this.serializeValue(value);
            if (value != null || setting.getAllowNullValues()) {
                tuple.put(keyBytes, valueBytes);
            }
        });
        RedisConnectionFactory connectionFactory = redisCacheFactory.getConnectionFactory();
        try (RedisConnection connection = connectionFactory.getConnection()) {
            connection.mSet(tuple);
        }
    }

    @Override
    public ValueWrapper get(Object key) {
        String storeKey = buildStoreKey(key.toString());
        byte[] keyBytes = this.serializeKey(storeKey);
        assert keyBytes != null;
        byte[] valueBytes;
        RedisConnectionFactory connectionFactory = redisCacheFactory.getConnectionFactory();
        try (RedisConnection connection = connectionFactory.getConnection()) {
            valueBytes = connection.get(keyBytes);
            if (valueBytes != null && setting.getUpdateLiveAfterAccess()) {
                int timeToLiveSeconds = setting.getTimeToLiveSeconds();
                connection.expire(keyBytes, timeToLiveSeconds);
            }
        }
        return deserializeValue(valueBytes);
    }


    @Override
    public Map<Object, ValueWrapper> batchGet(Collection<Object> keys) {
        byte[][] keyBytesArray = new byte[keys.size()][];
        int index = 0;
        for (Object key : keys) {
            String storeKey = this.buildStoreKey(key.toString());
            keyBytesArray[index++] = this.serializeKey(storeKey);
        }
        List<byte[]> values;
        RedisConnectionFactory connectionFactory = redisCacheFactory.getConnectionFactory();
        try (RedisConnection connection = connectionFactory.getConnection()) {
            values = connection.mGet(keyBytesArray);
        }
        assert values != null;
        Map<Object, ValueWrapper> resultMap = new HashMap<>();
        index = 0;
        for (Object key : keys) {
            byte[] valBytes = values.get(index++);
            if (valBytes != null) {
                ValueWrapper value = deserializeValue(valBytes);
                resultMap.put(key, value);
            }
        }
        return resultMap;
    }

    @Override
    public boolean evict(Object key) {
        String storeKey = buildStoreKey(key.toString());
        byte[] keyBytes = RedisSerializerFactory.getSerializer(setting.getKeySerializer()).serialize(storeKey);
        assert keyBytes != null;
        RedisConnectionFactory connectionFactory = redisCacheFactory.getConnectionFactory();
        try (RedisConnection connection = connectionFactory.getConnection()) {
            Long del = connection.del(keyBytes);
            return del != null && del > 0;
        }
    }

    @Override
    public void clear() {
        RedisConnectionFactory connectionFactory = redisCacheFactory.getConnectionFactory();
        try (RedisConnection connection = connectionFactory.getConnection()) {
            while (true) {
                ScanOptions scanOptions = ScanOptions.scanOptions().match(buildStoreKey(null)).count(1000).build();
                Cursor<byte[]> cursor = connection.scan(scanOptions);
                int count = 0;
                while (cursor.hasNext()) {
                    connection.del(cursor.next());
                    count++;
                }
                if (count == 0) {
                    break;
                }
            }
        }
    }

    private String buildStoreKey(String key) {
        return setting.getPrefix() + getName() + (key == null ? "" : ":" + key);
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
