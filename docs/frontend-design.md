# 智能家居控制系统前端设计文档

## 1. 项目概述

智能家居控制系统前端分为四个部分：
- **PC端**：基于Web的浏览器应用，用于管理家庭设备、设置自动化规则、查看设备状态等
- **移动Android端**：Android APP，提供移动端便捷控制设备
- **移动iOS端**：iOS APP，提供移动端便捷控制设备
- **微信小程序**：微信内置小程序，提供移动端便捷控制设备

## 2. 技术栈选择

### 2.1 统一技术栈策略
为了确保多端数据传输的一致性，采用统一的技术栈策略：
- **核心逻辑**：使用TypeScript编写统一的业务逻辑
- **状态管理**：使用统一的状态管理方案
- **API接口**：定义统一的API接口规范
- **数据模型**：定义统一的数据模型和类型定义

### 2.2 PC端技术栈
- **框架**：Vue 3.x + TypeScript
- **UI组件库**：Element Plus
- **状态管理**：Pinia
- **路由管理**：Vue Router
- **HTTP客户端**：Axios
- **构建工具**：Vite

### 2.3 移动端技术栈（Android/iOS）
- **跨平台框架**：React Native 或 uni-app
- **状态管理**：Redux/MobX 或 Pinia
- **HTTP客户端**：Axios 或 React Native内置fetch
- **UI组件库**：React Native Elements 或 uni-app内置组件

### 2.4 微信小程序技术栈
- **框架**：原生小程序或uni-app
- **状态管理**：Pinia或原生状态管理
- **HTTP客户端**：微信小程序request API或封装的网络库

## 3. 统一数据模型设计

### 3.1 用户模型
```typescript
interface User {
  id: number;
  username: string;
  phone: string;
  createdAt: Date;
  lastLoginAt: Date;
}
```

### 3.2 设备模型
```typescript
interface Device {
  id: number;
  userId: number;
  areaId: number;
  deviceId: string;
  deviceType: string; // temperature_sensor, humidity_sensor, light_sensor, led, curtain等
  deviceName: string;
  statusName: string;
  createdAt: Date;
  updatedAt: Date;
  onlineStatus?: boolean; // 设备在线状态
}
```

### 3.3 区域模型
```typescript
interface Area {
  id: number;
  userId: number;
  areaName: string;
  areaType: string; // fixed - 固定区域, general - 通用区域
  createdAt: Date;
  updatedAt: Date;
}
```

### 3.4 自动化规则模型
```typescript
interface AutomationRule {
  id: number;
  userId: number;
  ruleName: string;
  triggerType: string; // device_status - 设备状态, time_based - 定时
  triggerCondition: any; // JSON格式存储
  targetDeviceId?: string;
  targetDeviceType?: string;
  commandType?: string;
  commandParameters?: any; // JSON格式存储
  enabled: boolean;
  createdAt: Date;
  updatedAt: Date;
}
```

## 4. 统一API接口设计

所有端都使用相同的API接口规范：

### 4.1 用户相关接口
```
POST   /api/auth/login          # 用户登录
POST   /api/auth/register       # 用户注册
GET    /api/users/profile       # 获取用户信息
PUT    /api/users/profile       # 更新用户信息
```

### 4.2 设备相关接口
```
GET    /api/devices             # 获取用户设备列表
GET    /api/devices/{id}        # 获取特定设备信息
POST   /api/devices             # 添加设备
PUT    /api/devices/{id}        # 更新设备
DELETE /api/devices/{id}        # 删除设备
GET    /api/devices/with-status # 获取设备列表及在线状态
POST   /api/devices/{id}/control # 控制设备
```

### 4.3 区域相关接口
```
GET    /api/areas               # 获取用户区域列表
GET    /api/areas/{id}          # 获取特定区域信息
POST   /api/areas               # 添加区域
PUT    /api/areas/{id}          # 更新区域
DELETE /api/areas/{id}          # 删除区域
```

### 4.4 自动化规则相关接口
```
GET    /api/automation/rules    # 获取用户自动化规则列表
GET    /api/automation/rules/{id} # 获取特定规则信息
POST   /api/automation/rules    # 创建自动化规则
PUT    /api/automation/rules/{id} # 更新自动化规则
DELETE /api/automation/rules/{id} # 删除自动化规则
PUT    /api/automation/rules/{id}/enable  # 启用规则
PUT    /api/automation/rules/{id}/disable # 禁用规则
```

### 4.5 MQTT控制相关接口
```
POST   /api/mqtt/send-control-command    # 发送控制命令到设备
POST   /api/mqtt/send-batch-command/{userId} # 发送批量控制命令
```

### 4.6 连接状态相关接口
```
GET    /api/connection/is-online/{deviceId}  # 检查设备是否在线
GET    /api/connection/status/{deviceId}     # 获取设备连接状态详情
POST   /api/connection/set-online/{userId}/{deviceId}  # 手动设置设备为在线状态
POST   /api/connection/set-offline/{deviceId}          # 手动设置设备为离线状态
```

