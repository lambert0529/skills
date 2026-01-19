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

## 注意事项

1. **安全性**：敏感信息（如密码）不应出现在响应中
2. **性能**：避免 N+1 查询问题，合理使用关联查询
3. **版本控制**：API 变更时使用版本号，保持向后兼容
4. **文档**：使用 Swagger/OpenAPI 注解生成 API 文档
5. **幂等性**：PUT 和 DELETE 操作应该是幂等的
