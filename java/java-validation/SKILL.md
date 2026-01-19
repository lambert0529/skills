---
name: java-validation
description: 生成符合规范的 Java 参数校验代码，包括 Bean Validation 注解使用、自定义校验器、分组校验等
version: 1.0.0
tags: [java, spring-boot, validation, bean-validation, parameter-check]
---

# Java 参数校验规范

## 描述

本技能用于生成符合主流 Java 后端开发规范的参数校验代码，包括：
- Bean Validation 注解使用
- 自定义校验器
- 分组校验
- 嵌套对象校验
- 校验错误处理

## 触发条件

当用户需要：
- 为 DTO 添加参数校验
- 创建自定义校验规则
- 实现分组校验
- 处理校验错误

## 核心规范

### 1. Bean Validation 标准注解

**常用注解**：
- `@NotNull` - 不能为 null
- `@NotBlank` - 字符串不能为空（trim 后）
- `@NotEmpty` - 集合、数组、字符串不能为空
- `@Null` - 必须为 null
- `@Size` - 长度限制
- `@Min` / `@Max` - 数值范围
- `@DecimalMin` / `@DecimalMax` - 小数范围
- `@Email` - 邮箱格式
- `@Pattern` - 正则表达式
- `@Past` / `@Future` - 日期校验
- `@Positive` / `@Negative` - 正数/负数
- `@Digits` - 数字位数

**使用示例**：
```java
@Data
@Schema(description = "创建用户请求")
public class CreateUserRequest {
    
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在 3-20 之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
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
    
    @Min(value = 1, message = "年龄必须大于 0")
    @Max(value = 150, message = "年龄不能超过 150")
    @Schema(description = "年龄", example = "25")
    private Integer age;
    
    @Past(message = "生日必须是过去的日期")
    @Schema(description = "生日", example = "1998-01-01")
    private LocalDate birthday;
    
    @DecimalMin(value = "0.0", inclusive = false, message = "金额必须大于 0")
    @Digits(integer = 10, fraction = 2, message = "金额格式不正确")
    @Schema(description = "金额", example = "100.50")
    private BigDecimal amount;
}
```

### 2. Controller 层校验

**规范要求**：
- 使用 `@Valid` 或 `@Validated` 注解
- 在 Controller 方法参数上添加校验注解
- 使用 `@PathVariable`、`@RequestParam` 时使用 `@Validated` 在类上

**代码示例**：
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
            @RequestParam Integer page,
            @Min(value = 1, message = "每页数量必须大于 0") 
            @Max(value = 100, message = "每页数量不能超过 100") 
            @RequestParam Integer size) {
        // ...
    }
    
    // @PathVariable 需要类上 @Validated
    @GetMapping("/{id}")
    public ResponseEntity<Result<UserDTO>> getUser(
            @PathVariable @Min(value = 1, message = "用户ID必须大于 0") Long id) {
        // ...
    }
}
```

### 3. 分组校验

**规范要求**：
- 定义校验分组接口
- 在注解中指定 `groups` 属性
- 在 Controller 中使用 `@Validated(Group.class)`

**代码示例**：
```java
// 定义校验分组
public interface CreateGroup {}
public interface UpdateGroup {}

// DTO 中使用分组
@Data
public class UserRequest {
    
    @Null(groups = CreateGroup.class, message = "创建时ID必须为空")
    @NotNull(groups = UpdateGroup.class, message = "更新时ID不能为空")
    private Long id;
    
    @NotBlank(groups = {CreateGroup.class, UpdateGroup.class}, 
              message = "用户名不能为空")
    @Size(min = 3, max = 20, groups = {CreateGroup.class, UpdateGroup.class},
          message = "用户名长度必须在 3-20 之间")
    private String username;
    
    @NotBlank(groups = CreateGroup.class, message = "创建时密码不能为空")
    private String password;  // 更新时可以为空
}

// Controller 中使用分组
@PostMapping
public ResponseEntity<Result<UserDTO>> createUser(
        @Validated(CreateGroup.class) @RequestBody UserRequest request) {
    // ...
}

@PutMapping("/{id}")
public ResponseEntity<Result<UserDTO>> updateUser(
        @Validated(UpdateGroup.class) @RequestBody UserRequest request) {
    // ...
}
```

### 4. 嵌套对象校验

**规范要求**：
- 在嵌套对象上使用 `@Valid` 注解
- 确保嵌套对象的字段也被校验

**代码示例**：
```java
@Data
public class CreateOrderRequest {
    
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    @NotEmpty(message = "订单项不能为空")
    @Valid  // 重要：启用嵌套对象校验
    private List<OrderItemRequest> items;
    
    @Valid  // 重要：启用嵌套对象校验
    @NotNull(message = "收货地址不能为空")
    private AddressRequest address;
}

