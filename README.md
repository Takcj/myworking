# 智能家居控制系统

基于Spring Boot的智能家居控制系统，支持多用户、多区域设备管理，提供Web端和微信小程序前端界面。

## 项目结构

```
smart-home/
├── backend/                 # 后端服务代码
│   ├── api/                 # API接口定义
│   ├── service/             # 业务逻辑
│   │   ├── mqtt/            # MQTT消息处理
│   │   ├── device/          # 设备数据处理
│   │   ├── area/            # 区域管理服务
│   │   ├── user/            # 用户管理服务
│   │   ├── connection/      # 连接状态管理
│   │   └── automation/      # 自动化规则服务
│   ├── mapper/              # 数据访问层
│   ├── controller/          # 控制器
│   ├── common/              # 公共组件
│   ├── model/               # 数据模型
│   ├── config/              # 配置文件
│   └── dto/                 # 数据传输对象
├── frontend/                # 前端代码
│   ├── web/                 # Web端代码
│   │   ├── src/             # 源码
│   │   ├── public/          # 静态资源
│   │   └── package.json     # 依赖配置
│   └── wechat-app/          # 微信小程序代码
├── embedded/                # 嵌入式设备代码
├── docs/                    # 项目文档
├── scripts/                 # 脚本文件
├── README.md                # 项目说明
└── package.json             # 项目配置
```

## 技术栈

- **后端**: Java 17 + Spring Boot 3.2.0 + MyBatis Plus + MySQL 8.0
- **前端**: Vue.js 3.x + Element Plus (Web端), 微信小程序原生开发
- **通信协议**: MQTT (设备通信), WebSocket (实时推送), HTTP/RESTful API
- **消息队列**: Redis (缓存和会话管理)

## 功能特性

1. **多用户支持**：每个用户独立管理自己的设备和区域
2. **区域化管理**：支持客厅、卧室、厨房等固定区域及通用区域
3. **设备统一管理**：支持传感器和执行设备的统一管理
4. **实时通信**：通过WebSocket实现实时数据推送
5. **自动化规则**：支持自定义自动化规则
6. **MQTT协议**：轻量级、低功耗的设备通信协议

## 快速开始

1. 克隆项目
2. 配置数据库连接信息
3. 启动后端服务
4. 启动前端项目
5. 通过Web端或微信小程序访问

## 文档

- [总体设计文档](DESIGN.md)
- [后端详细设计文档](docs/backend-design.md)