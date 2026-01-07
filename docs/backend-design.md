# 智能家居控制系统后端设计文档

## 1. 系统概述

智能家居控制系统后端负责云端与下位机设备之间的通信，以及与前端的交互。系统采用微服务架构，主要处理设备数据上报、控制命令下发、用户管理、设备管理和自动化规则等功能。

## 2. 架构设计

### 2.1 系统架构图

```
┌─────────────┐    MQTT    ┌──────────────────┐    HTTP/WS    ┌──────────────────┐
│             │ ────────→  │                  │ ───────────→  │                  │
│  下位机设备  │ ←──────── │  智能家居后端    │ ←──────────   │    前端应用      │
│             │   控制     │                  │   API/WS      │  (Web/小程序)    │
└─────────────┘           └──────────────────┘             └──────────────────┘
```

### 2.2 技术栈

- **编程语言**: Java 11+
- **框架**: Spring Boot 2.7+
- **数据库**: MySQL 8.0
- **消息队列**: MQTT (Eclipse Paho)
- **ORM框架**: MyBatis Plus
- **缓存**: Redis
- **认证**: JWT
- **WebSocket**: 实现实时通信

## 3. 数据库设计

### 3.1 表结构设计

#### 3.1.1 用户表 (users)
- id: BIGINT PRIMARY KEY - 用户唯一标识
- username: VARCHAR(50) - 用户名
- password: VARCHAR(255) - 加密密码
- phone: VARCHAR(20) - 手机号
- created_at: DATETIME - 创建时间
- last_login_at: DATETIME - 最后登录时间

#### 3.1.2 房屋区域表 (house_areas)
- id: BIGINT PRIMARY KEY - 区域唯一标识
- user_id: BIGINT - 创建者用户ID (外键关联用户表)
- area_name: VARCHAR(50) - 区域名称
- area_type: VARCHAR(20) - 区域类型 (fixed-固定区域, general-通用区域)
- created_at: DATETIME - 创建时间
- updated_at: DATETIME - 更新时间

#### 3.1.3 设备表 (devices)
- id: BIGINT PRIMARY KEY - 设备唯一标识
- device_id: VARCHAR(100) UNIQUE - 设备唯一ID
- device_type: VARCHAR(50) - 设备类型
- device_name: VARCHAR(100) - 设备名称
- created_at: DATETIME - 创建时间
- updated_at: DATETIME - 更新时间

#### 3.1.4 用户设备权限表 (user_device_permissions) - 新增
- id: BIGINT PRIMARY KEY - 权限记录唯一标识
- user_id: BIGINT - 用户ID (外键关联用户表)
- device_id: VARCHAR(100) - 设备ID (外键关联设备表)
- permission_level: VARCHAR(20) - 权限等级 (owner-所有者, admin-管理员, user-普通用户)
- created_at: DATETIME - 创建时间
- updated_at: DATETIME - 更新时间

#### 3.1.5 连接状态表 (connection_status)
- id: BIGINT PRIMARY KEY - 记录唯一标识
- device_id: VARCHAR(100) - 设备ID
- user_id: VARCHAR(100) - 用户ID
- connection_status: VARCHAR(20) - 连接状态 (online-在线, offline-离线)
- connection_time: DATETIME - 连接时间
- disconnection_time: DATETIME - 断开连接时间
- last_heartbeat: DATETIME - 最后心跳时间
- created_at: DATETIME - 创建时间
- updated_at: DATETIME - 更新时间

#### 3.1.6 自动化规则表 (automation_rules)
- id: BIGINT PRIMARY KEY - 规则唯一标识
- user_id: BIGINT - 创建者用户ID (外键关联用户表)
- rule_name: VARCHAR(100) - 规则名称
- trigger_type: VARCHAR(30) - 触发类型 (device_status-设备状态, time_based-定时)
- trigger_condition: JSON - 触发条件
- target_device_id: VARCHAR(100) - 目标设备ID
- target_device_type: VARCHAR(50) - 目标设备类型
- command_type: VARCHAR(50) - 命令类型
- command_parameters: JSON - 命令参数
- is_enabled: TINYINT(1) - 是否启用
- created_at: DATETIME - 创建时间
- updated_at: DATETIME - 更新时间

#### 3.1.7 设备状态表 (device_status)
- id: BIGINT PRIMARY KEY - 状态记录唯一标识
- device_id: VARCHAR(100) - 设备ID (外键关联设备表)
- status_data: JSON - 状态数据 (JSON格式存储)
- created_at: DATETIME - 创建时间
- updated_at: DATETIME - 更新时间

## 4. 模块设计

### 4.1 用户管理模块

#### 4.1.1 功能描述
- 用户注册、登录
- 用户信息管理
- 密码修改

#### 4.1.2 实现要点
- 使用JWT进行身份认证
- 密码使用BCrypt加密存储
- 支持手机号登录

### 4.2 设备管理模块

#### 4.2.1 功能描述
- 设备注册与绑定
- 设备信息管理
- 设备状态查询
- 设备控制

#### 4.2.2 多用户对多设备关系
- 支持多个用户共享同一个设备
- 设备权限分级管理 (所有者、管理员、普通用户)
- 提供设备分享功能

### 4.3 MQTT通信模块

#### 4.3.1 功能描述
- 与下位机设备的MQTT通信
- 消息解析与路由
- 设备连接状态管理

#### 4.3.2 消息处理流程
1. 接收下位机上报的消息
2. 解析消息内容
3. 验证设备归属权限
4. 更新设备状态
5. 触发自动化规则

### 4.4 自动化规则模块

#### 4.4.1 功能描述
- 规则创建、编辑、删除
- 规则触发条件检测
- 规则执行管理

#### 4.4.2 规则类型
- 设备状态触发规则
- 定时触发规则
- 组合条件规则

## 5. API设计

### 5.1 统一响应格式

```json
{
  "code": 200,
  "message": "Success",
  "data": {},
  "timestamp": 1678886400000
}
```

### 5.2 认证机制

- 使用JWT Token进行身份认证
- 请求头格式: `Authorization: Bearer {token}`

## 6. 安全设计

### 6.1 认证与授权
- 用户登录后生成JWT Token
- 所有敏感接口需要验证Token
- 设备操作需要验证用户权限

### 6.2 数据安全
- 用户密码使用BCrypt加密
- 敏感数据传输使用HTTPS
- 防止SQL注入和XSS攻击

## 7. 性能优化

### 7.1 数据库优化
- 合理使用索引
- 分页查询大数据量
- 使用Redis缓存热点数据

### 7.2 消息处理优化
- 异步处理设备消息
- 使用连接池管理MQTT连接
- 消息队列处理高并发

## 8. 部署设计

### 8.1 环境要求
- Java 11+
- MySQL 8.0+
- Redis
- MQTT Broker

### 8.2 部署方式
- Docker容器化部署
- 支持集群部署
- 负载均衡配置
