# 智能家居控制系统项目设计文档

## 项目概述

智能家居控制系统是一个多端集成的系统，旨在实现对家庭设备的智能控制和管理。系统支持PC端、移动Android端、移动iOS端和微信小程序端，通过云端服务与下位机设备进行通信，实现设备状态监控、远程控制、自动化规则等功能。

## 系统架构

系统采用四层架构设计：

1. **感知层**：各类传感器和家庭设施
2. **边缘层**：本地控制中枢嵌入式设备
3. **云端层**：服务器后端与数据库
4. **应用层**：Web端、移动Android端、移动iOS端和微信小程序端

### 架构图
```
+--------------------------------------------------+
|                    应用层                         |
|  +----------+  +----------+  +----------+        |
|  |   PC端   |  | 移动Android| | 移动iOS端 |       |
|  |  (Web)   |  |   (APP)   | |  (APP)   |       |
|  +----------+  +----------+  +----------+        |
|         \           |              /              |
|          \          |             /               |
|           \         |            /                |
|  +------------------------------------------+    |
|  |              微信小程序端                  |    |
|  |              (MiniApp)                   |    |
|  +------------------------------------------+    |
+--------------------------------------------------+
                              |
                              v
+--------------------------------------------------+
|                    云端层                         |
|  +-------------------+ +---------------------+   |
|  |   后端服务        | |    数据库           |   |
|  |   (Spring Boot)   | |   (MySQL/Redis)   |   |
|  +-------------------+ +---------------------+   |
+--------------------------------------------------+
                              |
                              v
+--------------------------------------------------+
|                    边缘层                         |
|              下位机设备 (嵌入式)                  |
|        (传感器、LED、窗帘等执行设备)                |
+--------------------------------------------------+
                              |
                              v
+--------------------------------------------------+
|                    感知层                         |
|           各类传感器和家庭设施                     |
+--------------------------------------------------+
```

## 功能模块

### 云端层功能
- 用户管理：注册、登录、权限控制
- 设备管理：设备添加、编辑、删除、状态监控
- 区域管理：房屋区域划分和管理
- 自动化规则：基于设备状态的自动化控制规则
- 实时通信：与下位机设备的MQTT通信
- 数据存储：用户数据、设备数据、规则数据的持久化

### 应用层功能
- **PC端**：全面的设备管理、规则设置、数据分析
- **移动Android端**：便捷的设备控制、状态查看、通知提醒
- **移动iOS端**：便捷的设备控制、状态查看、通知提醒
- **微信小程序端**：快速设备控制、常用功能访问

## 技术栈

### 云端层
- **后端框架**：Spring Boot
- **数据库**：MySQL + Redis
- **消息协议**：MQTT
- **实时通信**：WebSocket
- **持久化框架**：MyBatis Plus

### 应用层
- **PC端**：Vue 3 + TypeScript + Element Plus
- **移动Android/iOS端**：React Native 或 uni-app
- **微信小程序**：原生小程序或uni-app

## 通信协议

### 云端与下位机通信
- **协议**：MQTT
- **消息格式**：JSON
- **消息结构**：
  ```
  {
    "user_id": "用户ID",
    "timestamp": "时间戳",
    "message_type": "消息类型",
    "data": {
      "area": "区域",
      "device_type": "设备类型",
      "device_id": "设备ID",
      "status": { "状态数据" },
      "command": { "控制命令" }
    }
  }
  ```

### 应用层与云端通信
- **协议**：HTTP/HTTPS + WebSocket
- **接口规范**：RESTful API
- **消息格式**：JSON

## 数据库设计

### 用户表 (users)
- id: BIGINT (主键)
- username: VARCHAR(50) (用户名)
- password: VARCHAR(255) (加密密码)
- phone: VARCHAR(20) (手机号)
- created_at: DATETIME (创建时间)
- last_login_at: DATETIME (最后登录时间)

### 房屋区域表 (house_areas)
- id: BIGINT (主键)
- user_id: BIGINT (用户ID，外键)
- area_name: VARCHAR(50) (区域名称)
- area_type: VARCHAR(20) (区域类型)
- created_at: DATETIME (创建时间)
- updated_at: DATETIME (更新时间)

### 设备表 (devices)
- id: BIGINT (主键)
- user_id: BIGINT (用户ID，外键)
- area_id: BIGINT (区域ID，外键)
- device_id: VARCHAR(100) (设备唯一ID)
- device_type: VARCHAR(50) (设备类型)
- device_name: VARCHAR(100) (设备名称)
- status_name: VARCHAR(100) (状态名称)
- created_at: DATETIME (创建时间)
- updated_at: DATETIME (更新时间)

### 连接状态表 (connection_status)
- id: BIGINT (主键)
- device_id: VARCHAR(100) (设备ID)
- user_id: VARCHAR(100) (用户ID)
- connection_status: VARCHAR(20) (连接状态)
- connection_time: DATETIME (连接时间)
- disconnection_time: DATETIME (断开连接时间)
- last_heartbeat: DATETIME (最后心跳时间)
- created_at: DATETIME (创建时间)
- updated_at: DATETIME (更新时间)

### 自动化规则表 (automation_rules)
- id: BIGINT (主键)
- user_id: BIGINT (用户ID，外键)
- rule_name: VARCHAR(100) (规则名称)
- trigger_type: VARCHAR(30) (触发类型)
- trigger_condition: JSON (触发条件)
- target_device_id: VARCHAR(100) (目标设备ID)
- target_device_type: VARCHAR(50) (目标设备类型)
- command_type: VARCHAR(50) (命令类型)
- command_parameters: JSON (命令参数)
- is_enabled: TINYINT (是否启用)
- created_at: DATETIME (创建时间)
- updated_at: DATETIME (更新时间)

## 项目结构

```
smart-home/
├── backend/              # 后端服务代码
│   ├── src/main/java/com/smart/home/
│   │   ├── common/       # 公共组件
│   │   ├── config/       # 配置类
│   │   ├── controller/   # 控制器
│   │   ├── mapper/       # 数据访问层
│   │   ├── model/        # 数据模型
│   │   │   ├── entity/   # 实体类
│   │   │   └── dto/      # 数据传输对象
│   │   ├── service/      # 业务逻辑层
│   │   └── utils/        # 工具类
│   └── src/main/resources/
│       ├── application.yml
│       └── mapper/       # MyBatis映射文件
├── frontend/             # 前端代码
│   ├── shared/           # 跨端共享代码
│   ├── web/              # PC端Web代码
│   ├── mobile/           # 移动端代码
│   └── wechat-app/       # 微信小程序代码
├── embedded/             # 嵌入式设备代码
├── docs/                 # 项目文档
├── scripts/              # 脚本文件
├── DESIGN.md             # 项目设计文档
├── README.md             # 项目说明
└── package.json          # 项目配置
```

## 开发规范

### 代码规范
- 后端使用Java，遵循Spring Boot最佳实践
- 前端使用TypeScript，遵循组件化开发模式
- 统一的代码格式化和命名规范
- 完善的注释和文档

### 接口规范
- RESTful API设计原则
- 统一的错误码和响应格式
- 接口版本管理
- 接口文档自动化生成

## 部署方案

### 后端部署
- 容器化部署（Docker）
- 支持云原生部署（Kubernetes）
- 数据库主从配置
- 负载均衡配置

### 前端部署
- PC端：静态资源部署到CDN
- 移动端：构建原生应用包发布到应用商店
- 小程序：上传到微信平台审核发布

## 安全考虑

- 用户认证和授权机制
- API接口访问控制
- 数据传输加密
- 敏感信息加密存储
- 防止常见Web安全漏洞
