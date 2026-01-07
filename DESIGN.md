# 智能家居控制系统整体设计

## 项目概述

本项目是一个智能家居控制系统，旨在实现家居环境监控、设备控制等功能。系统采用四层架构：感知层、边缘层、云端层和应用层，实现从传感器数据采集到云端处理再到前端展示的完整流程。

## 系统架构

根据项目需求，系统采用以下四层架构：

1. **感知层**：各类传感器（温湿度、光照、门窗状态等）和家庭设施（灯光、空调、窗帘等）
2. **边缘层**：本地控制中枢嵌入式设备，负责数据采集、预处理和通信
3. **云端层**：服务器后端与数据库，提供数据存储、业务逻辑处理
4. **应用层**：Web端和微信小程序等前端界面，提供用户交互

## 技术栈选择

### 后端技术栈

- **语言**: Java 17+
- **框架**: Spring Boot 2.7+
- **持久层**: MyBatis Plus
- **数据库**: MySQL 8.0
- **消息队列**: Redis (用于缓存和会话管理)
- **通信协议**:
  - WebSocket (实时数据推送)
  - MQTT (与嵌入式设备通信)
  - HTTP/RESTful API (与前端通信)

### 前端技术栈

- **Web端**:
  - 框架: Vue.js 3.x + Element Plus
  - 构建工具: Vite
  - 状态管理: Pinia
- **微信小程序**:
  - 原生小程序开发或使用uni-app框架

## 后端与下位机通信协议

### 通信协议规范

云端后端与下位机设备之间的数据交互遵循以下规范：

1. **通信协议**：使用MQTT协议进行双向通信
2. **消息格式**：所有消息采用JSON格式
3. **消息类型**：包括数据上报、控制命令、连接数据、心跳数据等多种类型
4. **认证机制**：每个下位机设备需要通过用户ID进行认证
5. **消息可靠性**：使用QoS 1（至少一次传递）确保消息传递
6. **数据结构层次**：采用多层结构，便于对单个设备进行管理
7. **字段次序**：在设备数据中，设备类型字段必须在设备ID字段之前

### 数据格式说明

#### 1. 一层数据格式

```
{
  "user_id": "user_001",           // 用户唯一标识
  "timestamp": 1678886400000,      // 时间戳
  "message_type": "...",           // 消息类型（device_data, control_command, connection, heartbeat等）
  "data": {                        // 一层数据体
    // 第二层数据展开于此
  }
}
```

#### 2. 一层消息类型

- **状态数据类型** (`device_data`)：下位机设备上报状态数据
- **控制数据类型** (`control_command`)：云端向设备发送控制命令
- **连接数据类型** (`connection`)：设备连接状态信息
- **心跳数据类型** (`heartbeat`)：设备在线状态信息

#### 3. 二层数据格式

根据一层消息类型的不同，二层数据格式如下：

##### ① 状态数据类型 (`device_data`)
```
{
  "area": "living_room",         // 区域标识
  "device_type": "temperature_sensor", // 设备类型
  "device_id": "temp_sensor_01", // 设备唯一标识
  "status": {                    // 状态数据体，根据具体设备确定
    // 具体设备状态数据
  },
  "timestamp": 1678886400000    // 设备数据采集时间
}
```

##### ② 控制数据类型 (`control_command`)
```
{
  "area": "living_room",         // 区域标识
  "device_type": "led",          // 目标设备类型
  "device_id": "led_01",         // 目标设备ID
  "command": {                   // 控制数据体
    "type": "set_state",         // 操作类型
    "parameters": {              // 操作参数
      // 具体控制参数
    }
  }
}
```

##### ③ 连接数据类型 (`connection`)
```
{
  "device_id": "esp32_001",      // 设备ID
  "connection_status": "connected", // 连接状态（connected, disconnected）
  "connection_time": 1678886400000 // 连接时间戳
}
```

