# 指标导出配置指南

## Prometheus 导出

### 依赖配置

```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

### 配置

```yaml
management:
  metrics:
    export:
      prometheus:
        enabled: true
        step: 30s
```

## 自定义指标

### 计数器

```java
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final MeterRegistry meterRegistry;
    
    public Order createOrder(CreateOrderRequest request) {
        Order order = orderRepository.save(/* ... */);
        
        // 增加计数器
        meterRegistry.counter("orders.created", 
            "status", order.getStatus(),
            "category", order.getCategory()
        ).increment();
        
        return order;
    }
}
```

### 计时器

```java
@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final MeterRegistry meterRegistry;
    
    public Product findById(Long id) {
        Timer.Sample sample = Timer.start(meterRegistry);
        
        try {
            Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
            return product;
        } finally {
            sample.stop(meterRegistry.timer("product.query.time",
                "operation", "findById"
            ));
        }
    }
}
```

### 仪表（Gauge）

```java
@Component
@RequiredArgsConstructor
public class CacheMetrics {
    
    private final CacheManager cacheManager;
    private final MeterRegistry meterRegistry;
    
    @PostConstruct
    public void registerCacheMetrics() {
        Gauge.builder("cache.size", cacheManager, cm -> {
            Cache cache = cm.getCache("products");
            return cache != null ? cache.getNativeCache().size() : 0;
        }).register(meterRegistry);
    }
}
```

## 指标标签

```java
@Configuration
public class MetricsConfig {
    
    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> registry.config()
            .commonTags("application", "product-service")
            .commonTags("environment", System.getProperty("spring.profiles.active", "default"));
    }
}
```
