---
name: java-mybatis-plus-generator
description: 生成基于 MyBatis-Plus 和 PageHelper 的完整 CRUD 代码，包括 Entity、Mapper、Service、Controller 层和 DTO。使用场景：(1) 创建基于 MyBatis-Plus 的 CRUD 操作，(2) 实现传统分层架构（Controller-Service-Mapper-Entity），(3) 生成分页查询功能，(4) 设计 DTO 和转换器，(5) 需要遵循 MyBatis-Plus 和 PageHelper 最佳实践时
---

# MyBatis-Plus CRUD 代码生成规范

## 快速开始

生成完整 CRUD 代码的基本步骤：

1. 创建实体类（Entity）- 使用 MyBatis-Plus 注解（见 `assets/EntityTemplate.java`）
2. 创建 Mapper 接口 - 继承 `BaseMapper<T>`（见 `assets/MapperInterfaceTemplate.java`）
3. 创建 Mapper XML（如需要自定义 SQL）（见 `assets/MapperXmlTemplate.xml`）
4. 创建 DTO 类 - Request/Response DTO（见 [references/dto-patterns.md](references/dto-patterns.md)）
5. 创建转换器（Converter）- Entity <-> DTO 转换（见 [references/converter-pattern.md](references/converter-pattern.md)）
6. 创建 Service 接口和实现 - 继承 `IService<T>`（见 `assets/ServiceInterfaceTemplate.java` 和 `ServiceImplTemplate.java`）
7. 创建 Controller 层 - REST API 端点（见 `assets/ControllerTemplate.java`）

## 架构结构

```
feature/product/
├── entity/
│   └── Product.java              # MyBatis-Plus 实体类
├── mapper/
│   ├── ProductMapper.java        # Mapper 接口（继承 BaseMapper）
│   └── ProductMapper.xml          # Mapper XML（可选）
├── dto/
│   ├── request/
│   │   ├── CreateProductRequest.java
│   │   └── UpdateProductRequest.java
│   ├── response/
│   │   └── ProductDTO.java
│   └── PageRequest.java
├── converter/
│   └── ProductConverter.java     # Entity <-> DTO 转换器
├── service/
│   ├── ProductService.java        # Service 接口（继承 IService）
│   └── impl/
│       └── ProductServiceImpl.java  # Service 实现（继承 ServiceImpl）
└── controller/
    └── ProductController.java    # REST Controller
```

## 实体类（Entity）

### 规范要求

- 使用 `@TableName` 指定表名
- 使用 `@TableId` 指定主键
- 使用 `@TableField` 指定字段映射
- 继承或实现必要的接口

代码模板见 `assets/EntityTemplate.java`

## Mapper 层

### Mapper 接口

- 继承 `BaseMapper<T>` 获得基础 CRUD 方法
- 定义自定义查询方法（如需要）

代码模板见 `assets/MapperInterfaceTemplate.java`

### Mapper XML

- 定义自定义 SQL 查询
- 使用 MyBatis-Plus 标签简化配置

代码模板见 `assets/MapperXmlTemplate.xml`

## Service 层

### Service 接口

- 继承 `IService<T>` 获得基础服务方法
- 定义业务方法

代码模板见 `assets/ServiceInterfaceTemplate.java`

### Service 实现

- 继承 `ServiceImpl<M, T>` 实现接口
- 实现业务逻辑

代码模板见 `assets/ServiceImplTemplate.java`

## Controller 层

### 规范要求

- 使用 PageHelper 进行分页
- 使用 MyBatis-Plus 的查询条件构建器
- 返回统一响应格式

代码模板见 `assets/ControllerTemplate.java`

## 分页查询

### PageHelper 使用

- 在查询前调用 `PageHelper.startPage(page, size)`
- 查询后自动封装分页信息

详细使用见 [references/pagehelper-usage.md](references/pagehelper-usage.md)

## MyBatis-Plus 查询条件

### QueryWrapper 使用

- 构建动态查询条件
- 支持链式调用

详细使用见 [references/query-wrapper.md](references/query-wrapper.md)

## DTO 设计

