# API 测试指南（MockMvc）

## 关键点
- 注解：`@WebMvcTest(Controller.class)` + `@AutoConfigureMockMvc`
- 模拟依赖：`@MockBean` Service
- 断言：状态码、JSON 字段、Header

```java
@WebMvcTest(ProductController.class)
class ProductControllerTest {
    @Autowired private MockMvc mockMvc;
    @MockBean private ProductService productService;

    @Test
    void should_return_product() throws Exception {
        when(productService.getById(1L))
            .thenReturn(new ProductResponse(1L, "Book", BigDecimal.TEN, "CAT"));

        mockMvc.perform(get("/api/v1/products/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Book"));
    }
}
```

## 常见检查
- 200/201/204 等状态码
- Location / X-Total-Count 等响应头
- 错误返回体结构（错误码、信息、timestamp）
