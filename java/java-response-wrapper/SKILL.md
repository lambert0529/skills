---
name: java-response-wrapper
description: 生成符合规范的 Java 统一响应格式包装类，包括成功响应、失败响应、分页响应等标准格式
version: 1.0.0
tags: [java, spring-boot, response, dto, api-design]
---

# Java 统一响应格式规范

## 描述

本技能用于生成符合主流 Java 后端开发规范的统一响应格式，包括：
- 统一响应包装类
- 成功响应格式
- 失败响应格式
- 分页响应格式
- 响应状态码定义

## 触发条件

当用户需要：
- 设计统一响应格式
- 创建响应包装类
- 实现分页响应
- 统一 API 返回格式

## 核心规范

### 1. 统一响应包装类

**规范要求**：
- 所有 API 响应使用统一的格式
- 包含：状态码、消息、数据、时间戳
- 提供静态工厂方法
- 支持泛型

**代码示例**：
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "统一响应格式")
public class Result<T> {
    
    @Schema(description = "响应码，200 表示成功，其他表示失败", example = "200")
    private Integer code;
    
    @Schema(description = "响应消息", example = "操作成功")
    private String message;
    
    @Schema(description = "响应数据")
    private T data;
    
    @Schema(description = "响应时间戳（毫秒）", example = "1640995200000")
    private Long timestamp;
    
    @Schema(description = "请求追踪ID（可选）", example = "trace-123456")
    private String traceId;
    
    /**
     * 成功响应（无数据）
     */
    public static <T> Result<T> success() {
        return Result.<T>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .message(ErrorCode.SUCCESS.getMessage())
                .timestamp(System.currentTimeMillis())
                .build();
    }
    
    /**
     * 成功响应（带数据）
     */
    public static <T> Result<T> success(T data) {
        return Result.<T>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .message(ErrorCode.SUCCESS.getMessage())
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build();
    }
    
    /**
     * 成功响应（自定义消息）
     */
    public static <T> Result<T> success(String message, T data) {
        return Result.<T>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .message(message)
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build();
    }
    
    /**
     * 失败响应
     */
    public static <T> Result<T> failure(Integer code, String message) {
        return Result.<T>builder()
                .code(code)
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build();
    }
    
    /**
     * 失败响应（使用错误码）
     */
    public static <T> Result<T> failure(ErrorCode errorCode) {
        return Result.<T>builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .timestamp(System.currentTimeMillis())
                .build();
    }
    
    /**
     * 失败响应（自定义消息）
     */
    public static <T> Result<T> failure(ErrorCode errorCode, String message) {
        return Result.<T>builder()
                .code(errorCode.getCode())
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build();
    }
    
    /**
     * 判断是否成功
     */
    public boolean isSuccess() {
        return ErrorCode.SUCCESS.getCode().equals(this.code);
    }
}
```

### 2. 分页响应格式

**规范要求**：
- 包含数据列表、总数、页码、每页数量、总页数
- 提供便捷的构建方法
- 支持从 Spring Data 的 Page 对象转换

**代码示例**：
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "分页响应")
public class PageResult<T> {
    
    @Schema(description = "数据列表")
    private List<T> content;
    
    @Schema(description = "总记录数", example = "100")
    private Long total;
    
    @Schema(description = "当前页码（从 1 开始）", example = "1")
    private Integer page;
    
    @Schema(description = "每页数量", example = "10")
    private Integer size;
    
    @Schema(description = "总页数", example = "10")
    private Integer totalPages;
    
    @Schema(description = "是否有上一页", example = "false")
    private Boolean hasPrevious;
    
    @Schema(description = "是否有下一页", example = "true")
    private Boolean hasNext;
    
    @Schema(description = "是否为第一页", example = "true")
    private Boolean isFirst;
    
    @Schema(description = "是否为最后一页", example = "false")
    private Boolean isLast;
    
    /**
     * 从 Spring Data Page 对象构建
     */
    public static <T> PageResult<T> of(Page<T> page, Integer pageNumber) {
        return PageResult.<T>builder()
                .content(page.getContent())
                .total(page.getTotalElements())
                .page(pageNumber)
                .size(page.getSize())
                .totalPages(page.getTotalPages())
                .hasPrevious(page.hasPrevious())
                .hasNext(page.hasNext())
                .isFirst(page.isFirst())
                .isLast(page.isLast())
                .build();
    }
    
    /**
     * 从列表和总数构建
     */
    public static <T> PageResult<T> of(List<T> content, Long total, 
                                       Integer page, Integer size) {
        int totalPages = (int) Math.ceil((double) total / size);
        return PageResult.<T>builder()
                .content(content)
                .total(total)
                .page(page)
                .size(size)
                .totalPages(totalPages)
                .hasPrevious(page > 1)
                .hasNext(page < totalPages)
                .isFirst(page == 1)
                .isLast(page >= totalPages)
                .build();
    }
    
    /**
     * 空分页结果
     */
    public static <T> PageResult<T> empty(Integer page, Integer size) {
        return PageResult.<T>builder()
                .content(Collections.emptyList())
                .total(0L)
                .page(page)
                .size(size)
                .totalPages(0)
                .hasPrevious(false)
                .hasNext(false)
                .isFirst(true)
                .isLast(true)
                .build();
    }
}
```

