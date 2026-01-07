# 智能家居控制系统

智能家居控制系统是一个多端集成的系统，旨在实现对家庭设备的智能控制和管理。系统支持PC端、移动Android端、移动iOS端和微信小程序端，通过云端服务与下位机设备进行通信，实现设备状态监控、远程控制、自动化规则等功能。

## 系统架构

系统采用四层架构设计：

1. **感知层**：各类传感器和家庭设施
2. **边缘层**：本地控制中枢嵌入式设备
3. **云端层**：服务器后端与数据库
4. **应用层**：Web端、移动Android端、移动iOS端和微信小程序端

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

## 功能特性

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

## 快速开始

### 后端启动

1. 确保已安装Java 8+、Maven 3.6+、MySQL 5.7+、Redis
2. 配置MySQL和Redis连接信息
3. 初始化数据库：
   ```bash
   mysql -u root -p < scripts/init_database.sql
   ```
4. 启动后端服务：
   ```bash
   cd backend
   mvn spring-boot:run
   ```

### 前端启动

#### PC端
```bash
cd frontend/web
npm install
npm run dev
```

#### 移动端
```bash
cd frontend/mobile
npm install
npm run android  # Android
npm run ios      # iOS
```

#### 微信小程序端
```bash
cd frontend/wechat-app
npm install
# 使用微信开发者工具打开项目
```

## API文档

详细的API文档请参考 [docs/api-documentation.md](docs/api-documentation.md)

## 贡献指南

欢迎提交Issue和Pull Request来帮助我们改进项目。

## 许可证

本项目采用MIT许可证，详情请参见LICENSE文件。