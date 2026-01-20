# 自定义健康指示器实现

## 实现 HealthIndicator 接口

```java
@Component
@RequiredArgsConstructor
public class CustomHealthIndicator implements HealthIndicator {
    
    private final SomeService someService;
    
    @Override
    public Health health() {
        try {
            // 检查服务状态
            boolean isHealthy = someService.checkHealth();
            
            if (isHealthy) {
                return Health.up()
                    .withDetail("service", "Available")
                    .withDetail("version", "1.0.0")
                    .build();
            } else {
                return Health.down()
                    .withDetail("service", "Unavailable")
                    .withDetail("reason", "Service check failed")
                    .build();
            }
        } catch (Exception e) {
            return Health.down()
                .withException(e)
                .withDetail("error", e.getMessage())
                .build();
        }
    }
}
```

## 实现 ReactiveHealthIndicator（响应式）

```java
@Component
@RequiredArgsConstructor
public class ReactiveCustomHealthIndicator implements ReactiveHealthIndicator {
    
    private final ReactiveSomeService someService;
    
    @Override
    public Mono<Health> health() {
        return someService.checkHealth()
            .map(isHealthy -> {
                if (isHealthy) {
                    return Health.up()
                        .withDetail("service", "Available")
                        .build();
                } else {
                    return Health.down()
                        .withDetail("service", "Unavailable")
                        .build();
                }
            })
            .onErrorResume(ex -> Mono.just(
                Health.down()
                    .withException(ex)
                    .build()
            ));
    }
}
```

## 健康指示器命名

健康指示器的名称由类名决定：
- `DatabaseHealthIndicator` -> `db`
- `RedisHealthIndicator` -> `redis`
- `CustomHealthIndicator` -> `custom`

可以通过 `@Component("customName")` 自定义名称。
