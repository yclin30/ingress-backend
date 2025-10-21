# Ingress Backend Project

本项目是一个基于 Spring Boot 的现代化后端服务，为一个虚构的、基于地理位置的在线游戏 "Ingress" 提供核心功能支持。项目由 **@yclin30** 在 `2025-10-19` 完成。

## ✨ 项目特色

*   **RESTful API 设计**: 提供清晰、标准的 API 接口用于客户端交互。
*   **用户认证与授权**: 基于 JWT (JSON Web Token) 的无状态认证系统，确保接口安全。
*   **角色分离**: 内置 `USER` 和 `ADMIN` 两种用户角色，实现了精细的权限控制。
*   **实时通信**: 集成 WebSocket 和 STOMP 协议，实现了服务器与客户端之间的实时消息推送（如全局公告）和双向通信（如公共聊天室）。
*   **自动化 API 文档**: 集成 `springdoc-openapi`，自动生成可交互的 Swagger UI 文档和静态 HTML 文档。
*   **内存数据库**: 使用 H2 内存数据库，项目启动即可运行，无需额外配置数据库环境。

## 🚀 技术栈

*   **后端框架**: Spring Boot 3
*   **语言**: Java 17
*   **构建工具**: Maven
*   **数据库**: PostgreSQL
*   **数据访问**: Spring Data JPA / Hibernate
*   **安全**: Spring Security 6
*   **实时通信**: Spring WebSocket & STOMP
*   **API 文档**: SpringDoc OpenAPI (Swagger UI) & Redoc

## 🏁 快速开始

### 先决条件

*   JDK 17 或更高版本
*   Apache Maven 3.6 或更高版本

### 运行项目

1.  **克隆仓库**
    ```bash
    git clone <your-repository-url>
    cd ingress-backend
    ```

2.  **使用 Maven 运行**
    在项目根目录打开终端，执行以下命令：
    ```bash
    mvn spring-boot:run
    ```

3.  **服务启动**
    当您在控制台看到 `Started IngressBackendApplication` 的日志时，表示后端服务已在 `http://localhost:8080` 成功启动。

## 📖 API 端点概览

### 1. 认证模块 (`/api/auth`)

*   `POST /api/auth/register`
    *   **功能**: 注册新用户。
    *   **认证**: 无需。
*   `POST /api/auth/login`
    *   **功能**: 用户登录，成功后返回 JWT。
    *   **认证**: 无需。

### 2. 管理员模块 (`/api/admin`)

*所有此模块下的接口都需要 `ADMIN` 角色权限。*

*   `GET /api/admin/users`
    *   **功能**: 分页获取所有用户列表。
*   `POST /api/admin/users/{id}/ban`
    *   **功能**: 封禁指定 ID 的用户。
*   `POST /api/admin/users/{id}/unban`
    *   **功能**: 解封指定 ID 的用户。
*   `POST /api/admin/locations`
    *   **功能**: 创建一个新的游戏据点。
*   `POST /api/admin/announcements`
    *   **功能**: 发布一条全局公告，并通过 WebSocket 广播。

### 3. 据点模块 (`/api/location`)

*   `GET /api/location/{id}`
    *   **功能**: 获取指定 ID 的据点详情。
    *   **认证**: 需要 `USER` 或 `ADMIN` 角色权限。

### 4. WebSocket 实时通信

*   **连接端点**: `ws://localhost:8080/ws`
*   **协议**: STOMP over WebSocket
*   **认证**: 在 STOMP 的 `CONNECT` 帧中通过 `Authorization: Bearer <JWT>` 头进行认证。

#### STOMP 目标地址 (Destinations)

*   **订阅全局公告**: `/topic/announcements`
*   **订阅公共聊天**: `/topic/public`
*   **发送聊天消息**: `/app/chat.sendMessage`

## 📚 API 文档

本项目提供了两种行业标准的 API 文档。

### 1. 动态交互式文档 (Swagger UI)

*   **访问地址**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
*   **功能**: 在浏览器中直接浏览、测试所有 API 端点，无需使用 Postman 等外部工具。

### 2. 静态文档 (Redoc)

https://ingress-backend.vercel.app/

---

**项目作者**: yclin30
