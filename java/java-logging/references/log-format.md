# 日志格式规范

## 规范要求

- 使用参数化日志，避免字符串拼接
- 包含关键业务信息（ID、用户名等）
- 异常日志包含堆栈信息
- 避免日志过长

## 正确示例

```java
// ✅ 正确：使用参数化日志
log.info("查询用户, id: {}, username: {}", id, username);

// ✅ 正确：异常日志包含堆栈
log.error("查询用户异常, id: {}", id, e);

// ✅ 正确：包含关键业务信息
log.info("订单创建成功, orderId: {}, userId: {}, amount: {}", 
        orderId, userId, amount);
```

## 错误示例

```java
// ❌ 错误：字符串拼接
log.info("查询用户, id: " + id + ", username: " + username);

// ❌ 错误：缺少异常堆栈
log.error("查询用户异常, id: " + id + ", error: " + e.getMessage());

// ❌ 错误：日志过长
log.info("查询用户, 完整对象: " + user.toString());
```