@Data
public class OrderItemRequest {
    
    @NotNull(message = "商品ID不能为空")
    private Long productId;
    
    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量必须大于 0")
    private Integer quantity;
    
    @NotNull(message = "单价不能为空")
    @DecimalMin(value = "0.0", inclusive = false, message = "单价必须大于 0")
    private BigDecimal price;
}

@Data
public class AddressRequest {
    
    @NotBlank(message = "省份不能为空")
    private String province;
    
    @NotBlank(message = "城市不能为空")
    private String city;
    
    @NotBlank(message = "详细地址不能为空")
    private String detail;
}
```

### 5. 自定义校验器

**规范要求**：
- 定义校验注解
- 实现 `ConstraintValidator` 接口
- 在 DTO 中使用自定义注解

**代码示例**：
```java
// 1. 定义校验注解
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PhoneNumberValidator.class)
@Documented
public @interface PhoneNumber {
    
    String message() default "手机号格式不正确";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}

// 2. 实现校验器
public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {
    
    private static final String PHONE_PATTERN = "^1[3-9]\\d{9}$";
    
    @Override
    public void initialize(PhoneNumber constraintAnnotation) {
        // 初始化方法
    }
    
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;  // null 值由 @NotNull 等注解处理
        }
        return value.matches(PHONE_PATTERN);
    }
}

// 3. 使用自定义校验器
@Data
public class CreateUserRequest {
    
    @NotBlank(message = "手机号不能为空")
    @PhoneNumber(message = "手机号格式不正确")
    @Schema(description = "手机号", example = "13800138000")
    private String phone;
}
```

### 6. 自定义校验器 - 跨字段校验

**代码示例**：
```java
// 1. 定义校验注解
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordMatchValidator.class)
@Documented
public @interface PasswordMatch {
    
    String message() default "密码和确认密码不匹配";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
    
    String password() default "password";
    
    String confirmPassword() default "confirmPassword";
}

// 2. 实现校验器
public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, Object> {
    
    private String passwordField;
    private String confirmPasswordField;
    
    @Override
    public void initialize(PasswordMatch constraintAnnotation) {
        this.passwordField = constraintAnnotation.password();
        this.confirmPasswordField = constraintAnnotation.confirmPassword();
    }
    
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        try {
            Field password = value.getClass().getDeclaredField(passwordField);
            Field confirmPassword = value.getClass().getDeclaredField(confirmPasswordField);
            
            password.setAccessible(true);
            confirmPassword.setAccessible(true);
            
            Object passwordValue = password.get(value);
            Object confirmPasswordValue = confirmPassword.get(value);
            
            if (passwordValue == null || confirmPasswordValue == null) {
                return true;  // null 值由其他注解处理
            }
            
            return passwordValue.equals(confirmPasswordValue);
        } catch (Exception e) {
            return false;
        }
    }
}

// 3. 使用
@PasswordMatch
@Data
public class RegisterRequest {
    
    @NotBlank(message = "密码不能为空")
    private String password;
    
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;
}
```

### 7. Service 层校验

**规范要求**：
- Service 层进行业务层面的校验
- 使用 `@Validated` 注解在类上
- 方法参数使用 `@Valid` 或校验注解

**代码示例**：
```java
@Service
@Validated
@Slf4j
public class UserServiceImpl implements UserService {
    
    @Override
    public UserDTO create(@Valid CreateUserRequest request) {
        // 参数已通过校验
        // 进行业务校验
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }
        // ...
    }
    
    @Override
    public UserDTO update(
            @Min(value = 1, message = "用户ID必须大于 0") Long id,
            @Valid UpdateUserRequest request) {
        // ...
    }
}
```

## 使用方法

1. **添加依赖**：确保项目包含 `spring-boot-starter-validation`
2. **定义校验规则**：在 DTO 字段上添加校验注解
3. **启用校验**：在 Controller 方法参数上使用 `@Valid`
4. **处理校验错误**：通过全局异常处理器处理校验失败

## 示例

**用户输入**：
```
为创建用户请求添加参数校验：
- 用户名：3-20 字符，只能包含字母、数字、下划线
- 邮箱：必须符合邮箱格式
- 密码：8-20 字符，必须包含大小写字母和数字
- 手机号：11 位数字，1 开头
```

**生成内容**：
- 更新 `CreateUserRequest.java`，添加校验注解
- 创建 `PhoneNumberValidator.java` 自定义校验器（如需要）

## 注意事项

1. **校验顺序**：先进行 Bean Validation，再进行业务校验
2. **性能考虑**：避免复杂的校验逻辑影响性能
3. **错误消息**：提供清晰的错误消息，便于前端展示
4. **分组使用**：合理使用分组校验，避免重复定义 DTO
5. **嵌套校验**：记得在嵌套对象上使用 `@Valid` 注解