##### ④ 心跳数据类型 (`heartbeat`)
```
{
  "device_id": "esp32_001",      // 设备ID
  "status": "online",            // 在线状态
  "battery_level": 85,           // 电池电量（如果适用）
  "signal_strength": -65         // 信号强度（dBm）
}
```

### 支持的设备列表及具体设备的二层数据体格式

#### 1. 温度传感器 (temperature_sensor)
状态数据体格式：
```
{
  "value": 23.5,                 // 温度值
  "unit": "celsius",             // 单位
  "power": "on"                  // 电源状态
}
```

控制数据体格式：
```
{
  "type": "set_power",           // 操作类型
  "parameters": {
    "power": "on"                // 电源状态（on/off）
  }
}
```

#### 2. 湿度传感器 (humidity_sensor)
状态数据体格式：
```
{
  "value": 60.2,                 // 湿度值
  "unit": "percent",             // 单位
  "power": "on"                  // 电源状态
}
```

控制数据体格式：
```
{
  "type": "set_power",           // 操作类型
  "parameters": {
    "power": "on"                // 电源状态（on/off）
  }
}
```

#### 3. 光照传感器 (light_sensor)
状态数据体格式：
```
{
  "value": 450,                  // 光照强度值
  "unit": "lux",                 // 单位
  "power": "on"                  // 电源状态
}
```

控制数据体格式：
```
{
  "type": "set_power",           // 操作类型
  "parameters": {
    "power": "on"                // 电源状态（on/off）
  }
}
```

#### 4. LED灯 (led)
状态数据体格式：
```
{
  "on": true,                    // 开关状态
  "color": {                     // 颜色值(RGB)
    "r": 255,
    "g": 100,
    "b": 50
  },
  "brightness": 80,              // 亮度值（0-100）
  "power": "on"                  // 电源状态
}
```

控制数据体格式：
```
{
  "type": "set_state",           // 操作类型
  "parameters": {
    "on": true,                  // 开关状态
    "color": {                   // 颜色值(RGB)
      "r": 255,
      "g": 100,
      "b": 50
    },
    "brightness": 75             // 亮度值（0-100）
  }
}
```

#### 5. 窗帘 (curtain)
状态数据体格式：
```
{
  "position": 30,                // 窗帘位置，0-100百分比
  "power": "on"                  // 电源状态
}
```

控制数据体格式：
```
{
  "type": "set_position",        // 操作类型
  "parameters": {
    "position": 50               // 目标位置
  }
}
```

#### 6. 空调 (air_conditioner) - 预留
状态数据体格式：
```
{
  "on": true,                    // 开关状态
  "mode": "cool",                // 模式（cool, heat, fan, dry）
  "temperature": 24,             // 温度设置
  "fan_speed": "medium",         // 风速（low, medium, high）
  "power": "on"                  // 电源状态
}
```

控制数据体格式：
```
{
  "type": "set_state",           // 操作类型
  "parameters": {
    "on": true,                  // 开关状态
    "mode": "cool",              // 模式
    "temperature": 24,           // 温度设置
    "fan_speed": "medium"        // 风速
  }
}
```

#### 7. 门窗传感器 (door_window_sensor) - 预留
状态数据体格式：
```
{
  "state": "closed",             // 状态（open, closed）
  "power": "on"                  // 电源状态
}
```

控制数据体格式：
```
{
  "type": "set_power",           // 操作类型
  "parameters": {
    "power": "on"                // 电源状态（on/off）
  }
}
```

## 数据库设计思路

### 数据库表结构规划

根据项目需求，采用单一数据库设计，包含以下数据表：

