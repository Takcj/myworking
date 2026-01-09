# 智能家居控制系统

## 项目概述

智能家居控制系统是一个多端集成的系统，旨在实现对家庭设备的智能控制和管理。系统支持PC端、移动Android端、移动iOS端和微信小程序端，通过云端服务与下位机设备进行通信，实现设备状态监控、远程控制、自动化规则等功能。

## 技术栈

- **后端框架**：Spring Boot 3.5.9
- **数据库**：MySQL 8.0
- **缓存**：Redis 7
- **消息协议**：MQTT
- **持久化框架**：MyBatis Plus

## 本地开发环境设置

### 1. 启动数据库和缓存服务

使用Docker Compose启动MySQL和Redis服务：

```bash
docker-compose up -d
```

### 2. 项目构建

```bash
# 使用Maven构建项目
mvn clean package

# 或者直接运行（开发模式）
mvn spring-boot:run
```

### 3. 依赖要求

- JDK 17+
- Maven 3.6+
- Docker (用于运行MySQL和Redis)

## 配置说明

### 数据库配置

项目使用Docker容器中的MySQL数据库，配置信息如下：

- **数据库名**: smart_home
- **用户名**: user
- **密码**: password
- **端口**: 3306

### Redis配置

- **端口**: 6379
- **数据库**: 0

### MQTT配置

- **地址**: tcp://localhost:1883
- **用户名**: admin
- **密码**: public

## 项目结构

```
smart-home/
├── src/main/java/com/example/demo/           # 主应用代码
├── src/main/resources/                       # 配置文件
├── docker-compose.yml                        # Docker服务配置
├── init.sql                                  # 数据库初始化脚本
└── pom.xml                                   # Maven依赖配置
```

## 环境变量

应用的配置项在[application.properties](src/main/resources/application.properties)中定义，包括数据库连接、Redis连接、JWT配置和MQTT配置等。

## 启动说明

1. 确保Docker服务已启动
2. 执行`docker-compose up -d`启动数据库和缓存服务
3. 使用IDE或命令行运行`DemoApplication.java`主类启动应用
4. 应用默认在8080端口启动

## API文档

API文档通过Spring REST Docs自动生成，访问地址：http://localhost:8080