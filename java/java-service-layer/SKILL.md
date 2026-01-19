---
name: java-service-layer
description: 生成符合规范的 Java Service 层代码，包括接口设计、事务管理、异常处理、日志记录等最佳实践
version: 1.0.0
tags: [java, spring-boot, service, transaction, business-logic]
---

# Java Service 层开发规范

## 描述

本技能用于生成符合主流 Java 后端开发规范的 Service 层代码，遵循 Spring Boot 最佳实践，包括：
- 接口与实现分离
- 事务管理规范
- 业务逻辑封装
- 异常处理
- 日志记录
- 参数校验

## 触发条件

当用户需要：
- 创建新的 Service 层代码
- 实现业务逻辑处理
- 设计 Service 接口
- 优化现有 Service 代码

## 核心规范

### 1. 接口与实现分离

**规范要求**：
- 定义 Service 接口（Interface）
- 实现类使用 `@Service` 注解
- 接口命名：`XxxService`
- 实现类命名：`XxxServiceImpl`

**代码模板**：
```java
// 接口定义
public interface UserService {
    
    /**
     * 根据ID查询用户
     * @param id 用户ID
     * @return 用户信息
     * @throws BusinessException 用户不存在时抛出
     */
    UserDTO getById(Long id);
    
    /**
     * 创建用户
     * @param request 创建请求
     * @return 用户信息
     * @throws BusinessException 用户名已存在时抛出
     */
    UserDTO create(CreateUserRequest request);
    
    /**
     * 更新用户
     * @param id 用户ID
     * @param request 更新请求
     * @return 用户信息
     */
    UserDTO update(Long id, UpdateUserRequest request);
    
    /**
     * 删除用户
     * @param id 用户ID
     */
    void delete(Long id);
    
    /**
     * 分页查询用户
     * @param pageRequest 分页请求
     * @return 分页结果
     */
    PageResult<UserDTO> list(PageRequest pageRequest);
}

// 实现类
@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final UserConverter userConverter;
    
    @Override
    @Transactional(readOnly = true)
    public UserDTO getById(Long id) {
        log.info("查询用户, id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return userConverter.toDTO(user);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDTO create(CreateUserRequest request) {
        log.info("创建用户, request: {}", request);
        
        // 业务校验
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }
        
        // 业务逻辑处理
        User user = userConverter.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreateTime(LocalDateTime.now());
        
        // 保存
        User savedUser = userRepository.save(user);
        log.info("用户创建成功, id: {}", savedUser.getId());
        
        return userConverter.toDTO(savedUser);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDTO update(Long id, UpdateUserRequest request) {
        log.info("更新用户, id: {}, request: {}", id, request);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        
        // 更新字段
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getNickname() != null) {
            user.setNickname(request.getNickname());
        }
        user.setUpdateTime(LocalDateTime.now());
        
        User updatedUser = userRepository.save(user);
        log.info("用户更新成功, id: {}", id);
        
        return userConverter.toDTO(updatedUser);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        log.info("删除用户, id: {}", id);
        
        if (!userRepository.existsById(id)) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        
        userRepository.deleteById(id);
        log.info("用户删除成功, id: {}", id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public PageResult<UserDTO> list(PageRequest pageRequest) {
        log.info("分页查询用户, pageRequest: {}", pageRequest);
        
        Pageable pageable = PageRequest.of(
                pageRequest.getPage() - 1,
                pageRequest.getSize(),
                Sort.by(Sort.Direction.fromString(pageRequest.getSortOrder()),
                        pageRequest.getSortBy())
        );
        
        Page<User> page = userRepository.findAll(pageable);
        
        List<UserDTO> content = page.getContent().stream()
                .map(userConverter::toDTO)
                .collect(Collectors.toList());
        
        return PageResult.<UserDTO>builder()
                .content(content)
                .total(page.getTotalElements())
                .page(pageRequest.getPage())
                .size(pageRequest.getSize())
                .totalPages(page.getTotalPages())
                .build();
    }
}
```

### 2. 事务管理规范

**规范要求**：
- 查询方法使用 `@Transactional(readOnly = true)`
- 写操作方法使用 `@Transactional(rollbackFor = Exception.class)`
- 事务注解放在实现类方法上，而非接口
- 避免在 Service 方法中捕获异常后不抛出，导致事务不回滚

**事务传播行为**：
```java
// 默认：REQUIRED - 如果当前存在事务，则加入该事务；如果不存在，则创建一个新事务
@Transactional(propagation = Propagation.REQUIRED)

// 需要新事务：REQUIRES_NEW - 创建一个新事务，如果当前存在事务，则把当前事务挂起
@Transactional(propagation = Propagation.REQUIRES_NEW)

// 只读事务：只读查询优化
@Transactional(readOnly = true)
```

### 3. 业务逻辑封装

**规范要求**：
- Service 层负责业务逻辑处理
- 数据校验应在 Service 层进行
- 复杂业务逻辑拆分为私有方法
- 使用领域模型而非贫血模型