#### 1. 用户表 (users)
- [id](file:///home/myworking/backend/src/main/java/com/example/model/User.java#L19-L19) - 用户唯一标识（主键）
- username - 用户名
- password - 密码（加密存储）
- phone - 手机号
- created_at - 创建时间
- last_login_at - 最后上线时间或最新使用时间

#### 2. 房屋区域表 (house_areas)
- [id](file:///home/myworking/backend/src/main/java/com/example/model/User.java#L19-L19) - 区域唯一标识（主键）
- user_id - 用户ID（外键，关联用户表）
- area_name - 区域名称（客厅、卧室、厨房、卫生间、阳台、通用区域等）
- area_type - 区域类型（fixed - 固定区域，general - 通用区域）
- created_at - 创建时间
- updated_at - 更新时间

#### 3. 设备表 (devices)
- [id](file:///home/myworking/backend/src/main/java/com/example/model/User.java#L19-L19) - 设备唯一标识（主键）
- user_id - 用户ID（外键，关联用户表）
- area_id - 区域ID（外键，关联房屋区域表）
- device_id - 设备唯一ID（由下位机提供）
- device_type - 设备类型（temperature_sensor, humidity_sensor, light_sensor, led, curtain等）
- device_name - 设备名称
- status_name - 状态名称（用于前端显示框架）
- created_at - 创建时间
- updated_at - 更新时间

#### 4. 连接状态表 (connection_status)
- [id](file:///home/myworking/backend/src/main/java/com/example/model/User.java#L19-L19) - 记录唯一标识（主键）
- device_id - 设备ID
- user_id - 用户ID
- connection_status - 连接状态（connected, disconnected）
- connection_time - 连接时间戳
- disconnection_time - 断开连接时间戳
- last_heartbeat - 最后心跳时间
- created_at - 创建时间
- updated_at - 更新时间

#### 5. 自动化规则表 (automation_rules)
- [id](file:///home/myworking/backend/src/main/java/com/example/model/User.java#L19-L19) - 规则唯一标识（主键）
- user_id - 用户ID（外键，关联用户表）
- rule_name - 规则名称
- trigger_type - 触发类型（device_status - 设备状态，time_based - 定时）
- trigger_condition - 触发条件（JSON格式存储）
- target_device_id - 目标设备ID（外键，关联设备表）
- target_device_type - 目标设备类型
- command_type - 命令类型
- command_parameters - 命令参数（JSON格式存储）
- is_enabled - 规则是否启用
- created_at - 创建时间
- updated_at - 更新时间

## 通信协议设计

### 云端与嵌入式设备通信

- 使用MQTT协议，实现轻量级、低功耗的双向通信
- 设备连接认证机制，确保安全性
- 以用户ID为单位进行数据隔离
- 支持断线重连和消息确认机制
- 后端需要实现消息解析、验证和存储功能
- 数据格式遵循多层结构设计，便于管理单个设备
- 设备类型字段必须在设备ID字段之前，便于解析

### 云端与前端通信

- RESTful API 提供数据查询接口
- WebSocket 实现实时数据推送
- JWT 实现用户认证和授权

## 项目目录结构

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
├── docs/                    # 项目文档
├── scripts/                 # 脚本文件
├── README.md                # 项目说明
└── package.json             # 项目配置
```

## 安全性考虑

- 用户认证与授权 (JWT)
- 用户数据隔离，确保不同用户数据安全
- API访问频率限制
- 数据传输加密
- MQTT设备认证机制

## 扩展性考虑

- 微服务架构设计（未来可拆分）
- 插件化设备支持
- 多租户支持（用户数据隔离）

## 部署方案

- 后端: 部署在云服务器，使用Docker容器化
- 前端: Web端部署到CDN，微信小程序独立发布
- 数据库: MySQL主从复制，Redis集群

## 开发计划

1. 第一阶段: 搭建基础架构，实现用户管理和区域管理
2. 第二阶段: 实现MQTT消息处理模块，支持数据上报和命令下发
3. 第三阶段: 实现数据存储和展示功能
4. 第四阶段: 实现设备控制功能
5. 第五阶段: 实现自动化规则和高级功能
6. 第六阶段: 优化和扩展功能