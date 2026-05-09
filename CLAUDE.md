# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述
这是一个基于Android Jetpack Compose开发的中国象棋APP项目，目前处于初始模板阶段。

## 技术栈
- 编程语言：Kotlin
- UI框架：Jetpack Compose (Material3)
- 构建工具：Gradle (KTS)
- 最低支持SDK：24 (Android 7.0)
- 目标SDK：36 (Android 14)

## 常用命令

### 构建项目
```bash
./gradlew build           # 全量构建
./gradlew assembleDebug   # 构建Debug版本
./gradlew assembleRelease # 构建Release版本
```

### 运行项目
```bash
./gradlew installDebug    # 安装Debug版本到连接的设备
```

### 测试
```bash
./gradlew test            # 运行本地单元测试
./gradlew connectedAndroidTest # 运行设备上的Instrumented测试
./gradlew testDebugUnitTest --tests "com.example.myapplication.ExampleUnitTest" # 运行单个测试类
```

### 其他命令
```bash
./gradlew lint            # 运行Lint检查
./gradlew clean           # 清理构建产物
```

## 代码架构
项目采用标准的Android Compose单模块结构：
- `app/src/main/java/com/example/myapplication/` - 主代码目录
  - `MainActivity.kt` - 应用入口Activity
  - `ui/theme/` - Compose主题定义（颜色、字体、主题）
- `app/src/main/res/` - 资源文件（布局、图片、字符串、样式）
- `app/src/test/` - 本地单元测试
- `app/src/androidTest/` - Instrumented测试

## 开发准则
本项目采用Karpathy行为准则来减少常见的LLM编码错误：

### 1. 编码前思考
- 明确陈述假设，不确定时询问
- 存在多种解释时全部提出，不默默选择
- 存在更简单方案时提出，必要时反对不合理需求
- 遇到不清楚的地方停止，说明困惑点并询问

### 2. 简洁优先
- 不添加超出需求的功能
- 不为单次使用的代码创建抽象
- 不添加未请求的"灵活性"或"可配置性"
- 不为不可能发生的场景添加错误处理
- 200行能完成的代码不要写500行

### 3. 精确修改
- 只修改必须修改的部分，不"改进"相邻代码、注释或格式
- 不重构未损坏的代码
- 匹配现有代码风格，即使与你的习惯不同
- 只删除你修改产生的未使用导入/变量/函数

### 4. 目标驱动执行
- 将任务转化为可验证的目标：
  - "添加验证" → "先写无效输入测试，再使其通过"
  - "修复Bug" → "先写重现测试，再使其通过"
  - "重构" → "确保重构前后测试都通过"
- 多步骤任务先列出简要计划和每个步骤的验证方式
