# 基于角色的访问控制（RBAC）模式

## 角色定义

```java
public enum Role {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN"),
    MODERATOR("ROLE_MODERATOR");
    
    private final String authority;
    
    Role(String authority) {
        this.authority = authority;
    }
    
    public String getAuthority() {
        return authority;
    }
}
```

## 权限定义

```java
public enum Permission {
    PRODUCT_READ("product:read"),
    PRODUCT_WRITE("product:write"),
    PRODUCT_DELETE("product:delete"),
    USER_READ("user:read"),
    USER_WRITE("user:write");
    
    private final String permission;
    
    Permission(String permission) {
        this.permission = permission;
    }
    
    public String getPermission() {
        return permission;
    }
}
```

## 角色-权限映射

```java
@Component
public class RolePermissionMapper {
    
    private static final Map<Role, Set<Permission>> ROLE_PERMISSIONS = Map.of(
        Role.USER, Set.of(Permission.PRODUCT_READ),
        Role.MODERATOR, Set.of(Permission.PRODUCT_READ, Permission.PRODUCT_WRITE),
        Role.ADMIN, Set.of(Permission.values())
    );
    
    public Set<Permission> getPermissions(Role role) {
        return ROLE_PERMISSIONS.getOrDefault(role, Collections.emptySet());
    }
}
```

## 自定义权限评估器

```java
@Component("permissionEvaluator")
public class CustomPermissionEvaluator implements PermissionEvaluator {
    
    private final UserService userService;
    private final RolePermissionMapper rolePermissionMapper;
    
    @Override
    public boolean hasPermission(
            Authentication authentication,
            Object targetDomainObject,
            Object permission) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        String username = authentication.getName();
        String permissionName = permission.toString();
        
        // 获取用户角色
        User user = userService.findByUsername(username);
        Role role = user.getRole();
        
        // 检查角色是否有权限
        Set<Permission> permissions = rolePermissionMapper.getPermissions(role);
        return permissions.stream()
            .anyMatch(p -> p.getPermission().equals(permissionName));
    }
    
    @Override
    public boolean hasPermission(
            Authentication authentication,
            Serializable targetId,
            String targetType,
            Object permission) {
        // 对象级权限控制
        return false;
    }
}
```

## 使用示例

```java
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductService productService;
    
    @GetMapping("/{id}")
    @PreAuthorize("hasPermission(null, 'product:read')")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getById(id));
    }
    
    @PostMapping
    @PreAuthorize("hasPermission(null, 'product:write')")
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody CreateProductRequest request) {
        return ResponseEntity.ok(productService.create(request));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission(null, 'product:delete')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
```
