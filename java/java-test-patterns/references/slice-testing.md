# 切片测试指南（@DataJpaTest / @WebMvcTest）

## Repository 切片
- 注解：`@DataJpaTest`
- 避免替换真实 DB：`@AutoConfigureTestDatabase(replace = NONE)`
- 搭配 Testcontainers：`@Testcontainers` + `@ServiceConnection`

## Controller 切片
- 注解：`@WebMvcTest(Controller.class)`
- 模拟依赖：`@MockBean` 注入 Service
- MockMvc 断言 JSON：`jsonPath("$.id").value(1)`

```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {
    @Autowired UserRepository userRepository;

    @Test void should_save_and_find() {
        User u = new User(null, "t@example.com");
        userRepository.save(u);
        assertThat(userRepository.findByEmail("t@example.com")).isPresent();
    }
}
```
