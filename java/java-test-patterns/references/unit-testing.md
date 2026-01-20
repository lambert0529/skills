# 单元测试指南（JUnit 5 + Mockito）

## 场景
- 纯业务逻辑，无需 Spring 上下文
- 依赖通过 Mockito mock 注入
- 重点验证分支与异常

## 断言与校验
- 使用 AssertJ：`assertThat(obj).isEqualTo(...)`
- 验证交互：`verify(repo, times(1)).save(...)`
- 异常断言：`assertThatThrownBy(() -> service.call()).isInstanceOf(...)`

## 示例
```java
class UserServiceTest {
    @Mock private UserRepository userRepository;
    @InjectMocks private UserService userService;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void should_find_user_by_id() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User(1L, "a@ex.com")));
        Optional<User> result = userService.findById(1L);
        assertThat(result).isPresent();
        verify(userRepository).findById(1L);
    }
}
```