## 5. 统一WebSocket实时通信

所有端使用相同的WebSocket通信协议：

### 5.1 WebSocket连接
- 连接地址：`ws://localhost:8080/ws`
- 连接建立后，前端订阅设备状态更新

### 5.2 消息格式
```json
{
  "type": "device_status_update",
  "payload": {
    "deviceId": "device_001",
    "deviceType": "temperature_sensor",
    "status": {
      "temperature": 25.5
    },
    "timestamp": 1678886400000
  }
}
```

## 6. 前端目录结构

```
frontend/
├── shared/                   # 跨端共享代码
│   ├── types/                # TypeScript类型定义
│   ├── models/               # 数据模型
│   ├── services/             # API服务封装
│   ├── utils/                # 通用工具函数
│   └── constants/            # 常量定义
├── web/                      # Web端代码
│   ├── src/                  # 源代码
│   │   ├── assets/           # 静态资源
│   │   ├── components/       # 公共组件
│   │   ├── views/            # 页面视图
│   │   ├── router/           # 路由配置
│   │   ├── store/            # Pinia状态管理
│   │   ├── api/              # API接口封装
│   │   ├── utils/            # 工具函数
│   │   ├── plugins/          # 插件配置
│   │   └── App.vue           # 根组件
│   ├── public/               # 公共资源
│   ├── package.json          # 项目配置
│   └── vite.config.js        # Vite配置
├── mobile/                   # 移动端代码（React Native或uni-app）
│   ├── src/                  # 源代码
│   │   ├── components/       # 公共组件
│   │   ├── screens/          # 页面视图
│   │   ├── store/            # 状态管理
│   │   ├── api/              # API接口封装
│   │   ├── utils/            # 工具函数
│   │   └── App.js            # 根组件
│   ├── package.json          # 项目配置
│   └── metro.config.js       # 构建配置
└── wechat-app/               # 微信小程序代码
    ├── pages/                # 页面
    ├── components/           # 组件
    ├── utils/                # 工具函数
    ├── api/                  # API接口封装
    └── app.json              # 小程序配置
```

## 7. 各端功能模块设计

### 7.1 PC端功能模块
- **用户模块**：用户注册/登录、个人信息管理、密码修改
- **设备管理模块**：设备列表、设备控制、设备添加/编辑/删除
- **区域管理模块**：区域列表、区域管理、区域内设备展示
- **自动化规则模块**：规则列表、规则创建/编辑/删除、规则状态管理
- **实时监控模块**：设备状态图表、数据趋势图、在线状态监控

### 7.2 移动端功能模块
- **主页**：快速设备控制、设备状态概览
- **区域页面**：按房间分类展示设备、快速进入房间控制
- **设备控制**：设备详细控制界面、参数调节
- **自动化**：常用自动化规则快速开关
- **个人中心**：用户信息管理、设置

### 7.3 微信小程序功能模块
- **主页**：快速设备控制、设备状态概览
- **区域页面**：按房间分类展示设备、快速进入房间控制
- **设备控制**：设备详细控制界面、参数调节
- **自动化**：常用自动化规则快速开关
- **我的**：用户信息、设置

## 8. 统一UI设计规范

### 8.1 设计原则
- 保持一致的视觉风格和交互体验
- 适配不同屏幕尺寸和分辨率
- 响应式设计，确保在各端体验良好

### 8.2 组件库设计
- 基于统一的设计系统构建组件库
- 各端组件在视觉和交互上保持一致
- 考虑不同平台的UI规范和用户习惯

## 9. 安全设计

### 9.1 统一认证机制
- 使用JWT Token进行身份验证
- Token存储方式根据平台特性调整
- 请求头携带Authorization: Bearer {token}

### 9.2 权限控制
- 统一的权限控制逻辑
- 各端根据用户角色显示/隐藏功能

## 10. 性能优化

### 10.1 代码复用
- 最大化共享业务逻辑代码
- 使用统一的数据模型和接口定义

### 10.2 平台特定优化
- 各端根据平台特性进行特定优化
- 移动端考虑性能和电池消耗
- Web端考虑加载速度和资源大小

## 11. 部署方案

### 11.1 开发环境
- 各端独立的开发服务器
- 统一的后端API代理

### 11.2 生产环境
- Web端：构建静态资源，部署到CDN或Web服务器
- 移动端：构建原生应用包，发布到应用商店
- 小程序：上传到微信平台审核发布

## 12. 测试策略

### 12.1 统一测试策略
- 使用相同的测试用例覆盖业务逻辑
- 各端根据平台特性进行特定测试

### 12.2 跨端一致性测试
- 确保各端功能和交互一致
- 验证API调用和数据处理的一致性