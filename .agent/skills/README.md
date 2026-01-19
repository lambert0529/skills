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
