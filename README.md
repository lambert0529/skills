# Skills Collection

> 遵循 Anthropic Skills 开放标准的 AI Agent Skills 集合，用于 Java 后端开发。

[English](./README.en.md) | 中文

---

## 📖 项目简介

本仓库包含遵循 [Anthropic Skills 开放标准](https://github.com/anthropics/skills) 的 **Agent Skills（技能）** 集合。这些技能旨在帮助 AI 编程助手（Cursor、Claude Code、Windsurf、Aider 等）生成符合行业最佳实践的高质量 Java 后端代码。

## ✨ 特性

- 🌐 **开放标准**：遵循 Anthropic Skills 规范，跨平台兼容
- 📦 **渐进式加载**：按需加载技能，节省上下文窗口空间
- 🔄 **可复用**：可在多个项目和 AI 编程助手间共享
- 📚 **全面覆盖**：涵盖 Java 后端开发的核心方面
- 🎯 **最佳实践**：基于 Spring Boot 和行业标准

## 📦 可用的 Skills

### 1. `java-rest-api-design`
**RESTful API 设计规范**
- RESTful 路径设计
- Controller 层代码结构
- 请求/响应 DTO 设计
- HTTP 状态码使用
- API 版本化策略

### 2. `java-service-layer`
**Service 层开发规范**
- 接口与实现分离
- 事务管理规范
- 业务逻辑封装
- 异常处理
- 日志记录

### 3. `java-exception-handling`
**异常处理规范**
- 自定义业务异常类
- 全局异常处理器
- 错误码定义
- 异常响应格式
- 异常日志记录

### 4. `java-validation`
**参数校验规范**
- Bean Validation 注解使用
- 自定义校验器
- 分组校验
- 嵌套对象校验
- 校验错误处理

### 5. `java-response-wrapper`
**统一响应格式规范**
- 统一响应包装类
- 成功响应格式
- 失败响应格式
- 分页响应格式
- 响应状态码定义

### 6. `java-logging`
**日志记录规范**
- 日志级别使用
- 日志格式规范
- 敏感信息脱敏
- 性能优化
- 结构化日志

## 🚀 快速开始

### 前置要求

- Node.js ≥ v20.6
- Git（用于从 GitHub 安装技能）

### 安装 OpenSkills CLI

```bash
# 全局安装 OpenSkills CLI
npm install -g openskills

# 验证安装
openskills --version
```

### 安装方式

#### 方式一：从 GitHub 直接安装（推荐）

项目已推送到 GitHub，可以直接从 GitHub 安装：

```bash
# 在你的项目根目录执行
cd /path/to/your/project

# 从 GitHub 仓库安装所有 Java 技能
openskills install lambert0529/skills/java --universal

# 或者安装整个仓库的技能（如果仓库根目录包含技能）
openskills install lambert0529/skills --universal
```

**注意**：如果技能在仓库的子目录（如 `java/`），OpenSkills 会尝试从该路径查找技能。如果仓库根目录直接包含技能目录，可以直接安装整个仓库。

#### 方式二：从本地路径安装

1. **克隆仓库**：
```bash
git clone https://github.com/lambert0529/skills.git
cd skills
```

2. **安装技能**：
```bash
# 安装 java 目录下的所有技能到项目
openskills install ./java --universal

# 或者安装单个技能
openskills install ./java/java-rest-api-design --universal
```

#### 方式三：全局安装（所有项目共享）

```bash
# 从 GitHub 全局安装
openskills install lambert0529/skills/java --global

# 或从本地路径全局安装
openskills install ./java --global
```

### 同步技能到 AGENTS.md（用于 Cursor）

安装完成后，需要同步技能信息到 AGENTS.md，以便 Cursor Agent 发现：

```bash
# 在项目根目录执行
openskills sync --yes -o .cursor/rules/AGENTS.md
```

### 验证安装

```bash
# 列出已安装的技能
openskills list

# 读取技能内容（验证）
openskills read java-rest-api-design
```

### 使用方法

**在 Cursor 中**：
- Skills 会自动从 `.cursor/rules/AGENTS.md` 中被发现
- 直接向 AI 助手请求使用技能：
  - "为用户管理创建 REST API"
  - "创建用户 Service 层，包含事务管理"
  - "设计异常处理机制"
  - "为创建用户请求添加参数校验"
  - "设计统一响应格式"
  - "为 Service 添加日志记录"

**在 Claude Code 中**：
- Skills 会自动从 `.agent/skills/` 或 `.claude/skills/` 中被发现
- 无需额外配置

## 📁 项目结构

```
skills/
├── README.md                 # 本文件（中文）
├── README.en.md              # 英文版 README
├── LICENSE                   # Apache 2.0 许可证
├── doc/                      # 文档目录
│   └── Skills介绍及使用方式.md
├── java/                     # Java Skills（开发目录）
│   ├── README.md
│   ├── java-rest-api-design/
│   ├── java-service-layer/
│   ├── java-exception-handling/
│   ├── java-validation/
│   ├── java-response-wrapper/
│   └── java-logging/
└── .agent/skills/            # 已安装的技能（运行时）
    └── ...
```

## 🎯 规范覆盖范围

这些 Skills 覆盖了 Java 后端开发的核心规范：

- ✅ RESTful API 设计
- ✅ 分层架构（Controller-Service-Repository）
- ✅ 异常处理机制
- ✅ 参数校验
- ✅ 统一响应格式
- ✅ 日志记录
- ✅ 事务管理
- ✅ 代码规范

## 📚 文档

- [Skills 介绍及使用方式](./doc/Skills介绍及使用方式.md)
- [Java Skills README](./java/README.md)

## 🔗 相关资源

- [Anthropic Skills 标准](https://github.com/anthropics/skills)
- [Anthropic Skills 市场](https://github.com/anthropics/skills)
- [OpenSkills CLI](https://github.com/numman-ali/openskills)
- [Spring Boot 官方文档](https://spring.io/projects/spring-boot)
- [Bean Validation 规范](https://beanvalidation.org/)

## 🤝 贡献

欢迎贡献！请随时提交 Pull Request。

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingSkill`)
3. 提交更改 (`git commit -m 'Add some AmazingSkill'`)
4. 推送到分支 (`git push origin feature/AmazingSkill`)
5. 开启 Pull Request

## 📝 许可证

本项目采用 Apache License 2.0 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 🙏 致谢

- [Anthropic](https://www.anthropic.com/) 定义了 Skills 开放标准
- [OpenSkills](https://github.com/numman-ali/openskills) 提供了 CLI 工具
- Java 和 Spring Boot 社区的最佳实践

---

**最后更新**：2026-01-19
