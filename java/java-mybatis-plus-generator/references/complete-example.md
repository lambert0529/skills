# 完整示例：Product 功能模块

## 目录结构

```
com.example.product/
├── entity/
│   └── Product.java
├── mapper/
│   ├── ProductMapper.java
│   └── ProductMapper.xml
├── dto/
│   ├── request/
│   │   ├── CreateProductRequest.java
│   │   └── UpdateProductRequest.java
│   ├── response/
│   │   └── ProductDTO.java
│   └── PageRequest.java
├── converter/
│   └── ProductConverter.java
├── service/
│   ├── ProductService.java
│   └── impl/
│       └── ProductServiceImpl.java
└── controller/
    └── ProductController.java
```

## 实现步骤

### 1. 创建 Entity

```java
@Data
@TableName("product")
public class Product {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    private BigDecimal price;
    private String category;
    private String description;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
```

### 2. 创建 Mapper

```java
@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    // 自定义查询方法（如需要）
}
```

### 3. 创建 DTO

```java
// CreateProductRequest.java
public record CreateProductRequest(
    @NotBlank String name,
    @NotNull @DecimalMin("0.01") BigDecimal price,
    @NotBlank String category,
    String description
) {}

// ProductDTO.java
public record ProductDTO(
    Long id,
    String name,
    BigDecimal price,
    String category,
    String description,
    LocalDateTime createTime
) {}
```

### 4. 创建 Converter

```java
@Component
public class ProductConverter {
    public ProductDTO toDTO(Product entity) {
        return new ProductDTO(
            entity.getId(),
            entity.getName(),
            entity.getPrice(),
            entity.getCategory(),
            entity.getDescription(),
            entity.getCreateTime()
        );
    }
    
    public Product toEntity(CreateProductRequest request) {
        Product product = new Product();
        product.setName(request.name());
        product.setPrice(request.price());
        product.setCategory(request.category());
        product.setDescription(request.description());
        return product;
    }
}
```

### 5. 创建 Service

```java
public interface ProductService extends IService<Product> {
    ProductDTO getById(Long id);
    ProductDTO create(CreateProductRequest request);
    ProductDTO update(Long id, UpdateProductRequest request);
    void delete(Long id);
    PageResult<ProductDTO> list(PageRequest pageRequest);
}
```

### 6. 实现 Service

```java
@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> 
        implements ProductService {
    
    private final ProductConverter converter;
    
    @Override
    @Transactional(readOnly = true)
    public ProductDTO getById(Long id) {
        Product product = baseMapper.selectById(id);
        if (product == null) {
            throw new BusinessException("Product not found");
        }
        return converter.toDTO(product);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductDTO create(CreateProductRequest request) {
        Product product = converter.toEntity(request);
        save(product);
        return converter.toDTO(product);
    }
    
    // ... 其他方法
}
```

### 7. 创建 Controller

```java
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductService productService;
    
    @GetMapping("/{id}")
    public ResponseEntity<Result<ProductDTO>> get(@PathVariable Long id) {
        return ResponseEntity.ok(Result.success(productService.getById(id)));
    }
    
    @PostMapping
    public ResponseEntity<Result<ProductDTO>> create(
            @Valid @RequestBody CreateProductRequest request) {
        ProductDTO dto = productService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Result.success(dto));
    }
    
    // ... 其他方法
}
```

## 关键点

1. **分层清晰**：Entity -> Mapper -> Service -> Controller
2. **DTO 分离**：使用 DTO 分离 API 契约和数据库模型
3. **转换器集中**：所有转换逻辑在 Converter 中
4. **事务管理**：Service 层使用 `@Transactional`
5. **异常处理**：使用全局异常处理器统一处理
