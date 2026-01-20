# Actuator 端点配置指南

## 基本配置

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
      base-path: /actuator
      path-mapping:
        health: healthcheck
  endpoint:
    health:
      show-details: when-authorized
      show-components: always
```

## 端点列表

### 健康检查端点
- `/actuator/health` - 应用健康状态
- `/actuator/health/liveness` - Kubernetes liveness 探针
- `/actuator/health/readiness` - Kubernetes readiness 探针

### 指标端点
- `/actuator/metrics` - 所有可用指标
- `/actuator/metrics/{metricName}` - 特定指标详情
- `/actuator/prometheus` - Prometheus 格式指标

### 信息端点
- `/actuator/info` - 应用信息

### 诊断端点
- `/actuator/conditions` - 自动配置条件评估
- `/actuator/startup` - 启动指标（Spring Boot 3.5+）
- `/actuator/httpexchanges` - HTTP 交换记录

## 生产环境配置

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: never
      probes:
        enabled: true
  metrics:
    export:
      prometheus:
        enabled: true
```

## 开发环境配置

```yaml
management:
  endpoints:
    web:
      exposure:
        include: "*"
  endpoint:
    health:
      show-details: always
```