### 请求 DTO（Request DTO）

- 区分创建请求和更新请求
- 使用 Bean Validation 注解进行参数校验
- 使用 Java records 或 Lombok `@Data` 类

**示例**：
```java
public record CreateProductRequest(
    @NotBlank(message = "产品名称不能为空")
    @Size(min = 1, max = 100, message = "产品名称长度必须在1-100之间")
    String name,
    
    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.01", message = "价格必须大于0")
    BigDecimal price,
    
    @NotBlank(message = "分类不能为空")
    String category
) {}
```

### 响应 DTO（Response DTO）

- 只包含需要返回给前端的字段
- 避免直接返回实体类
- 使用统一的响应包装类（`Result<T>`）

**示例**：
```java
public record ProductDTO(
    Long id,
    String name,
    BigDecimal price,
    String category,
    LocalDateTime createTime
) {}
```

### DTO 转换器（Converter）

- 负责 Entity 和 DTO 之间的转换
- 可以使用 MapStruct 或手动实现
- 保持转换逻辑集中管理

详细模式见 [references/converter-pattern.md](references/converter-pattern.md)

## 最佳实践

### 1. 使用构造函数注入
```java
@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {
    private final ProductConverter productConverter;  // final 字段，不可变
}
```

### 2. 分离 Entity 和 DTO
- Entity 用于数据库映射
- DTO 用于 API 契约
- 使用 Converter 进行转换

### 3. 事务管理
- 查询方法：使用 `@Transactional(readOnly = true)`
- 写操作方法：使用 `@Transactional(rollbackFor = Exception.class)`

### 4. 分页查询
- 使用 PageHelper 进行分页
- 统一分页参数（page、size、sort）
- 返回统一的分页响应格式（`PageResult<T>`）

### 5. 异常处理
- 使用自定义业务异常
- 在全局异常处理器中统一处理
- 不要直接暴露数据库异常

### 6. 日志记录
- 使用 `@Slf4j` 注解
- 记录关键操作（创建、更新、删除）
- 敏感信息需要脱敏

## 约束和警告

### 1. 永远不要直接暴露 Entity
不要在 Controller 中直接返回 Entity，使用 DTO 来分离 API 契约和数据库模型。

### 2. 使用构造函数注入
避免字段注入（`@Autowired`）以获得更好的可测试性和明确的依赖声明。

### 3. 保持 Service 层可测试
Service 层应该不依赖 Spring 上下文即可实例化（通过构造函数注入）。

### 4. 事务边界要清晰
事务应该覆盖完整的业务操作，不要在事务内部调用其他 Service 的私有方法。

### 5. 不要吞掉异常
不要捕获异常后不处理，导致事务不回滚。让异常向上传播，由全局异常处理器统一处理。

### 6. 避免在循环中进行数据库操作
使用批量操作（`saveBatch`、`removeBatch`）优化性能。

## 详细参考

- **PageHelper 使用**：见 [references/pagehelper-usage.md](references/pagehelper-usage.md)
- **QueryWrapper 使用**：见 [references/query-wrapper.md](references/query-wrapper.md)
- **DTO 模式**：见 [references/dto-patterns.md](references/dto-patterns.md)
- **转换器模式**：见 [references/converter-pattern.md](references/converter-pattern.md)
- **完整示例**：见 [references/complete-example.md](references/complete-example.md)
- **代码模板**：见 `assets/` 目录

## 注意事项

1. **依赖配置**：确保添加 MyBatis-Plus 和 PageHelper 依赖
2. **配置扫描**：配置 Mapper 接口扫描路径（`@MapperScan`）
3. **分页插件**：配置 PageHelper 分页插件
4. **主键策略**：合理配置主键生成策略（`IdType.AUTO`、`IdType.ASSIGN_ID` 等）
5. **字段映射**：注意数据库字段与实体字段的映射关系（`@TableField`）
6. **逻辑删除**：使用 `@TableLogic` 实现逻辑删除
7. **自动填充**：使用 `@TableField(fill = FieldFill.INSERT)` 实现自动填充（创建时间、更新时间）
