# 缓存策略指南

## Cache-Aside（旁路缓存）

应用负责缓存读写：

```java
@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final ProductRepository productRepository;
    private final CacheManager cacheManager;
    
    public Product findById(Long id) {
        // 1. 先查缓存
        Cache cache = cacheManager.getCache("products");
        Cache.ValueWrapper wrapper = cache.get(id);
        if (wrapper != null) {
            return (Product) wrapper.get();
        }
        
        // 2. 缓存未命中，查数据库
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException(id));
        
        // 3. 写入缓存
        cache.put(id, product);
        
        return product;
    }
}
```

## Read-Through（读穿透）

使用 `@Cacheable` 自动处理：

```java
@Service
public class ProductService {
    
    @Cacheable(value = "products", key = "#id")
    public Product findById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException(id));
    }
}
```

## Write-Through（写穿透）

使用 `@CachePut` 同时写入缓存和数据库：

```java
@Service
public class ProductService {
    
    @CachePut(value = "products", key = "#result.id")
    public Product update(Long id, UpdateProductRequest request) {
        Product product = findById(id);
        product.update(request);
        return productRepository.save(product);
    }
}
```

## Write-Behind（写回）

异步写入数据源：

```java
@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final AsyncCacheWriter asyncCacheWriter;
    
    @CachePut(value = "products", key = "#result.id")
    public Product update(Long id, UpdateProductRequest request) {
        Product product = findById(id);
        product.update(request);
        
        // 同步更新缓存
        // 异步写入数据库
        asyncCacheWriter.writeAsync(product);
        
        return product;
    }
}
```
