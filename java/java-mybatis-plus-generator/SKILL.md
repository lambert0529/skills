---
name: java-mybatis-plus-generator
description: 生成基于 MyBatis-Plus 和 PageHelper 的完整分层代码，包括 Controller、Service、Dao、Mapper 层。使用场景：(1) 创建基于 MyBatis-Plus 的 CRUD 操作，(2) 生成分页查询功能，(3) 实现 Service 层和 DAO 层代码，(4) 生成 Mapper XML 文件，(5) 需要遵循 MyBatis-Plus 和 PageHelper 最佳实践时
---

# MyBatis-Plus 代码生成规范

## 快速开始

生成完整分层代码的基本步骤：

1. 创建实体类（Entity）
2. 创建 Mapper 接口（使用 MyBatis-Plus BaseMapper）
3. 创建 Mapper XML（如需要自定义 SQL）
4. 创建 Service 接口和实现（使用 MyBatis-Plus IService）
5. 创建 Controller 层（使用 PageHelper 分页）

## 实体类（Entity）

### 规范要求

- 使用 `@TableName` 指定表名
- 使用 `@TableId` 指定主键
- 使用 `@TableField` 指定字段映射
- 继承或实现必要的接口

代码模板见 `assets/EntityTemplate.java`

## Mapper 层

### Mapper 接口

- 继承 `BaseMapper<T>` 获得基础 CRUD 方法
- 定义自定义查询方法（如需要）

代码模板见 `assets/MapperInterfaceTemplate.java`

### Mapper XML

- 定义自定义 SQL 查询
- 使用 MyBatis-Plus 标签简化配置

代码模板见 `assets/MapperXmlTemplate.xml`

## Service 层

### Service 接口

- 继承 `IService<T>` 获得基础服务方法
- 定义业务方法

代码模板见 `assets/ServiceInterfaceTemplate.java`

### Service 实现

- 继承 `ServiceImpl<M, T>` 实现接口
- 实现业务逻辑

代码模板见 `assets/ServiceImplTemplate.java`

## Controller 层

### 规范要求

- 使用 PageHelper 进行分页
- 使用 MyBatis-Plus 的查询条件构建器
- 返回统一响应格式

代码模板见 `assets/ControllerTemplate.java`

## 分页查询

### PageHelper 使用

- 在查询前调用 `PageHelper.startPage(page, size)`
- 查询后自动封装分页信息

详细使用见 [references/pagehelper-usage.md](references/pagehelper-usage.md)

## MyBatis-Plus 查询条件

### QueryWrapper 使用

- 构建动态查询条件
- 支持链式调用

详细使用见 [references/query-wrapper.md](references/query-wrapper.md)

## 详细参考

- **PageHelper 使用**：见 [references/pagehelper-usage.md](references/pagehelper-usage.md)
- **QueryWrapper 使用**：见 [references/query-wrapper.md](references/query-wrapper.md)
- **代码生成示例**：见 [references/generation-examples.md](references/generation-examples.md)
- **代码模板**：见 `assets/` 目录

## 注意事项

1. **依赖配置**：确保添加 MyBatis-Plus 和 PageHelper 依赖
2. **配置扫描**：配置 Mapper 接口扫描路径
3. **分页插件**：配置 PageHelper 分页插件
4. **主键策略**：合理配置主键生成策略
5. **字段映射**：注意数据库字段与实体字段的映射关系
