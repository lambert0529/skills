# 健康检查配置指南

## 基本健康检查

```yaml
management:
  endpoint:
    health:
      show-details: when-authorized
      probes:
        enabled: true
```

## 健康检查组

```yaml
management:
  endpoint:
    health:
      group:
        readiness:
          include: db,redis,external-service
        liveness:
          include: ping
```

## 自定义健康指示器

```java
@Component
@RequiredArgsConstructor
public class DatabaseHealthIndicator implements HealthIndicator {
    
    private final DataSource dataSource;
    
    @Override
    public Health health() {
        try (Connection conn = dataSource.getConnection()) {
            if (conn.isValid(2)) {
                return Health.up()
                    .withDetail("database", "PostgreSQL")
                    .withDetail("validationQuery", "SELECT 1")
                    .withDetail("responseTime", measureResponseTime())
                    .build();
            }
        } catch (SQLException e) {
            return Health.down()
                .withException(e)
                .withDetail("error", "Database connection failed")
                .build();
        }
        return Health.down().build();
    }
    
    private long measureResponseTime() {
        long start = System.currentTimeMillis();
        // 执行查询
        return System.currentTimeMillis() - start;
    }
}
```

## Redis 健康指示器

```java
@Component
@RequiredArgsConstructor
public class RedisHealthIndicator implements HealthIndicator {
    
    private final RedisTemplate<String, String> redisTemplate;
    
    @Override
    public Health health() {
        try {
            String result = redisTemplate.execute((RedisCallback<String>) connection -> {
                return connection.ping();
            });
            
            if ("PONG".equals(result)) {
                return Health.up()
                    .withDetail("redis", "Available")
                    .build();
            }
        } catch (Exception e) {
            return Health.down()
                .withException(e)
                .withDetail("error", "Redis connection failed")
                .build();
        }
        return Health.down().build();
    }
}
```

## 外部服务健康指示器

```java
@Component
@RequiredArgsConstructor
public class ExternalServiceHealthIndicator implements HealthIndicator {
    
    private final RestTemplate restTemplate;
    private final String serviceUrl;
    
    @Override
    public Health health() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(
                serviceUrl + "/health",
                String.class
            );
            
            if (response.getStatusCode().is2xxSuccessful()) {
                return Health.up()
                    .withDetail("external-service", "Available")
                    .withDetail("response-time", measureResponseTime())
                    .build();
            }
        } catch (Exception e) {
            return Health.down()
                .withException(e)
                .withDetail("error", "External service unavailable")
                .build();
        }
        return Health.down().build();
    }
}
```
