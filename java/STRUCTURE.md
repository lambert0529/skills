# Skills 目录结构说明

根据 [Anthropic Skills 开放标准](https://github.com/anthropics/skills)，每个 Skill 应该包含以下结构：

## 标准目录结构

```
skill-name/
├── SKILL.md          # 必需：技能定义（YAML front matter + Markdown）
├── scripts/          # 可选：脚本文件（自动化任务逻辑）
├── templates/        # 可选：模板文件（代码模板）
└── resources/        # 可选：资源文件（代码片段、示例、配置文件等）
```

## 目录说明

### SKILL.md（必需）
- 技能的定义和说明文档
- 包含 YAML front matter（name、description、version、tags）
- 描述技能的用途、触发条件、使用方法等

### scripts/（可选）
- 执行自动化任务的脚本
- 例如：代码生成脚本、验证脚本、格式化脚本等
- 可以是任何可执行脚本（Shell、Python、Node.js 等）

### templates/（可选）
- 代码模板文件
- Agent 可以根据模板生成代码
- 支持占位符替换（如 `{packageName}`, `{entityName}` 等）

### resources/（可选）
- 资源文件和参考资料
- 代码片段示例
- 配置文件示例
- 使用文档和最佳实践

## 当前 Skills 结构

### java-rest-api-design
- ✅ `SKILL.md` - RESTful API 设计规范
- ✅ `templates/` - Controller 和 DTO 模板
- ✅ `resources/` - 使用示例和最佳实践

### java-service-layer
- ✅ `SKILL.md` - Service 层开发规范
- ✅ `templates/` - Service 接口和实现类模板
- ⬜ `scripts/` - （待添加）
- ⬜ `resources/` - （待添加）

### java-exception-handling
- ✅ `SKILL.md` - 异常处理规范
- ✅ `templates/` - 全局异常处理器模板
- ✅ `resources/` - 错误码定义示例
- ⬜ `scripts/` - （待添加）

### java-validation
- ✅ `SKILL.md` - 参数校验规范
- ✅ `templates/` - 自定义校验器模板
- ✅ `resources/` - 校验示例文档
- ⬜ `scripts/` - （待添加）

### java-response-wrapper
- ✅ `SKILL.md` - 统一响应格式规范
- ✅ `templates/` - Result 响应类模板
- ⬜ `scripts/` - （待添加）
- ⬜ `resources/` - （待添加）

### java-logging
- ✅ `SKILL.md` - 日志记录规范
- ✅ `resources/` - Logback 配置示例、脱敏工具类
- ⬜ `templates/` - （待添加）
- ⬜ `scripts/` - （待添加）

## 后续开发建议

1. **scripts/** - 可以添加：
   - 代码生成脚本
   - 代码检查脚本
   - 格式化脚本

2. **templates/** - 可以添加更多模板：
   - Repository 模板
   - Entity 模板
   - DTO 转换器模板

3. **resources/** - 可以添加：
   - 更多代码示例
   - 配置文件示例
   - 流程图和架构图

## 使用方式

Agent 在使用 Skill 时，会：
1. 读取 `SKILL.md` 了解技能定义
2. 参考 `templates/` 中的模板生成代码
3. 查看 `resources/` 中的示例和最佳实践
4. 执行 `scripts/` 中的脚本（如果 Agent 支持）

---

**注意**：所有后续开发都在 `java/` 目录中进行，`.agent/` 目录是运行时目录，不应提交到版本控制。
