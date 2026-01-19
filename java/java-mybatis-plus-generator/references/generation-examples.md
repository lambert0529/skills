# 代码生成示例

## 完整生成示例

### 用户输入

```
为用户管理模块生成完整的 CRUD 代码，包括：
- Entity 实体类
- Mapper 接口和 XML
- Service 接口和实现
- Controller 层
- 使用 MyBatis-Plus 和 PageHelper
```

### 生成的文件

1. **User.java** - 实体类
   - 使用 `@TableName("user")`
   - 使用 `@TableId(type = IdType.AUTO)`
   - 包含 createTime、updateTime、deleted 字段

2. **UserMapper.java** - Mapper 接口
   - 继承 `BaseMapper<User>`

3. **UserMapper.xml** - Mapper XML
   - 基础结果映射
   - 自定义查询（如需要）

4. **UserService.java** - Service 接口
   - 继承 `IService<User>`
   - 定义业务方法

5. **UserServiceImpl.java** - Service 实现
   - 继承 `ServiceImpl<UserMapper, User>`
   - 使用 PageHelper 分页
   - 使用 LambdaQueryWrapper 构建查询

6. **UserController.java** - Controller 层
   - RESTful API 接口
   - 使用统一响应格式

## 配置示例

### MyBatis-Plus 配置

```java
@Configuration
public class MybatisPlusConfig {
    
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
```

### PageHelper 配置

```yaml
pagehelper:
  helper-dialect: mysql
  reasonable: true
  support-methods-arguments: true
  params: count=countSql
```

## 依赖配置

### Maven

```xml
<dependencies>
    <!-- MyBatis-Plus -->
    <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>mybatis-plus-boot-starter</artifactId>
        <version>3.5.5</version>
    </dependency>
    
    <!-- PageHelper -->
    <dependency>
        <groupId>com.github.pagehelper</groupId>
        <artifactId>pagehelper-spring-boot-starter</artifactId>
        <version>1.4.7</version>
    </dependency>
</dependencies>
```

### Gradle

```gradle
dependencies {
    implementation 'com.baomidou:mybatis-plus-boot-starter:3.5.5'
    implementation 'com.github.pagehelper:pagehelper-spring-boot-starter:1.4.7'
}
```
