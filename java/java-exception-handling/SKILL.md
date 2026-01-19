---
name: java-exception-handling
description: 生成符合规范的 Java 异常处理机制，包括自定义异常、全局异常处理器、错误码定义等。使用场景：(1) 设计异常处理机制，(2) 创建自定义异常类，(3) 实现全局异常处理器，(4) 定义错误码体系，(5) 统一异常响应格式，(6) 需要遵循 Spring Boot 最佳实践和异常处理规范时
---

# Java 异常处理规范

## 快速开始

实现异常处理机制的基本步骤：

1. 定义错误码枚举（见 [references/error-code-definition.md](references/error-code-definition.md)）
2. 创建异常类层次结构（见 [references/exception-classes.md](references/exception-classes.md)）
3. 实现全局异常处理器（使用 `assets/GlobalExceptionHandlerTemplate.java`）
4. 在 Service 层使用异常

## 异常类层次结构

### 规范要求

- 定义基础业务异常 `BusinessException`
- 定义系统异常 `SystemException`
- 异常类继承 `RuntimeException`
- 包含错误码和错误消息

详细实现见 [references/exception-classes.md](references/exception-classes.md)

## 错误码定义

### 规范要求

- 使用枚举定义错误码
- 错误码包含：代码、消息、HTTP 状态码
- 错误码分类：业务错误、系统错误、参数错误等

详细定义见 [references/error-code-definition.md](references/error-code-definition.md)，示例代码见 `assets/ErrorCodeExample.java`

## 全局异常处理器

### 规范要求

- 使用 `@RestControllerAdvice` 或 `@ControllerAdvice`
- 处理不同类型的异常
- 返回统一的错误响应格式
- 记录异常日志

代码模板见 `assets/GlobalExceptionHandlerTemplate.java`

## 统一错误响应格式

### 规范要求

- 使用统一的响应包装类
- 包含错误码、错误消息、时间戳等信息

## 异常使用示例

### 在 Service 层使用

```java
@Override
public UserDTO getById(Long id) {
    if (id == null) {
        throw new BusinessException(ErrorCode.PARAM_NULL, "用户ID不能为空");
    }
    
    User user = userRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    
    return userConverter.toDTO(user);
}
```

## 详细参考

- **异常类设计**：见 [references/exception-classes.md](references/exception-classes.md)
- **错误码定义**：见 [references/error-code-definition.md](references/error-code-definition.md)
- **代码模板**：见 `assets/` 目录

## 注意事项

1. **异常分类**：区分业务异常和系统异常
2. **错误码管理**：错误码要统一管理，避免重复
3. **异常信息**：异常消息要清晰明确，便于排查问题
4. **日志记录**：不同级别的异常使用不同的日志级别
5. **安全性**：不要向客户端暴露敏感的系统信息
