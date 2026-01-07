# API文档

## 概述

本文档详细描述了智能家居控制系统的所有API接口，包括请求方法、路径、参数、响应格式等信息。

## 基础信息

- **协议**：HTTP/HTTPS
- **数据格式**：JSON
- **认证方式**：JWT Token
- **基础URL**：`http://localhost:8080/api`

## 通用响应格式

所有API响应遵循以下格式：

```json
{
  "code": 200,
  "message": "请求成功",
  "data": {},
  "timestamp": 1678886400000
}
```

### 响应字段说明

- `code`：状态码，200表示成功，其他值表示错误
- `message`：响应消息，描述请求结果
- `data`：响应数据，根据接口不同而变化
- `timestamp`：响应时间戳

## 认证机制

除登录注册接口外，所有接口需要在请求头中携带JWT Token：

```
Authorization: Bearer {token}
```

## 接口列表

### 用户相关接口

#### 用户登录
- **接口**：`POST /auth/login`
- **描述**：用户登录获取JWT Token
- **请求参数**：
  ```json
  {
    "username": "用户名或手机号",
    "password": "密码"
  }
  ```
- **响应数据**：
  ```json
  {
    "token": "JWT Token",
    "user": {
      "id": 1,
      "username": "用户名",
      "phone": "手机号"
    }
  }
  ```

#### 用户注册
- **接口**：`POST /auth/register`
- **描述**：用户注册
- **请求参数**：
  ```json
  {
    "username": "用户名",
    "phone": "手机号",
    "password": "密码"
  }
  ```

### 设备相关接口

#### 获取设备列表
- **接口**：`GET /devices`
- **描述**：获取用户设备列表
- **请求参数**：
  - `userId`：用户ID
- **响应数据**：
  ```json
  [
    {
      "id": 1,
      "userId": 1,
      "areaId": 1,
      "deviceId": "device_001",
      "deviceType": "temperature_sensor",
      "deviceName": "客厅温度传感器",
      "statusName": "温度传感器",
      "onlineStatus": true
    }
  ]
  ```

#### 控制设备
- **接口**：`POST /devices/{id}/control`
- **描述**：控制指定设备
- **路径参数**：
  - `id`：设备ID
- **请求参数**：
  ```json
  {
    "command": "switch",
    "parameters": {
      "state": "on"
    }
  }
  ```

### 区域相关接口

#### 获取区域列表
- **接口**：`GET /areas`
- **描述**：获取用户区域列表
- **请求参数**：
  - `userId`：用户ID
- **响应数据**：
  ```json
  [
    {
      "id": 1,
      "userId": 1,
      "areaName": "客厅",
      "areaType": "fixed",
      "createdAt": "2023-01-01T00:00:00",
      "updatedAt": "2023-01-01T00:00:00"
    }
  ]
  ```

### 自动化规则相关接口

#### 获取自动化规则列表
- **接口**：`GET /automation/rules`
- **描述**：获取用户自动化规则列表
- **请求参数**：
  - `userId`：用户ID
- **响应数据**：
  ```json
  [
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
  ]
  ```

## WebSocket接口

### 设备状态实时更新
- **连接地址**：`ws://localhost:8080/ws`
- **消息格式**：
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

## 错误码

- `200`：请求成功
- `400`：请求参数错误
- `401`：未授权
- `403`：权限不足
- `404`：资源不存在
- `500`：服务器内部错误

## 更新记录

- `2023-01-01`：创建文档