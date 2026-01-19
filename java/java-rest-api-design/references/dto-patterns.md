# DTO 设计模式

## 请求 DTO 设计

### 规范要求

- 使用 `@Valid` 和 Bean Validation 注解
- 字段命名使用驼峰命名
- 提供清晰的字段说明（使用 `@Schema` 或 JavaDoc）
- 区分创建请求和更新请求

### 创建请求示例

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

## 响应 DTO 设计

### 规范要求

- 响应 DTO 只包含需要返回给前端的字段
- 避免直接返回实体类（Entity）
- 使用统一的响应包装类

### 响应 DTO 示例

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
