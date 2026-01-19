# Skills 目录结构说明

根据 [Anthropic Skills 开放标准](https://github.com/anthropics/skills)，每个 Skill 应该包含以下结构：

## 标准目录结构

```
skill-name/
├── SKILL.md          # 必需：技能定义（YAML front matter + Markdown）
├── scripts/          # 可选：脚本文件（自动化任务逻辑）
├── references/       # 可选：文档和参考资料（按需加载）
└── assets/           # 可选：资源文件（用于输出的模板、图标、字体等）
```

## 目录说明

### SKILL.md（必需）
- 技能的定义和说明文档
- 包含 YAML front matter（**只包含 `name` 和 `description`**）
- 描述技能的用途、触发条件、使用方法等
- **应保持简洁（<500行，推荐 <300行）**

### scripts/（可选）
- 执行自动化任务的脚本
- 例如：代码生成脚本、验证脚本、格式化脚本等
- 可以是任何可执行脚本（Shell、Python、Node.js 等）
- **何时包含**：当相同代码被重复编写，或需要确定性可靠性时

### references/（可选）
- 文档和参考资料，按需加载到上下文中
- 例如：API 文档、数据库模式、工作流指南、详细规范等
- **何时包含**：对于 Claude 在工作时应该参考的文档
- **最佳实践**：如果文件较大（>10k 词），在 SKILL.md 中包含 grep 搜索模式
- **避免重复**：信息应只存在于 SKILL.md 或 references 文件中，不要重复

### assets/（可选）
- 不加载到上下文，而是用于输出的文件
- 例如：代码模板、图标、字体、PPT 模板、HTML/React 样板等
- **何时包含**：当技能需要文件用于最终输出时
- **用途**：模板、图片、图标、样板代码、字体、示例文档等

## 当前 Skills 结构

### java-rest-api-design
- ✅ `SKILL.md` - RESTful API 设计规范（82 行）
- ✅ `assets/` - Controller 和 DTO 模板
- ✅ `references/` - RESTful 模式、DTO 模式、使用示例
- ⬜ `scripts/` - （待添加）

### java-service-layer
- ✅ `SKILL.md` - Service 层开发规范（88 行）
- ✅ `assets/` - Service 接口和实现类模板
- ✅ `references/` - 事务管理、业务逻辑模式
- ⬜ `scripts/` - （待添加）

### java-exception-handling
- ✅ `SKILL.md` - 异常处理规范（86 行）
- ✅ `assets/` - 全局异常处理器模板、错误码示例
- ✅ `references/` - 异常类设计、错误码定义
- ⬜ `scripts/` - （待添加）

### java-validation
- ✅ `SKILL.md` - 参数校验规范（127 行）
- ✅ `assets/` - 自定义校验器模板
- ✅ `references/` - Bean Validation 注解、分组校验、校验示例
- ⬜ `scripts/` - （待添加）

### java-response-wrapper
- ✅ `SKILL.md` - 统一响应格式规范（80 行）
- ✅ `assets/` - Result 响应类模板
- ✅ `references/` - 响应格式示例、分页响应
- ⬜ `scripts/` - （待添加）

### java-logging
- ✅ `SKILL.md` - 日志记录规范（120 行）
- ✅ `assets/` - Logback 配置示例、脱敏工具类
- ✅ `references/` - 日志级别使用、日志格式规范
- ⬜ `scripts/` - （待添加）

## 渐进式披露原则

Skills 使用三级加载系统来高效管理上下文：

1. **Metadata（name + description）** - 始终在上下文中（~100 词）
2. **SKILL.md body** - 当技能触发时（<5k 词）
3. **Bundled resources** - 按 Claude 需要时（无限制，因为脚本可以执行而不读入上下文窗口）

### 关键原则

- 保持 SKILL.md 简洁，只包含核心流程和指导
- 详细内容移到 `references/` 文件
- 使用链接引用详细内容，确保读者知道它们存在以及何时使用
- 避免深层嵌套引用 - 保持 references 从 SKILL.md 一级深度

## 后续开发建议

1. **scripts/** - 可以添加：
   - 代码生成脚本
   - 代码检查脚本
   - 格式化脚本

2. **assets/** - 可以添加更多模板：
   - Repository 模板
   - Entity 模板
   - DTO 转换器模板

3. **references/** - 可以添加：
   - 更多详细规范文档
   - 更多代码示例
   - 架构图和流程图

## 使用方式

Agent 在使用 Skill 时，会：
1. 读取 `SKILL.md` 的 frontmatter（name + description）了解技能定义
2. 当技能触发时，加载 `SKILL.md` 的 body 内容
3. 根据需要，参考 `assets/` 中的模板生成代码
4. 按需加载 `references/` 中的详细文档和示例
5. 执行 `scripts/` 中的脚本（如果 Agent 支持）

---

**注意**：
- 所有后续开发都在 `java/` 目录中进行
- `.agent/` 目录是运行时目录，不应提交到版本控制
- 遵循 Anthropic Skills 开放标准，确保跨平台兼容
