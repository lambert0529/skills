# Caffeine 本地缓存配置指南

## 依赖配置

```xml
<dependency>
    <groupId>com.github.ben-manes.caffeine</groupId>
    <artifactId>caffeine</artifactId>
</dependency>
```

## 基本配置

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
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .recordStats());
        return cacheManager;
    }
}
```

## 高级配置

```java
@Configuration
@EnableCaching
public class CaffeineCacheConfig {
    
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        
        // 不同缓存使用不同配置
        cacheManager.setCaffeine(Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(10, TimeUnit.MINUTES));
        
        cacheManager.setCaffeineSpec(CaffeineSpec.parse(
            "maximumSize=500,expireAfterWrite=5m"
        ));
        
        cacheManager.setCacheSpecification("maximumSize=200,expireAfterWrite=2m");
        
        return cacheManager;
    }
}
```

## 缓存预热

```java
@Component
@RequiredArgsConstructor
public class CacheWarmup {
    
    private final ProductService productService;
    
    @PostConstruct
    public void warmupCache() {
        // 预加载热点数据
        List<Long> hotProductIds = Arrays.asList(1L, 2L, 3L);
        hotProductIds.forEach(productService::findById);
    }
}
```
