# Agent Skills 完整指南

**文档版本**：v2.1.0  
**更新日期**：2026-01-19  
**作者**：lambert  
**状态**：已发布

> **说明**：本文档基于 **Anthropic Skills 开放标准**编写，适用于所有支持该标准的 AI 编程助手（Cursor、Claude Code、Windsurf、Aider 等）。文档中会特别说明 Cursor 中的实现方式。

---

## 📖 目录

1. [Skills 是什么](#1-skills-是什么)
2. [Skills vs Commands vs Rules vs MCP 对比](#2-skills-vs-commands-vs-rules-vs-mcp-对比)
3. [Skills 能帮我们做什么](#3-skills-能帮我们做什么)
4. [各大编程工具对 Skills 的适配情况](#4-各大编程工具对-skills-的适配情况)
5. [OpenSkills 安装及使用](#5-openskills-安装及使用)
6. [Cursor 中使用 Skills](#6-cursor-中使用-skills)

---

## 1. Skills 是什么

### 1.1 定义

**Skills（技能）** 是由 **Anthropic** 提出的一个**开放标准（Open Standard）**，用于增强 AI Agent 在特定任务上的能力。Skills 是一种可重用、结构化的知识包，封装了特定领域知识、工作流程、脚本和最佳实践，供 Agent 根据任务需要按需加载，而不是始终包含在上下文中。

**核心特性**：
- 🌐 **开放标准**：由 Anthropic 定义，遵循统一的规范格式
- 🔄 **跨平台兼容**：可在多个 AI 编程助手间共享（Cursor、Claude Code、Windsurf、Aider 等）
- 📦 **渐进式加载**：采用"渐进式披露（Progressive Disclosure）"机制，节省上下文窗口
- 🔧 **可扩展**：支持自定义命令、钩子脚本、领域知识等多种内容类型

### 1.2 标准规范

**Skills 标准**由 Anthropic 定义，遵循以下规范：

1. **文件结构**
   - 每个 Skill 是一个目录，包含 `SKILL.md` 文件
   - 可选的脚本、模板、参考资料等资源文件
   - 目录名即为技能名称（kebab-case）

2. **SKILL.md 格式**
   - **YAML Front Matter**（必需）：包含元数据
     ```yaml
     ---
     name: skill-name          # 必需：技能名称（kebab-case）
     description: 技能描述      # 必需：技能描述（用于 Agent 匹配任务，应包含具体使用场景）
     ---
     ```
     **注意**：根据 Anthropic Skills 规范，frontmatter 只包含 `name` 和 `description` 两个字段，不包含 `version` 和 `tags`。
   - **Markdown 内容**：技能说明、使用方法、行为定义等（应保持简洁，<500行，推荐<300行）

3. **核心组成**

   - **自定义命令（Custom Commands）**
     - 可以通过 `/command-name` 在 Agent 输入中触发的可重用工作流
     - 例如：`/code-review`、`/test-generator`

   - **Hooks（钩子脚本）**
     - 在 Agent 执行动作之前或之后运行的脚本
     - 用于自动化处理任务、流程控制等
     - 例如：任务完成前自动检查代码风格、任务后自动运行测试

   - **领域知识（Domain Knowledge）**
     - 特定任务的专业指令或知识
     - 当 Agent 识别任务与特定领域相关时，可以拉进这些知识来帮助完成任务
     - 例如：代码审查规范、测试生成模板

   - **assets/**（资源文件）
     - 不加载到上下文，而是用于输出的文件
     - 例如：代码模板、图标、字体、PPT 模板、HTML/React 样板等
   
   - **references/**（参考资料）
     - 文档和参考资料，按需加载到上下文中
     - 例如：API 文档、数据库模式、工作流指南、详细规范等
     - 最佳实践：如果文件较大（>10k 词），在 SKILL.md 中包含 grep 搜索模式

### 1.3 核心特点

- ✅ **开放标准**：遵循 Anthropic Skills 规范，跨平台兼容
- ✅ **动态加载**：采用渐进式披露机制，只在需要时才加载，节省上下文空间（Token）
- ✅ **按需触发**：Agent 扫描技能名称和描述，根据任务相关性判断是否加载
- ✅ **可复用**：一次定义，可在多个项目、多个 Agent 平台间共享
- ✅ **模块化**：每个技能专注一个功能领域，职责清晰
- ✅ **版本控制**：技能文件可以纳入 Git 管理，支持版本追踪
- ✅ **可移植性**：标准化的格式确保技能可以在不同 AI 编程助手间迁移

---

## 2. Skills vs Commands vs Rules vs MCP 对比

### 2.1 概念对比表

| 特性 | Skills（技能） | Commands（命令） | Rules（规则） | MCP（模型上下文协议） |
|------|--------------|----------------|--------------|---------------------|
| **标准/协议** | Anthropic Skills 开放标准 | 平台特定（Slash Commands） | 平台特定（如 Cursor Rules） | Model Context Protocol 开放协议 |
| **核心作用** | 教 Agent **如何做**，提供知识、流程、判断、风格 | 简单的 prompt 模板，快速执行特定行为 | 对 Agent 输出/行为的静态约束或规范 | 给 Agent **能做什么动作**，访问外部系统/数据/工具 |
| **执行能力** | 本身不直接执行外部动作（除非 Skill 内含脚本并被执行） | 无执行能力，仅提供 prompt 模板 | 没有执行外部工具能力，仅约束文本/输出内容 | 本质是执行动作/调用外部逻辑 |
| **文件结构** | 目录形式：`SKILL.md` + 脚本/模板/资源 | 单个 Markdown 文件 | `.mdc` 或 `.md` 文件 | JSON 配置 |
| **加载方式** | 渐进式加载（按需，扫描名称和描述后决定） | 手动显式调用（用户输入 `/command`） | 静态加载（总是或按规则） | 按需调用外部服务 |
| **上下文消耗** | 低（渐进式披露，仅在需要时载入） | 低（仅在调用时加载） | 中等（常驻文本，可能消耗较多 Token） | 工具描述占用部分，工具调用开销较大 |
| **自动发现** | ✅ 支持（Agent 扫描匹配） | ❌ 不支持（需显式调用） | ✅ 支持（自动包含） | ✅ 支持（按需调用） |
| **使用场景** | 特定任务的专业能力、工作流程、领域知识 | 快速简短、频繁重用的命令 | 编码规范、团队标准、全局规则 | 访问外部数据源、执行外部工具、查询数据库 |
| **存放位置** | `.agent/skills/`（通用）或 `.claude/skills/` | `.claude/commands/` 或 `.cursor/commands/` | `.cursor/rules/*.mdc` 或 `AGENTS.md` | `.cursor/mcp.json` 或 `~/.cursor/mcp.json` |
| **文件格式** | `SKILL.md`（YAML front matter + Markdown，标准格式） | `.md`（简单 Markdown，无特殊格式要求） | `.mdc` 或 `.md`（平台特定） | JSON 配置 |
| **触发方式** | Agent 扫描匹配或 `/skill-name` 命令 | 用户显式输入 `/command-name` | 自动包含或按匹配规则 | Agent 需要时调用 |
| **可移植性** | 高（开放标准，跨平台兼容） | 较有限（平台专有，但格式简单） | 较有限（平台专有格式） | 较好（协议标准，但不同 Agent 支持程度可能不同） |
| **创建难度** | 中等（需要 SKILL.md + 可选脚本/资源） | 低（只需一个 Markdown 文件） | 最基础（创建门槛低） | 较高（需搭建 server、定义 schema、权限、安全、认证等） |
| **安全性** | 如果 Skill 中含脚本，有代码执行权限，需要谨慎信任来源 | 相对安全（仅文本 prompt，无脚本执行） | 相对安全（只包含文本规则，没有执行外部代码） | 动作调用本身含有安全性风险，权限控制必需 |
| **复杂度支持** | 高（支持多文件、脚本、复杂流程） | 低（简单 prompt 模板） | 低（静态规则） | 高（可连接复杂外部系统） |

### 2.2 详细对比

#### Skills（技能）- Anthropic 开放标准

**定义**：Skills 是由 Anthropic 定义的开放标准，用于封装复杂的能力包，包含 `SKILL.md` 文件以及可选的脚本、模板、参考资料等资源。

**结构**：
```
skill-name/
├── SKILL.md          # 必需：技能定义（YAML front matter + Markdown）
├── scripts/          # 可选：脚本文件（自动化任务逻辑）
├── assets/           # 可选：资源文件（用于输出的模板、图标、字体等）
└── references/       # 可选：文档和参考资料（按需加载）
```

**调用方式**：
- 自动触发：Agent 扫描技能描述，根据任务相关性自动加载
- 显式调用：用户可以通过 `/skill-name` 命令手动触发

**优势**：
- ✅ **开放标准**：遵循 Anthropic Skills 规范，跨平台兼容（Cursor、Claude Code、Windsurf、Aider 等）
- ✅ **节省上下文**：渐进式加载机制，只在需要时加载，不占用不必要的上下文空间（Token）
- ✅ **灵活性强**：可以根据任务动态选择使用，Agent 扫描技能描述后决定是否加载
- ✅ **可复用**：一次定义，可在多个项目、多个 Agent 平台间共享，版本控制友好
- ✅ **模块化**：每个技能专注一个功能领域，职责清晰
- ✅ **可移植性高**：标准化的 SKILL.md 格式确保技能可以在不同 AI 编程助手间迁移

**劣势**：
- ⚠️ 需要 Agent 正确识别何时使用（依赖技能描述的质量）
- ⚠️ 部分平台支持仍在完善中（如 Cursor 的 Nightly 版本）
- ⚠️ 需要手动定义和维护技能文件
- ⚠️ 如果技能包含脚本，需要谨慎信任来源（安全性考虑）

**适用场景**：
- 代码审查、测试生成、文档生成等特定任务
- 需要复杂工作流程的场景
- 团队共享的专业知识和最佳实践
- 跨项目、跨平台的知识复用

#### Commands（命令）- Slash Commands

**定义**：Commands（也称为 Slash Commands）是简单的 prompt 模板，通常是一个 Markdown 文件，用于快速执行特定行为。用户必须显式输入 `/command-name` 才能触发。

**结构**：
```
commands/
└── command-name.md    # 简单的 Markdown 文件，包含 prompt 模板
```

**调用方式**：
- 仅手动触发：用户必须输入 `/command-name` 才会执行
- 不支持自动发现：Agent 不会自动扫描或匹配 Commands

**优势**：
- ✅ **简单快速**：只需一个 Markdown 文件，编辑方便
- ✅ **可控性强**：用户明确触发，行为可预测
- ✅ **安全性好**：内容简单，无复杂依赖，审查容易
- ✅ **开销小**：结构简单，创建和维护成本低

**劣势**：
- ⚠️ **灵活性低**：不支持复杂流程、多个文件、标准化资源
- ⚠️ **自动化能力差**：需要用户每次明确调用，不能基于上下文自动匹配
- ⚠️ **可移植性有限**：平台特定，但格式简单，迁移相对容易

**适用场景**：
- 快速简短、频繁重用的命令
- 简单的 prompt 模板，如 `/review code`、`/optimize`
- 不需要复杂脚本或流程的行为
- 个人或项目特定的快捷命令

**示例**：
```markdown
# 代码审查命令

请审查以下代码，检查：
1. 编码规范是否符合项目标准
2. 是否有潜在的性能问题
3. 是否有安全风险
```

#### Rules（规则）

**优势**：
- ✅ 持续生效：始终包含在上下文中
- ✅ 稳定性高：功能成熟，广泛使用
- ✅ 自动应用：无需手动触发

**劣势**：
- ⚠️ 占用上下文：始终加载，可能占用较多 token
- ⚠️ 不够灵活：所有对话都会包含，即使不相关

**适用场景**：
- 编码规范、命名约定
- 项目结构说明
- 团队工作流程
- 需要持续指导的规则

#### MCP（Model Context Protocol）

**优势**：
- ✅ 连接外部系统：可以访问数据库、API、文件系统等
- ✅ 实时数据：获取最新的外部数据
- ✅ 工具集成：可以调用外部工具和脚本

**劣势**：
- ⚠️ 需要配置：需要设置 MCP 服务器
- ⚠️ 依赖外部：需要外部系统可用
- ⚠️ 性能考虑：网络调用可能较慢

**适用场景**：
- 访问数据库查询数据
- 调用外部 API
- 执行系统命令
- 读取文件系统

### 2.3 Skills vs Commands 详细对比

#### 核心区别

| 维度 | Commands | Skills |
|------|----------|--------|
| **复杂度** | 简单（单个文件） | 复杂（目录结构，多文件） |
| **调用方式** | 仅手动（`/command`） | 自动 + 手动（Agent 匹配或 `/skill`） |
| **自动发现** | ❌ 不支持 | ✅ 支持（Agent 扫描描述） |
| **资源支持** | 仅文本 prompt | 文本 + 脚本 + 模板 + 资源 |
| **适用场景** | 简单、频繁重用的命令 | 复杂工作流、团队规范、跨平台共享 |
| **可移植性** | 较有限（平台特定） | 高（开放标准） |
| **安全性** | 高（仅文本） | 中等（可能包含脚本） |

#### 融合趋势

**重要更新**：在 Claude Code 2.1.3+ 版本中，Anthropic 正在**合并** Commands 和 Skills，将两者视为同一种能力单元，主要区别仅在"调用方式"（显式 vs 自动触发）。

**合并后的特点**：
- 无论是 `.claude/commands/` 还是 `.claude/skills/` 中的内容，从系统角度看都是 Skill
- 可以通过 Settings 或 frontmatter 设定是否允许自动触发
- 支持 `user-invocable`、`disable-model-invocation` 等设置

**建议**：
- 新项目建议直接使用 Skills 标准格式
- 简单命令可以创建为 Skill，但设置 `disable-model-invocation: true` 仅允许手动调用
- 复杂工作流使用完整的 Skills 结构，支持自动发现

### 2.4 使用建议

**组合使用**（最佳实践）：
- **Rules**：放置编码规范、架构约束等静态规则（平台特定）
- **Commands**：放置简单、频繁重用的快捷命令（平台特定，或迁移到 Skills）
- **Skills**：放置可执行的工作流、特定领域的动态知识（跨平台标准，推荐）
- **MCP**：连接外部系统，获取实时数据（开放协议）

**最强组合**：Skills + MCP + Rules 并用
- **Skills** 提供**何时做什么**以及**如何做**的知识
- **MCP** 提供**能做什么动作**的能力
- **Rules** 提供**必须遵循**的约束和规范
- **Commands** 提供**快速执行**的简单命令（可选，建议迁移到 Skills）

**选择建议**：

| 场景                    | 推荐方案                     | 原因            |
| --------------------- | ------------------------ | ------------- |
| 简单的 prompt 模板，如"审查代码" | Commands 或 Skills（设置仅手动） | 简单快速，无需复杂结构   |
| 需要脚本、多文件、复杂流程         | Skills                   | 支持完整的能力包结构    |
| 团队共享、跨平台复用            | Skills                   | 开放标准，可移植性强    |
| 需要自动发现和匹配             | Skills                   | 支持 Agent 自动扫描 |
| 编码规范、架构约束             | Rules                    | 静态规则，持续生效     |
| 访问外部系统、执行操作           | MCP                      | 提供执行能力        |

**示例**：
```
Rules（AGENTS.md，Cursor 特定）：
  - 编码规范：4空格缩进、PascalCase 类名
  - 项目结构：backend/、frontend/ 目录说明

Commands（可选，建议迁移到 Skills）：
  - /quick-review：快速代码审查
  - /format-code：代码格式化

Skills（java-rest-api-design，Anthropic 标准）：
  - RESTful API 设计工作流（跨平台可复用）
  - Controller 和 DTO 生成模板
  - 支持自动发现和手动调用

MCP（数据库连接，开放协议）：
  - 查询模型数据
  - 执行数据库操作
```

---

## 3. Skills 能帮我们做什么

### 3.1 核心能力

Skills 可以帮助我们：

1. **自动化重复任务**
   - 代码审查
   - 单元测试生成
   - API 文档生成
   - DDL 语句生成

2. **标准化工作流程**
   - 统一的代码审查标准
   - 一致的测试生成规范
   - 规范的文档格式

3. **团队知识共享**
   - 最佳实践封装
   - 专业知识复用
   - 团队标准统一

4. **提高开发效率**
   - 减少重复工作
   - 快速生成代码和文档
   - 自动化质量检查

### 3.2 实际应用场景

#### 场景 1：RESTful API 设计

**技能**：`java-rest-api-design`

**功能**：
- 生成符合规范的 RESTful API 接口
- 创建 Controller 层代码
- 生成 API 相关的 DTO 类
- 遵循 Spring Boot 最佳实践和 RESTful 规范

**使用**：
```
用户：为用户管理创建 REST API 接口
Agent：使用 java-rest-api-design 技能 → 生成 Controller 和 DTO
```

#### 场景 2：Service 层开发

**技能**：`java-service-layer`

**功能**：
- 生成 Service 接口和实现类
- 实现业务逻辑和事务管理
- 遵循分层架构最佳实践

**使用**：
```
用户：创建用户 Service 层，包含事务管理
Agent：使用 java-service-layer 技能 → 生成 Service 代码
```

#### 场景 3：异常处理机制

**技能**：`java-exception-handling`

**功能**：
- 设计全局异常处理器
- 定义错误码和异常类
- 统一异常响应格式

**使用**：
```
用户：设计异常处理机制
Agent：使用 java-exception-handling 技能 → 生成异常处理代码
```

#### 场景 4：参数校验

**技能**：`java-validation`

**功能**：
- 为请求参数添加校验注解
- 创建自定义校验器
- 实现分组校验

**使用**：
```
用户：为创建用户请求添加参数校验
Agent：使用 java-validation 技能 → 生成校验代码
```

#### 场景 5：统一响应格式

**技能**：`java-response-wrapper`

**功能**：
- 创建统一响应包装类
- 实现分页响应格式
- 统一 API 返回格式

**使用**：
```
用户：设计统一响应格式
Agent：使用 java-response-wrapper 技能 → 生成响应类
```

#### 场景 6：日志记录

**技能**：`java-logging`

**功能**：
- 配置日志格式和级别
- 实现日志脱敏
- 遵循日志记录最佳实践

**使用**：
```
用户：为 Service 添加日志记录
Agent：使用 java-logging 技能 → 生成日志配置和代码
```

#### 场景 7：MyBatis-Plus 代码生成

**技能**：`java-mybatis-plus-generator`

**功能**：
- 生成基于 MyBatis-Plus 的完整分层代码
- 创建 Controller、Service、Mapper 层
- 集成 PageHelper 分页功能

**使用**：
```
用户：为用户管理生成基于 MyBatis-Plus 的 CRUD 代码
Agent：使用 java-mybatis-plus-generator 技能 → 生成完整代码
```

### 3.3 当前项目的 Skills

本项目已创建了以下 Java 后端开发 Skills：

1. **java-rest-api-design** - RESTful API 设计规范
2. **java-service-layer** - Service 层开发规范
3. **java-exception-handling** - 异常处理规范
4. **java-validation** - 参数校验规范
5. **java-response-wrapper** - 统一响应格式规范
6. **java-logging** - 日志记录规范
7. **java-mybatis-plus-generator** - MyBatis-Plus 代码生成

---

## 4. 各大编程工具对 Skills 的适配情况

### 4.1 适配情况总览

| 工具/平台                       | 支持状态                | 支持类型                      | 使用方式                                            | 限制与注意事项                  |
| --------------------------- | ------------------- | ------------------------- | ----------------------------------------------- | ------------------------ |
| **Claude Code**             | ✅ **完全支持**          | 原生支持 Anthropic Skills 标准  | 文件系统目录结构（`.claude/skills/` 或 `.agent/skills/`）  | 自动触发机制依赖技能描述质量           |
| **Claude.ai（Web）**          | ✅ **完全支持**          | 预构建 Skills + 自定义 Skills   | 设置中启用，上传 ZIP 或使用 Partner Skills                 | 需开启 Code Execution 权限    |
| **Claude API**              | ✅ **完全支持**          | 通过 API 管理 Skills          | `/v1/skills` 端点上传管理                             | 需要 API 密钥和相应权限           |
| **Cursor**                  | ⚠️ **Beta/Nightly** | 通过 OpenSkills 和 AGENTS.md | 使用 OpenSkills CLI 同步到 `.cursor/rules/AGENTS.md` | 功能尚未完全稳定，自动触发不保证         |
| **Windsurf**                | ✅ **支持**            | 原生支持 Anthropic Skills 标准  | 类似 Claude Code 的文件系统结构                          | 社区反馈较少，功能相对稳定            |
| **Aider**                   | ✅ **支持**            | 原生支持 Anthropic Skills 标准  | 文件系统目录结构                                        | 支持自动发现和手动调用              |
| **VS Code（GitHub Copilot）** | ✅ **已集成**           | 通过 Microsoft 集成           | 在 VS Code 中定义 Skills                            | 需要 GitHub Copilot 订阅     |
| **OpenAI Codex CLI**        | ✅ **部分支持**          | Skills 框架（类似 Anthropic）   | `~/.codex/skills/` 目录                           | 格式与 Anthropic 高度一致，但功能较新 |
| **ChatGPT**                 | ✅ **部分支持**          | Code Interpreter 预置 + 自定义 | 通过设置上传 Skills                                   | 触发机制不如 Claude 稳定         |
| **Trae**                    | ❌ **不支持**           | 无公开支持信息                   | 无                                                 | 曾支持 Claude 模型，但 2025 年 11 月被禁止使用 |

### 4.2 Claude Code 详细支持情况

#### 4.2.1 核心特性

**Claude Code** 是 Anthropic 官方开发的 AI 编程助手，对 Skills 标准提供**最完整和原生**的支持。

**支持的功能**：
- ✅ **自动发现**：Agent 自动扫描技能目录，根据任务相关性匹配技能
- ✅ **自动触发**：当任务匹配技能描述时，自动加载技能内容
- ✅ **手动调用**：支持通过 `/skill-name` 命令手动触发
- ✅ **目录结构**：支持 `.claude/skills/` 和 `.agent/skills/` 目录
- ✅ **完整格式**：支持 SKILL.md + 脚本 + 模板 + 资源的完整结构
- ✅ **组织级管理**：支持团队共享和组织级 Skills

#### 4.2.2 Commands 与 Skills 的融合

**重要更新**：在 **Claude Code v2.1.3+** 版本中，Slash Commands 与 Skills 的底层工具已**合并为统一的 Skill 工具**。

**融合后的特点**：
- 用户仍可在 `.claude/commands/` 和 `.claude/skills/` 目录定义内容
- Agent 在行为层面不再区分 Commands 和 Skills
- Slash commands 的语法（`/something`）现在可以调用 Skills
- Skills 可以通过 `user-invocable` 标记使其在界面中以 Slash 方式出现

**配置示例**：
```yaml
---
name: java-rest-api-design
description: 生成符合主流规范的 Java RESTful API 接口设计，包括 Controller、DTO、统一响应格式、异常处理等。使用场景：(1) 创建新的 REST API 接口，(2) 设计 Controller 层代码，(3) 生成 API 相关的 DTO 类，(4) 审查或优化现有 API 设计，(5) 需要遵循 Spring Boot 最佳实践和 RESTful 规范时
user-invocable: true  # 允许手动调用，会在界面中显示为 /java-rest-api-design
disable-model-invocation: false  # 允许自动触发
---
```

#### 4.2.3 使用方式

**方式一：项目级 Skills**
```
项目根目录/
└── .claude/
    └── skills/
        └── java-rest-api-design/
            └── SKILL.md
```

**方式二：通用 Skills（推荐）**
```
项目根目录/
└── .agent/
    └── skills/
        └── java-rest-api-design/
            └── SKILL.md
```

**方式三：全局 Skills**
```
~/.claude/skills/
└── java-rest-api-design/
    └── SKILL.md
```

#### 4.2.4 已知问题与解决方案

**问题 1：自动触发率低**

**现象**：Skills 有时不会被自动触发，用户需要手动提示。

**原因**：
- 技能描述不够具体
- 关键词匹配不够精确
- Agent 判断任务相关性时过于保守

**解决方案**：
1. **优化技能描述**：在 YAML front matter 的 `description` 字段中使用具体、明确的关键词
2. **使用 forced eval hook**：在技能中添加评估机制，强制 Agent 评估每个技能是否匹配
3. **手动提示**：在任务中明确提到技能名称或相关关键词

**问题 2：脚本执行环境**

**现象**：Skills 中的脚本可能因为依赖、环境变量等问题无法执行。

**解决方案**：
1. 在技能文档中明确说明依赖要求
2. 使用相对路径而非绝对路径
3. 提供环境检查和错误处理机制

### 4.3 Cursor 支持情况

#### 4.3.1 当前状态

**支持程度**：⚠️ **Beta/Nightly 阶段，功能尚未完全稳定**

**实现方式**：
- 通过 **OpenSkills CLI** 工具管理技能
- 使用 **AGENTS.md** 文件让 Agent 发现可用技能
- Agent 通过 `openskills read` 命令加载技能内容

#### 4.3.2 使用流程

1. **安装技能**：使用 OpenSkills 安装到 `.agent/skills/` 目录
2. **同步到 AGENTS.md**：执行 `openskills sync -o .cursor/rules/AGENTS.md`
3. **Agent 发现**：Cursor Agent 读取 AGENTS.md 中的技能列表
4. **技能加载**：Agent 执行 `openskills read <skill-name>` 加载技能

#### 4.3.3 限制与注意事项

- ⚠️ **自动触发不稳定**：Agent 可能不会自动识别和使用技能
- ⚠️ **需要手动提示**：建议在任务中明确提到技能名称
- ⚠️ **Nightly 版本**：需要切换到 Cursor Nightly 渠道
- ⚠️ **权限问题**：使用 `openskills` 而非 `npx openskills` 避免权限错误

### 4.4 其他工具支持情况

#### 4.4.1 Windsurf

**支持状态**：✅ **原生支持**

**特点**：
- 原生支持 Anthropic Skills 标准
- 支持自动发现和手动调用
- 文件系统结构与 Claude Code 类似
- 社区反馈较少，但功能相对稳定

#### 4.4.2 Aider

**支持状态**：✅ **原生支持**

**特点**：
- 原生支持 Anthropic Skills 标准
- 支持 `.aider/skills/` 目录
- 自动发现机制较为稳定

#### 4.4.3 VS Code（GitHub Copilot）

**支持状态**：✅ **已集成**

**特点**：
- Microsoft 在 Anthropic 宣布开放标准后迅速集成
- 需要 GitHub Copilot 订阅
- 支持在 VS Code 中定义和使用 Skills

#### 4.4.4 OpenAI Codex CLI / ChatGPT

**支持状态**：✅ **部分支持**

**特点**：
- Skills 框架与 Anthropic 类似
- 格式高度一致（YAML front matter + Markdown）
- 功能较新，触发机制不如 Claude 稳定
- 社区反馈文档稀疏，体验略粗糙

#### 4.4.5 Trae

**支持状态**：❌ **不支持**

**背景**：
- Trae 是 ByteDance（字节跳动）出品的 AI 编程助手
- 曾支持 Anthropic 的 Claude 系列模型（包括 Sonnet 系列）
- 在 2025 年 11 月，因 Anthropic 的政策限制（禁止向"50% 以上由中国公司控股的实体"提供服务），Trae 被禁止继续使用 Claude 模型

**当前状态**：
- ❌ **不支持 Anthropic Skills 标准**：没有公开资料表明 Trae 已支持 Skills 文件夹结构、SKILL.md 标准格式、技能自动加载等功能
- ❌ **模型访问受限**：失去 Claude 模型访问后，也失去了支持 Skills 标准的技术基础
- ⚠️ **自定义模型尝试**：部分用户尝试通过 API Key 或自定义模型方式接入 Claude，但官方未清晰开放此类配置

**限制因素**：
1. **政策限制**：Anthropic 的政策调整导致 Trae 无法使用 Claude 服务
2. **技术依赖**：Skills 标准需要 Claude 模型或兼容的执行环境
3. **基础设施**：Skills 需要代码执行环境、安全隔离、技能管理等基础设施支持

**未来可能性**：
如果 Trae 希望支持 Skills 标准，可能需要：
- 支持 Skills 文件夹结构（`SKILL.md` + 脚本/资源文件）
- 实现 Skills API 能力（上传、管理、调用技能）
- 提供安全的代码执行环境
- 在模型可用的前提下，支持技能自动发现和触发机制

### 4.5 时间线与标准化进程

#### 关键时间节点

| 时间 | 事件 | 影响 |
|------|------|------|
| **2025 年 10 月中旬** | Anthropic 正式在 Claude 中推出 Agent Skills 功能 | 首次公开发布 Skills 概念 |
| **2025 年 11 月** | Anthropic 政策调整，Trae 等工具被禁止使用 Claude 模型 | 部分工具失去 Claude 模型支持 |
| **2025 年 12 月 18 日** | Anthropic 将 Agent Skills 发布为**开放标准**（agent-skills.io） | 标准化进程，Microsoft、OpenAI 等迅速集成 |
| **Claude Code v2.1.1** | 开始合并 Commands 和 Skills | 统一底层工具 |
| **Claude Code v2.1.3** | Commands 和 Skills 完全合并 | 用户可见的融合完成 |
| **2026 年 1 月** | 多个平台宣布支持或正在集成 | 生态逐步完善 |

#### 标准化影响

**开放标准发布后的影响**：
- ✅ **Microsoft**：在 VS Code 和 GitHub 中迅速集成
- ✅ **OpenAI**：在 ChatGPT 和 Codex CLI 中开始支持
- ✅ **第三方工具**：Cursor、Windsurf、Aider 等逐步支持
- ✅ **企业工具**：Notion、Figma、Atlassian 等提供 Partner Skills

### 4.6 选择建议

#### 根据工具选择

| 使用场景 | 推荐工具 | 原因 |
|---------|---------|------|
| **最完整的 Skills 支持** | Claude Code | 原生支持，功能最全面 |
| **团队协作** | Claude Code + Claude.ai | 支持组织级 Skills 管理 |
| **VS Code 用户** | VS Code + GitHub Copilot | 集成在熟悉的编辑器中 |
| **跨平台兼容** | 使用 `.agent/skills/` 目录 | 通用目录，多工具共享 |
| **Beta 体验** | Cursor Nightly | 功能在完善中，可提前体验 |

#### 迁移建议

**从 Commands 迁移到 Skills**：
1. 将 `.claude/commands/` 中的文件迁移到 `.claude/skills/` 或 `.agent/skills/`
2. 添加 YAML front matter（name、description）
3. 设置 `user-invocable: true` 保持手动调用能力
4. 测试自动触发功能

**跨平台共享**：
1. 使用 `.agent/skills/` 目录而非 `.claude/skills/`
2. 确保技能描述清晰，提高自动匹配率
3. 避免使用平台特定的功能

---

## 5. OpenSkills 安装及使用

### 4.1 什么是 OpenSkills

**OpenSkills** 是一个由社区开发的开源 CLI 工具，用于管理和使用遵循 **Anthropic Skills 标准**的技能。它将 Anthropic 定义的 Skills 开放标准扩展到任何支持的 AI 编程代理中（Cursor、Claude Code、Windsurf、Aider 等）。

**核心价值**：
- 🔧 提供统一的技能管理接口
- 📦 支持从 Anthropic 官方市场、GitHub 仓库、本地路径安装技能
- 🔄 自动同步技能信息到 `AGENTS.md`，供 Agent 发现和使用
- 🌐 支持多种安装模式（项目级、全局、通用），适配不同使用场景

### 4.2 安装 OpenSkills

#### 前置要求

- Node.js ≥ v20.6
- Git（用于从 GitHub 安装技能）

#### 安装步骤

```bash
# 全局安装 OpenSkills CLI
npm install -g openskills

# 验证安装
openskills --version
```

### 4.3 核心功能

#### 1. 安装技能（Install）

```bash
# 从 Anthropic 官方市场安装所有技能
openskills install anthropics/skills

# 从 GitHub 仓库安装特定技能
openskills install username/repo-name

# 从本地路径安装
openskills install ./local-skill-path

# 安装到通用目录（多 Agent 共享，推荐）
openskills install anthropics/skills --universal

# 全局安装（所有项目共享）
openskills install anthropics/skills --global

# 跳过确认提示（适合 CI/CD）
openskills install anthropics/skills --yes
```

**⚠️ 重要：工作目录要求**
- **项目级和通用模式**：必须在项目根目录执行
- **全局模式**：可以在任何目录执行

#### 2. 列出已安装技能（List）

```bash
# 列出当前项目的技能
openskills list

# 列出全局技能
openskills list --global

# 列出通用技能
openskills list --universal
```

#### 3. 同步技能到 AGENTS.md（Sync）

```bash
# 同步到默认位置
openskills sync

# 同步到指定路径（推荐）
openskills sync -o .cursor/rules/AGENTS.md

# 同步到通用位置
openskills sync --universal

# 跳过确认提示
openskills sync --yes
```

**生成的 AGENTS.md 格式**：
```xml
<available_skills>
  <skill>
    <name>java-rest-api-design</name>
    <description>生成符合主流规范的 Java RESTful API 接口设计，包括 Controller、DTO、统一响应格式、异常处理等。使用场景：(1) 创建新的 REST API 接口，(2) 设计 Controller 层代码，(3) 生成 API 相关的 DTO 类，(4) 审查或优化现有 API 设计，(5) 需要遵循 Spring Boot 最佳实践和 RESTful 规范时</description>
    <location>project</location>
  </skill>
</available_skills>
```

#### 4. 读取技能内容（Read）

```bash
# 读取技能内容
openskills read java-rest-api-design

# 读取多个技能
openskills read skill-one,skill-two
```

#### 5. 删除技能（Remove）

```bash
# 删除指定技能
openskills remove <skill-name>

# 交互式管理（批量删除）
openskills manage
```

### 4.4 安装模式详解

| 模式 | 命令参数 | 安装位置 | 需要项目目录 | 适用场景 |
|------|---------|---------|------------|---------|
| **项目级** | 无参数（默认） | `项目目录/.claude/skills/` | ✅ 是 | 项目特定技能 |
| **全局** | `--global` | `~/.claude/skills/` | ❌ 否 | 所有项目共享 |
| **通用** | `--universal` | `项目目录/.agent/skills/` 或 `~/.agent/skills/` | ✅ 是 | 多 Agent 共享（推荐） |

### 4.5 目录识别规则

**OpenSkills 只识别以下目录**：
- ✅ `.claude/skills/` - Claude 生态技能目录
- ✅ `.agent/skills/` - 通用技能目录（推荐）
- ❌ `.cursor/skills/` - **不被 OpenSkills 识别**

**目录查找优先级**（从高到低）：
1. `./.agent/skills/` - 当前项目的通用技能
2. `~/.agent/skills/` - 全局通用技能
3. `./.claude/skills/` - 当前项目的 Claude 技能
4. `~/.claude/skills/` - 全局 Claude 技能

### 4.6 SKILL.md 文件格式（Anthropic Skills 标准）

**遵循 Anthropic Skills 标准格式**：

```yaml
---
name: java-rest-api-design                    # 必需：技能名称（kebab-case）
description: 生成符合主流规范的 Java RESTful API 接口设计，包括 Controller、DTO、统一响应格式、异常处理等。使用场景：(1) 创建新的 REST API 接口，(2) 设计 Controller 层代码，(3) 生成 API 相关的 DTO 类，(4) 审查或优化现有 API 设计，(5) 需要遵循 Spring Boot 最佳实践和 RESTful 规范时
---

# Java RESTful API 设计规范

## 快速开始
生成 RESTful API 接口的基本步骤...

## 详细参考
- RESTful 模式：见 [references/restful-patterns.md](references/restful-patterns.md)
- DTO 模式：见 [references/dto-patterns.md](references/dto-patterns.md)
- 代码模板：见 `assets/ControllerTemplate.java` 和 `assets/CreateRequestTemplate.java`
```

**标准要求**：
- ✅ **YAML Front Matter**（必需）：只包含 `name` 和 `description` 两个字段
- ✅ **Markdown 内容**：技能说明、使用方法、行为定义等（应保持简洁，<500行，推荐<300行）
- ✅ **目录结构**：技能目录名应与 `name` 字段一致（kebab-case）
- ✅ **可选的资源文件**：
  - `scripts/` - 可执行脚本（自动化任务逻辑）
  - `assets/` - 用于输出的文件（模板、图标、字体等）
  - `references/` - 按需加载的文档和参考资料

**重要说明**：
- ✅ YAML front matter 中的 `description` 会被 OpenSkills 同步到 AGENTS.md，应包含具体使用场景
- ✅ 遵循渐进式披露原则：SKILL.md 保持简洁（<500行，推荐<300行），详细内容放在 `references/` 中
- ✅ 代码模板放在 `assets/` 中，不加载到上下文，仅用于输出
- ✅ 遵循标准格式的技能可以在不同 AI 编程助手间共享

**渐进式披露原则**：
Skills 使用三级加载系统来高效管理上下文：
1. **Metadata（name + description）** - 始终在上下文中（~100 词）
2. **SKILL.md body** - 当技能触发时加载（<5k 词，建议 <300 行）
3. **Bundled resources** - 按 Claude 需要时加载（无限制，因为脚本可以执行而不读入上下文窗口）

**关键原则**：
- 保持 SKILL.md 简洁，只包含核心流程和指导
- 详细内容移到 `references/` 文件
- 使用链接引用详细内容，确保读者知道它们存在以及何时使用
- 避免深层嵌套引用 - 保持 references 从 SKILL.md 一级深度

### 4.7 常见问题

#### Q1: 为什么描述没有同步到 AGENTS.md？

**A**: SKILL.md 文件缺少 YAML front matter。需要在文件开头添加：

```yaml
---
name: java-rest-api-design
description: 生成符合主流规范的 Java RESTful API 接口设计，包括 Controller、DTO、统一响应格式、异常处理等。使用场景：(1) 创建新的 REST API 接口，(2) 设计 Controller 层代码，(3) 生成 API 相关的 DTO 类，(4) 审查或优化现有 API 设计，(5) 需要遵循 Spring Boot 最佳实践和 RESTful 规范时
---
```

**注意**：根据 Anthropic Skills 规范，frontmatter 只包含 `name` 和 `description`，不需要 `version` 和 `tags`。

#### Q2: 为什么 `openskills list` 显示没有技能？

**A**: 检查技能是否在正确的目录：
- 确保在项目根目录执行
- 检查 `.agent/skills/` 或 `.claude/skills/` 目录
- 确保 SKILL.md 文件存在

#### Q3: 为什么 `npx openskills` 报权限错误？

**A**: 使用全局安装的 `openskills` 命令，而不是 `npx openskills`：

```bash
# ✅ 正确
openskills read java-rest-api-design

# ❌ 错误（可能权限问题）
npx openskills read java-rest-api-design
```

---

## 6. Cursor 中使用 Skills

### 6.1 Skills 标准在 Cursor 中的实现

**Skills 是 Anthropic 定义的开放标准**，Cursor 作为 AI 编程助手之一，通过以下方式支持 Skills：

1. **读取 AGENTS.md**：Cursor Agent 会读取 `.cursor/rules/AGENTS.md` 中的 `<available_skills>` 区块
2. **技能发现**：Agent 扫描技能名称和描述，根据任务相关性决定是否使用
3. **技能加载**：通过执行 `openskills read <skill-name>` 加载技能完整内容

**当前状态**：
- ⚠️ Cursor 对 Skills 的支持仍在完善中（Nightly 版本）
- ✅ 通过 OpenSkills 和 AGENTS.md 可以实现 Skills 标准的使用
- ✅ 技能文件遵循 Anthropic Skills 标准，可在其他平台复用

### 6.2 前提条件

1. **Cursor 版本**：Nightly 2.3.29+（建议使用最新 Nightly 版本）
2. **更新渠道**：切换到 Nightly 渠道
   - 设置 → Beta → Update Channel → Nightly
3. **技能已安装**：使用 OpenSkills 安装或手动创建遵循标准的技能
4. **OpenSkills CLI**：已全局安装 `openskills` 命令

### 6.3 使用流程

#### 步骤 1：同步技能到 AGENTS.md

```bash
# 在项目根目录执行
cd /path/to/your/project

# 同步技能（遵循 Anthropic Skills 标准）
openskills sync --yes -o .cursor/rules/AGENTS.md
```

**生成的 AGENTS.md 格式**（标准格式）：
```xml
<available_skills>
  <skill>
    <name>java-rest-api-design</name>
    <description>生成符合主流规范的 Java RESTful API 接口设计，包括 Controller、DTO、统一响应格式、异常处理等。使用场景：(1) 创建新的 REST API 接口，(2) 设计 Controller 层代码，(3) 生成 API 相关的 DTO 类，(4) 审查或优化现有 API 设计，(5) 需要遵循 Spring Boot 最佳实践和 RESTful 规范时</description>
    <location>project</location>
  </skill>
</available_skills>
```

#### 步骤 2：Cursor Agent 发现技能

Cursor Agent 会读取 `.cursor/rules/AGENTS.md` 文件中的 `<available_skills>` 区块，获取可用技能列表（名称和描述）。

**工作原理**：
1. Agent 分析用户任务
2. 扫描 `<available_skills>` 中的技能描述
3. 根据任务相关性匹配技能
4. 决定是否加载技能

#### 步骤 3：使用技能

**方式一：自然语言触发（推荐）**

```
你：为用户管理创建 REST API 接口

Agent：
1. 识别到需要"REST API 设计"任务
2. 在 AGENTS.md 中找到 java-rest-api-design 技能
3. 执行：openskills read java-rest-api-design
4. 按照技能定义生成 Controller 和 DTO 代码
```

**方式二：明确指定技能**

```
你：使用 java-rest-api-design 技能为用户管理创建 REST API
```

**方式三：使用命令触发**

```
你：/java-rest-api-design 为用户管理创建 REST API
```

### 6.4 工作原理（Skills 标准流程）

```
用户提出需求
    ↓
Agent 分析任务类型
    ↓
读取 .cursor/rules/AGENTS.md 中的 <available_skills> 区块
    ↓
扫描技能名称和描述，匹配任务相关性
    ↓
决定是否使用技能（基于描述匹配度）
    ↓
执行：openskills read <skill-name>
    ↓
加载技能完整内容（SKILL.md，遵循 Anthropic Skills 标准）
    ↓
按照技能定义执行任务（遵循技能中的行为定义）
    ↓
返回结果
```

**关键点**：
- 🔍 **技能发现**：通过 AGENTS.md 中的描述进行匹配
- 📖 **技能加载**：使用 `openskills read` 加载完整技能内容
- ✅ **标准兼容**：整个过程遵循 Anthropic Skills 标准

### 6.5 验证技能是否被使用

#### 方法 1：观察 Agent 响应

如果 Agent 使用了技能，应该：
- 提到技能名称
- 展示技能加载过程
- 按照技能定义的格式输出

#### 方法 2：查看执行日志

如果 Agent 执行了 `openskills read`，应该能看到：
- 命令执行记录
- 技能内容加载

#### 方法 3：对比输出格式

如果 Agent 使用了技能，输出应该符合技能定义的格式。

### 6.6 当前项目配置

**技能目录**：`.agent/skills/`

**已安装技能**（从 `java/` 目录安装）：
- `java-rest-api-design` - RESTful API 设计规范
- `java-service-layer` - Service 层开发规范
- `java-exception-handling` - 异常处理规范
- `java-validation` - 参数校验规范
- `java-response-wrapper` - 统一响应格式规范
- `java-logging` - 日志记录规范
- `java-mybatis-plus-generator` - MyBatis-Plus 代码生成

**AGENTS.md 位置**：`.cursor/rules/AGENTS.md`

**使用示例**：
```
在 Cursor 中直接说：
"为用户管理创建 REST API 接口"
"创建用户 Service 层，包含事务管理"
"设计异常处理机制"
"为创建用户请求添加参数校验"
"设计统一响应格式"
"为 Service 添加日志记录"
"为用户管理生成基于 MyBatis-Plus 的 CRUD 代码"
```

### 6.7 注意事项

1. **确保在项目目录**：Agent 执行命令时需要在项目根目录
2. **技能描述要清晰**：如果技能描述为空，可能无法匹配
3. **重新同步**：添加新技能后需要重新同步：
   ```bash
   openskills sync --yes -o .cursor/rules/AGENTS.md
   ```
4. **使用全局命令**：使用 `openskills` 而不是 `npx openskills`，避免权限问题

---

## 📋 快速参考

### 常用命令

```bash
# 安装技能
openskills install anthropics/skills --universal

# 列出技能
openskills list

# 同步到 AGENTS.md
openskills sync --yes -o .cursor/rules/AGENTS.md

# 读取技能
openskills read java-rest-api-design

# 删除技能
openskills remove old-skill
```

### 目录结构

```
项目根目录/
├── .agent/
│   └── skills/              # 通用技能（推荐）
│       ├── java-rest-api-design/
│       │   └── SKILL.md
│       ├── java-service-layer/
│       │   └── SKILL.md
│       └── ...
├── .claude/
│   └── skills/             # Claude 技能
└── .cursor/
    ├── rules/
    │   └── AGENTS.md        # 技能列表（同步生成）
    └── skills/              # 手动创建（不被 OpenSkills 识别）
```

---

## 🔗 相关资源

### 标准与规范
- **Anthropic Skills 标准**：https://github.com/anthropics/skills（官方标准定义）
- **Anthropic Skills 市场**：https://github.com/anthropics/skills（官方技能库）

### 工具与实现
- **OpenSkills GitHub**：https://github.com/numman-ali/openskills（开源 CLI 工具）
- **OpenSkills 文档**：https://github.com/numman-ali/openskills#readme

### 平台支持
- **Cursor 文档**：https://docs.cursor.com（Skills 支持情况请查看最新文档）
- **Claude Code**：原生支持 Anthropic Skills 标准
- **Windsurf**：支持 Anthropic Skills 标准
- **Aider**：支持 Anthropic Skills 标准

### 项目资源
- **项目技能库**：`.agent/skills/README.md`

---

**文档维护**：lambert  
**最后更新**：2026-01-19
