# RESTful API 设计模式

## RESTful 路径设计

### 规范要求

- 使用名词复数形式，不使用动词
- 路径层级不超过 3 层
- 使用连字符 `-` 而非下划线
- API 版本化：`/api/v1/` 或 `/api/v2/`

### 示例

```
✅ 正确：
GET    /api/v1/users
GET    /api/v1/users/{id}
POST   /api/v1/users
PUT    /api/v1/users/{id}
DELETE /api/v1/users/{id}
GET    /api/v1/users/{id}/orders

❌ 错误：
GET    /api/getUser
POST   /api/createUser
GET    /api/user_list
```

## HTTP 状态码使用

### 规范要求

- `200 OK`：成功获取资源或更新资源
- `201 Created`：成功创建资源
- `204 No Content`：成功删除资源（无返回体）
- `400 Bad Request`：请求参数错误
- `401 Unauthorized`：未认证
- `403 Forbidden`：无权限
- `404 Not Found`：资源不存在
- `500 Internal Server Error`：服务器内部错误

## 分页查询规范

### 请求参数

```java
@Data
@Schema(description = "分页查询请求")
public class PageRequest {
    
    @Min(value = 1, message = "页码必须大于 0")
    @Schema(description = "页码，从 1 开始", example = "1", defaultValue = "1")
    private Integer page = 1;
    
    @Min(value = 1, message = "每页数量必须大于 0")
    @Max(value = 100, message = "每页数量不能超过 100")
    @Schema(description = "每页数量", example = "10", defaultValue = "10")
    private Integer size = 10;
    
    @Schema(description = "排序字段", example = "createTime")
    private String sortBy;
    
    @Schema(description = "排序方向", example = "desc", allowableValues = {"asc", "desc"})
    private String sortOrder = "desc";
}
```

### 响应格式

```java
@Data
@Schema(description = "分页响应")
public class PageResult<T> {
    
    @Schema(description = "数据列表")
    private List<T> content;
    
    @Schema(description = "总记录数")
    private Long total;
    
    @Schema(description = "当前页码")
    private Integer page;
    
    @Schema(description = "每页数量")
    private Integer size;
    
    @Schema(description = "总页数")
    private Integer totalPages;
}
```
