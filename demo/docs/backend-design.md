# 智能家居控制系统 - 后端详细设计文档

## 1. 项目概述

智能家居控制系统后端采用 Spring Boot 3.5.9 构建，基于 Java 17 开发，实现对家庭设备的智能控制和管理。系统支持用户管理、设备管理、区域管理、自动化规则等功能。

## 2. 技术架构

### 2.1 技术栈
- **后端框架**: Spring Boot 3.5.9
- **运行环境**: Java 17
- **数据库**: MySQL 8.0 + Redis 7
- **消息协议**: MQTT
- **实时通信**: WebSocket
- **持久化框架**: JPA + MyBatis Plus
- **安全框架**: Spring Security
- **API文档**: Spring REST Docs + Asciidoctor
- **配置管理**: Spring Configuration Properties

### 2.2 项目结构
```
src/main/java/com/example/demo/
├── common/                 # 公共组件
├── config/                 # 配置类
├── controller/             # 控制器
├── mapper/                 # 数据访问层（MyBatis）
├── entity/                 # 实体类
├── dto/                    # 数据传输对象
├── service/                # 业务逻辑层
├── repository/             # JPA 数据访问层
└── utils/                  # 工具类
```

## 3. 核心模块设计

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

根据数据库设计，系统包含以下实体：

#### 3.2.1 用户实体 (User)
```java
- id: Long (主键)
- username: String (用户名)
- password: String (加密密码)
- phone: String (手机号)
- createdAt: LocalDateTime (创建时间)
- lastLoginAt: LocalDateTime (最后登录时间)
```

#### 3.2.2 房屋区域实体 (HouseArea)
```java
- id: Long (主键)
- userId: Long (用户ID，外键)
- areaName: String (区域名称)
- areaType: String (区域类型)
- createdAt: LocalDateTime (创建时间)
- updatedAt: LocalDateTime (更新时间)
```

#### 3.2.3 设备实体 (Device)
```java
- id: Long (主键)
- userId: Long (用户ID，外键)
- areaId: Long (区域ID，外键)
- deviceId: String (设备唯一ID)
- deviceType: String (设备类型)
- deviceName: String (设备名称)
- statusName: String (状态名称)
- createdAt: LocalDateTime (创建时间)
- updatedAt: LocalDateTime (更新时间)
```

#### 3.2.4 自动化规则实体 (AutomationRule)
```java
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
```

### 3.3 数据访问层 (repository/mapper)

#### 3.3.1 JPA Repository
使用 Spring Data JPA 提供基础的 CRUD 操作：
- `UserRepository`
- `HouseAreaRepository`
- `DeviceRepository`
- `AutomationRuleRepository`

#### 3.3.2 MyBatis Mapper
对于复杂查询，使用 MyBatis 提供自定义 SQL：
- 设备状态统计查询
- 用户设备关联查询
- 自动化规则执行查询

### 3.4 业务逻辑层 (service)

#### 3.4.1 用户服务 (UserService)
- 用户注册、登录
- 密码加密与验证
- JWT Token 生成与验证
- 用户信息更新

#### 3.4.2 设备服务 (DeviceService)
- 设备注册、编辑、删除
- 设备状态更新
- 设备控制命令发送
- 设备状态查询

#### 3.4.3 区域服务 (HouseAreaService)
- 区域创建、编辑、删除
- 区域设备关联管理
- 区域设备统计

#### 3.4.4 自动化规则服务 (AutomationRuleService)
- 规则创建、编辑、删除
- 规则启用/禁用
- 规则执行逻辑
- 规则触发检查

#### 3.4.5 MQTT 服务 (MqttService)
- MQTT 连接管理
- 设备状态上报处理
- 设备控制命令下发
- 设备在线状态管理

### 3.5 控制器层 (controller)

#### 3.5.1 用户控制器 (UserController)
- `/api/auth/register` - 用户注册
- `/api/auth/login` - 用户登录
- `/api/user/profile` - 获取用户信息
- `/api/user/update` - 更新用户信息

#### 3.5.2 设备控制器 (DeviceController)
- `/api/devices` - 获取设备列表
- `/api/devices/{id}` - 获取设备详情
- `/api/devices` - 创建设备
- `/api/devices/{id}` - 更新设备
- `/api/devices/{id}` - 删除设备
- `/api/devices/{id}/control` - 控制设备

#### 3.5.3 区域控制器 (HouseAreaController)
- `/api/areas` - 获取区域列表
- `/api/areas/{id}` - 获取区域详情
- `/api/areas` - 创建区域
- `/api/areas/{id}` - 更新区域
- `/api/areas/{id}` - 删除区域

#### 3.5.4 自动化规则控制器 (AutomationRuleController)
- `/api/rules` - 获取规则列表
- `/api/rules/{id}` - 获取规则详情
- `/api/rules` - 创建规则
- `/api/rules/{id}` - 更新规则
- `/api/rules/{id}` - 删除规则
- `/api/rules/{id}/toggle` - 启用/禁用规则

#### 3.5.5 WebSocket 控制器
- `/ws/device-status` - 设备状态实时推送
- `/ws/automation-events` - 自动化事件推送

### 3.6 工具类 (utils)

#### 3.6.1 JWT 工具类 (JwtUtil)
- JWT Token 生成
- Token 验证与解析
- Token 过期检查

#### 3.6.2 密码加密工具类 (PasswordUtil)
- 使用 BCrypt 算法加密密码
- 密码验证

#### 3.6.3 JSON 工具类 (JsonUtil)
- JSON 序列化与反序列化
- JSON 格式验证

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

## 5. 通信协议

### 5.1 云端与下位机通信 (MQTT)
- 使用 MQTT 协议与设备通信
- 消息格式为 JSON
- 消息结构包含用户ID、时间戳、消息类型和数据

### 5.2 应用层与云端通信
- 使用 RESTful API 进行数据交互
- WebSocket 实现实时通信
- 统一的错误响应格式

## 6. 部署配置

### 6.1 数据库配置
- MySQL 8.0 数据库
- 连接池使用 HikariCP
- 配置参数包括连接超时、验证超时等

### 6.2 Redis 配置
- Redis 7 作为缓存服务
- 配置连接池参数
- 设置缓存过期策略

### 6.3 应用配置
- 配置文件使用 application.properties
- 包含数据库、Redis、MQTT、JWT 等配置
- 支持多环境配置 (dev, test, prod)

## 7. 监控与健康检查

### 7.1 数据库健康检查
- 通过 `DatabaseHealthIndicator` 检查数据库连接
- 提供详细的连接信息

### 7.2 应用监控
- 集成 Spring Boot Actuator
- 提供应用运行状态信息
- 监控关键指标

## 8. 开发规范

### 8.1 代码规范
- 使用驼峰命名法
- 统一的代码格式化
- 完善的注释和文档

### 8.2 接口规范
- RESTful API 设计原则
- 统一的错误码和响应格式
- 接口版本管理