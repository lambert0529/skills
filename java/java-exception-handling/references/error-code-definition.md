# 错误码定义

## 错误码枚举结构

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

## 错误码分类规则

- **1xxx**：用户相关错误
- **2xxx**：订单相关错误
- **3xxx**：参数校验错误
- **4xxx**：数据访问错误
- **5xxx**：系统错误
