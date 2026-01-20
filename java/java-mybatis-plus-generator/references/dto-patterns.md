# DTO 设计模式

## 请求 DTO（Request DTO）

### 创建请求 DTO

```java
public record CreateProductRequest(
    @NotBlank(message = "产品名称不能为空")
    @Size(min = 1, max = 100, message = "产品名称长度必须在1-100之间")
    String name,
    
    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.01", message = "价格必须大于0")
    BigDecimal price,
    
    @NotBlank(message = "分类不能为空")
    String category,
    
    @Size(max = 500, message = "描述长度不能超过500")
    String description
) {}
```

### 更新请求 DTO

```java
public record UpdateProductRequest(
    @Size(min = 1, max = 100, message = "产品名称长度必须在1-100之间")
    String name,
    
    @DecimalMin(value = "0.01", message = "价格必须大于0")
    BigDecimal price,
    
    String category,
    
    @Size(max = 500, message = "描述长度不能超过500")
    String description
) {}
```

### 分页请求 DTO

```java
public class PageRequest {
    @Min(value = 0, message = "页码不能小于0")
    private Integer page = 0;
    
    @Min(value = 1, message = "每页大小不能小于1")
    @Max(value = 100, message = "每页大小不能超过100")
    private Integer size = 20;
    
    private String sortBy = "createTime";
    
    private String sortOrder = "desc";  // asc 或 desc
}
```

## 响应 DTO（Response DTO）

### 基本响应 DTO

```java
public record ProductDTO(
    Long id,
    String name,
    BigDecimal price,
    String category,
    String description,
    LocalDateTime createTime,
    LocalDateTime updateTime
) {}
```

### 分页响应 DTO

```java
@Builder
@Data
public class PageResult<T> {
    private List<T> content;
    private Long total;
    private Integer page;
    private Integer size;
    private Integer totalPages;
    private Boolean hasPrevious;
    private Boolean hasNext;
    private Boolean isFirst;
    private Boolean isLast;
}
```

## 最佳实践

1. **使用 Java records**：优先使用 Java records 创建不可变 DTO
2. **验证注解**：使用 Bean Validation 注解验证输入
3. **字段命名**：保持与前端约定一致的字段命名
4. **避免循环引用**：DTO 中不要包含可能导致循环引用的对象
5. **敏感信息**：不要在响应 DTO 中包含敏感信息（如密码）