### 3. Controller 层使用

**规范要求**：
- 所有 Controller 方法返回 `ResponseEntity<Result<T>>`
- 使用 `Result.success()` 或 `Result.failure()` 构建响应
- HTTP 状态码与业务状态码分离

**代码示例**：
```java
@RestController
@RequestMapping("/api/v1/users")
@Slf4j
public class UserController {
    
    /**
     * 查询单个用户
     */
    @GetMapping("/{id}")
    public ResponseEntity<Result<UserDTO>> getUser(@PathVariable Long id) {
        log.info("查询用户, id: {}", id);
        UserDTO user = userService.getById(id);
        return ResponseEntity.ok(Result.success(user));
    }
    
    /**
     * 创建用户
     */
    @PostMapping
    public ResponseEntity<Result<UserDTO>> createUser(
            @Valid @RequestBody CreateUserRequest request) {
        log.info("创建用户, request: {}", request);
        UserDTO user = userService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Result.success("用户创建成功", user));
    }
    
    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    public ResponseEntity<Result<UserDTO>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        log.info("更新用户, id: {}, request: {}", id, request);
        UserDTO user = userService.update(id, request);
        return ResponseEntity.ok(Result.success("用户更新成功", user));
    }
    
    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Result<Void>> deleteUser(@PathVariable Long id) {
        log.info("删除用户, id: {}", id);
        userService.delete(id);
        return ResponseEntity.ok(Result.success("用户删除成功"));
    }
    
    /**
     * 分页查询用户列表
     */
    @GetMapping
    public ResponseEntity<Result<PageResult<UserDTO>>> listUsers(
            @Valid PageRequest pageRequest) {
        log.info("分页查询用户, pageRequest: {}", pageRequest);
        PageResult<UserDTO> pageResult = userService.list(pageRequest);
        return ResponseEntity.ok(Result.success(pageResult));
    }
}
```

### 4. 响应格式示例

**成功响应（单个对象）**：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "zhangsan",
    "email": "zhangsan@example.com",
    "createTime": "2024-01-01T10:00:00"
  },
  "timestamp": 1704067200000,
  "traceId": "trace-123456"
}
```

**成功响应（列表）**：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "username": "zhangsan",
      "email": "zhangsan@example.com"
    },
    {
      "id": 2,
      "username": "lisi",
      "email": "lisi@example.com"
    }
  ],
  "timestamp": 1704067200000
}
```

**成功响应（分页）**：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "content": [
      {
        "id": 1,
        "username": "zhangsan",
        "email": "zhangsan@example.com"
      }
    ],
    "total": 100,
    "page": 1,
    "size": 10,
    "totalPages": 10,
    "hasPrevious": false,
    "hasNext": true,
    "isFirst": true,
    "isLast": false
  },
  "timestamp": 1704067200000
}
```

**失败响应**：
```json
{
  "code": 1001,
  "message": "用户不存在",
  "data": null,
  "timestamp": 1704067200000,
  "traceId": "trace-123456"
}
```

### 5. 响应工具类（可选）

**代码示例**：
```java
public class ResponseUtils {
    
    /**
     * 成功响应
     */
    public static <T> ResponseEntity<Result<T>> ok(T data) {
        return ResponseEntity.ok(Result.success(data));
    }
    
    /**
     * 成功响应（自定义消息）
     */
    public static <T> ResponseEntity<Result<T>> ok(String message, T data) {
        return ResponseEntity.ok(Result.success(message, data));
    }
    
    /**
     * 创建成功响应
     */
    public static <T> ResponseEntity<Result<T>> created(T data) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Result.success("创建成功", data));
    }
    
    /**
     * 无内容响应
     */
    public static <T> ResponseEntity<Result<T>> noContent() {
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(Result.success());
    }
    
    /**
     * 失败响应
     */
    public static <T> ResponseEntity<Result<T>> failure(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(Result.failure(errorCode));
    }
    
    /**
     * 失败响应（自定义消息）
     */
    public static <T> ResponseEntity<Result<T>> failure(ErrorCode errorCode, String message) {
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(Result.failure(errorCode, message));
    }
}
```

## 使用方法

1. **创建响应类**：创建 `Result` 和 `PageResult` 类
2. **在 Controller 中使用**：所有方法返回 `ResponseEntity<Result<T>>`
3. **统一格式**：确保所有 API 使用相同的响应格式

## 示例

**用户输入**：
```
设计统一响应格式，包括：
- 成功响应（单个对象、列表、分页）
- 失败响应
- 包含状态码、消息、数据、时间戳
```

**生成内容**：
- `Result.java` - 统一响应包装类
- `PageResult.java` - 分页响应类
- `ResponseUtils.java` - 响应工具类（可选）

## 注意事项

1. **HTTP 状态码**：HTTP 状态码表示请求处理状态，业务状态码表示业务结果
2. **时间戳格式**：使用毫秒时间戳，前端可根据需要转换
3. **空值处理**：成功时 data 可以为 null，失败时建议为 null
4. **分页参数**：页码从 1 开始，而非 0
5. **向后兼容**：响应格式变更时考虑向后兼容性
