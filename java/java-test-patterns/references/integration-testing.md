# 集成测试指南（Testcontainers）

## 关键点
- 使用 `@SpringBootTest` + `@Testcontainers`
- Spring Boot 3.5+ 推荐 `@ServiceConnection` 自动注入容器属性
- 将容器声明为 `static` 以 JVM 级复用

```java
@SpringBootTest
@Testcontainers
class ProductServiceIT {
    @Container @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
        .withDatabaseName("testdb");

    @Autowired ProductService productService;

    @Test void should_create_and_get() {
        ProductResponse created = productService.create(new CreateProductRequest("P", BigDecimal.TEN, "CAT"));
        assertThat(productService.getById(created.id()).name()).isEqualTo("P");
    }
}
```

## 性能
- 静态容器复用
- 避免不必要的 `@SpringBootTest`，能切片就切片
