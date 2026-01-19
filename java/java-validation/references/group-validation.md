# 分组校验

## 分组校验使用

### 定义校验分组

```java
// 定义校验分组
public interface CreateGroup {}
public interface UpdateGroup {}
```

### DTO 中使用分组

```java
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
```

### Controller 中使用分组

```java
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
