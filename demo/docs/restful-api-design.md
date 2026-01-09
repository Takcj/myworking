# 智能家居控制系统 - RESTful API设计文档

## 1. API设计概述

智能家居控制系统提供标准化的 RESTful API 接口，供前端应用调用。API 设计遵循 REST 原则，使用 JSON 格式进行数据传输，通过 HTTP 状态码表示请求结果，并使用 JWT 进行身份认证。

## 2. API基础规范

### 2.1 协议与地址
- 协议: HTTPS (生产环境) / HTTP (开发环境)
- 基础路径: `/api/v1`
- 字符编码: UTF-8
- 数据格式: JSON

### 2.2 通用请求头
| Header | 必需 | 说明 |
|--------|------|------|
| Content-Type | 否 | application/json |
| Authorization | 条件 | Bearer {token}，需要认证的接口必需 |

### 2.3 通用响应格式
成功响应:
```json
{
  "success": true,
  "code": 200,
  "message": "请求成功",
  "data": {},
  "timestamp": "2026-01-09T03:15:58Z"
}
```

错误响应:
```json
{
  "success": false,
  "code": 400,
  "message": "错误信息",
  "error": "具体错误详情(可选)",
  "timestamp": "2026-01-09T03:15:58Z"
}
```

### 2.4 HTTP状态码
| 状态码 | 说明 |
|--------|------|
| 200 | 请求成功 |
| 201 | 创建成功 |
| 400 | 请求参数错误 |
| 401 | 未认证 |
| 403 | 无权限访问 |
| 404 | 资源不存在 |
| 422 | 参数校验失败 |
| 500 | 服务器内部错误 |

## 3. 用户管理接口

### 3.1 用户注册
- **接口**: `POST /api/v1/auth/register`
- **功能**: 用户注册
- **认证**: 无需认证

**请求参数**:
```json
{
  "username": "testuser",
  "password": "password123",
  "phone": "13800138000"
}
```

**请求示例**:
```bash
curl -X POST "http://localhost:8080/api/v1/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123",
    "phone": "13800138000"
  }'
```

**响应示例**:
```json
{
  "success": true,
  "code": 201,
  "message": "注册成功",
  "data": {
    "id": 1,
    "username": "testuser",
    "phone": "13800138000",
    "createdAt": "2026-01-09T03:15:58Z"
  },
  "timestamp": "2026-01-09T03:15:58Z"
}
```

### 3.2 用户登录
- **接口**: `POST /api/v1/auth/login`
- **功能**: 用户登录
- **认证**: 无需认证

**请求参数**:
```json
{
  "username": "testuser",
  "password": "password123"
}
```

**请求示例**:
```bash
curl -X POST "http://localhost:8080/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

**响应示例**:
```json
{
  "success": true,
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImlhdCI6MTYxMzIzNDU2NywiaWQiOjEsImV4cCI6MTYxMzIzODE2N30.example",
    "user": {
      "id": 1,
      "username": "testuser",
      "phone": "13800138000"
    }
  },
  "timestamp": "2026-01-09T03:15:58Z"
}
```

### 3.3 获取用户信息
- **接口**: `GET /api/v1/user/profile`
- **功能**: 获取当前用户信息
- **认证**: 需要认证

**请求示例**:
```bash
curl -X GET "http://localhost:8080/api/v1/user/profile" \
  -H "Authorization: Bearer {token}"
```

**响应示例**:
```json
{
  "success": true,
  "code": 200,
  "message": "获取成功",
  "data": {
    "id": 1,
    "username": "testuser",
    "phone": "13800138000",
    "createdAt": "2026-01-09T03:15:58Z",
    "lastLoginAt": "2026-01-09T02:30:15Z"
  },
  "timestamp": "2026-01-09T03:15:58Z"
}
```

### 3.4 更新用户信息
- **接口**: `PUT /api/v1/user/profile`
- **功能**: 更新当前用户信息
- **认证**: 需要认证

**请求参数**:
```json
{
  "phone": "13900139000"
}
```

**请求示例**:
```bash
curl -X PUT "http://localhost:8080/api/v1/user/profile" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{
    "phone": "13900139000"
  }'
```

**响应示例**:
```json
{
  "success": true,
  "code": 200,
  "message": "更新成功",
  "data": {
    "id": 1,
    "username": "testuser",
    "phone": "13900139000",
    "createdAt": "2026-01-09T03:15:58Z",
    "lastLoginAt": "2026-01-09T02:30:15Z"
  },
  "timestamp": "2026-01-09T03:15:58Z"
}
```

## 4. 区域管理接口

### 4.1 获取区域列表
- **接口**: `GET /api/v1/areas`
- **功能**: 获取当前用户的所有区域
- **认证**: 需要认证

**请求示例**:
```bash
curl -X GET "http://localhost:8080/api/v1/areas" \
  -H "Authorization: Bearer {token}"
