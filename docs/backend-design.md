# 智能家居控制系统后端详细设计文档

## 1. 概述

智能家居控制系统后端是整个项目的中心枢纽，负责连接前端界面、数据库和下位机设备。后端采用Spring Boot框架开发，提供RESTful API、WebSocket实时通信和MQTT协议处理功能，实现多用户隔离、区域化管理及设备统一管理。

## 2. 技术架构

### 2.1 技术栈
- **语言**: Java 17+
- **框架**: Spring Boot 3.2.0
- **持久层**: MyBatis Plus
- **数据库**: MySQL 8.0
- **缓存**: Redis
- **消息队列**: MQTT (Eclipse Paho)
- **实时通信**: WebSocket

### 2.2 项目结构
```
backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── smart/
│   │   │           └── home/
│   │   │               ├── SmartHomeApplication.java          # 主启动类
│   │   │               ├── config/                           # 配置类
│   │   │               │   ├── MqttConfig.java               # MQTT配置
│   │   │               │   ├── WebSocketConfig.java          # WebSocket配置
│   │   │               │   ├── MybatisPlusConfig.java        # MyBatis Plus配置
│   │   │               │   ├── SecurityConfig.java           # 安全配置
│   │   │               │   └── RedisConfig.java              # Redis配置
│   │   │               ├── controller/                       # 控制器层
│   │   │               │   ├── UserController.java           # 用户管理控制器
│   │   │               │   ├── DeviceController.java         # 设备管理控制器
│   │   │               │   ├── AreaController.java           # 区域管理控制器
│   │   │               │   ├── MqttController.java           # MQTT通信控制器
│   │   │               │   └── AuthController.java           # 认证控制器
│   │   │               ├── service/                          # 业务逻辑层
│   │   │               │   ├── UserService.java              # 用户服务
│   │   │               │   ├── DeviceService.java            # 设备服务
│   │   │               │   ├── AreaService.java              # 区域服务
│   │   │               │   ├── MqttService.java              # MQTT服务
│   │   │               │   ├── ConnectionService.java        # 连接状态服务
│   │   │               │   └── AutomationService.java        # 自动化规则服务
│   │   │               ├── mapper/                           # 数据访问层
│   │   │               │   ├── UserMapper.java               # 用户数据访问
│   │   │               │   ├── DeviceMapper.java             # 设备数据访问
│   │   │               │   ├── AreaMapper.java               # 区域数据访问
│   │   │               │   ├── ConnectionStatusMapper.java   # 连接状态数据访问
│   │   │               │   └── AutomationRuleMapper.java     # 自动化规则数据访问
│   │   │               ├── model/                            # 数据模型
│   │   │               │   ├── entity/                       # 实体类
│   │   │               │   │   ├── User.java                 # 用户实体
│   │   │               │   │   ├── Device.java               # 设备实体
│   │   │               │   │   ├── Area.java                 # 区域实体
│   │   │               │   │   ├── ConnectionStatus.java     # 连接状态实体
│   │   │               │   │   └── AutomationRule.java       # 自动化规则实体
│   │   │               │   └── dto/                          # 数据传输对象
│   │   │               │       ├── LoginRequest.java         # 登录请求
│   │   │               │       ├── DeviceDataDTO.java        # 设备数据传输对象
│   │   │               │       ├── DeviceControlDTO.java     # 设备控制传输对象
│   │   │               │       └── AutomationRuleDTO.java    # 自动化规则传输对象
│   │   │               ├── common/                           # 公共组件
│   │   │               │   ├── Result.java                   # 统一返回结果
│   │   │               │   ├── GlobalExceptionHandler.java   # 全局异常处理器
│   │   │               │   ├── JwtUtil.java                  # JWT工具类
│   │   │               │   └── Constants.java                # 常量类
│   │   │               └── utils/                            # 工具类
│   │   │                   ├── MqttUtils.java                # MQTT工具类
│   │   │                   └── ValidationUtils.java          # 验证工具类
│   │   └── resources/
│   │       ├── application.yml                               # 主配置文件
│   │       ├── application-dev.yml                           # 开发环境配置
│   │       ├── application-prod.yml                          # 生产环境配置
│   │       ├── mapper/                                       # MyBatis映射文件
│   │       │   ├── UserMapper.xml
│   │       │   ├── DeviceMapper.xml
│   │       │   └── ...
│   │       └── static/                                       # 静态资源
│   └── test/                                                 # 测试代码
│       └── java/
│           └── com/
│               └── smart/
│                   └── home/
│                       └── ...
├── pom.xml                                                   # Maven依赖管理
├── Dockerfile                                                # Docker部署配置
└── docker-compose.yml                                          # Docker Compose配置
```

## 3. 数据格式处理设计

