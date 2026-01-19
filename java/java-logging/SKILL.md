---
name: java-logging
description: 生成符合规范的 Java 日志记录代码，包括日志级别使用、日志格式、敏感信息脱敏、性能优化等最佳实践。使用场景：(1) 添加日志记录代码，(2) 优化现有日志，(3) 处理敏感信息脱敏，(4) 配置日志框架，(5) 需要遵循 SLF4J 和 Logback 最佳实践时
---

# Java 日志记录规范

## 快速开始

添加日志记录的基本步骤：

1. 在类上使用 `@Slf4j` 注解
2. 在方法入口、出口、异常处记录日志
3. 对敏感信息进行脱敏（使用 `assets/desensitization-utils.java`）
4. 配置日志框架（参考 `assets/logback-example.xml`）

## 日志框架选择

### 规范要求

- 使用 SLF4J 作为日志门面
- 使用 Logback 作为日志实现
- 使用 Lombok 的 `@Slf4j` 注解简化代码

### 代码使用

```java
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    
    public void someMethod() {
        log.info("这是一条信息日志");
        log.debug("这是一条调试日志");
        log.warn("这是一条警告日志");
        log.error("这是一条错误日志", exception);
    }
}
```

## 日志级别使用

### 规范要求

日志级别使用场景见 [references/log-levels.md](references/log-levels.md)

## 日志格式规范

### 规范要求

- 使用参数化日志，避免字符串拼接
- 包含关键业务信息
- 异常日志包含堆栈信息

详细规范见 [references/log-format.md](references/log-format.md)

## 敏感信息脱敏

### 规范要求

- 密码、token、密钥等敏感信息不能记录
- 身份证号、手机号、银行卡号等需要脱敏
- 使用脱敏工具类处理

脱敏工具类见 `assets/desensitization-utils.java`

### 使用示例

```java
@Override
public UserDTO create(CreateUserRequest request) {
    // ✅ 正确：敏感信息脱敏
    log.info("创建用户开始, username: {}, phone: {}, email: {}", 
            request.getUsername(),
            DesensitizationUtils.maskPhone(request.getPhone()),
            DesensitizationUtils.maskEmail(request.getEmail()));
    
    // ❌ 错误：直接记录敏感信息
    // log.info("创建用户, password: {}", request.getPassword());
}
```

## 日志配置

### Logback 配置

参考 `references/logback-example.xml` 配置文件。

### 配置要点

- 开发环境：控制台输出，DEBUG 级别
- 生产环境：文件输出，INFO 级别
- 日志文件按日期滚动
- 日志文件大小限制
- 保留历史日志天数

## MDC（Mapped Diagnostic Context）

### 规范要求

- 使用 MDC 存储请求追踪信息
- 在日志中自动包含 traceId、userId 等
- 请求结束时清除 MDC

## 详细参考

- **日志级别使用**：见 [references/log-levels.md](references/log-levels.md)
- **日志格式规范**：见 [references/log-format.md](references/log-format.md)
- **Logback 配置**：见 `assets/logback-example.xml`
- **脱敏工具类**：见 `assets/desensitization-utils.java`

## 注意事项

1. **性能影响**：避免在高频方法中记录过多日志
2. **日志级别**：生产环境使用 INFO 级别，避免 DEBUG
3. **敏感信息**：严格禁止记录密码、token 等敏感信息
4. **日志文件**：定期清理历史日志，避免磁盘空间不足
5. **异常堆栈**：ERROR 级别日志必须包含异常堆栈信息