```

**响应示例**:
```json
{
  "success": true,
  "code": 200,
  "message": "获取成功",
  "data": {
    "areas": [
      {
        "id": 1,
        "areaName": "客厅",
        "areaType": "living_room",
        "createdAt": "2026-01-09T03:15:58Z",
        "updatedAt": "2026-01-09T03:15:58Z"
      },
      {
        "id": 2,
        "areaName": "卧室",
        "areaType": "bedroom",
        "createdAt": "2026-01-09T03:15:58Z",
        "updatedAt": "2026-01-09T03:15:58Z"
      }
    ]
  },
  "timestamp": "2026-01-09T03:15:58Z"
}
```

### 4.2 创建区域
- **接口**: `POST /api/v1/areas`
- **功能**: 创建新区域
- **认证**: 需要认证

**请求参数**:
```json
{
  "areaName": "厨房",
  "areaType": "kitchen"
}
```

**请求示例**:
```bash
curl -X POST "http://localhost:8080/api/v1/areas" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{
    "areaName": "厨房",
    "areaType": "kitchen"
  }'
```

**响应示例**:
```json
{
  "success": true,
  "code": 201,
  "message": "创建成功",
  "data": {
    "id": 3,
    "areaName": "厨房",
    "areaType": "kitchen",
    "createdAt": "2026-01-09T03:15:58Z",
    "updatedAt": "2026-01-09T03:15:58Z"
  },
  "timestamp": "2026-01-09T03:15:58Z"
}
```

### 4.3 更新区域
- **接口**: `PUT /api/v1/areas/{id}`
- **功能**: 更新区域信息
- **认证**: 需要认证

**请求参数**:
```json
{
  "areaName": "新厨房",
  "areaType": "kitchen"
}
```

**请求示例**:
```bash
curl -X PUT "http://localhost:8080/api/v1/areas/3" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{
    "areaName": "新厨房",
    "areaType": "kitchen"
  }'
```

**响应示例**:
```json
{
  "success": true,
  "code": 200,
  "message": "更新成功",
  "data": {
    "id": 3,
    "areaName": "新厨房",
    "areaType": "kitchen",
    "createdAt": "2026-01-09T03:15:58Z",
    "updatedAt": "2026-01-09T03:15:59Z"
  },
  "timestamp": "2026-01-09T03:15:59Z"
}
```

### 4.4 删除区域
- **接口**: `DELETE /api/v1/areas/{id}`
- **功能**: 删除区域
- **认证**: 需要认证

**请求示例**:
```bash
curl -X DELETE "http://localhost:8080/api/v1/areas/3" \
  -H "Authorization: Bearer {token}"
```

**响应示例**:
```json
{
  "success": true,
  "code": 200,
  "message": "删除成功",
  "data": null,
  "timestamp": "2026-01-09T03:15:59Z"
}
```

## 5. 设备管理接口

### 5.1 获取设备列表
- **接口**: `GET /api/v1/devices`
- **功能**: 获取当前用户的所有设备
- **认证**: 需要认证

**请求参数**:
- `areaId` (可选): 按区域过滤设备
- `page` (可选): 页码，默认为1
- `size` (可选): 每页数量，默认为10

**请求示例**:
```bash
curl -X GET "http://localhost:8080/api/v1/devices?page=1&size=10" \
  -H "Authorization: Bearer {token}"
```

**响应示例**:
```json
{
  "success": true,
  "code": 200,
  "message": "获取成功",
  "data": {
    "content": [
      {
        "id": 1,
        "deviceId": "light-001",
        "deviceName": "客厅灯",
        "deviceType": "light",
        "statusName": "on",
        "areaId": 1,
        "areaName": "客厅",
        "createdAt": "2026-01-09T03:15:58Z",
        "updatedAt": "2026-01-09T03:15:58Z"
      }
    ],
    "page": 1,
    "size": 10,
    "totalElements": 1,
    "totalPages": 1
  },
  "timestamp": "2026-01-09T03:15:58Z"
}
```

### 5.2 获取设备详情
- **接口**: `GET /api/v1/devices/{id}`
- **功能**: 获取指定设备详情
- **认证**: 需要认证

**请求示例**:
```bash
curl -X GET "http://localhost:8080/api/v1/devices/1" \
  -H "Authorization: Bearer {token}"
