# 业务逻辑封装模式

## 业务逻辑组织

### 规范要求

- Service 层负责业务逻辑处理
- 数据校验应在 Service 层进行
- 复杂业务逻辑拆分为私有方法
- 使用领域模型而非贫血模型

## 代码示例

```java
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
```

## 依赖注入

### 规范要求

- 使用构造器注入（推荐）
- 使用 `@RequiredArgsConstructor`（Lombok）简化代码
- 避免使用 `@Autowired` 字段注入

### 示例

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
