# 缓存模式指南

## 缓存穿透防护

缓存空值，避免频繁查询数据库：

```java
@Cacheable(value = "products", key = "#id", unless = "#result == null")
public Product findById(Long id) {
    Product product = productRepository.findById(id).orElse(null);
    
    // 如果不存在，缓存 null（需要配置 allowNullValues）
    return product;
}
```

## 缓存雪崩防护

设置随机过期时间：

```java
@Bean
public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
    RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
        .entryTtl(Duration.ofMinutes(10 + new Random().nextInt(5))); // 10-15分钟随机
    
    return RedisCacheManager.builder(connectionFactory)
        .cacheDefaults(config)
        .build();
}
```

## 缓存击穿防护

使用分布式锁：

```java
@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    public Product findById(Long id) {
        String cacheKey = "product:" + id;
        Product product = (Product) redisTemplate.opsForValue().get(cacheKey);
        
        if (product != null) {
            return product;
        }
        
        // 使用分布式锁
        String lockKey = "lock:product:" + id;
        Boolean lock = redisTemplate.opsForValue()
            .setIfAbsent(lockKey, "locked", Duration.ofSeconds(10));
        
        if (Boolean.TRUE.equals(lock)) {
            try {
                // 双重检查
                product = (Product) redisTemplate.opsForValue().get(cacheKey);
                if (product != null) {
                    return product;
                }
                
                // 查询数据库
                product = productRepository.findById(id)
                    .orElseThrow(() -> new ProductNotFoundException(id));
                
                // 写入缓存
                redisTemplate.opsForValue().set(cacheKey, product, Duration.ofMinutes(10));
                
                return product;
            } finally {
                redisTemplate.delete(lockKey);
            }
        } else {
            // 等待一段时间后重试
            try {
                Thread.sleep(100);
                return findById(id);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }
    }
}
```

## 缓存更新策略

### 先更新数据库，再删除缓存

```java
@CacheEvict(value = "products", key = "#id")
public Product update(Long id, UpdateProductRequest request) {
    Product product = productRepository.findById(id)
        .orElseThrow(() -> new ProductNotFoundException(id));
    product.update(request);
    return productRepository.save(product);
}
```

### 先删除缓存，再更新数据库

```java
@CacheEvict(value = "products", key = "#id", beforeInvocation = true)
public Product update(Long id, UpdateProductRequest request) {
    Product product = productRepository.findById(id)
        .orElseThrow(() -> new ProductNotFoundException(id));
    product.update(request);
    return productRepository.save(product);
}
```
