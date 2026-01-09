# 智能家居控制系统项目设计文档

## 项目概述

智能家居控制系统是一个多端集成的系统，旨在实现对家庭设备的智能控制和管理。系统支持PC端、移动Android端、移动iOS端和微信小程序端，通过云端服务与下位机设备进行通信，实现设备状态监控、远程控制、自动化规则等功能。作为个人小型毕设项目，着重考虑基本功能的实现，不过多考虑并发、安全等问题。

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
|              下位机设备或本地控制中枢 (嵌入式)                  |
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
- 用户管理：用户注册、登录
- 设备管理：设备添加、编辑、删除、状态监控
- 区域管理：房屋区域划分和管理
- 自动化规则：基于设备状态或定时触发的设备控制
- 实时通信：与下位机设备的MQTT通信
- 数据存储：用户数据、设备数据、规则数据的持久化

### 应用层功能
- **PC端**：全面的设备管理、规则设置、数据分析
- **移动Android端**：便捷的设备控制、状态查看、通知提醒
- **移动iOS端**：便捷的设备控制、状态查看、通知提醒
- **微信小程序端**：快速设备控制、常用功能访问

## MVP 范围与安全取舍

- **项目定位**：个人小型毕设（MVP），优先实现基本功能，不追求高并发与完整企业级安全，但保留必要的最小安全措施以降低关键风险。

- **MVP 功能清单（优先级）**
  1. 用户注册/登录（密码哈希 + 简单认证）
  2. 设备注册/绑定、设备状态上报与远程控制（MQTT 基本通信）
  3. Web/PC 管理界面：设备管理、状态展示、规则设置
  4. 简单自动化规则（基于设备状态或定时触发）
  5. 基本日志与设备在线状态（last_seen）

- **最低限度安全措施（推荐且必要）**
  - 密码使用 bcrypt/argon2 哈希存储，绝不保存明文
  - API 使用 HTTPS（开发/部署时说明）
  - 简单认证：使用 JWT 或短时有效 token 进行 API 鉴权
  - MQTT 使用用户名/密码，条件允许时启用 TLS；对设备控制做基础权限校验
  - 对重要操作（设备绑定、重置）增加二次确认或权限检查

- **可延后/扩展项**
  - 完善的 MQTT ACL、设备证书与签名、OTA 签名验证、时序数据库（TSDB）或 HA 部署、完整审计与监控系统

## 技术栈

### 云端层
- **后端框架**：Spring Boot 3.x+ (要求Java 17+环境)
- **数据库**：MySQL + Redis
- **消息协议**：MQTT
- **实时通信**：WebSocket
- **持久化框架**：MyBatis Plus
- **API文档**：Springdoc OpenAPI
- **安全框架**：Spring Security
- **工具库**：Lombok, Hutool

### 应用层
- **PC端**：Vue 3 + TypeScript + Element Plus
- **移动Android/iOS端**：React Native 或 uni-app
- **微信小程序**：原生小程序或uni-app

## 通信协议

### 云端与下位机通信
- **协议**：MQTT
- **消息格式**：JSON
- **消息结构**：采用多层结构，每一层包含在上一层的data字段
  ```
  {
    "user_id": "用户ID",
    "timestamp": "时间戳",
    "message_type": "消息类型",
    "data": {
      #二层数据，根据消息类型确定结构，本文档不详细展开
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
│   │   ├── security/     # 安全相关
│   │   ├── utils/        # 工具类
│   │   └── logging/      # 日志记录
│   └── src/main/resources/
│       ├── application.yml
│       └── mapper/       # MyBatis映射文件
├── frontend/             # 前端代码
│   ├── shared/           # 跨端共享代码
│   ├── web/              # PC端Web代码
│   ├── mobile/           # 移动端代码
│   └── wechat-app/       # 微信小程序代码
├── docs/                 # 项目前端详细设计文档、后端详细文档，RESTful API文档，MQTT通信文档
├── scripts/              # 脚本文件，数据库表自动化创建等脚本
├── DESIGN.md             # 项目整体设计文档
├── README.md             # 项目说明
└── package.json          # 项目配置
```

## 日志记录实现方案

为了满足MVP阶段就应考虑的运维功能，系统需要实现完整的日志记录体系，具体如下：

### 1. 日志分类
- **访问日志**：记录用户访问信息，包括用户ID、访问时间、访问接口、IP地址等
- **操作日志**：记录用户关键操作，如设备控制、规则设置等
- **错误日志**：记录系统异常、业务错误等
- **设备通信日志**：记录与下位机设备的MQTT通信信息
- **审计日志**：记录敏感操作，如用户登录、权限变更等

### 2. 技术实现
- **日志框架**：使用SLF4J + Logback组合
- **日志级别**：DEBUG、INFO、WARN、ERROR
- **日志格式**：统一的日志格式，便于日志收集和分析
- **日志切面**：使用AOP实现访问日志和操作日志的自动记录

### 3. 日志配置
- **日志文件分割**：按日期和大小分割日志文件
- **日志保留策略**：访问日志保留30天，错误日志保留90天，审计日志保留一年
- **异步日志**：提高日志写入性能，减少对业务的影响

### 4. 日志记录示例
#### 访问日志
```java
@Aspect
@Component
public class AccessLogAspect {
    
    private static final Logger logger = LoggerFactory.getLogger("access-log");
    
    @Around("@annotation(accessLog)")
    public Object around(ProceedingJoinPoint point, AccessLog accessLog) throws Throwable {
        // 记录访问信息
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        String ip = IpUtils.getIpAddr(request);
        String method = request.getMethod();
        String url = request.getRequestURL().toString();
        String params = JSONUtil.toJsonStr(point.getArgs());
        
        long startTime = System.currentTimeMillis();
        Object result = point.proceed();
        long time = System.currentTimeMillis() - startTime;
        
        logger.info("访问日志 - URL: {}, IP: {}, 耗时: {}ms, 参数: {}, 结果: {}", 
            url, ip, time, params, JSONUtil.toJsonStr(result));
        
        return result;
    }
}
```

#### 设备通信日志
```java
@Component
public class DeviceCommunicationLogger {
    
    private static final Logger deviceLogger = LoggerFactory.getLogger("device-communication");
    
    public void logIncomingMessage(String deviceId, String message) {
        deviceLogger.info("设备消息接收 - 设备ID: {}, 消息: {}", deviceId, message);
    }
    
    public void logOutgoingMessage(String deviceId, String message) {
        deviceLogger.info("设备消息发送 - 设备ID: {}, 消息: {}", deviceId, message);
    }
    
    public void logCommunicationError(String deviceId, Exception e) {
        deviceLogger.error("设备通信错误 - 设备ID: {}, 错误: {}", deviceId, e.getMessage(), e);
    }
}
```

### 5. 日志安全
- 对敏感信息进行脱敏处理，如密码、手机号等
- 审计日志写入只读存储，防止篡改
- 限制日志访问权限，仅授权人员可查看

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