---
name: java-actuator
description: 为 Spring Boot 应用配置生产级监控、健康检查、安全的管理端点和 Micrometer 指标。使用场景：(1) 启用 Actuator 端点进行监控，(2) 配置健康检查探针（liveness/readiness），(3) 导出指标到 Prometheus，(4) 配置安全管理端点，(5) 实现自定义健康指示器
---

# Java Actuator 监控规范

## 快速开始

配置 Actuator 的基本步骤：

1. 添加依赖：`spring-boot-starter-actuator`
2. 配置端点暴露（见 [references/endpoint-config.md](references/endpoint-config.md)）
3. 配置健康检查（见 [references/health-checks.md](references/health-checks.md)）
4. 配置指标导出（见 [references/metrics-export.md](references/metrics-export.md)）
5. 配置安全管理（见 [references/security-config.md](references/security-config.md)）

## 核心功能

### 1. 健康检查端点
- `/actuator/health` - 应用健康状态
- `/actuator/health/liveness` - Kubernetes liveness 探针
- `/actuator/health/readiness` - Kubernetes readiness 探针

### 2. 指标端点
- `/actuator/metrics` - 所有可用指标
- `/actuator/prometheus` - Prometheus 格式指标

### 3. 信息端点
- `/actuator/info` - 应用信息

### 4. 诊断端点
- `/actuator/conditions` - 自动配置条件评估
- `/actuator/startup` - 启动指标（Spring Boot 3.5+）

## 依赖配置

### Maven 依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>

<!-- Prometheus 指标导出（可选） -->
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

## 配置示例

### 基本配置（application.yml）

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
      base-path: /actuator
  endpoint:
    health:
      show-details: when-authorized
      probes:
        enabled: true
  metrics:
    export:
      prometheus:
        enabled: true
```

### 自定义健康指示器

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
}
```

### 自定义指标

```java
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final MeterRegistry meterRegistry;
    private final OrderRepository orderRepository;
    
    public Order createOrder(CreateOrderRequest request) {
        Timer.Sample sample = Timer.start(meterRegistry);
        
        try {
            Order order = orderRepository.save(/* ... */);
            meterRegistry.counter("orders.created", 
                "status", order.getStatus()).increment();
            return order;
        } finally {
            sample.stop(meterRegistry.timer("orders.creation.time"));
        }
    }
}
```

### Spring Security 配置

```java
@Configuration
@EnableWebSecurity
public class ActuatorSecurityConfig {
    
    @Bean
    public SecurityFilterChain actuatorSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/actuator/**")
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health/**").permitAll()
                .requestMatchers(EndpointRequest.toAnyEndpoint()).hasRole("ACTUATOR")
                .anyRequest().denyAll()
            )
            .httpBasic();
        
        return http.build();
    }
}
```

## 最佳实践

### 1. 端点暴露策略
- 生产环境：只暴露必要的端点（health、info、metrics）
- 开发环境：可以暴露更多端点（conditions、startup）
- 使用 `management.endpoints.web.exposure.include` 精确控制

### 2. 健康检查配置
- 启用探针：`management.endpoint.health.probes.enabled=true`
- 配置健康组：为不同场景定义不同的健康检查组
- 实现自定义指示器：检查关键依赖（数据库、Redis、外部服务）

### 3. 指标导出
- 使用 Prometheus：适合 Kubernetes 环境
- 添加业务标签：使用 `MeterRegistryCustomizer` 添加应用、环境等标签
- 监控关键指标：HTTP 请求、数据库连接、业务指标

### 4. 安全管理
- 保护管理端点：使用 Spring Security 保护 `/actuator/**`
- 公开健康检查：`/actuator/health` 可以公开（用于负载均衡器）
- 使用独立端口：`management.server.port` 配置独立管理端口

### 5. 性能优化
- 限制指标数量：避免导出过多指标影响性能
- 采样配置：合理配置指标采样率
- 异步导出：使用异步方式导出指标

## 约束和警告

### 1. 不要在生产环境暴露所有端点
使用 `management.endpoints.web.exposure.include` 精确控制暴露的端点。

### 2. 保护敏感端点
`/actuator/env`、`/actuator/configprops` 等端点包含敏感信息，必须保护。

### 3. 健康检查要真实
健康检查应该真实反映应用状态，不要总是返回 UP。

### 4. 指标命名规范
遵循 Micrometer 命名规范，使用小写字母和点分隔符。

### 5. 避免指标爆炸
不要为每个请求创建大量指标，合理使用标签和聚合。

## 详细参考

- **端点配置**：见 [references/endpoint-config.md](references/endpoint-config.md)
- **健康检查**：见 [references/health-checks.md](references/health-checks.md)
- **指标导出**：见 [references/metrics-export.md](references/metrics-export.md)
- **安全管理**：见 [references/security-config.md](references/security-config.md)
- **自定义指示器**：见 [references/custom-indicators.md](references/custom-indicators.md)
- **代码模板**：见 `assets/` 目录

## 注意事项

1. **端点安全**：生产环境必须保护管理端点
2. **健康检查**：配置 liveness 和 readiness 探针用于 Kubernetes
3. **指标导出**：选择合适的指标导出方式（Prometheus、OTLP 等）
4. **性能影响**：监控 Actuator 端点对性能的影响
5. **日志记录**：记录健康检查失败和指标异常情况
