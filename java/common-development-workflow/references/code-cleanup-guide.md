# 代码清理指南

## 清理目标

在代码修改完成后，必须清理以下无引用的代码：

1. **无引用的类**：删除未被使用的类
2. **无引用的方法**：删除未被调用的方法
3. **无引用的变量**：删除未被使用的变量、常量、字段
4. **无用的导入**：删除未被使用的 import 语句
5. **无用的注释**：清理过时或无效的注释

## 清理检查清单

### 1. 检查无引用的类

**检查方法**：
- 搜索类名是否在其他地方被引用
- 检查是否在 Spring 容器中注册
- 检查是否被反射调用

**示例**：

```java
// ❌ 无引用类（应该删除）
public class UnusedService {
    public void doSomething() {
        // ...
    }
}

// ✅ 有引用类（保留）
@Service
public class UserService {
    // ...
}
```

### 2. 检查无引用的方法

**检查方法**：
- 搜索方法名是否在其他地方被调用
- 检查是否是接口实现方法
- 检查是否是重写方法
- 检查是否被注解标记（如 @Override、@EventListener）

**示例**：

```java
public class UserService {
    
    // ❌ 无引用方法（应该删除）
    public void unusedMethod() {
        // ...
    }
    
    // ✅ 有引用方法（保留）
    @Autowired
    public User findById(Long id) {
        // ...
    }
    
    // ✅ 接口实现方法（保留）
    @Override
    public void save(User user) {
        // ...
    }
}
```

### 3. 检查无引用的变量

**检查方法**：
- 搜索变量名是否在代码中被使用
- 检查是否是常量（public static final）
- 检查是否被注解使用

**示例**：

```java
public class UserController {
    
    // ❌ 无引用变量（应该删除）
    private String unusedField;
    
    // ✅ 有引用变量（保留）
    private final UserService userService;
    
    // ✅ 常量（保留）
    private static final String DEFAULT_NAME = "User";
}
```

### 4. 检查无用的导入

**检查方法**：
- IDE 通常会自动标记未使用的导入
- 手动检查每个 import 语句

**示例**：

```java
// ❌ 无用导入（应该删除）
import java.util.ArrayList;  // 未使用
import java.util.List;       // 未使用

// ✅ 有用导入（保留）
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
```

### 5. 检查无用的注释

**检查方法**：
- 检查注释是否过期
- 检查 TODO、FIXME 注释是否已处理
- 删除过时的注释代码

**示例**：

```java
public class UserService {
    
    // ❌ 无用注释（应该删除）
    // TODO: 这个方法以后要改
    // 这个功能已经实现了
    
    // ✅ 有用注释（保留）
    /**
     * 根据 ID 查找用户
     * @param id 用户 ID
     * @return 用户对象
     */
    public User findById(Long id) {
        // ...
    }
}
```

## 清理工具

### IDE 自动清理

大多数 IDE 提供自动清理功能：

**IntelliJ IDEA**：
- `Code` → `Optimize Imports`（优化导入）
- `Code` → `Code Cleanup`（代码清理）
- `Code` → `Inspect Code`（代码检查）

**Eclipse**：
- `Source` → `Organize Imports`（组织导入）
- `Source` → `Clean Up`（清理代码）

### 命令行工具

**Maven 检查**：
```bash
mvn clean compile
# 查看编译警告中的未使用变量、方法等
```

**SpotBugs**（静态代码分析）：
```bash
mvn spotbugs:check
```

## 清理顺序

建议按照以下顺序进行清理：

1. **先清理无用的导入**（最简单，风险最低）
2. **再清理无引用的变量**（相对安全）
3. **然后清理无引用的方法**（需要仔细检查）
4. **最后清理无引用的类**（风险最高，需谨慎）

## 注意事项

### 1. 不要删除接口方法

即使实现类中方法未被调用，如果是接口方法，也不能删除。

```java
// ✅ 不能删除（接口方法）
@Override
public void save(User user) {
    // ...
}
```

### 2. 不要删除注解方法

即使方法未被调用，如果是注解标记的方法，也不能删除。

```java
// ✅ 不能删除（事件监听方法）
@EventListener
public void handleUserCreated(UserCreatedEvent event) {
    // ...
}
```

### 3. 不要删除测试代码

测试代码中的方法可能看起来未被使用，但它们是测试用例。

### 4. 谨慎删除公开 API

如果类是公开 API 的一部分，即使暂时未被使用，也不要删除。

### 5. 使用版本控制

清理前确保代码已提交到版本控制，以便必要时回滚。

## 清理验证

清理完成后，进行以下验证：

- [ ] 代码能够正常编译
- [ ] 所有测试用例通过
- [ ] 功能正常工作
- [ ] 没有引入新的问题