```

**响应示例**:
```json
{
  "success": true,
  "code": 200,
  "message": "获取成功",
  "data": {
    "id": 1,
    "deviceId": "light-001",
    "deviceName": "客厅灯",
    "deviceType": "light",
    "statusName": "on",
    "areaId": 1,
    "areaName": "客厅",
    "createdAt": "2026-01-09T03:15:58Z",
    "updatedAt": "2026-01-09T03:15:58Z"
  },
  "timestamp": "2026-01-09T03:15:58Z"
}
```

### 5.3 创建设备
- **接口**: `POST /api/v1/devices`
- **功能**: 创建新设备
- **认证**: 需要认证

**请求参数**:
```json
{
  "areaId": 1,
  "deviceId": "light-002",
  "deviceName": "卧室灯",
  "deviceType": "light"
}
```

**请求示例**:
```bash
curl -X POST "http://localhost:8080/api/v1/devices" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{
    "areaId": 1,
    "deviceId": "light-002",
    "deviceName": "卧室灯",
    "deviceType": "light"
  }'
```

**响应示例**:
```json
{
  "success": true,
  "code": 201,
  "message": "创建成功",
  "data": {
    "id": 2,
    "deviceId": "light-002",
    "deviceName": "卧室灯",
    "deviceType": "light",
    "statusName": null,
    "areaId": 1,
    "areaName": "客厅",
    "createdAt": "2026-01-09T03:15:59Z",
    "updatedAt": "2026-01-09T03:15:59Z"
  },
  "timestamp": "2026-01-09T03:15:59Z"
}
```

### 5.4 更新设备
- **接口**: `PUT /api/v1/devices/{id}`
- **功能**: 更新设备信息
- **认证**: 需要认证

**请求参数**:
```json
{
  "areaId": 2,
  "deviceName": "主卧灯",
  "deviceType": "light"
}
```

**请求示例**:
```bash
curl -X PUT "http://localhost:8080/api/v1/devices/2" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{
    "areaId": 2,
    "deviceName": "主卧灯",
    "deviceType": "light"
  }'
```

**响应示例**:
```json
{
  "success": true,
  "code": 200,
  "message": "更新成功",
  "data": {
    "id": 2,
    "deviceId": "light-002",
    "deviceName": "主卧灯",
    "deviceType": "light",
    "statusName": null,
    "areaId": 2,
    "areaName": "卧室",
    "createdAt": "2026-01-09T03:15:59Z",
    "updatedAt": "2026-01-09T03:16:00Z"
  },
  "timestamp": "2026-01-09T03:16:00Z"
}
```

### 5.5 删除设备
- **接口**: `DELETE /api/v1/devices/{id}`
- **功能**: 删除设备
- **认证**: 需要认证

**请求示例**:
```bash
curl -X DELETE "http://localhost:8080/api/v1/devices/2" \
  -H "Authorization: Bearer {token}"
```

**响应示例**:
```json
{
  "success": true,
  "code": 200,
  "message": "删除成功",
  "data": null,
  "timestamp": "2026-01-09T03:16:00Z"
}
```

### 5.6 控制设备
- **接口**: `POST /api/v1/devices/{id}/control`
- **功能**: 控制设备
- **认证**: 需要认证

**请求参数**:
```json
{
  "command": "turn_on",
  "parameters": {
    "brightness": 80
  }
}
```

**请求示例**:
```bash
curl -X POST "http://localhost:8080/api/v1/devices/1/control" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{
    "command": "turn_on",
    "parameters": {
      "brightness": 80
    }
  }'
```

**响应示例**:
```json
{
  "success": true,
  "code": 200,
  "message": "控制指令已发送",
  "data": {
    "command": "turn_on",
    "status": "sent"
  },
  "timestamp": "2026-01-09T03:16:00Z"
}
```

## 6. 自动化规则接口

### 6.1 获取规则列表
- **接口**: `GET /api/v1/rules`
- **功能**: 获取当前用户的所有自动化规则
- **认证**: 需要认证

**请求参数**:
- `enabled` (可选): 过滤启用状态
- `page` (可选): 页码，默认为1
- `size` (可选): 每页数量，默认为10

**请求示例**:
```bash
curl -X GET "http://localhost:8080/api/v1/rules?enabled=true" \
  -H "Authorization: Bearer {token}"
