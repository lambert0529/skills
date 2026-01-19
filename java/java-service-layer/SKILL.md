---
name: java-service-layer
description: 生成符合规范的 Java Service 层代码，包括接口设计、事务管理、异常处理、日志记录等最佳实践。使用场景：(1) 创建新的 Service 层代码，(2) 实现业务逻辑处理，(3) 设计 Service 接口，(4) 优化现有 Service 代码，(5) 需要遵循 Spring Boot 最佳实践和分层架构规范时
---

# Java Service 层开发规范

## 快速开始

生成 Service 层代码的基本步骤：

1. 定义 Service 接口（使用 `assets/ServiceInterfaceTemplate.java`）
2. 实现 Service 接口（使用 `assets/ServiceImplTemplate.java`）
3. 配置事务管理（见 [references/transaction-management.md](references/transaction-management.md)）
4. 封装业务逻辑（见 [references/business-logic-patterns.md](references/business-logic-patterns.md)）

## 接口与实现分离

### 规范要求

- 定义 Service 接口（Interface）
- 实现类使用 `@Service` 注解
- 接口命名：`XxxService`
- 实现类命名：`XxxServiceImpl`

### 代码模板

参考 `assets/ServiceInterfaceTemplate.java` 和 `assets/ServiceImplTemplate.java` 模板文件。

## 事务管理

### 基本规则

- 查询方法：使用 `@Transactional(readOnly = true)`
- 写操作方法：使用 `@Transactional(rollbackFor = Exception.class)`
- 事务注解放在实现类方法上

详细内容见 [references/transaction-management.md](references/transaction-management.md)

## 业务逻辑封装

### 规范要求

- Service 层负责业务逻辑处理
- 数据校验应在 Service 层进行
- 复杂业务逻辑拆分为私有方法

详细模式见 [references/business-logic-patterns.md](references/business-logic-patterns.md)

## 异常处理

### 规范要求

- 使用自定义业务异常 `BusinessException`
- 异常信息要清晰明确
- 不要吞掉异常，要向上抛出
- 记录异常日志

## 日志记录

### 规范要求

- 使用 `@Slf4j` 注解（Lombok）
- 方法入口记录参数（脱敏处理）
- 方法出口记录结果或关键信息
- 异常时记录错误日志

## 参数校验

### 规范要求

- Service 层进行业务层面的参数校验
- 使用断言或条件判断
- 校验失败抛出 `BusinessException`

## 详细参考

- **事务管理**：见 [references/transaction-management.md](references/transaction-management.md)
- **业务逻辑模式**：见 [references/business-logic-patterns.md](references/business-logic-patterns.md)
- **代码模板**：见 `assets/` 目录

## 注意事项

1. **事务边界**：事务应该覆盖完整的业务操作
2. **异常处理**：不要捕获异常后不处理，导致事务不回滚
3. **性能优化**：避免在循环中进行数据库操作
4. **代码复用**：提取公共逻辑到私有方法或工具类
5. **单一职责**：每个 Service 方法只做一件事