### 3.1 消息格式层级结构
后端需要处理的消息格式遵循多层结构设计：
- **第一层（基础消息层）**：包含用户ID、时间戳、消息类型、数据体
- **第二层（区域设备层）**：按区域组织设备数据，具体格式由设备类型决定

### 3.2 消息类型处理
- **状态数据类型** (`device_data`)：处理设备状态上报
- **控制数据类型** (`control_command`)：处理控制命令下发
- **连接数据类型** (`connection`)：处理设备连接状态
- **心跳数据类型** (`heartbeat`)：处理设备在线状态

### 3.3 设备数据处理
后端需支持以下设备类型的处理：
- 温度传感器 (temperature_sensor)
- 湿度传感器 (humidity_sensor)
- 光照传感器 (light_sensor)
- LED灯 (led)
- 窗帘 (curtain)
- 空调 (air_conditioner)
- 门窗传感器 (door_window_sensor)

## 4. 服务层设计

### 4.1 用户服务 (UserService)
- 用户注册、登录、认证
- 用户信息管理
- JWT令牌管理
- 用户权限控制

### 4.2 区域服务 (AreaService)
- 用户区域的增删改查
- 支持固定区域（客厅、卧室等）和通用区域
- 区域与设备的关联管理

### 4.3 设备服务 (DeviceService)
- 设备信息管理
- 设备状态查询
- 设备类型验证
- 设备与区域的关联管理

### 4.4 MQTT服务 (MqttService)
- 与下位机设备的MQTT通信
- 消息解析和验证（确保设备类型在设备ID之前）
- 设备数据存储
- 控制命令下发
- 连接状态管理
- 心跳检测

### 4.5 连接状态服务 (ConnectionService)
- 设备连接状态跟踪
- 在线设备管理
- 断线重连处理

### 4.6 自动化规则服务 (AutomationService)
- 自动化规则管理
- 规则触发检测
- 自动控制命令生成和下发

## 5. API接口设计

### 5.1 用户管理接口
```
POST /api/auth/login - 用户登录
POST /api/auth/register - 用户注册
GET /api/user/profile - 获取用户信息
PUT /api/user/profile - 更新用户信息
```

### 5.2 区域管理接口
```
GET /api/areas - 获取用户所有区域
POST /api/areas - 创建区域
PUT /api/areas/{id} - 更新区域
DELETE /api/areas/{id} - 删除区域
```

### 5.3 设备管理接口
```
GET /api/devices - 获取用户所有设备
GET /api/devices/{id} - 获取特定设备信息
POST /api/devices - 添加设备
PUT /api/devices/{id} - 更新设备
DELETE /api/devices/{id} - 删除设备
GET /api/devices/{id}/status - 获取设备当前状态
```

### 5.4 控制命令接口
```
POST /api/devices/{id}/control - 发送控制命令
POST /api/automation/rules - 创建自动化规则
GET /api/automation/rules - 获取自动化规则
PUT /api/automation/rules/{id} - 更新自动化规则
DELETE /api/automation/rules/{id} - 删除自动化规则
```

## 6. MQTT通信设计

### 6.1 主题设计
- 上行主题：`user/{userId}/device/data` - 设备数据上报
- 下行主题：`user/{userId}/device/control` - 控制命令下发
- 心跳主题：`user/{userId}/heartbeat` - 心跳检测

### 6.2 消息处理流程
1. 接收下位机设备MQTT消息
2. 验证用户ID和设备ID
3. 解析消息类型和数据（确保设备类型在设备ID之前）
4. 根据消息类型进行相应处理
5. 存储数据到数据库
6. 通过WebSocket向前端推送实时数据

## 7. WebSocket实时通信

### 7.1 连接管理
- 用户连接认证
- 连接状态跟踪
- 断线重连机制

### 7.2 数据推送
- 设备状态变化推送
- 自动化规则触发通知
- 系统消息推送

## 8. 数据库访问设计

### 8.1 实体类设计
- 遵循JPA规范
- 包含必要的注解配置
- 支持软删除和乐观锁

### 8.2 数据访问层
- 使用MyBatis Plus增强CRUD操作
- 自定义复杂查询
- 支持分页和排序

## 9. 安全设计

### 9.1 认证授权
- JWT令牌认证
- 接口权限控制
- 设备认证机制

### 9.2 数据安全
- 密码加密存储
- 敏感数据加密传输
- SQL注入防护

## 10. 性能优化

### 10.1 缓存策略
- 使用Redis缓存热点数据
- 用户会话管理
- 设备连接状态缓存

### 10.2 消息队列
- 异步处理设备数据
- 控制命令队列管理
- 批量操作优化

## 11. 部署方案

### 11.1 Docker容器化部署
- 后端服务容器化
- MySQL数据库容器化
- Redis缓存容器化
- 使用Docker Compose编排

### 11.2 配置管理
- 环境变量配置
- 外部化配置文件
- 配置热更新支持