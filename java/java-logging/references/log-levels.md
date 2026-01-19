# 日志级别使用规范

## 日志级别（从低到高）

- `TRACE`：最详细的调试信息，通常不使用
- `DEBUG`：详细的调试信息，开发环境使用
- `INFO`：关键业务流程信息，生产环境使用
- `WARN`：警告信息，需要关注但不影响运行
- `ERROR`：错误信息，需要立即处理

## 使用场景

| 场景 | 日志级别 | 示例 |
|------|---------|------|
| 方法入口 | INFO | `log.info("查询用户开始, id: {}", id)` |
| 方法出口（成功） | INFO | `log.info("查询用户成功, id: {}", id)` |
| 业务校验失败 | WARN | `log.warn("用户名已存在, username: {}", username)` |
| 参数异常 | WARN | `log.warn("参数错误, param: {}", param)` |
| 异常捕获 | ERROR | `log.error("查询用户异常, id: {}", id, e)` |
| 系统错误 | ERROR | `log.error("系统异常", e)` |
| 详细调试信息 | DEBUG | `log.debug("SQL 执行, sql: {}", sql)` |

## 代码示例

```java
@Override
public UserDTO getById(Long id) {
    log.info("查询用户开始, id: {}", id);
    
    try {
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("用户不存在, id: {}", id);
                    return new BusinessException(ErrorCode.USER_NOT_FOUND);
                });
        
        log.info("查询用户成功, id: {}, username: {}", id, user.getUsername());
        return userConverter.toDTO(user);
        
    } catch (BusinessException e) {
        log.warn("查询用户失败, id: {}, error: {}", id, e.getMessage());
        throw e;
    } catch (Exception e) {
        log.error("查询用户异常, id: {}", id, e);
        throw new SystemException(ErrorCode.SYSTEM_ERROR, e);
    }
}
```
