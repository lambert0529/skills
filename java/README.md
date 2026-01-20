# Java 后端开发 Skills

本目录包含符合主流 Java 后端开发规范的 Skills（技能），遵循 Anthropic Skills 开放标准。

## 📦 已创建的 Skills

### 1. java-rest-api-design
**RESTful API 设计规范**
- RESTful 路径设计
- Controller 层代码结构
- 请求/响应 DTO 设计
- HTTP 状态码使用
- API 版本化策略

### 2. java-service-layer
**Service 层开发规范**
- 接口与实现分离
- 事务管理规范
- 业务逻辑封装
- 异常处理
- 日志记录

### 3. java-exception-handling
**异常处理规范**
- 自定义业务异常类
- 全局异常处理器
- 错误码定义
- 异常响应格式
- 异常日志记录

### 4. java-validation
**参数校验规范**
- Bean Validation 注解使用
- 自定义校验器
- 分组校验
- 嵌套对象校验
- 校验错误处理

### 5. java-response-wrapper
**统一响应格式规范**
- 统一响应包装类
- 成功响应格式
- 失败响应格式
- 分页响应格式
- 响应状态码定义

### 6. java-logging
**日志记录规范**
- 日志级别使用
- 日志格式规范
- 敏感信息脱敏
- 性能优化
- 结构化日志

### 7. java-mybatis-plus-generator
**MyBatis-Plus CRUD 代码生成（传统分层架构）**
- Entity 实体类生成（MyBatis-Plus 注解）
- Mapper 接口和 XML 生成（继承 BaseMapper）
- DTO 设计（Request/Response DTO）
- DTO 转换器（Entity <-> DTO）
- Service 层代码生成（继承 IService）
- Controller 层代码生成（REST API）
- PageHelper 分页支持
- QueryWrapper 动态查询
- 传统分层架构（Controller-Service-Mapper-Entity）

### 8. java-test-patterns
**测试模式**
- 单元测试（JUnit 5 + Mockito）
- 切片测试（@DataJpaTest、@WebMvcTest）
- 集成测试（Testcontainers）
- REST API 测试（MockMvc）
- 测试性能优化

### 9. java-security-jwt
**JWT 安全认证**
- JWT Token 生成和验证
- Spring Security 配置
- 认证过滤器实现
- 基于角色的访问控制（RBAC）
- Token 刷新机制
- OAuth2 集成

### 10. java-actuator
**监控和健康检查**
- Actuator 端点配置
- 健康检查探针（liveness/readiness）
- Prometheus 指标导出
- 自定义健康指示器
- 安全管理端点
- Micrometer 指标

### 11. java-cache
**Spring Cache 缓存策略**
- 缓存注解使用（@Cacheable、@CachePut、@CacheEvict）
- Redis 缓存配置
- Caffeine 本地缓存
- 缓存策略（Cache-Aside、Read-Through、Write-Through）
- 缓存失效和预热
- 多级缓存实现

## 🚀 使用方法

### 在 Cursor 中使用

1. **同步 Skills 到 AGENTS.md**：
```bash
openskills sync --yes -o .cursor/rules/AGENTS.md
```

2. **在 Cursor 中直接使用**：
```
"为用户管理创建 REST API"
"创建用户 Service 层，包含事务管理"
"设计异常处理机制"
"为创建用户请求添加参数校验"
"设计统一响应格式"
"为 Service 添加日志记录"
```

### 在 Claude Code 中使用

Skills 会自动被发现和使用，无需额外配置。

## 📋 规范覆盖范围

这些 Skills 覆盖了 Java 后端开发的核心规范：

- ✅ RESTful API 设计
- ✅ 分层架构（Controller-Service-Repository）
- ✅ 异常处理机制
- ✅ 参数校验
- ✅ 统一响应格式
- ✅ 日志记录
- ✅ 事务管理
- ✅ 代码规范
- ✅ MyBatis-Plus CRUD 代码生成（传统分层架构）
- ✅ 测试模式（单元测试、集成测试、Testcontainers）
- ✅ JWT 安全认证和授权
- ✅ 监控和健康检查（Actuator）
- ✅ 缓存策略（Spring Cache）

## 🔗 相关资源

- [Anthropic Skills 标准](https://github.com/anthropics/skills)
- [Spring Boot 官方文档](https://spring.io/projects/spring-boot)
- [Bean Validation 规范](https://beanvalidation.org/)

## 📝 维护说明

- 所有 Skills 遵循 Anthropic Skills 开放标准
- 每个 Skill 包含完整的 YAML front matter
- 代码示例基于 Spring Boot 最佳实践
- 规范基于主流 Java 后端开发标准

---

**最后更新**：2026-01-19
