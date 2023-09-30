package io.github.ithamal.itcache.boot;

import cn.hutool.cache.CacheUtil;
import io.github.ithamal.itcache.config.CacheConfig;
import io.github.ithamal.itcache.core.CacheManager;
import io.github.ithamal.itcache.support.hutool.HutoolCacheManager;
import io.github.ithamal.itcache.support.redis.RedisCacheFactory;
import io.github.ithamal.itcache.support.redis.RedisCacheManager;
import io.github.ithamal.itcache.support.spring.SpringCacheManager;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * @author: ken.lin
 * @since: 2023-09-27 10:12
 */
@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
@ConditionalOnProperty(prefix = "itcache", name = "enable", havingValue = "true", matchIfMissing = true)
public class ItCacheAutoConfig {

    @Bean
    @ConfigurationProperties(prefix = "itcache")
    public CacheConfig cacheConfig() {
        return new CacheConfig();
    }

    @Bean
    @Primary
    public AutoCacheManager itCacheManager(CacheConfig config) {
        return new AutoCacheManager(config);
    }

    @Bean
    @ConditionalOnProperty(prefix = "itcache", name = "spring", havingValue = "true", matchIfMissing = true)
    public org.springframework.cache.CacheManager cacheManager(CacheConfig config, CacheManager cacheManager) {
        return new SpringCacheManager(config, cacheManager);
    }

    @Bean
    @ConditionalOnBean(RedisConnectionFactory.class)
    public RedisCacheManager redisCacheManager(CacheConfig config, RedisConnectionFactory redisConnectionFactory) {
        RedisCacheFactory redisCacheFactory = new RedisCacheFactory(config, redisConnectionFactory);
        return new RedisCacheManager(redisCacheFactory);
    }


    @Bean
    @ConditionalOnClass(CacheUtil.class)
    public HutoolCacheManager hutoolCacheManager(CacheConfig config) {
        return new HutoolCacheManager(config);
    }

}
