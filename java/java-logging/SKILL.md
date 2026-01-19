---
name: java-logging
description: 生成符合规范的 Java 日志记录代码，包括日志级别使用、日志格式、敏感信息脱敏、性能优化等最佳实践
version: 1.0.0
tags: [java, spring-boot, logging, slf4j, logback]
---

# Java 日志记录规范

## 描述

本技能用于生成符合主流 Java 后端开发规范的日志记录代码，包括：
- 日志框架选择（SLF4J + Logback）
- 日志级别使用规范
- 日志格式规范
- 敏感信息脱敏
- 性能优化
- 结构化日志

## 触发条件

当用户需要：
- 添加日志记录代码
- 优化现有日志
- 处理敏感信息脱敏
- 配置日志框架

## 核心规范

### 1. 日志框架选择

**规范要求**：
- 使用 SLF4J 作为日志门面
- 使用 Logback 作为日志实现
- 使用 Lombok 的 `@Slf4j` 注解简化代码
- 避免直接使用 Log4j、Log4j2 等

**依赖配置**：
```xml
<!-- Spring Boot 默认包含，无需额外配置 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
</dependency>

<!-- 或单独引入 -->
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
</dependency>
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
</dependency>
```

**代码使用**：
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

### 2. 日志级别使用规范

**日志级别（从低到高）**：
- `TRACE`：最详细的调试信息，通常不使用
- `DEBUG`：详细的调试信息，开发环境使用
- `INFO`：关键业务流程信息，生产环境使用
- `WARN`：警告信息，需要关注但不影响运行
- `ERROR`：错误信息，需要立即处理

**使用场景**：

| 场景 | 日志级别 | 示例 |
|------|---------|------|
| 方法入口 | INFO | `log.info("查询用户开始, id: {}", id)` |
| 方法出口（成功） | INFO | `log.info("查询用户成功, id: {}", id)` |
| 业务校验失败 | WARN | `log.warn("用户名已存在, username: {}", username)` |
| 参数异常 | WARN | `log.warn("参数错误, param: {}", param)` |
| 异常捕获 | ERROR | `log.error("查询用户异常, id: {}", id, e)` |
| 系统错误 | ERROR | `log.error("系统异常", e)` |
| 详细调试信息 | DEBUG | `log.debug("SQL 执行, sql: {}", sql)` |

**代码示例**：
```java
@Slf4j
@Service
public class UserServiceImpl implements UserService {
    
    @Override
    public UserDTO getById(Long id) {
        log.info("查询用户开始, id: {}", id);
        
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("用户不存在, id: {}", id);
                        return new BusinessException(ErrorCode.USER_NOT_FOUND);
                    });
            
            log.info("查询用户成功, id: {}, username: {}", id, user.getUsername());
            return userConverter.toDTO(user);
            
        } catch (BusinessException e) {
            log.warn("查询用户失败, id: {}, error: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("查询用户异常, id: {}", id, e);
            throw new SystemException(ErrorCode.SYSTEM_ERROR, e);
        }
    }
    
    @Override
    public UserDTO create(CreateUserRequest request) {
        log.info("创建用户开始, username: {}", request.getUsername());
        
        try {
            // 业务逻辑...
            log.debug("用户数据准备完成, username: {}", request.getUsername());
            
            User savedUser = userRepository.save(user);
            log.info("创建用户成功, id: {}, username: {}", 
                    savedUser.getId(), savedUser.getUsername());
            
            return userConverter.toDTO(savedUser);
            
        } catch (BusinessException e) {
            log.warn("创建用户失败, username: {}, error: {}", 
                    request.getUsername(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("创建用户异常, username: {}", request.getUsername(), e);
            throw new SystemException(ErrorCode.SYSTEM_ERROR, e);
        }
    }
}
```

### 3. 日志格式规范

**规范要求**：
- 使用参数化日志，避免字符串拼接
- 包含关键业务信息（ID、用户名等）
- 异常日志包含堆栈信息
- 避免日志过长

**正确示例**：
```java
// ✅ 正确：使用参数化日志
log.info("查询用户, id: {}, username: {}", id, username);

// ✅ 正确：异常日志包含堆栈
log.error("查询用户异常, id: {}", id, e);

// ✅ 正确：包含关键业务信息
log.info("订单创建成功, orderId: {}, userId: {}, amount: {}", 
        orderId, userId, amount);
```

**错误示例**：
```java
// ❌ 错误：字符串拼接
log.info("查询用户, id: " + id + ", username: " + username);

// ❌ 错误：缺少异常堆栈
log.error("查询用户异常, id: " + id + ", error: " + e.getMessage());

// ❌ 错误：日志过长
log.info("查询用户, 完整对象: " + user.toString());
```

### 4. 敏感信息脱敏

**规范要求**：
- 密码、token、密钥等敏感信息不能记录
- 身份证号、手机号、银行卡号等需要脱敏
- 使用脱敏工具类处理

**脱敏工具类示例**：
```java
public class DesensitizationUtils {
    
    /**
     * 手机号脱敏：138****8888
     */
    public static String maskPhone(String phone) {
        if (phone == null || phone.length() < 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }
    
    /**
     * 身份证号脱敏：110101********1234
     */
    public static String maskIdCard(String idCard) {
        if (idCard == null || idCard.length() < 8) {
            return idCard;
        }
        return idCard.substring(0, 6) + "********" + idCard.substring(idCard.length() - 4);
    }
    
    /**
     * 邮箱脱敏：zh****@example.com
     */
    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        String[] parts = email.split("@");
        if (parts[0].length() <= 2) {
            return "****@" + parts[1];
        }
        return parts[0].substring(0, 2) + "****@" + parts[1];
    }
    
    /**
     * 银行卡号脱敏：6222 **** **** 1234
     */
    public static String maskBankCard(String bankCard) {
        if (bankCard == null || bankCard.length() < 8) {
            return bankCard;
        }
        return bankCard.substring(0, 4) + " **** **** " + 
               bankCard.substring(bankCard.length() - 4);
    }
}
```