**示例**：
```java
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderDTO createOrder(CreateOrderRequest request) {
        log.info("创建订单, request: {}", request);
        
        // 1. 业务校验
        validateCreateOrder(request);
        
        // 2. 获取用户信息
        User user = getUserById(request.getUserId());
        
        // 3. 计算订单金额
        BigDecimal totalAmount = calculateOrderAmount(request);
        
        // 4. 检查库存
        checkInventory(request.getItems());
        
        // 5. 创建订单
        Order order = buildOrder(user, request, totalAmount);
        Order savedOrder = orderRepository.save(order);
        
        // 6. 扣减库存
        deductInventory(request.getItems());
        
        // 7. 发送消息
        sendOrderCreatedMessage(savedOrder);
        
        log.info("订单创建成功, orderId: {}", savedOrder.getId());
        return orderConverter.toDTO(savedOrder);
    }
    
    private void validateCreateOrder(CreateOrderRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BusinessException(ErrorCode.ORDER_ITEMS_EMPTY);
        }
        // 更多校验...
    }
    
    private BigDecimal calculateOrderAmount(CreateOrderRequest request) {
        // 计算逻辑...
    }
    
    // 其他私有方法...
}
```

### 4. 异常处理

**规范要求**：
- 使用自定义业务异常 `BusinessException`
- 异常信息要清晰明确
- 不要吞掉异常，要向上抛出
- 记录异常日志

**示例**：
```java
@Override
public UserDTO getById(Long id) {
    try {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return userConverter.toDTO(user);
    } catch (BusinessException e) {
        log.warn("查询用户失败, id: {}, error: {}", id, e.getMessage());
        throw e;
    } catch (Exception e) {
        log.error("查询用户异常, id: {}", id, e);
        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "查询用户失败");
    }
}
```

### 5. 日志记录规范

**规范要求**：
- 使用 `@Slf4j` 注解（Lombok）
- 方法入口记录参数（脱敏处理）
- 方法出口记录结果或关键信息
- 异常时记录错误日志
- 使用合适的日志级别：DEBUG、INFO、WARN、ERROR

**日志级别使用**：
- `DEBUG`：详细的调试信息，生产环境通常关闭
- `INFO`：关键业务流程信息，如方法入口、业务操作成功
- `WARN`：警告信息，如业务校验失败、参数异常
- `ERROR`：错误信息，如异常、系统错误

**示例**：
```java
@Override
@Transactional(rollbackFor = Exception.class)
public UserDTO create(CreateUserRequest request) {
    log.info("创建用户开始, username: {}", request.getUsername());
    
    try {
        // 业务逻辑...
        UserDTO result = userConverter.toDTO(savedUser);
        log.info("创建用户成功, userId: {}", result.getId());
        return result;
    } catch (BusinessException e) {
        log.warn("创建用户失败, username: {}, error: {}", 
                request.getUsername(), e.getMessage());
        throw e;
    } catch (Exception e) {
        log.error("创建用户异常, username: {}", request.getUsername(), e);
        throw new BusinessException(ErrorCode.SYSTEM_ERROR);
    }
}
```

### 6. 参数校验

**规范要求**：
- Service 层进行业务层面的参数校验
- 使用断言或条件判断
- 校验失败抛出 `BusinessException`

**示例**：
```java
private void validateCreateOrder(CreateOrderRequest request) {
    if (request == null) {
        throw new BusinessException(ErrorCode.REQUEST_PARAM_ERROR, "请求参数不能为空");
    }
    if (request.getUserId() == null) {
        throw new BusinessException(ErrorCode.REQUEST_PARAM_ERROR, "用户ID不能为空");
    }
    if (request.getItems() == null || request.getItems().isEmpty()) {
        throw new BusinessException(ErrorCode.ORDER_ITEMS_EMPTY);
    }
    // 更多业务校验...
}
```

### 7. 依赖注入

**规范要求**：
- 使用构造器注入（推荐）
- 使用 `@RequiredArgsConstructor`（Lombok）简化代码
- 避免使用 `@Autowired` 字段注入

**示例**：
```java
@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final UserConverter userConverter;
    private final PasswordEncoder passwordEncoder;
    
    // 构造器由 Lombok 自动生成
}
```

## 使用方法

1. **明确业务需求**：说明需要实现的业务功能
2. **生成接口**：先定义 Service 接口
3. **生成实现**：实现 Service 接口，包含业务逻辑
4. **检查规范**：确保符合事务、异常、日志等规范

## 示例

**用户输入**：
```
为用户管理创建 Service 层，包括：
- 查询用户（根据ID）
- 创建用户（需要校验用户名唯一性）
- 更新用户
- 删除用户
- 分页查询用户列表
```

**生成内容**：
- `UserService.java` - Service 接口
- `UserServiceImpl.java` - Service 实现类
- 包含事务管理、异常处理、日志记录等

## 注意事项

1. **事务边界**：事务应该覆盖完整的业务操作
2. **异常处理**：不要捕获异常后不处理，导致事务不回滚
3. **性能优化**：避免在循环中进行数据库操作
4. **代码复用**：提取公共逻辑到私有方法或工具类
5. **单一职责**：每个 Service 方法只做一件事
