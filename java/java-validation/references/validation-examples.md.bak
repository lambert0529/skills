# 参数校验示例

## Bean Validation 标准注解

### 常用注解示例

```java
@NotBlank(message = "用户名不能为空")
@Size(min = 3, max = 20, message = "用户名长度必须在 3-20 之间")
private String username;

@NotBlank(message = "邮箱不能为空")
@Email(message = "邮箱格式不正确")
private String email;

@NotNull(message = "年龄不能为空")
@Min(value = 1, message = "年龄必须大于 0")
@Max(value = 150, message = "年龄不能超过 150")
private Integer age;

@Past(message = "生日必须是过去的日期")
private LocalDate birthday;
```

## 自定义校验器示例

### 手机号校验

```java
@PhoneNumber(message = "手机号格式不正确")
private String phone;
```

### 密码强度校验

```java
@PasswordStrength(minLength = 8, requireUppercase = true, requireDigit = true)
private String password;
```

## 分组校验示例

```java
// 定义分组
public interface CreateGroup {}
public interface UpdateGroup {}

// 使用分组
@Null(groups = CreateGroup.class, message = "创建时ID必须为空")
@NotNull(groups = UpdateGroup.class, message = "更新时ID不能为空")
private Long id;
```
