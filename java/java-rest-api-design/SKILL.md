---
name: java-rest-api-design
description: 生成符合主流规范的 Java RESTful API 接口设计，包括 Controller、DTO、统一响应格式、异常处理等。使用场景：(1) 创建新的 REST API 接口，(2) 设计 Controller 层代码，(3) 生成 API 相关的 DTO 类，(4) 审查或优化现有 API 设计，(5) 需要遵循 Spring Boot 最佳实践和 RESTful 规范时
---

# Java REST API 设计规范

## 快速开始

生成 RESTful API 接口的基本步骤：

1. 设计 RESTful 路径（见 [references/restful-patterns.md](references/restful-patterns.md)）
2. 创建 Controller 类（使用 `assets/ControllerTemplate.java` 模板）
3. 定义请求/响应 DTO（见 [references/dto-patterns.md](references/dto-patterns.md)）
4. 配置统一响应格式

## Controller 层结构

### 必需元素

- 使用 `@RestController` 或 `@Controller` + `@ResponseBody`
- 使用 `@RequestMapping` 定义基础路径
- 方法使用 `@GetMapping`、`@PostMapping`、`@PutMapping`、`@DeleteMapping` 等
- 使用 `@Valid` 进行参数校验
- 使用 `@PathVariable`、`@RequestParam`、`@RequestBody` 接收参数

### 代码模板

参考 `assets/ControllerTemplate.java` 模板文件。

基本结构：

```java
@RestController
@RequestMapping("/api/v1/users")
@Validated
@Slf4j
public class UserController {
    
    private final UserService userService;
    
    @GetMapping("/{id}")
    public ResponseEntity<Result<UserDTO>> getUser(@PathVariable Long id) {
        // ...
    }
    
    @PostMapping
    public ResponseEntity<Result<UserDTO>> createUser(
            @Valid @RequestBody CreateUserRequest request) {
        // ...
    }
}
```

## DTO 设计

### 请求 DTO

- 使用 Bean Validation 注解进行参数校验
- 区分创建请求和更新请求
- 详细示例见 [references/dto-patterns.md](references/dto-patterns.md)

### 响应 DTO

- 只包含需要返回给前端的字段
- 避免直接返回实体类
- 使用统一的响应包装类

## 详细参考

- **RESTful 设计模式**：见 [references/restful-patterns.md](references/restful-patterns.md)
- **DTO 设计模式**：见 [references/dto-patterns.md](references/dto-patterns.md)
- **使用示例**：见 [references/example-usage.md](references/example-usage.md)
- **代码模板**：见 `assets/` 目录

## 最佳实践

### 1. 使用构造函数注入
```java
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor  // Lombok 自动生成构造函数
public class UserController {
    private final UserService userService;  // final 字段，不可变
    // 依赖明确且可测试
}
```

### 2. 优先使用不可变 DTO
```java
// Java records (JDK 16+)
public record UserResponse(Long id, String name, String email, LocalDateTime createdAt) {}

// 或使用 Lombok @Value
@Value
public class UserResponse {
    Long id;
    String name;
    String email;
    LocalDateTime createdAt;
}
```

### 3. 早期验证输入
```java
@PostMapping
public ResponseEntity<Result<UserDTO>> createUser(@Valid @RequestBody CreateUserRequest request) {
    // 验证在方法执行前自动完成
    return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(request));
}
```

### 4. 使用 ResponseEntity 灵活返回
```java
return ResponseEntity.status(HttpStatus.CREATED)
    .header("Location", "/api/v1/users/" + created.getId())
    .header("X-Total-Count", String.valueOf(userService.count()))
    .body(created);
```

### 5. 实现适当的事务管理
```java
@Service
@Transactional
public class UserService {
    
    @Transactional(readOnly = true)  // 只读操作优化
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    @Transactional  // 写操作需要事务
    public User create(User user) {
        return userRepository.save(user);
    }
}
```

### 6. 添加有意义的日志
```java
@Slf4j
@RestController
public class UserController {
    public ResponseEntity<Result<UserDTO>> createUser(@Valid @RequestBody CreateUserRequest request) {
        log.info("Creating user with email: {}", request.getEmail());
        // ...
    }
}
```

## 约束和警告

### 1. 永远不要直接暴露实体类
使用 DTO 来分离 API 契约和领域模型。这可以防止意外暴露内部数据结构，并允许在不改变数据库模式的情况下演进 API。

### 2. 严格遵循 REST 约定
- 使用名词作为资源名称，不要使用动词
- 为操作使用正确的 HTTP 方法
- 使用复数资源名称（/users，而不是 /user）
- 为每个操作返回适当的 HTTP 状态码

### 3. 全局处理所有异常
使用 `@RestControllerAdvice` 一致地捕获所有异常。不要让原始异常冒泡到客户端。

### 4. 始终对大型结果集进行分页
对于可能返回许多结果的 GET 端点，实现分页以防止性能问题和 DDoS 漏洞。

### 5. 验证所有输入数据
永远不要信任客户端输入。在所有请求 DTO 上使用 Jakarta 验证注解，在控制器边界验证数据。

### 6. 仅使用构造函数注入
避免字段注入（`@Autowired`）以获得更好的可测试性和明确的依赖声明。

### 7. 保持控制器精简
控制器应该只处理 HTTP 请求/响应适配。将业务逻辑委托给服务层。

## 注意事项

1. **安全性**：敏感信息（如密码）不应出现在响应中
2. **性能**：避免 N+1 查询问题，合理使用关联查询
3. **版本控制**：API 变更时使用版本号，保持向后兼容
4. **文档**：使用 Swagger/OpenAPI 注解生成 API 文档
5. **幂等性**：PUT 和 DELETE 操作应该是幂等的
