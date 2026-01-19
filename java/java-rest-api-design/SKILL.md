---
name: java-rest-api-design
description: 生成符合主流规范的 Java RESTful API 接口设计，包括 Controller、DTO、统一响应格式、异常处理等
version: 1.0.0
tags: [java, spring-boot, rest-api, controller, dto]
---

# Java REST API 设计规范

## 描述

本技能用于生成符合主流 Java 后端开发规范的 RESTful API 接口代码，遵循 Spring Boot 最佳实践，包括：
- RESTful 路径设计规范
- Controller 层代码结构
- 请求/响应 DTO 设计
- 统一响应格式
- HTTP 状态码使用规范
- API 版本化策略

## 触发条件

当用户需要：
- 创建新的 REST API 接口
- 设计 Controller 层代码
- 生成 API 相关的 DTO 类
- 审查或优化现有 API 设计

## 核心规范

### 1. RESTful 路径设计

**规范要求**：
- 使用名词复数形式，不使用动词
- 路径层级不超过 3 层
- 使用连字符 `-` 而非下划线
- API 版本化：`/api/v1/` 或 `/api/v2/`

**示例**：
```
✅ 正确：
GET    /api/v1/users
GET    /api/v1/users/{id}
POST   /api/v1/users
PUT    /api/v1/users/{id}
DELETE /api/v1/users/{id}
GET    /api/v1/users/{id}/orders

❌ 错误：
GET    /api/getUser
POST   /api/createUser
GET    /api/user_list
```

### 2. Controller 层结构

**必需元素**：
- 使用 `@RestController` 或 `@Controller` + `@ResponseBody`
- 使用 `@RequestMapping` 定义基础路径
- 方法使用 `@GetMapping`、`@PostMapping`、`@PutMapping`、`@DeleteMapping` 等
- 使用 `@Valid` 进行参数校验
- 使用 `@PathVariable`、`@RequestParam`、`@RequestBody` 接收参数

**代码模板**：
```java
@RestController
@RequestMapping("/api/v1/users")
@Validated
@Slf4j
public class UserController {
    
    private final UserService userService;
    
    @GetMapping("/{id}")
    public ResponseEntity<Result<UserDTO>> getUser(@PathVariable Long id) {
        log.info("查询用户, id: {}", id);
        UserDTO user = userService.getById(id);
        return ResponseEntity.ok(Result.success(user));
    }
    
    @PostMapping
    public ResponseEntity<Result<UserDTO>> createUser(
            @Valid @RequestBody CreateUserRequest request) {
        log.info("创建用户, request: {}", request);
        UserDTO user = userService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Result.success(user));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Result<UserDTO>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        log.info("更新用户, id: {}, request: {}", id, request);
        UserDTO user = userService.update(id, request);
        return ResponseEntity.ok(Result.success(user));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Result<Void>> deleteUser(@PathVariable Long id) {
        log.info("删除用户, id: {}", id);
        userService.delete(id);
        return ResponseEntity.ok(Result.success());
    }
}
```

### 3. 请求 DTO 设计

**规范要求**：
- 使用 `@Valid` 和 Bean Validation 注解
- 字段命名使用驼峰命名
- 提供清晰的字段说明（使用 `@Schema` 或 JavaDoc）
- 区分创建请求和更新请求

**示例**：
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "创建用户请求")
public class CreateUserRequest {
    
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在 3-20 之间")
    @Schema(description = "用户名", example = "zhangsan", required = true)
    private String username;
    
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Schema(description = "邮箱", example = "zhangsan@example.com", required = true)
    private String email;
    
    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 20, message = "密码长度必须在 8-20 之间")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$", 
             message = "密码必须包含大小写字母和数字")
    @Schema(description = "密码", required = true)
    private String password;
}
```

### 4. 响应 DTO 设计

**规范要求**：
- 响应 DTO 只包含需要返回给前端的字段
- 避免直接返回实体类（Entity）
- 使用统一的响应包装类

**示例**：
```java
@Data
@Schema(description = "用户信息")
public class UserDTO {
    
    @Schema(description = "用户ID")
    private Long id;
    
    @Schema(description = "用户名")
    private String username;
    
    @Schema(description = "邮箱")
    private String email;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    // 不包含敏感信息如密码
}
```

### 5. HTTP 状态码使用

**规范要求**：
- `200 OK`：成功获取资源或更新资源
- `201 Created`：成功创建资源
- `204 No Content`：成功删除资源（无返回体）
- `400 Bad Request`：请求参数错误
- `401 Unauthorized`：未认证
- `403 Forbidden`：无权限
- `404 Not Found`：资源不存在
- `500 Internal Server Error`：服务器内部错误

### 6. 分页查询规范

**请求参数**：
```java
@Data
@Schema(description = "分页查询请求")
public class PageRequest {
    
    @Min(value = 1, message = "页码必须大于 0")
    @Schema(description = "页码，从 1 开始", example = "1", defaultValue = "1")
    private Integer page = 1;
    
    @Min(value = 1, message = "每页数量必须大于 0")
    @Max(value = 100, message = "每页数量不能超过 100")
    @Schema(description = "每页数量", example = "10", defaultValue = "10")
    private Integer size = 10;
    
    @Schema(description = "排序字段", example = "createTime")
    private String sortBy;
    
    @Schema(description = "排序方向", example = "desc", allowableValues = {"asc", "desc"})
    private String sortOrder = "desc";
}
```

**响应格式**：
```java
@Data
@Schema(description = "分页响应")
public class PageResult<T> {
    
    @Schema(description = "数据列表")
    private List<T> content;
    
    @Schema(description = "总记录数")
    private Long total;
    
    @Schema(description = "当前页码")
    private Integer page;
    
    @Schema(description = "每页数量")
    private Integer size;
    
    @Schema(description = "总页数")
    private Integer totalPages;
}
```

## 使用方法

1. **明确 API 需求**：说明需要创建的接口功能、路径、请求参数、响应数据
2. **生成代码**：根据规范生成 Controller、DTO 等代码
3. **检查规范**：确保符合 RESTful 规范和项目约定

## 示例

**用户输入**：
```
为用户管理模块创建 REST API，包括：
- 查询用户列表（分页）
- 根据 ID 查询用户详情
- 创建用户
- 更新用户
- 删除用户
```

**生成内容**：
- `UserController.java` - Controller 层代码
- `CreateUserRequest.java` - 创建请求 DTO
- `UpdateUserRequest.java` - 更新请求 DTO
- `UserDTO.java` - 响应 DTO
- `PageRequest.java` - 分页请求
- `PageResult.java` - 分页响应

## 注意事项

1. **安全性**：敏感信息（如密码）不应出现在响应中
2. **性能**：避免 N+1 查询问题，合理使用关联查询
3. **版本控制**：API 变更时使用版本号，保持向后兼容
4. **文档**：使用 Swagger/OpenAPI 注解生成 API 文档
5. **幂等性**：PUT 和 DELETE 操作应该是幂等的
