---
name: java-response-wrapper
description: 生成符合规范的 Java 统一响应格式包装类，包括成功响应、失败响应、分页响应等标准格式。使用场景：(1) 设计统一响应格式，(2) 创建响应包装类，(3) 实现分页响应，(4) 统一 API 返回格式，(5) 需要遵循 Spring Boot 最佳实践和 API 设计规范时
---

# Java 统一响应格式规范

## 快速开始

实现统一响应格式的基本步骤：

1. 创建统一响应包装类（使用 `assets/ResultTemplate.java`）
2. 创建分页响应类（使用 `assets/PageResultTemplate.java`）
3. 在 Controller 中使用统一响应格式
4. 配置全局异常处理器返回统一格式

## 统一响应包装类

### 规范要求

- 所有 API 响应使用统一的格式
- 包含：状态码、消息、数据、时间戳
- 提供静态工厂方法
- 支持泛型

代码模板见 `assets/ResultTemplate.java`

## 分页响应格式

### 规范要求

- 包含数据列表、总数、页码、每页数量、总页数
- 提供便捷的构建方法
- 支持从 Spring Data 的 Page 对象转换

代码模板见 `assets/PageResultTemplate.java`

## Controller 层使用

### 规范要求

- 所有 Controller 方法返回 `ResponseEntity<Result<T>>`
- 使用 `Result.success()` 或 `Result.failure()` 构建响应
- HTTP 状态码与业务状态码分离

### 示例

```java
@GetMapping("/{id}")
public ResponseEntity<Result<UserDTO>> getUser(@PathVariable Long id) {
    UserDTO user = userService.getById(id);
    return ResponseEntity.ok(Result.success(user));
}

@PostMapping
public ResponseEntity<Result<UserDTO>> createUser(
        @Valid @RequestBody CreateUserRequest request) {
    UserDTO user = userService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED)
            .body(Result.success("用户创建成功", user));
}
```

## 响应格式示例

详细示例见 [references/response-formats.md](references/response-formats.md)

## 详细参考

- **响应格式示例**：见 [references/response-formats.md](references/response-formats.md)
- **分页响应**：见 [references/page-result.md](references/page-result.md)
- **代码模板**：见 `assets/ResultTemplate.java` 和 `assets/PageResultTemplate.java`

## 注意事项

1. **HTTP 状态码**：HTTP 状态码表示请求处理状态，业务状态码表示业务结果
2. **时间戳格式**：使用毫秒时间戳，前端可根据需要转换
3. **空值处理**：成功时 data 可以为 null，失败时建议为 null
4. **分页参数**：页码从 1 开始，而非 0
5. **向后兼容**：响应格式变更时考虑向后兼容性
