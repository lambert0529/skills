# 实施文档模板

## 标准实施文档格式

```markdown
## 实施文档

### 实施步骤

#### 步骤 1：[步骤名称]
- **操作**：[具体操作内容]
- **文件**：[涉及的文件路径]
- **内容**：[详细的修改或创建内容]
- **注意事项**：[需要特别注意的事项]

#### 步骤 2：[步骤名称]
[同上格式]

### 代码修改清单

#### 新建文件
- **[文件路径 1]**
  - 用途：[文件用途说明]
  - 关键内容：[列出关键类、方法或配置]
  
- **[文件路径 2]**
  [同上格式]

#### 修改文件
- **[文件路径 1]**
  - 修改点 1：[具体修改位置和内容]
  - 修改点 2：[具体修改位置和内容]
  
- **[文件路径 2]**
  [同上格式]

#### 删除文件
- **[文件路径]**：[删除原因]

### 代码规范
[列出需要遵循的代码规范和约定]

### 优化计划
#### 代码清理
- [ ] 清理无引用的类
- [ ] 清理无引用的方法
- [ ] 清理无引用的变量
- [ ] 清理无用的导入

#### 代码重构
- [ ] 优化代码结构
- [ ] 提高代码可读性
- [ ] 添加必要的注释
- [ ] 统一代码风格

### 编译验证计划
- [ ] 检查编译错误
- [ ] 检查编译警告
- [ ] 验证依赖关系
- [ ] 验证代码语法

### 测试计划
[如果需要，列出测试计划]

### 回滚方案
[如果出现问题，说明如何回滚]
```

## 实施文档示例

```markdown
## 实施文档

### 实施步骤

#### 步骤 1：创建 JWT Token 服务
- **操作**：创建 JwtTokenService 类
- **文件**：`src/main/java/com/example/auth/JwtTokenService.java`
- **内容**：
  - 实现 Token 生成方法（generateAccessToken、generateRefreshToken）
  - 实现 Token 验证方法（validateToken）
  - 实现 Token 解析方法（getUsernameFromToken、getAuthoritiesFromToken）
- **注意事项**：
  - 使用 HMAC SHA-256 算法
  - Token 过期时间配置在 application.yml

#### 步骤 2：创建认证服务
- **操作**：创建 AuthService 类
- **文件**：`src/main/java/com/example/auth/AuthService.java`
- **内容**：
  - 实现登录方法（login）
  - 实现注册方法（register）
  - 实现刷新 Token 方法（refreshToken）
- **注意事项**：
  - 使用 BCrypt 加密密码
  - 验证用户凭证

#### 步骤 3：创建认证 Controller
- **操作**：创建 AuthController 类
- **文件**：`src/main/java/com/example/auth/AuthController.java`
- **内容**：
  - 实现 POST `/api/v1/auth/login` 端点
  - 实现 POST `/api/v1/auth/register` 端点
  - 实现 POST `/api/v1/auth/refresh` 端点
- **注意事项**：
  - 使用统一响应格式（Result）
  - 参数校验使用 @Valid

#### 步骤 4：更新 Security 配置
- **操作**：更新 SecurityConfig 类
- **文件**：`src/main/java/com/example/config/SecurityConfig.java`
- **内容**：
  - 添加 JwtAuthenticationFilter 到过滤器链
  - 配置认证端点允许匿名访问
- **注意事项**：
  - 确保过滤器顺序正确
  - 配置 CORS 策略

### 代码修改清单

#### 新建文件
- **`src/main/java/com/example/auth/JwtTokenService.java`**
  - 用途：JWT Token 生成和验证服务
  - 关键内容：TokenService 类、Token 生成和验证方法
  
- **`src/main/java/com/example/auth/AuthService.java`**
  - 用途：用户认证服务
  - 关键内容：AuthService 接口和实现、登录和注册方法
  
- **`src/main/java/com/example/auth/AuthController.java`**
  - 用途：用户认证 Controller
  - 关键内容：登录、注册、刷新 Token 端点

#### 修改文件
- **`src/main/java/com/example/config/SecurityConfig.java`**
  - 修改点 1：添加 JwtAuthenticationFilter Bean
  - 修改点 2：配置过滤器链，添加 JWT 过滤器
  - 修改点 3：配置认证端点允许匿名访问

### 代码规范
- 遵循 Spring Boot 最佳实践
- 使用 Lombok 简化代码
- 使用统一的异常处理
- 使用统一的响应格式
- 添加必要的日志

### 优化计划
#### 代码清理
- [ ] 检查是否有未使用的导入
- [ ] 检查是否有未使用的变量
- [ ] 检查是否有未使用的方法

#### 代码重构
- [ ] 确保代码风格一致
- [ ] 添加必要的注释
- [ ] 优化方法命名

### 编译验证计划
- [ ] 执行 Maven 编译（mvn clean compile）
- [ ] 检查编译错误
- [ ] 检查编译警告
- [ ] 验证依赖是否正确

### 测试计划
- 手动测试登录功能
- 手动测试注册功能
- 手动测试 Token 刷新功能

### 回滚方案
如果出现问题，可以删除新创建的文件，并恢复 SecurityConfig 的修改。
```
