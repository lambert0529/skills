---
name: java-cache
description: 使用 Spring Cache 抽象实现缓存策略，支持多种缓存提供商（Redis、Caffeine、EhCache 等）。使用场景：(1) 减少数据库查询，(2) 提高 API 响应时间，(3) 实现读写缓存策略，(4) 配置缓存失效策略，(5) 实现多级缓存
---

# Java 缓存规范

## 快速开始

实现缓存的基本步骤：

1. 添加依赖：`spring-boot-starter-cache` + 缓存提供商（如 Redis）
2. 启用缓存：`@EnableCaching`
3. 配置缓存管理器（见 [references/cache-config.md](references/cache-config.md)）
4. 使用缓存注解：`@Cacheable`、`@CachePut`、`@CacheEvict`
5. 配置缓存策略（见 [references/cache-strategies.md](references/cache-strategies.md)）

## 核心注解

### 1. @Cacheable
- 缓存方法结果
- 首次调用执行方法并缓存结果
- 后续调用直接返回缓存结果

### 2. @CachePut
- 更新缓存
- 总是执行方法并更新缓存

### 3. @CacheEvict
- 清除缓存
- 支持按 key 清除或清除所有

### 4. @Caching
- 组合多个缓存操作

## 依赖配置

### Maven 依赖

```xml
<!-- Spring Cache -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>

<!-- Redis 缓存提供商 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>

<!-- Caffeine 本地缓存（可选） -->
<dependency>
    <groupId>com.github.ben-manes.caffeine</groupId>
    <artifactId>caffeine</artifactId>
</dependency>
```

## 配置示例

### Redis 缓存配置

```java
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(10))
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair
                    .fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair
                    .fromSerializer(new GenericJackson2JsonRedisSerializer()));
        
        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(config)
            .withCacheConfiguration("products",
                config.entryTtl(Duration.ofHours(1)))
            .withCacheConfiguration("users",
                config.entryTtl(Duration.ofMinutes(30)))
            .build();
    }
}
```

### 基本缓存使用

```java
@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final ProductRepository productRepository;
    
    @Cacheable(value = "products", key = "#id")
    public Product findById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException(id));
    }
    
    @CachePut(value = "products", key = "#result.id")
    public Product update(Long id, UpdateProductRequest request) {
        Product product = findById(id);
        product.update(request);
        return productRepository.save(product);
    }
    
    @CacheEvict(value = "products", key = "#id")
    public void delete(Long id) {
        productRepository.deleteById(id);
    }
    
    @CacheEvict(value = "products", allEntries = true)
    public void clearCache() {
        // 清除所有缓存
    }
}
```

### 条件缓存

```java
@Cacheable(
    value = "users", 
    key = "#email",
    condition = "#email != null",
    unless = "#result == null"
)
public User findByEmail(String email) {
    return userRepository.findByEmail(email).orElse(null);
}
```

### 多级缓存

```java
@Cacheable(value = {"l1Cache", "l2Cache"}, key = "#id")
public Product findById(Long id) {
    // 先查 L1（本地缓存），再查 L2（Redis），最后查数据库
    return productRepository.findById(id)
        .orElseThrow(() -> new ProductNotFoundException(id));
}
```

## 缓存策略

### 1. Cache-Aside（旁路缓存）
- 应用负责缓存读写
- 适合读多写少的场景

### 2. Read-Through（读穿透）
- 缓存自动从数据源加载
- 适合缓存提供商支持的场景

### 3. Write-Through（写穿透）
- 同时写入缓存和数据源
- 保证数据一致性

### 4. Write-Behind（写回）
- 异步写入数据源
- 提高写入性能

## 最佳实践

### 1. 缓存键设计
- 使用有意义的键：`"product:123"` 而不是 `"123"`
- 避免键冲突：使用命名空间
- 键要简洁：避免过长的键

### 2. 缓存过期时间
- 根据数据更新频率设置 TTL
- 热点数据：较长的 TTL
- 实时数据：较短的 TTL

### 3. 缓存预热
- 应用启动时预加载热点数据
- 使用 `@PostConstruct` 或 `ApplicationRunner`

### 4. 缓存穿透防护
- 缓存空值：避免频繁查询数据库
- 使用布隆过滤器：快速判断数据是否存在

### 5. 缓存雪崩防护
- 设置随机过期时间：避免同时失效
- 使用多级缓存：分散风险

## 约束和警告

### 1. 不要缓存可变对象
缓存的对象应该是不可变的，或者确保缓存更新时同步更新。

### 2. 注意缓存一致性
更新数据时，确保同时更新或清除相关缓存。

### 3. 避免缓存穿透
对于不存在的 key，也要缓存（缓存 null 或空对象），避免频繁查询数据库。

### 4. 监控缓存命中率
监控缓存命中率，优化缓存策略。

### 5. 合理设置缓存大小
避免缓存过大导致内存溢出。

## 详细参考

- **缓存配置**：见 [references/cache-config.md](references/cache-config.md)
- **缓存策略**：见 [references/cache-strategies.md](references/cache-strategies.md)
- **Redis 配置**：见 [references/redis-config.md](references/redis-config.md)
- **Caffeine 配置**：见 [references/caffeine-config.md](references/caffeine-config.md)
- **缓存模式**：见 [references/cache-patterns.md](references/cache-patterns.md)
- **代码模板**：见 `assets/` 目录

## 注意事项

1. **缓存失效**：合理设计缓存失效策略，避免脏数据
2. **内存管理**：监控缓存内存使用，避免 OOM
3. **性能优化**：缓存应该提高性能，而不是降低性能
4. **数据一致性**：确保缓存和数据源的一致性
5. **错误处理**：缓存失败不应该影响业务逻辑
