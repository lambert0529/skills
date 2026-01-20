# DTO 转换器模式

## 手动实现转换器

### 基本转换器

```java
@Component
@RequiredArgsConstructor
public class ProductConverter {
    
    /**
     * Entity 转 DTO
     */
    public ProductDTO toDTO(Product entity) {
        if (entity == null) {
            return null;
        }
        return new ProductDTO(
            entity.getId(),
            entity.getName(),
            entity.getPrice(),
            entity.getCategory(),
            entity.getDescription(),
            entity.getCreateTime(),
            entity.getUpdateTime()
        );
    }
    
    /**
     * DTO 转 Entity（用于创建）
     */
    public Product toEntity(CreateProductRequest request) {
        if (request == null) {
            return null;
        }
        Product product = new Product();
        product.setName(request.name());
        product.setPrice(request.price());
        product.setCategory(request.category());
        product.setDescription(request.description());
        return product;
    }
    
    /**
     * 更新 Entity（用于更新）
     */
    public void updateEntity(Product entity, UpdateProductRequest request) {
        if (request == null || entity == null) {
            return;
        }
        if (request.name() != null) {
            entity.setName(request.name());
        }
        if (request.price() != null) {
            entity.setPrice(request.price());
        }
        if (request.category() != null) {
            entity.setCategory(request.category());
        }
        if (request.description() != null) {
            entity.setDescription(request.description());
        }
    }
    
    /**
     * List<Entity> 转 List<DTO>
     */
    public List<ProductDTO> toDTOList(List<Product> entities) {
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
```

## 使用 MapStruct（推荐）

### 添加依赖

```xml
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
    <version>1.5.5.Final</version>
</dependency>
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct-processor</artifactId>
    <version>1.5.5.Final</version>
    <scope>provided</scope>
</dependency>
```

### MapStruct 转换器

```java
@Mapper(componentModel = "spring")
public interface ProductConverter {
    
    ProductDTO toDTO(Product entity);
    
    Product toEntity(CreateProductRequest request);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void updateEntity(@MappingTarget Product entity, UpdateProductRequest request);
    
    List<ProductDTO> toDTOList(List<Product> entities);
}
```

## 最佳实践

1. **集中管理**：所有转换逻辑集中在 Converter 中
2. **空值处理**：转换器应该处理 null 值
3. **性能优化**：批量转换时使用 Stream API
4. **使用 MapStruct**：对于复杂转换，推荐使用 MapStruct 自动生成代码
5. **保持简单**：转换逻辑应该简单直接，复杂逻辑应该在 Service 层处理
