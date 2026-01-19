# REST API 设计示例

## 使用示例

### 1. 创建用户管理 API

**请求**：
```
为用户管理创建 REST API，包括：
- 查询用户列表（分页）
- 根据 ID 查询用户详情
- 创建用户
- 更新用户
- 删除用户
```

**生成的代码**：
- `UserController.java` - Controller 层
- `CreateUserRequest.java` - 创建请求 DTO
- `UpdateUserRequest.java` - 更新请求 DTO
- `UserDTO.java` - 响应 DTO

### 2. RESTful 路径规范

```
✅ 正确示例：
GET    /api/v1/users
GET    /api/v1/users/{id}
POST   /api/v1/users
PUT    /api/v1/users/{id}
DELETE /api/v1/users/{id}
GET    /api/v1/users/{id}/orders

❌ 错误示例：
GET    /api/getUser
POST   /api/createUser
GET    /api/user_list
```

### 3. HTTP 状态码使用

- `200 OK` - 成功获取或更新资源
- `201 Created` - 成功创建资源
- `204 No Content` - 成功删除资源
- `400 Bad Request` - 请求参数错误
- `404 Not Found` - 资源不存在
- `500 Internal Server Error` - 服务器内部错误
