# 前后端数据传输规范

## 1. 通信协议

### 1.1 HTTP/HTTPS API
- **协议**：使用HTTP/1.1或HTTP/2协议
- **传输加密**：生产环境强制使用HTTPS
- **接口风格**：RESTful API设计原则
- **数据格式**：请求和响应数据统一使用JSON格式

### 1.2 WebSocket实时通信
- **协议**：WebSocket协议
- **传输加密**：生产环境使用WSS协议
- **数据格式**：使用JSON格式传输消息
- **心跳机制**：客户端定时发送ping消息维持连接

## 2. API接口设计规范

### 2.1 路径设计
```
/api/v1/{resource}/{id}/{sub-resource}
```

示例：
- `GET /api/v1/devices` - 获取设备列表
- `GET /api/v1/devices/{id}` - 获取特定设备
- `POST /api/v1/devices` - 创建设备
- `PUT /api/v1/devices/{id}` - 更新设备
- `DELETE /api/v1/devices/{id}` - 删除设备

### 2.2 HTTP方法约定
- `GET` - 查询操作，幂等
- `POST` - 创建操作，非幂等
- `PUT` - 更新操作，幂等
- `DELETE` - 删除操作，幂等

### 2.3 状态码约定
- `200 OK` - 成功
- `201 Created` - 创建成功
- `400 Bad Request` - 请求参数错误
- `401 Unauthorized` - 未认证
- `403 Forbidden` - 权限不足
- `404 Not Found` - 资源不存在
- `500 Internal Server Error` - 服务器内部错误

## 3. 统一响应格式

### 3.1 成功响应
```json
{
  "code": 200,
  "message": "Success",
  "data": {},
  "timestamp": 1678886400000
}
```

### 3.2 错误响应
```json
{
  "code": 400,
  "message": "Error message",
  "data": null,
  "timestamp": 1678886400000
}
```

### 3.3 字段说明
- `code`：状态码
- `message`：消息描述
- `data`：响应数据，根据接口不同而变化
- `timestamp`：响应时间戳（毫秒）

## 4. 认证机制

### 4.1 JWT Token认证
- **认证方式**：JWT (JSON Web Token)
- **传递方式**：请求头 Authorization: Bearer {token}
- **Token有效期**：默认24小时，可刷新
- **刷新机制**：提供专门的刷新接口

### 4.2 请求头示例
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json; charset=utf-8
```

## 5. 数据模型规范

### 5.1 用户数据模型
```json
{
  "id": 1,
  "username": "user123",
  "phone": "13800138000",
  "createdAt": "2023-01-01T00:00:00",
  "lastLoginAt": "2023-01-01T12:00:00"
}
```

### 5.2 设备数据模型
```json
{
  "id": 1,
  "userId": 1,
  "areaId": 1,
  "deviceId": "device_001",
  "deviceType": "temperature_sensor",
  "deviceName": "客厅温度传感器",
  "statusName": "温度传感器",
  "createdAt": "2023-01-01T00:00:00",
  "updatedAt": "2023-01-01T00:00:00",
  "onlineStatus": true
}
```

### 5.3 区域数据模型
```json
{
  "id": 1,
  "userId": 1,
  "areaName": "客厅",
  "areaType": "fixed",
  "createdAt": "2023-01-01T00:00:00",
  "updatedAt": "2023-01-01T00:00:00"
}
```

### 5.4 自动化规则数据模型
```json
{
  "id": 1,
  "userId": 1,
  "ruleName": "温度过高自动开空调",
  "triggerType": "device_status",
  "triggerCondition": {
    "deviceType": "temperature_sensor",
    "condition": ">",
    "value": 26
  },
  "targetDeviceId": "ac_001",
  "targetDeviceType": "air_conditioner",
  "commandType": "power",
  "commandParameters": {
    "state": "on"
  },
  "enabled": true,
  "createdAt": "2023-01-01T00:00:00",
  "updatedAt": "2023-01-01T00:00:00"
}
```

## 6. WebSocket消息格式

### 6.1 连接建立
客户端连接至：`ws://localhost:8080/ws`

### 6.2 消息结构
```json
{
  "type": "message_type",
  "payload": {}
}
```

### 6.3 消息类型

#### 6.3.1 设备状态更新
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

#### 6.3.2 设备在线状态更新
```json
{
  "type": "device_online_status_update",
  "payload": {
    "deviceId": "device_001",
    "isOnline": true,
    "timestamp": 1678886400000
  }
}
```

#### 6.3.3 自动化规则执行
```json
{
  "type": "automation_executed",
  "payload": {
    "ruleId": 1,
    "ruleName": "温度过高自动开空调",
    "executedAt": 1678886400000
  }
}
```

## 7. 分页规范

对于列表查询接口，统一使用以下分页参数：

### 7.1 请求参数
- `page`：页码，从1开始，默认1
- `size`：每页数量，默认10，最大100
- `sort`：排序字段，格式：field,asc 或 field,desc

### 7.2 响应格式
```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "content": [...],
    "totalElements": 100,
    "totalPages": 10,
    "currentPage": 1,
    "size": 10
  },
  "timestamp": 1678886400000
}
```

## 8. 错误处理

### 8.1 通用错误码
- `200`：成功
- `400`：请求参数错误
- `401`：未认证
- `403`：权限不足
- `404`：资源不存在
- `422`：业务逻辑错误
- `500`：服务器内部错误

### 8.2 业务错误码
- `10001`：用户名已存在
- `10002`：手机号已注册
- `10003`：设备ID已存在
- `10004`：区域名称已存在
- `10005`：用户无权访问此设备
- `10006`：设备不在线

## 9. 版本管理

### 9.1 API版本
在URL路径中体现版本号：`/api/v1/`, `/api/v2/`

### 9.2 向后兼容
- 新增字段应保持向后兼容
- 删除或修改字段需要提供迁移方案
- 重大变更应发布新版本API

## 10. 安全规范

### 10.1 输入验证
- 所有用户输入必须进行验证
- 防止SQL注入、XSS攻击
- 限制请求频率

### 10.2 敏感数据
- 密码等敏感信息必须加密传输和存储
- 不在日志中记录敏感信息
- Token等认证信息设置适当有效期