---
name: java-validation
description: 生成符合规范的 Java 参数校验代码，包括 Bean Validation 注解使用、自定义校验器、分组校验等。使用场景：(1) 为 DTO 添加参数校验，(2) 创建自定义校验规则，(3) 实现分组校验，(4) 处理嵌套对象校验，(5) 需要遵循 Bean Validation 规范和 Spring Boot 最佳实践时
---

# Java 参数校验规范

## 快速开始

添加参数校验的基本步骤：

1. 在 DTO 字段上添加 Bean Validation 注解（见 [references/bean-validation-annotations.md](references/bean-validation-annotations.md)）
2. 在 Controller 方法参数上使用 `@Valid` 或 `@Validated`
3. 如需分组校验，定义分组接口（见 [references/group-validation.md](references/group-validation.md)）
4. 如需自定义校验，创建自定义校验器（使用 `assets/CustomValidatorTemplate.java`）

## Bean Validation 标准注解

### 常用注解

- `@NotNull` - 不能为 null
- `@NotBlank` - 字符串不能为空
- `@Size` - 长度限制
- `@Min` / `@Max` - 数值范围
- `@Email` - 邮箱格式
- `@Pattern` - 正则表达式

详细注解列表和示例见 [references/bean-validation-annotations.md](references/bean-validation-annotations.md)

## Controller 层校验

### 规范要求

- 使用 `@Valid` 或 `@Validated` 注解
- 在 Controller 方法参数上添加校验注解
- 使用 `@PathVariable`、`@RequestParam` 时使用 `@Validated` 在类上

### 示例

```java
@RestController
@RequestMapping("/api/v1/users")
@Validated  // 用于 @RequestParam 和 @PathVariable 的校验
@Slf4j
public class UserController {
    
    // @RequestBody 使用 @Valid
    @PostMapping
    public ResponseEntity<Result<UserDTO>> createUser(
            @Valid @RequestBody CreateUserRequest request) {
        // ...
    }
    
    // @RequestParam 需要类上 @Validated
    @GetMapping
    public ResponseEntity<Result<PageResult<UserDTO>>> listUsers(
            @Min(value = 1, message = "页码必须大于 0") 
            @RequestParam Integer page) {
        // ...
    }
}
```

## 分组校验

### 规范要求

- 定义校验分组接口
- 在注解中指定 `groups` 属性
- 在 Controller 中使用 `@Validated(Group.class)`

详细内容见 [references/group-validation.md](references/group-validation.md)

## 嵌套对象校验

### 规范要求

- 在嵌套对象上使用 `@Valid` 注解
- 确保嵌套对象的字段也被校验

### 示例

```java
@Data
public class CreateOrderRequest {
    
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    @NotEmpty(message = "订单项不能为空")
    @Valid  // 重要：启用嵌套对象校验
    private List<OrderItemRequest> items;
}
```

## 自定义校验器

### 规范要求

- 定义校验注解
- 实现 `ConstraintValidator` 接口
- 在 DTO 中使用自定义注解

代码模板见 `assets/CustomValidatorTemplate.java`

## Service 层校验

### 规范要求

- Service 层进行业务层面的校验
- 使用 `@Validated` 注解在类上
- 方法参数使用 `@Valid` 或校验注解

## 详细参考

- **Bean Validation 注解**：见 [references/bean-validation-annotations.md](references/bean-validation-annotations.md)
- **分组校验**：见 [references/group-validation.md](references/group-validation.md)
- **校验示例**：见 [references/validation-examples.md](references/validation-examples.md)
- **自定义校验器模板**：见 `assets/CustomValidatorTemplate.java`

## 注意事项

1. **校验顺序**：先进行 Bean Validation，再进行业务校验
2. **性能考虑**：避免复杂的校验逻辑影响性能
3. **错误消息**：提供清晰的错误消息，便于前端展示
4. **分组使用**：合理使用分组校验，避免重复定义 DTO
5. **嵌套校验**：记得在嵌套对象上使用 `@Valid` 注解
