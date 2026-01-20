---
name: java-test-patterns
description: 为 Spring Boot 应用编写健壮的测试套件，包括单元测试、集成测试、切片测试和基于容器的测试，使用 JUnit 5、Mockito、Testcontainers 和性能优化。使用场景：(1) 编写单元测试，(2) 实现集成测试，(3) 配置 Testcontainers 进行数据库测试，(4) 测试 REST API，(5) 优化测试性能
---

# Java 测试模式

## 快速开始

编写测试的基本步骤：

1. 单元测试：使用 Mockito 测试业务逻辑（见 [references/unit-testing.md](references/unit-testing.md)）
2. 切片测试：使用 `@DataJpaTest`、`@WebMvcTest` 进行分层测试（见 [references/slice-testing.md](references/slice-testing.md)）
3. 集成测试：使用 Testcontainers 进行真实数据库测试（见 [references/integration-testing.md](references/integration-testing.md)）
4. API 测试：使用 MockMvc 测试 REST 接口（见 [references/api-testing.md](references/api-testing.md)）

## 测试架构

### 1. 单元测试
- 快速、隔离的测试，无需 Spring 上下文
- 使用 Mockito 进行依赖注入
- 专注于业务逻辑验证
- 目标完成时间：< 50ms/测试

### 2. 切片测试
- 最小化 Spring 上下文加载，针对特定层
- 使用 `@DataJpaTest` 测试 Repository
- 使用 `@WebMvcTest` 测试 Controller
- 目标完成时间：< 100ms/测试

### 3. 集成测试
- 完整的 Spring 上下文和真实依赖
- 使用 `@SpringBootTest` 和 `@ServiceConnection` 容器
- 测试完整的应用流程
- 目标完成时间：< 500ms/测试

## 核心测试注解

### Spring Boot 测试注解
- `@SpringBootTest`：加载完整应用上下文（谨慎使用）
- `@DataJpaTest`：仅加载 JPA 组件（Repository、Entity）
- `@WebMvcTest`：仅加载 MVC 层（Controller、@ControllerAdvice）
- `@JsonTest`：仅加载 JSON 序列化组件

### Testcontainers 注解
- `@ServiceConnection`：将 Testcontainer 连接到 Spring Boot 测试（Spring Boot 3.5+）
- `@DynamicPropertySource`：在运行时注册动态属性
- `@Testcontainers`：启用 Testcontainers 生命周期管理

## 依赖配置

### Maven 依赖
```xml
<dependencies>
    <!-- Spring Boot Test Starter -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>

    <!-- Testcontainers -->
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>1.19.0</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>postgresql</artifactId>
        <version>1.19.0</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

## 测试模式示例

### 单元测试模式
```java
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserService userService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    void shouldFindUserByIdWhenExists() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setEmail("test@example.com");
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        
        // Act
        Optional<User> result = userService.findById(userId);
        
        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("test@example.com");
        verify(userRepository, times(1)).findById(userId);
    }
}
```

### 切片测试模式（Repository）
```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    void shouldSaveAndRetrieveUser() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");
        user.setName("Test User");
        
        // Act
        User saved = userRepository.save(user);
        userRepository.flush();
        
        Optional<User> retrieved = userRepository.findByEmail("test@example.com");
        
        // Assert
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getName()).isEqualTo("Test User");
    }
}
```

### 集成测试模式（Testcontainers）
```java
@SpringBootTest
@Testcontainers
class ProductServiceIntegrationTest {
    
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
        DockerImageName.parse("postgres:16-alpine"))
        .withDatabaseName("testdb");
    
    @Autowired
    private ProductService productService;
    
    @Test
    void shouldCreateAndRetrieveProduct() {
        // Given
        CreateProductRequest request = new CreateProductRequest(
            "Test Product",
            BigDecimal.valueOf(99.99),
            "Electronics"
        );
        
        // When
        ProductResponse created = productService.create(request);
        ProductResponse retrieved = productService.getById(created.id());
        
        // Then
        assertThat(retrieved)
            .isNotNull()
            .satisfies(product -> {
                assertThat(product.name()).isEqualTo("Test Product");
                assertThat(product.price()).isEqualByComparingTo("99.99");
            });
    }
}
```

### REST API 测试模式
```java
@WebMvcTest(ProductController.class)
class ProductControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private ProductService productService;
    
    @Test
    void shouldReturnProductWhenExists() throws Exception {
        // Given
        ProductResponse response = new ProductResponse(
            1L, "Test Product", BigDecimal.valueOf(99.99), "Electronics"
        );
        when(productService.getById(1L)).thenReturn(response);
        
        // When & Then
        mockMvc.perform(get("/api/v1/products/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Test Product"))
            .andExpect(jsonPath("$.price").value(99.99));
    }
}
```

## 最佳实践

### 1. 使用 @ServiceConnection 自动配置
```java
@Container
@ServiceConnection  // Spring Boot 3.5+ 自动配置
static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
    DockerImageName.parse("postgres:16-alpine"));
```

### 2. 使用静态容器实现 JVM 级别复用
```java
@Container
static PostgreSQLContainer<?> postgres = ...;  // static 关键字实现复用
```

### 3. 编写聚焦的测试，最小化上下文
- 使用 `@WebMvcTest`、`@DataJpaTest` 进行切片测试
- 避免不必要的 `@SpringBootTest`

### 4. 使用 @MockitoBean 模拟外部依赖
```java
@MockBean
private ExternalService externalService;  // 模拟外部服务
```

### 5. 清理测试数据
- 使用 `@Transactional` 自动回滚
- 或显式清理测试数据

## 约束和警告

### 1. 避免过度使用 @SpringBootTest
`@SpringBootTest` 加载完整上下文，速度较慢。优先使用切片测试。

### 2. 测试隔离
每个测试应该独立运行，不依赖其他测试的状态。

### 3. 测试命名
使用描述性的测试方法名，说明测试的场景和预期结果。

### 4. 测试数据管理
使用 `@Sql` 或 `@Transactional` 管理测试数据，确保测试可重复。

### 5. 性能优化
- 使用静态容器实现容器复用
- 使用 `@DirtiesContext` 谨慎，避免不必要的上下文重建
- 使用 `@TestPropertySource` 覆盖配置，而不是加载完整上下文

## 详细参考

- **单元测试**：见 [references/unit-testing.md](references/unit-testing.md)
- **切片测试**：见 [references/slice-testing.md](references/slice-testing.md)
- **集成测试**：见 [references/integration-testing.md](references/integration-testing.md)
- **API 测试**：见 [references/api-testing.md](references/api-testing.md)
- **测试模板**：见 `assets/` 目录

## 注意事项

1. **测试速度**：优先使用单元测试和切片测试，集成测试用于关键路径
2. **测试覆盖**：目标 80%+ 测试覆盖率，重点关注关键业务逻辑
3. **测试维护**：保持测试代码简洁，避免测试代码中的复杂逻辑
4. **CI/CD**：配置 CI/CD 管道运行集成测试，使用 Docker 环境
5. **测试数据**：使用工厂模式或 Builder 模式创建测试数据
