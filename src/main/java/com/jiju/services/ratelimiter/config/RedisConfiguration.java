package com.jiju.services.ratelimiter.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

@Configuration
@EnableCaching
public class RedisConfiguration extends CachingConfigurerSupport {
    @Bean
    public JedisConnectionFactory redisConnectionFactory() {
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
        jedisConnectionFactory.setUsePool(true);
        return jedisConnectionFactory;
    }

    @Bean
    public CacheManager cacheManager() {
       // return new RedisCacheManager(redisTemplate(redisConnectionFactory(),redisStringSerializer()));
        return new RedisCacheManager(RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory()
                ), RedisCacheConfiguration.defaultCacheConfig(),true);
    }
}