```

**响应示例**:
```json
{
  "success": true,
  "code": 200,
  "message": "获取成功",
  "data": {
    "content": [
      {
        "id": 1,
        "ruleName": "回家开灯",
        "triggerType": "device_status",
        "triggerCondition": "{\"device_id\":\"light-001\",\"status\":\"motion_detected\"}",
        "targetDeviceId": "light-001",
        "targetDeviceType": "light",
        "commandType": "turn_on",
        "commandParameters": "{}",
        "isEnabled": true,
        "createdAt": "2026-01-09T03:15:58Z",
        "updatedAt": "2026-01-09T03:15:58Z"
      }
    ],
    "page": 1,
    "size": 10,
    "totalElements": 1,
    "totalPages": 1
  },
  "timestamp": "2026-01-09T03:15:58Z"
}
```

### 6.2 创建自动化规则
- **接口**: `POST /api/v1/rules`
- **功能**: 创建新自动化规则
- **认证**: 需要认证

**请求参数**:
```json
{
  "ruleName": "离家关灯",
  "triggerType": "time_based",
  "triggerCondition": "{\"time\":\"22:00\",\"repeat\":\"daily\"}",
  "targetDeviceId": "light-001",
  "targetDeviceType": "light",
  "commandType": "turn_off",
  "commandParameters": "{}",
  "isEnabled": true
}
```

**请求示例**:
```bash
curl -X POST "http://localhost:8080/api/v1/rules" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{
    "ruleName": "离家关灯",
    "triggerType": "time_based",
    "triggerCondition": "{\\"time\\":\\"22:00\\",\\"repeat\\":\\"daily\\"}",
    "targetDeviceId": "light-001",
    "targetDeviceType": "light",
    "commandType": "turn_off",
    "commandParameters": "{}",
    "isEnabled": true
  }'
```

**响应示例**:
```json
{
  "success": true,
  "code": 201,
  "message": "创建成功",
  "data": {
    "id": 2,
    "ruleName": "离家关灯",
    "triggerType": "time_based",
    "triggerCondition": "{\"time\":\"22:00\",\"repeat\":\"daily\"}",
    "targetDeviceId": "light-001",
    "targetDeviceType": "light",
    "commandType": "turn_off",
    "commandParameters": "{}",
    "isEnabled": true,
    "createdAt": "2026-01-09T03:16:01Z",
    "updatedAt": "2026-01-09T03:16:01Z"
  },
  "timestamp": "2026-01-09T03:16:01Z"
}
```

### 6.3 启用/禁用规则
- **接口**: `PUT /api/v1/rules/{id}/toggle`
- **功能**: 启用或禁用自动化规则
- **认证**: 需要认证

**请求示例**:
```bash
curl -X PUT "http://localhost:8080/api/v1/rules/2/toggle" \
  -H "Authorization: Bearer {token}"
```

**响应示例**:
```json
{
  "success": true,
  "code": 200,
  "message": "更新成功",
  "data": {
    "id": 2,
    "isEnabled": false
  },
  "timestamp": "2026-01-09T03:16:02Z"
}
```

## 7. WebSocket接口

### 7.1 设备状态实时推送
- **端点**: `/ws/device-status`
- **功能**: 实时推送设备状态更新

**消息格式**:
```json
{
  "type": "device_status_update",
  "deviceId": "light-001",
  "status": {
    "power": "on",
    "brightness": 80
  },
  "timestamp": "2026-01-09T03:15:58Z"
}
```

### 7.2 自动化事件推送
- **端点**: `/ws/automation-events`
- **功能**: 实时推送自动化规则执行事件

**消息格式**:
```json
{
  "type": "automation_event",
  "ruleId": 1,
  "ruleName": "回家开灯",
  "status": "executed",
  "timestamp": "2026-01-09T03:15:58Z"
}
```

## 8. 错误码定义

| 错误码 | 说明 |
|--------|------|
| 10001 | 用户名已存在 |
| 10002 | 用户不存在 |
| 10003 | 密码错误 |
| 10004 | 令牌无效或已过期 |
| 20001 | 设备ID已存在 |
| 20002 | 设备不存在 |
| 20003 | 设备控制命令发送失败 |
| 30001 | 区域不存在 |
| 40001 | 自动化规则不存在 |

## 9. 参数校验规则

### 9.1 用户相关
- username: 3-20个字符，字母数字下划线
- password: 6-20个字符
- phone: 11位手机号格式

### 9.2 设备相关
- deviceId: 1-100个字符
- deviceName: 1-100个字符
- deviceType: 预定义类型值

### 9.3 区域相关
- areaName: 1-50个字符
- areaType: 预定义类型值

## 10. API版本管理

当前 API 版本为 v1，后续版本将通过 URL 路径区分，如 `/api/v2`。版本升级时将保持向后兼容性，废弃的接口会提前通知。