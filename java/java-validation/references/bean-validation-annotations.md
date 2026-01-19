# Bean Validation 标准注解

## 常用注解

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

## 使用示例

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
