# 智能家居控制系统 - 后端整体设计文档

## 1. 项目概述

智能家居控制系统后端采用 Spring Boot 3.5.9 构建，基于 Java 17 开发，实现对家庭设备的智能控制和管理。系统支持用户管理、设备管理、区域管理、自动化规则等功能，通过 RESTful API 与前端交互，并通过 MQTT 协议与下位机设备通信。

## 2. 系统架构

### 2.1 技术架构

系统采用典型的 Spring Boot 单体应用架构，分层结构包括：

- **Web 层**: 处理 HTTP 请求，实现 RESTful API
- **Service 层**: 实现业务逻辑处理
- **Data Access 层**: 负责数据持久化操作
- **Infrastructure 层**: 提供配置、工具等基础设施

```
+------------------+
|   Frontend Apps  |  <- RESTful API
+------------------+
         |
         v
+------------------+
|   Controller     |  <- Spring MVC
+------------------+
         |
         v
+------------------+
|    Service       |  <- Business Logic
+------------------+
         |
         v
+------------------+
|   Repository/    |  <- Data Access
|   Mapper         |
+------------------+
         |
         v
+------------------+
|    Database      |  <- MySQL/Redis
+------------------+
         |
         v
+------------------+
|   MQTT Broker    |  <- Device Communication
+------------------+
         |
         v
+------------------+
|  Lower Computer  |  <- Physical Devices
+------------------+
```

### 2.2 技术栈

- **后端框架**: Spring Boot 3.5.9
- **运行环境**: Java 17
- **数据库**: MySQL 8.0 + Redis 7
- **消息协议**: MQTT
- **实时通信**: WebSocket
- **持久化框架**: JPA + MyBatis Plus
- **安全框架**: Spring Security
- **API文档**: Spring REST Docs + Asciidoctor

## 3. 模块设计

### 3.1 配置模块 (config)

#### 3.1.1 AppProperties
使用 `@ConfigurationProperties` 注解管理应用自定义配置，包括：
- JWT 配置：密钥、过期时间
- MQTT 配置：服务器地址、认证信息
- WebSocket 配置：端点路径

#### 3.1.2 数据库健康检查
- `DatabaseHealthIndicator` 实现了 `HealthIndicator` 接口
- 验证数据库连接状态
- 通过 `isValid(5)` 检查连接有效性

#### 3.1.3 Redis 配置
- 配置 RedisTemplate 用于数据缓存
- 设置 JSON 序列化器
- 配置连接池参数

#### 3.1.4 WebSocket 配置
- 配置 WebSocket 端点和消息代理
- 启用消息代理转发
- 设置消息传输参数

### 3.2 数据实体 (entity)

系统包含以下核心实体：

#### 3.2.1 用户实体 (User)
- id: Long (主键)
- username: String (用户名)
- password: String (加密密码)
- phone: String (手机号)
- createdAt: LocalDateTime (创建时间)
- lastLoginAt: LocalDateTime (最后登录时间)

#### 3.2.2 房屋区域实体 (HouseArea)
- id: Long (主键)
- userId: Long (用户ID，外键)
- areaName: String (区域名称)
- areaType: String (区域类型)
- createdAt: LocalDateTime (创建时间)
- updatedAt: LocalDateTime (更新时间)

#### 3.2.3 设备实体 (Device)
- id: Long (主键)
- userId: Long (用户ID，外键)
- areaId: Long (区域ID，外键)
- deviceId: String (设备唯一ID)
- deviceType: String (设备类型)
- deviceName: String (设备名称)
- statusName: String (状态名称)
- createdAt: LocalDateTime (创建时间)
- updatedAt: LocalDateTime (更新时间)

#### 3.2.4 自动化规则实体 (AutomationRule)
- id: Long (主键)
- userId: Long (用户ID，外键)
- ruleName: String (规则名称)
- triggerType: String (触发类型)
- triggerCondition: String (触发条件，JSON格式)
- targetDeviceId: String (目标设备ID)
- targetDeviceType: String (目标设备类型)
- commandType: String (命令类型)
- commandParameters: String (命令参数，JSON格式)
- isEnabled: Boolean (是否启用)
- createdAt: LocalDateTime (创建时间)
- updatedAt: LocalDateTime (更新时间)

