---
name: java-exception-handling
description: 生成符合规范的 Java 异常处理机制，包括自定义异常、全局异常处理器、错误码定义等
version: 1.0.0
tags: [java, spring-boot, exception, error-handling, global-exception-handler]
---

# Java 异常处理规范

## 描述

本技能用于生成符合主流 Java 后端开发规范的异常处理机制，包括：
- 自定义业务异常类
- 全局异常处理器
- 错误码定义
- 异常响应格式
- 异常日志记录

## 触发条件

当用户需要：
- 设计异常处理机制
- 创建自定义异常类
- 实现全局异常处理器
- 定义错误码体系
- 统一异常响应格式

## 核心规范

### 1. 异常类层次结构

**规范要求**：
- 定义基础业务异常 `BusinessException`
- 定义系统异常 `SystemException`
- 异常类继承 `RuntimeException`
- 包含错误码和错误消息

**代码结构**：
```java
// 基础异常类
public class BaseException extends RuntimeException {
    
    private final ErrorCode errorCode;
    private final Object[] args;
    
    public BaseException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.args = null;
    }
    
    public BaseException(ErrorCode errorCode, Object... args) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.args = args;
    }
    
    public BaseException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.args = null;
    }
    
    public ErrorCode getErrorCode() {
        return errorCode;
    }
    
    public Object[] getArgs() {
        return args;
    }
}

// 业务异常
public class BusinessException extends BaseException {
    
    public BusinessException(ErrorCode errorCode) {
        super(errorCode);
    }
    
    public BusinessException(ErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }
    
    public BusinessException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}

// 系统异常
public class SystemException extends BaseException {
    
    public SystemException(ErrorCode errorCode) {
        super(errorCode);
    }
    
    public SystemException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
```

### 2. 错误码定义

**规范要求**：
- 使用枚举定义错误码
- 错误码包含：代码、消息、HTTP 状态码
- 错误码分类：业务错误、系统错误、参数错误等

**代码示例**：
```java
@Getter
@AllArgsConstructor
public enum ErrorCode {
    
    // ========== 通用错误码 ==========
    SUCCESS(200, "操作成功", HttpStatus.OK),
    PARAM_ERROR(400, "参数错误", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(401, "未授权", HttpStatus.UNAUTHORIZED),
    FORBIDDEN(403, "无权限", HttpStatus.FORBIDDEN),
    NOT_FOUND(404, "资源不存在", HttpStatus.NOT_FOUND),
    METHOD_NOT_ALLOWED(405, "请求方法不允许", HttpStatus.METHOD_NOT_ALLOWED),
    SYSTEM_ERROR(500, "系统错误", HttpStatus.INTERNAL_SERVER_ERROR),
    
    // ========== 用户相关错误码 ==========
    USER_NOT_FOUND(1001, "用户不存在", HttpStatus.NOT_FOUND),
    USERNAME_ALREADY_EXISTS(1002, "用户名已存在", HttpStatus.BAD_REQUEST),
    EMAIL_ALREADY_EXISTS(1003, "邮箱已存在", HttpStatus.BAD_REQUEST),
    PASSWORD_ERROR(1004, "密码错误", HttpStatus.BAD_REQUEST),
    USER_DISABLED(1005, "用户已被禁用", HttpStatus.FORBIDDEN),
    
    // ========== 订单相关错误码 ==========
    ORDER_NOT_FOUND(2001, "订单不存在", HttpStatus.NOT_FOUND),
    ORDER_ITEMS_EMPTY(2002, "订单项不能为空", HttpStatus.BAD_REQUEST),
    ORDER_STATUS_ERROR(2003, "订单状态错误", HttpStatus.BAD_REQUEST),
    INSUFFICIENT_INVENTORY(2004, "库存不足", HttpStatus.BAD_REQUEST),
    
    // ========== 参数校验错误码 ==========
    PARAM_NULL(3001, "参数不能为空", HttpStatus.BAD_REQUEST),
    PARAM_INVALID(3002, "参数格式不正确", HttpStatus.BAD_REQUEST),
    PARAM_OUT_OF_RANGE(3003, "参数超出范围", HttpStatus.BAD_REQUEST),
    
    // ========== 数据访问错误码 ==========
    DATA_NOT_FOUND(4001, "数据不存在", HttpStatus.NOT_FOUND),
    DATA_ALREADY_EXISTS(4002, "数据已存在", HttpStatus.BAD_REQUEST),
    DATA_CONSTRAINT_VIOLATION(4003, "数据约束违反", HttpStatus.BAD_REQUEST);
    
    private final Integer code;
    private final String message;
    private final HttpStatus httpStatus;
}
```

### 3. 全局异常处理器

**规范要求**：
- 使用 `@RestControllerAdvice` 或 `@ControllerAdvice`
- 处理不同类型的异常
- 返回统一的错误响应格式
- 记录异常日志

