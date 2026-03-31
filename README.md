# 阿木简易记账

一个简洁好用的个人记账小程序，包含微信小程序端、后台管理系统和后端服务。

---

## 项目简介

阿木记账是一款帮助用户记录日常收支的记账应用。用户可以通过微信小程序快速记录每一笔消费和收入，并在后台管理系统中查看统计数据和管理分类。

### 主要功能

- **记账功能**：快速记录收入和支出，支持自定义分类和备注
- **数据统计**：今日支出、本月收支一目了然
- **报表展示**：可视化图表展示消费趋势
- **分类管理**：支持系统预置分类和用户自定义分类
- **用户管理**：后台可管理用户信息和数据

---

## 项目结构

```
amu-bookkeeping-App/
├── AmuAccountBook-weapp/      # 微信小程序（用户端）
├── AmuAccountBook-admin/      # 后台管理系统（管理端）
└── AmuAccountBook-server/     # 后端服务（API）
```

---

## 技术栈

### 微信小程序端
- **uni-app** - 跨平台开发框架
- **Vue 3** - 前端框架
- **Pinia** - 状态管理
- **uview-plus** - UI 组件库

### 后台管理系统
- **Vue 3** - 前端框架
- **Vite** - 构建工具
- **Element Plus** - UI 组件库
- **Pinia** - 状态管理
- **Axios** - HTTP 请求

### 后端服务
- **Spring Boot 3** - Java 后端框架
- **MyBatis-Plus** - ORM 框架
- **Sa-Token** - 权限认证
- **MySQL** - 数据库
- **Redis** - 缓存

---

## 快速开始

### 环境要求

- Node.js 20+ 
- Java 21
- MySQL 8.0+
- Redis
- 微信开发者工具（用于小程序）

### 后端服务启动

1. 创建数据库
```sql
CREATE DATABASE bookkeeping_app;
```

2. 修改配置文件 `application.yml`，配置数据库和 Redis 连接信息

3. 启动后端服务
```bash
cd AmuAccountBook-server/bookkeeping-App
./mvnw spring-boot:run
```

后端服务启动后，可访问 Swagger API 文档：`http://localhost:8080/bookkeeping/swagger-ui.html`

### 后台管理系统启动

```bash
cd AmuAccountBook-admin/amu-bookkeeping
npm install
npm run dev
```

### 微信小程序启动

1. 使用微信开发者工具打开 `AmuAccountBook-weapp/amu-accountbook` 目录
2. 在开发者工具中编译运行

---

### 微信小程序
- **首页**：展示今日支出、本月收支统计，以及交易明细列表
- **报表**：可视化图表展示消费数据
- **我的**：个人中心页面

### 后台管理系统
- **分类管理**：管理收入/支出分类
- **用户管理**：查看和管理用户信息
- **数据分析**：查看平台数据统计

---

## 项目亮点

1. **全栈开发**：从数据库设计到前后端开发，完整体验全栈开发流程
2. **微信登录**：集成微信小程序登录功能
3. **权限认证**：使用 Sa-Token 实现用户认证
4. **响应式设计**：后台管理系统适配不同屏幕尺寸
5. **可拖拽悬浮按钮**：小程序端记账按钮支持拖拽移动

---

## 许可证

MIT License