**使用示例**：
```java
@Slf4j
@Service
public class UserServiceImpl implements UserService {
    
    @Override
    public UserDTO create(CreateUserRequest request) {
        // ✅ 正确：敏感信息脱敏
        log.info("创建用户开始, username: {}, phone: {}, email: {}", 
                request.getUsername(),
                DesensitizationUtils.maskPhone(request.getPhone()),
                DesensitizationUtils.maskEmail(request.getEmail()));
        
        // ❌ 错误：直接记录敏感信息
        // log.info("创建用户, password: {}", request.getPassword());
        
        // 业务逻辑...
    }
    
    @Override
    public void login(LoginRequest request) {
        // ✅ 正确：不记录密码
        log.info("用户登录, username: {}", request.getUsername());
        
        // ❌ 错误：记录密码
        // log.info("用户登录, username: {}, password: {}", 
        //         request.getUsername(), request.getPassword());
    }
}
```

### 5. 日志配置（logback-spring.xml）

**规范要求**：
- 开发环境：控制台输出，DEBUG 级别
- 生产环境：文件输出，INFO 级别
- 日志文件按日期滚动
- 日志文件大小限制
- 保留历史日志天数

**配置示例**：
```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <include resource="org/springframework/boot/logging/logback/defaults.xml"/>
    
    <!-- 控制台输出 -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n</pattern>
            <charset>UTF-8</charset>
        </encoder>
    </appender>
    
    <!-- 文件输出 -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/application.log</file>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n</pattern>
            <charset>UTF-8</charset>
        </encoder>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/application.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
            <totalSizeCap>10GB</totalSizeCap>
        </rollingPolicy>
    </appender>
    
    <!-- 错误日志单独输出 -->
    <appender name="ERROR_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/error.log</file>
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <level>ERROR</level>
        </filter>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n</pattern>
            <charset>UTF-8</charset>
        </encoder>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/error.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>90</maxHistory>
        </rollingPolicy>
    </appender>
    
    <!-- 日志级别配置 -->
    <springProfile name="dev">
        <root level="DEBUG">
            <appender-ref ref="CONSOLE"/>
        </root>
    </springProfile>
    
    <springProfile name="prod">
        <root level="INFO">
            <appender-ref ref="FILE"/>
            <appender-ref ref="ERROR_FILE"/>
        </root>
    </springProfile>
    
    <!-- 第三方库日志级别 -->
    <logger name="org.springframework" level="INFO"/>
    <logger name="org.mybatis" level="DEBUG"/>
    <logger name="com.example" level="DEBUG"/>
</configuration>
```

### 6. 结构化日志（可选）

**规范要求**：
- 使用 JSON 格式输出日志
- 便于日志收集和分析（ELK、Loki 等）
- 包含 traceId、spanId 等追踪信息

**配置示例**：
```xml
<appender name="JSON_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <file>logs/application.json</file>
    <encoder class="net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder">
        <providers>
            <timestamp/>
            <version/>
            <logLevel/>
            <message/>
            <mdc/>
            <stackTrace/>
        </providers>
    </encoder>
    <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
        <fileNamePattern>logs/application.%d{yyyy-MM-dd}.json</fileNamePattern>
        <maxHistory>30</maxHistory>
    </rollingPolicy>
</appender>
```

### 7. MDC（Mapped Diagnostic Context）

**规范要求**：
- 使用 MDC 存储请求追踪信息
- 在日志中自动包含 traceId、userId 等
- 请求结束时清除 MDC

**使用示例**：
```java
@Slf4j
@Component
public class LoggingFilter implements Filter {
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, 
                        FilterChain chain) throws IOException, ServletException {
        try {
            // 设置 traceId
            String traceId = UUID.randomUUID().toString();
            MDC.put("traceId", traceId);
            MDC.put("requestId", traceId);
            
            // 设置用户ID（如果已认证）
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            String userId = getUserId(httpRequest);
            if (userId != null) {
                MDC.put("userId", userId);
            }
            
            chain.doFilter(request, response);
        } finally {
            // 清除 MDC
            MDC.clear();
        }
    }
}
```

**日志格式包含 MDC**：
```xml
<pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] [%X{traceId}] [%X{userId}] %-5level %logger{50} - %msg%n</pattern>
```

## 使用方法

1. **添加日志注解**：在类上使用 `@Slf4j`
2. **记录关键信息**：在方法入口、出口、异常处记录日志
3. **脱敏处理**：对敏感信息进行脱敏
4. **配置日志**：配置 logback-spring.xml

## 示例

**用户输入**：
```
为 UserService 添加日志记录，包括：
- 方法入口和出口
- 异常处理
- 敏感信息脱敏（手机号、邮箱）
```

**生成内容**：
- 更新 Service 实现类，添加日志记录
- 创建脱敏工具类（如需要）
- 配置日志文件（如需要）

## 注意事项

1. **性能影响**：避免在高频方法中记录过多日志
2. **日志级别**：生产环境使用 INFO 级别，避免 DEBUG
3. **敏感信息**：严格禁止记录密码、token 等敏感信息
4. **日志文件**：定期清理历史日志，避免磁盘空间不足
5. **异常堆栈**：ERROR 级别日志必须包含异常堆栈信息