**代码示例**：
```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<Void>> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(Result.failure(errorCode.getCode(), errorCode.getMessage()));
    }
    
    /**
     * 处理系统异常
     */
    @ExceptionHandler(SystemException.class)
    public ResponseEntity<Result<Void>> handleSystemException(SystemException e) {
        log.error("系统异常: {}", e.getMessage(), e);
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(Result.failure(errorCode.getCode(), errorCode.getMessage()));
    }
    
    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Void>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e) {
        log.warn("参数校验失败: {}", e.getMessage());
        
        StringBuilder errorMessage = new StringBuilder();
        e.getBindingResult().getFieldErrors().forEach(error -> {
            errorMessage.append(error.getField())
                    .append(": ")
                    .append(error.getDefaultMessage())
                    .append("; ");
        });
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Result.failure(ErrorCode.PARAM_ERROR.getCode(), 
                        errorMessage.toString()));
    }
    
    /**
     * 处理请求参数绑定异常
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<Result<Void>> handleBindException(BindException e) {
        log.warn("参数绑定失败: {}", e.getMessage());
        
        StringBuilder errorMessage = new StringBuilder();
        e.getBindingResult().getFieldErrors().forEach(error -> {
            errorMessage.append(error.getField())
                    .append(": ")
                    .append(error.getDefaultMessage())
                    .append("; ");
        });
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Result.failure(ErrorCode.PARAM_ERROR.getCode(), 
                        errorMessage.toString()));
    }
    
    /**
     * 处理请求方法不支持异常
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Result<Void>> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException e) {
        log.warn("请求方法不支持: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(Result.failure(ErrorCode.METHOD_NOT_ALLOWED.getCode(),
                        ErrorCode.METHOD_NOT_ALLOWED.getMessage()));
    }
    
    /**
     * 处理资源不存在异常
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Result<Void>> handleNoHandlerFoundException(
            NoHandlerFoundException e) {
        log.warn("资源不存在: {}", e.getRequestURL());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Result.failure(ErrorCode.NOT_FOUND.getCode(),
                        ErrorCode.NOT_FOUND.getMessage()));
    }
    
    /**
     * 处理数据访问异常
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Result<Void>> handleDataAccessException(DataAccessException e) {
        log.error("数据访问异常: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.failure(ErrorCode.SYSTEM_ERROR.getCode(),
                        "数据访问失败，请稍后重试"));
    }
    
    /**
     * 处理所有未捕获的异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleException(Exception e) {
        log.error("系统异常: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.failure(ErrorCode.SYSTEM_ERROR.getCode(),
                        "系统异常，请稍后重试"));
    }
}
```

### 4. 统一错误响应格式

**规范要求**：
- 使用统一的响应包装类
- 包含错误码、错误消息、时间戳等信息

**代码示例**：
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Result<T> {
    
    @Schema(description = "响应码，200 表示成功")
    private Integer code;
    
    @Schema(description = "响应消息")
    private String message;
    
    @Schema(description = "响应数据")
    private T data;
    
    @Schema(description = "响应时间戳")
    private Long timestamp;
    
    public static <T> Result<T> success() {
        return Result.<T>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .message(ErrorCode.SUCCESS.getMessage())
                .timestamp(System.currentTimeMillis())
                .build();
    }
    
    public static <T> Result<T> success(T data) {
        return Result.<T>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .message(ErrorCode.SUCCESS.getMessage())
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build();
    }
    
    public static <T> Result<T> success(String message, T data) {
        return Result.<T>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .message(message)
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build();
    }
    
    public static <T> Result<T> failure(Integer code, String message) {
        return Result.<T>builder()
                .code(code)
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build();
    }
    
    public static <T> Result<T> failure(ErrorCode errorCode) {
        return Result.<T>builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .timestamp(System.currentTimeMillis())
                .build();
    }
}
```

### 5. 异常使用示例

**在 Service 层使用**：
```java
@Service
@Slf4j
public class UserServiceImpl implements UserService {
    
    @Override
    public UserDTO getById(Long id) {
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAM_NULL, "用户ID不能为空");
        }
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        
        return userConverter.toDTO(user);
    }
    
    @Override
    public UserDTO create(CreateUserRequest request) {
        // 参数校验
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAM_NULL, "请求参数不能为空");
        }
        
        // 业务校验
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }
        
        // 业务逻辑...
    }
}
```

## 使用方法

1. **定义错误码**：在 `ErrorCode` 枚举中定义所有错误码
2. **创建异常类**：创建 `BusinessException` 和 `SystemException`
3. **实现全局处理器**：创建 `GlobalExceptionHandler` 处理所有异常
4. **使用异常**：在 Service 层抛出业务异常

## 示例

**用户输入**：
```
设计异常处理机制，包括：
- 自定义业务异常
- 全局异常处理器
- 错误码定义
- 统一错误响应格式
```

**生成内容**：
- `ErrorCode.java` - 错误码枚举
- `BaseException.java` - 基础异常类
- `BusinessException.java` - 业务异常
- `SystemException.java` - 系统异常
- `GlobalExceptionHandler.java` - 全局异常处理器
- `Result.java` - 统一响应格式（更新）

## 注意事项

1. **异常分类**：区分业务异常和系统异常
2. **错误码管理**：错误码要统一管理，避免重复
3. **异常信息**：异常消息要清晰明确，便于排查问题
4. **日志记录**：不同级别的异常使用不同的日志级别
5. **安全性**：不要向客户端暴露敏感的系统信息
