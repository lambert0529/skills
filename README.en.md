# Skills Collection

> A collection of AI Agent Skills following the Anthropic Skills open standard for Java backend development.

English | [中文](./README.md)

---

## 📖 Overview

This repository contains a collection of **Agent Skills** that follow the [Anthropic Skills open standard](https://github.com/anthropics/skills). These skills are designed to help AI programming assistants (Cursor, Claude Code, Windsurf, Aider, etc.) generate high-quality Java backend code that adheres to industry best practices.

## ✨ Features

- 🌐 **Open Standard**: Follows the Anthropic Skills specification for cross-platform compatibility
- 📦 **Progressive Loading**: Skills are loaded on-demand, saving context window space
- 🔄 **Reusable**: Share skills across multiple projects and AI programming assistants
- 📚 **Comprehensive**: Covers core aspects of Java backend development
- 🎯 **Best Practices**: Based on Spring Boot and industry standards

## 📦 Available Skills

### 1. `java-rest-api-design`
**RESTful API Design Standards**
- RESTful path design
- Controller layer structure
- Request/Response DTO design
- HTTP status code usage
- API versioning strategy

### 2. `java-service-layer`
**Service Layer Development Standards**
- Interface and implementation separation
- Transaction management
- Business logic encapsulation
- Exception handling
- Logging

### 3. `java-exception-handling`
**Exception Handling Standards**
- Custom business exception classes
- Global exception handler
- Error code definitions
- Exception response format
- Exception logging

### 4. `java-validation`
**Parameter Validation Standards**
- Bean Validation annotations
- Custom validators
- Group validation
- Nested object validation
- Validation error handling

### 5. `java-response-wrapper`
**Unified Response Format Standards**
- Unified response wrapper class
- Success response format
- Failure response format
- Pagination response format
- Response status code definitions

### 6. `java-logging`
**Logging Standards**
- Log level usage
- Log format standards
- Sensitive information masking
- Performance optimization
- Structured logging

## 🚀 Quick Start

### Prerequisites

- Node.js ≥ v20.6
- Git (for installing skills from GitHub)

### Install OpenSkills CLI

```bash
# Install OpenSkills CLI globally
npm install -g openskills

# Verify installation
openskills --version
```

### Installation Methods

#### Method 1: Install from GitHub (Recommended)

The repository is available on GitHub, you can install directly:

```bash
# Execute in your project root directory
cd /path/to/your/project

# Install all Java skills from GitHub repository
openskills install lambert0529/skills/java --universal

# Or install skills from the entire repository (if skills are in root)
openskills install lambert0529/skills --universal
```

**Note**: If skills are in a subdirectory (like `java/`), OpenSkills will try to find skills from that path. If the repository root directly contains skill directories, you can install the entire repository.

#### Method 2: Install from Local Path

1. **Clone the repository**:
```bash
git clone https://github.com/lambert0529/skills.git
cd skills
```

2. **Install skills**:
```bash
# Install all skills from java directory to your project
openskills install ./java --universal

# Or install a single skill
openskills install ./java/java-rest-api-design --universal
```

#### Method 3: Global Installation (Shared Across All Projects)

```bash
# Install globally from GitHub
openskills install lambert0529/skills/java --global

# Or install globally from local path
openskills install ./java --global
```

### Sync Skills to AGENTS.md (for Cursor)

After installation, sync skill information to AGENTS.md so Cursor Agent can discover them:

```bash
# Execute in project root directory
openskills sync --yes -o .cursor/rules/AGENTS.md
```

### Verify Installation

```bash
# List installed skills
openskills list

# Read skill content (verify)
openskills read java-rest-api-design
```

### Usage

**In Cursor**:
- Skills will be automatically discovered from `.cursor/rules/AGENTS.md`
- Simply ask the AI assistant to use the skills:
  - "Create a REST API for user management"
  - "Create a user Service layer with transaction management"
  - "Design exception handling mechanism"
  - "Add parameter validation for create user request"
  - "Design unified response format"
  - "Add logging to Service"

**In Claude Code**:
- Skills are automatically discovered from `.agent/skills/` or `.claude/skills/`
- No additional configuration needed

## 📁 Project Structure

```
skills/
├── README.md                 # This file (Chinese)
├── README.en.md              # English README
├── LICENSE                   # Apache 2.0 License
├── doc/                      # Documentation
│   └── Skills介绍及使用方式.md
├── java/                     # Java Skills (development)
│   ├── README.md
│   ├── java-rest-api-design/
│   ├── java-service-layer/
│   ├── java-exception-handling/
│   ├── java-validation/
│   ├── java-response-wrapper/
│   └── java-logging/
└── .agent/skills/            # Installed skills (runtime)
    └── ...
```

## 🎯 Standards Coverage

These skills cover core aspects of Java backend development:

- ✅ RESTful API design
- ✅ Layered architecture (Controller-Service-Repository)
- ✅ Exception handling mechanism
- ✅ Parameter validation
- ✅ Unified response format
- ✅ Logging standards
- ✅ Transaction management
- ✅ Code standards

## 📚 Documentation

- [Skills Introduction and Usage Guide (Chinese)](./doc/Skills介绍及使用方式.md)
- [Java Skills README](./java/README.md)

## 🔗 Related Resources

- [Anthropic Skills Standard](https://github.com/anthropics/skills)
- [Anthropic Skills Marketplace](https://github.com/anthropics/skills)
- [OpenSkills CLI](https://github.com/numman-ali/openskills)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Bean Validation Specification](https://beanvalidation.org/)

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingSkill`)
3. Commit your changes (`git commit -m 'Add some AmazingSkill'`)
4. Push to the branch (`git push origin feature/AmazingSkill`)
5. Open a Pull Request

## 📝 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- [Anthropic](https://www.anthropic.com/) for defining the Skills open standard
- [OpenSkills](https://github.com/numman-ali/openskills) for providing the CLI tool
- The Java and Spring Boot communities for best practices

---

**Last Updated**: 2026-01-19
