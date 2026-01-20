# 缓存配置指南

## Redis 缓存配置

```java
@Configuration
@EnableCaching
public class RedisCacheConfig {
    
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(10))
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair
                    .fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair
                    .fromSerializer(new GenericJackson2JsonRedisSerializer()))
            .disableCachingNullValues();
        
        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(config)
            .withCacheConfiguration("products",
                config.entryTtl(Duration.ofHours(1)))
            .withCacheConfiguration("users",
                config.entryTtl(Duration.ofMinutes(30)))
            .transactionAware()
            .build();
    }
}
```

## Caffeine 本地缓存配置

```java
@Configuration
@EnableCaching
public class CaffeineCacheConfig {
    
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .recordStats());
        return cacheManager;
    }
}
```

## 多级缓存配置

```java
@Configuration
@EnableCaching
public class MultiLevelCacheConfig {
    
    @Bean
    @Primary
    public CacheManager cacheManager(
            RedisConnectionFactory redisConnectionFactory,
            CaffeineCacheManager caffeineCacheManager) {
        
        // L1: Caffeine (本地缓存)
        // L2: Redis (分布式缓存)
        
        return new CompositeCacheManager(
            caffeineCacheManager,
            redisCacheManager(redisConnectionFactory)
        );
    }
}
```

## application.yml 配置

```yaml
spring:
  cache:
    type: redis
    redis:
      time-to-live: 600000  # 10分钟（毫秒）
      cache-null-values: false
  data:
    redis:
      host: localhost
      port: 6379
      password: 
      timeout: 2000ms
```