### 3.3 业务逻辑层 (service)

#### 3.3.1 用户服务 (UserService)
- 用户注册、登录
- 密码加密与验证
- JWT Token 生成与验证
- 用户信息更新

#### 3.3.2 设备服务 (DeviceService)
- 设备注册、编辑、删除
- 设备状态更新
- 设备控制命令发送
- 设备状态查询

#### 3.3.3 区域服务 (HouseAreaService)
- 区域创建、编辑、删除
- 区域设备关联管理
- 区域设备统计

#### 3.3.4 自动化规则服务 (AutomationRuleService)
- 规则创建、编辑、删除
- 规则启用/禁用
- 规则执行逻辑
- 规则触发检查

#### 3.3.5 MQTT 服务 (MqttService)
- MQTT 连接管理
- 设备状态上报处理
- 设备控制命令下发
- 设备在线状态管理

### 3.4 控制器层 (controller)

#### 3.4.1 用户控制器 (UserController)
- `/api/auth/register` - 用户注册
- `/api/auth/login` - 用户登录
- `/api/user/profile` - 获取用户信息
- `/api/user/update` - 更新用户信息

#### 3.4.2 设备控制器 (DeviceController)
- `/api/devices` - 获取设备列表
- `/api/devices/{id}` - 获取设备详情
- `/api/devices` - 创建设备
- `/api/devices/{id}` - 更新设备
- `/api/devices/{id}` - 删除设备
- `/api/devices/{id}/control` - 控制设备

#### 3.4.3 区域控制器 (HouseAreaController)
- `/api/areas` - 获取区域列表
- `/api/areas/{id}` - 获取区域详情
- `/api/areas` - 创建区域
- `/api/areas/{id}` - 更新区域
- `/api/areas/{id}` - 删除区域

#### 3.4.4 自动化规则控制器 (AutomationRuleController)
- `/api/rules` - 获取规则列表
- `/api/rules/{id}` - 获取规则详情
- `/api/rules` - 创建规则
- `/api/rules/{id}` - 更新规则
- `/api/rules/{id}` - 删除规则
- `/api/rules/{id}/toggle` - 启用/禁用规则

#### 3.4.5 WebSocket 控制器
- `/ws/device-status` - 设备状态实时推送
- `/ws/automation-events` - 自动化事件推送

## 4. 安全设计

### 4.1 认证机制
- 使用 JWT 进行用户认证
- Token 包含用户ID和过期时间
- 拦截器验证请求中的 Token

### 4.2 授权机制
- 基于角色的访问控制 (RBAC)
- 不同接口设置不同的访问权限
- 用户只能访问自己的数据

### 4.3 数据安全
- 密码使用 BCrypt 加密存储
- API 接口使用 HTTPS 传输
- 敏感信息加密存储

## 5. 部署配置

### 5.1 数据库配置
- MySQL 8.0 数据库
- 连接池使用 HikariCP
- 配置参数包括连接超时、验证超时等

### 5.2 Redis 配置
- Redis 7 作为缓存服务
- 配置连接池参数
- 设置缓存过期策略

### 5.3 应用配置
- 配置文件使用 application.properties
- 包含数据库、Redis、MQTT、JWT 等配置
- 支持多环境配置 (dev, test, prod)

## 6. 监控与健康检查

### 6.1 数据库健康检查
- 通过 `DatabaseHealthIndicator` 检查数据库连接
- 提供详细的连接信息

### 6.2 应用监控
- 集成 Spring Boot Actuator
- 提供应用运行状态信息
- 监控关键指标

## 7. 开发规范

### 7.1 代码规范
- 使用驼峰命名法
- 统一的代码格式化
- 完善的注释和文档

### 7.2 接口规范
- RESTful API 设计原则
- 统一的错误码和响应格式
- 接口版本管理