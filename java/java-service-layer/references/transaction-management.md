# 事务管理规范

## 事务注解使用

### 规范要求

- 查询方法使用 `@Transactional(readOnly = true)`
- 写操作方法使用 `@Transactional(rollbackFor = Exception.class)`
- 事务注解放在实现类方法上，而非接口
- 避免在 Service 方法中捕获异常后不抛出，导致事务不回滚

### 事务传播行为

```java
// 默认：REQUIRED - 如果当前存在事务，则加入该事务；如果不存在，则创建一个新事务
@Transactional(propagation = Propagation.REQUIRED)

// 需要新事务：REQUIRES_NEW - 创建一个新事务，如果当前存在事务，则把当前事务挂起
@Transactional(propagation = Propagation.REQUIRES_NEW)

// 只读事务：只读查询优化
@Transactional(readOnly = true)
```

## 代码示例

```java
@Override
@Transactional(readOnly = true)
public UserDTO getById(Long id) {
    // 查询操作
}

@Override
@Transactional(rollbackFor = Exception.class)
public UserDTO create(CreateUserRequest request) {
    // 写操作
}
```
