# 智能家居控制系统API文档

## 1. 概述

本文档详细描述了智能家居控制系统的所有API接口，包括请求方法、路径、参数、响应格式等信息。API遵循前后端数据传输规范，确保各端与后端通信的一致性。

## 2. 基础信息

- **协议**：HTTP/HTTPS
- **数据格式**：JSON
- **认证方式**：JWT Token
- **基础URL**：`http://localhost:8080/api/v1`
- **API版本**：v1

## 3. 通用响应格式

所有API响应遵循以下格式：

```json
{
  "code": 200,
  "message": "请求成功",
  "data": {},
  "timestamp": 1678886400000
}
```

### 3.1 响应字段说明

- `code`：状态码，200表示成功，其他值表示错误
- `message`：响应消息，描述请求结果
- `data`：响应数据，根据接口不同而变化
- `timestamp`：响应时间戳

## 4. 认证机制

除登录注册接口外，所有接口需要在请求头中携带JWT Token：

```
Authorization: Bearer {token}
```

## 5. 用户与设备关系说明

本系统支持多用户对多设备的关系，即一个设备可以被多个用户共享使用，一个用户也可以拥有多个设备。

- **设备归属管理**：通过用户设备归属表管理用户对设备的归属关系
- **归属类型**：
  - `owner`：设备所有者，具有最高权限，可删除设备和分享设备
  - `shared`：共享用户，可查看和控制设备

## 6. 接口列表

### 6.1 用户相关接口

#### 6.1.1 用户登录
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
    "code": 200,
    "message": "登录成功",
    "data": {
      "token": "JWT Token",
      "user": {
        "id": 1,
        "username": "用户名",
        "phone": "手机号",
        "createdAt": "2023-01-01T00:00:00",
        "lastLoginAt": "2023-01-01T12:00:00"
      }
    },
    "timestamp": 1678886400000
  }
  ```

#### 6.1.2 用户注册
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
- **响应数据**：
  ```json
  {
    "code": 200,
    "message": "注册成功",
    "data": {
      "id": 1,
      "username": "用户名",
      "phone": "手机号",
      "createdAt": "2023-01-01T00:00:00",
      "lastLoginAt": null
    },
    "timestamp": 1678886400000
  }
  ```

#### 6.1.3 获取用户信息
- **接口**：`GET /users/profile`
- **描述**：获取当前登录用户信息
- **请求头**：`Authorization: Bearer {token}`
- **响应数据**：
  ```json
  {
    "code": 200,
    "message": "获取用户信息成功",
    "data": {
      "id": 1,
      "username": "用户名",
      "phone": "手机号",
      "createdAt": "2023-01-01T00:00:00",
      "lastLoginAt": "2023-01-01T12:00:00"
    },
    "timestamp": 1678886400000
  }
  ```

### 6.2 设备相关接口

#### 6.2.1 获取设备列表
- **接口**：`GET /devices`
- **描述**：获取用户有权限访问的设备列表
- **请求头**：`Authorization: Bearer {token}`
- **查询参数**：
  - `page`: 页码，默认1
  - `size`: 每页数量，默认10
- **响应数据**：
  ```json
  {
    "code": 200,
    "message": "获取设备列表成功",
    "data": [
      {
        "id": 1,
        "deviceId": "device_001",
        "deviceType": "temperature_sensor",
        "deviceName": "客厅温度传感器",
        "createdAt": "2023-01-01T00:00:00",
        "updatedAt": "2023-01-01T00:00:00",
        "onlineStatus": true,
        "permissionLevel": "owner"
      }
    ],
    "timestamp": 1678886400000
  }
  ```

#### 6.2.2 获取设备列表（包含在线状态）
- **接口**：`GET /devices/with-status`
- **描述**：获取用户有权限访问的设备列表及在线状态
- **请求头**：`Authorization: Bearer {token}`
- **查询参数**：
  - `page`: 页码，默认1
  - `size`: 每页数量，默认10
- **响应数据**：
  ```json
  {
    "code": 200,
    "message": "获取设备及在线状态成功",
    "data": [
      {
        "id": 1,
        "deviceId": "device_001",
        "deviceType": "temperature_sensor",
        "deviceName": "客厅温度传感器",
        "createdAt": "2023-01-01T00:00:00",
        "updatedAt": "2023-01-01T00:00:00",
        "onlineStatus": true,
        "permissionLevel": "shared"
      }
    ],
    "timestamp": 1678886400000
  }
  ```

#### 6.2.3 获取设备列表（包含详细状态）
- **接口**：`GET /devices/with-detailed-status`
- **描述**：获取用户有权限访问的设备列表及详细状态
- **请求头**：`Authorization: Bearer {token}`
- **查询参数**：
  - `page`: 页码，默认1
  - `size`: 每页数量，默认10
- **响应数据**：
  ```json
  {
    "code": 200,
    "message": "获取设备及详细状态成功",
    "data": [
      {
        "id": 1,
        "deviceId": "device_001",
        "deviceType": "temperature_sensor",
        "deviceName": "客厅温度传感器",
        "createdAt": "2023-01-01T00:00:00",
        "updatedAt": "2023-01-01T00:00:00",
        "onlineStatus": true,
        "statusData": "{\"temperature\": 25.5, \"humidity\": 60}",
        "permissionLevel": "owner"
      }
    ],
    "timestamp": 1678886400000
  }
  ```

#### 6.2.4 获取特定设备
- **接口**：`GET /devices/{id}`
- **描述**：获取特定设备信息
- **路径参数**：
  - `id`: 设备ID
- **请求头**：`Authorization: Bearer {token}`
- **响应数据**：
  ```json
  {
    "code": 200,
    "message": "获取设备信息成功",
    "data": {
      "id": 1,
      "deviceId": "device_001",
      "deviceType": "temperature_sensor",
      "deviceName": "客厅温度传感器",
      "createdAt": "2023-01-01T00:00:00",
      "updatedAt": "2023-01-01T00:00:00",
      "onlineStatus": true,
      "permissionLevel": "owner"
    },
    "timestamp": 1678886400000
  }
  ```

#### 6.2.5 绑定设备到用户
- **接口**：`POST /devices/bind`
- **描述**：将设备绑定到用户
- **请求头**：`Authorization: Bearer {token}`
- **请求参数**：
  - `deviceId`: 设备ID
  - `isOwner`: 是否为所有者，默认false
- **响应数据**：
  ```json
  {
    "code": 200,
    "message": "设备绑定成功",
    "data": {
      "id": 1,
      "deviceId": "device_001",
      "deviceType": "temperature_sensor",
      "deviceName": "客厅温度传感器",
      "createdAt": "2023-01-01T12:00:00",
      "updatedAt": "2023-01-01T12:00:00",
      "onlineStatus": false,
      "permissionLevel": "owner"
    },
    "timestamp": 1678886400000
  }
  ```

#### 6.2.6 设备分享（新增）
- **接口**：`POST /devices/{deviceId}/share`
- **描述**：将设备分享给其他用户
- **路径参数**：
  - `deviceId`: 设备ID
- **请求头**：`Authorization: Bearer {token}`
- **请求参数**：
  - `sourceUserId`: 分享者用户ID
  - `targetUserId`: 目标用户ID
- **响应数据**：
  ```json
  {
    "code": 200,
    "message": "设备分享成功",
    "data": null,
    "timestamp": 1678886400000
  }
  ```

#### 6.2.7 添加设备
- **接口**：`POST /devices`
- **描述**：添加新设备
- **请求头**：`Authorization: Bearer {token}`
- **请求体**：
  ```json
  {
    "deviceId": "device_002",
    "deviceType": "led",
    "deviceName": "客厅LED灯"
  }
  ```
- **响应数据**：
  ```json
  {
    "code": 200,
    "message": "设备添加成功",
    "data": {
      "id": 2,
      "deviceId": "device_002",
      "deviceType": "led",
      "deviceName": "客厅LED灯",
      "createdAt": "2023-01-01T12:00:00",
      "updatedAt": "2023-01-01T12:00:00",
      "onlineStatus": false,
      "permissionLevel": "owner"
    },
    "timestamp": 1678886400000
  }
  ```

#### 6.2.8 更新设备
- **接口**：`PUT /devices/{id}`
- **描述**：更新设备信息
- **路径参数**：
  - `id`: 设备ID
- **请求头**：`Authorization: Bearer {token}`
- **请求体**：
  ```json
  {
    "deviceName": "更新后的设备名"
  }
  ```
- **响应数据**：
  ```json
  {
    "code": 200,
    "message": "设备更新成功",
    "data": {
      "id": 1,
      "deviceId": "device_001",
      "deviceType": "temperature_sensor",
      "deviceName": "更新后的设备名",
      "createdAt": "2023-01-01T00:00:00",
      "updatedAt": "2023-01-01T13:00:00",
      "onlineStatus": true,
      "permissionLevel": "owner"
    },
    "timestamp": 1678886400000
  }
  ```

#### 6.2.9 删除设备
- **接口**：`DELETE /devices/{id}`
- **描述**：删除设备
- **路径参数**：
  - `id`: 设备ID
- **请求头**：`Authorization: Bearer {token}`
- **响应数据**：
  ```json
  {
    "code": 200,
    "message": "设备删除成功",
    "data": null,
    "timestamp": 1678886400000
  }
  ```

#### 6.2.10 控制设备
- **接口**：`POST /devices/{id}/control`
- **描述**：控制指定设备
- **路径参数**：
  - `id`: 设备ID
- **请求头**：`Authorization: Bearer {token}`
- **请求体**：
  ```json
  {
    "command": "switch",
    "parameters": {
      "state": "on"
    }
  }
  ```
- **响应数据**：
  ```json
  {
    "code": 200,
    "message": "控制命令已发送",
    "data": null,
    "timestamp": 1678886400000
  }
  ```

### 6.3 区域相关接口

#### 6.3.1 获取区域列表
- **接口**：`GET /areas`
- **描述**：获取用户有权限访问的区域列表
- **请求头**：`Authorization: Bearer {token}`
- **响应数据**：
  ```json
  {
    "code": 200,
    "message": "获取区域列表成功",
    "data": [
      {
        "id": 1,
        "areaName": "客厅",
        "areaType": "fixed",
        "createdAt": "2023-01-01T00:00:00",
        "updatedAt": "2023-01-01T00:00:00"
      }
    ],
    "timestamp": 1678886400000
  }
  ```

#### 6.3.2 获取特定区域
- **接口**：`GET /areas/{id}`
- **描述**：获取特定区域信息
- **路径参数**：
  - `id`: 区域ID
- **请求头**：`Authorization: Bearer {token}`
- **响应数据**：
  ```json
  {
    "code": 200,
    "message": "获取区域信息成功",
    "data": {
      "id": 1,
      "areaName": "客厅",
      "areaType": "fixed",
      "createdAt": "2023-01-01T00:00:00",
      "updatedAt": "2023-01-01T00:00:00"
    },
    "timestamp": 1678886400000
  }
  ```

#### 6.3.3 添加区域
- **接口**：`POST /areas`
- **描述**：添加新区域
- **请求头**：`Authorization: Bearer {token}`
- **请求体**：
  ```json
  {
    "areaName": "卧室",
    "areaType": "fixed"
  }
  ```
- **响应数据**：
  ```json
  {
    "code": 200,
    "message": "区域添加成功",
    "data": {
      "id": 2,
      "areaName": "卧室",
      "areaType": "fixed",
      "createdAt": "2023-01-01T12:00:00",
      "updatedAt": "2023-01-01T12:00:00"
    },
    "timestamp": 1678886400000
  }
  ```

#### 6.3.4 更新区域
- **接口**：`PUT /areas/{id}`
- **描述**：更新区域信息
- **路径参数**：
  - `id`: 区域ID
- **请求头**：`Authorization: Bearer {token}`
- **请求体**：
  ```json
  {
    "areaName": "主卧",
    "areaType": "fixed"
  }
  ```
- **响应数据**：
  ```json
  {
    "code": 200,
    "message": "区域更新成功",
    "data": {
      "id": 1,
      "areaName": "主卧",
      "areaType": "fixed",
      "createdAt": "2023-01-01T00:00:00",
      "updatedAt": "2023-01-01T13:00:00"
    },
    "timestamp": 1678886400000
  }
  ```

#### 6.3.5 删除区域
- **接口**：`DELETE /areas/{id}`
- **描述**：删除区域
- **路径参数**：
  - `id`: 区域ID
- **请求头**：`Authorization: Bearer {token}`
- **响应数据**：
  ```json
  {
    "code": 200,
    "message": "区域删除成功",
    "data": null,
    "timestamp": 1678886400000
  }
  ```

### 6.4 自动化规则相关接口

#### 6.4.1 获取自动化规则列表
- **接口**：`GET /automation/rules`
- **描述**：获取用户创建的自动化规则列表
- **请求头**：`Authorization: Bearer {token}`
- **请求参数**：
  - `userId`: 用户ID
- **响应数据**：
  ```json
  {
    "code": 200,
    "message": "获取自动化规则列表成功",
    "data": [
      {
        "id": 1,
        "userId": 1,
        "ruleName": "温度过高自动开空调",
        "triggerType": "device_status",
        "triggerCondition": "{\"deviceType\": \"temperature_sensor\", \"field\": \"temperature\", \"conditionType\": \">\", \"value\": 26}",
        "targetDeviceId": "ac_001",
        "targetDeviceType": "air_conditioner",
        "commandType": "power",
        "commandParameters": "{\"state\": \"on\"}",
        "isEnabled": true,
        "createdAt": "2023-01-01T00:00:00",
        "updatedAt": "2023-01-01T00:00:00"
      }
    ],
    "timestamp": 1678886400000
  }
  ```

#### 6.4.2 获取特定自动化规则
- **接口**：`GET /automation/rules/{id}`
- **描述**：获取特定自动化规则信息
- **路径参数**：
  - `id`: 规则ID
- **请求头**：`Authorization: Bearer {token}`
- **请求参数**：
  - `userId`: 用户ID
- **响应数据**：
  ```json
  {
    "code": 200,
    "message": "获取自动化规则信息成功",
    "data": {
      "id": 1,
      "userId": 1,
      "ruleName": "温度过高自动开空调",
      "triggerType": "device_status",
      "triggerCondition": "{\"deviceType\": \"temperature_sensor\", \"field\": \"temperature\", \"conditionType\": \">\", \"value\": 26}",
      "targetDeviceId": "ac_001",
      "targetDeviceType": "air_conditioner",
      "commandType": "power",
      "commandParameters": "{\"state\": \"on\"}",
      "isEnabled": true,
      "createdAt": "2023-01-01T00:00:00",
      "updatedAt": "2023-01-01T00:00:00"
    },
    "timestamp": 1678886400000
  }
  ```

#### 6.4.3 创建自动化规则
- **接口**：`POST /automation/rules`
- **描述**：创建自动化规则
- **请求头**：`Authorization: Bearer {token}`
- **请求参数**：
  - `userId`: 用户ID
- **请求体**：
  ```json
  {
    "ruleName": "湿度高时开除湿机",
    "triggerType": "device_status",
    "triggerCondition": "{\"deviceType\": \"humidity_sensor\", \"field\": \"humidity\", \"conditionType\": \">\", \"value\": 70}",
    "targetDeviceId": "dehumidifier_001",
    "targetDeviceType": "dehumidifier",
    "commandType": "power",
    "commandParameters": "{\"state\": \"on\"}",
    "isEnabled": true
  }
  ```
- **响应数据**：
  ```json
  {
    "code": 200,
    "message": "自动化规则创建成功",
    "data": {
      "id": 2,
      "userId": 1,
      "ruleName": "湿度高时开除湿机",
      "triggerType": "device_status",
      "triggerCondition": "{\"deviceType\": \"humidity_sensor\", \"field\": \"humidity\", \"conditionType\": \">\", \"value\": 70}",
      "targetDeviceId": "dehumidifier_001",
      "targetDeviceType": "dehumidifier",
      "commandType": "power",
      "commandParameters": "{\"state\": \"on\"}",
      "isEnabled": true,
      "createdAt": "2023-01-01T12:00:00",
      "updatedAt": "2023-01-01T12:00:00"
    },
    "timestamp": 1678886400000
  }
  ```

#### 6.4.4 更新自动化规则
- **接口**：`PUT /automation/rules/{id}`
- **描述**：更新自动化规则
- **路径参数**：
  - `id`: 规则ID
- **请求头**：`Authorization: Bearer {token}`
- **请求参数**：
  - `userId`: 用户ID
- **请求体**：
  ```json
  {
    "ruleName": "更新后的规则名",
    "isEnabled": false
  }
  ```
- **响应数据**：
  ```json
  {
    "code": 200,
    "message": "自动化规则更新成功",
    "data": {
      "id": 1,
      "userId": 1,
      "ruleName": "更新后的规则名",
      "triggerType": "device_status",
      "triggerCondition": "{\"deviceType\": \"temperature_sensor\", \"field\": \"temperature\", \"conditionType\": \">\", \"value\": 26}",
      "targetDeviceId": "ac_001",
      "targetDeviceType": "air_conditioner",
      "commandType": "power",
      "commandParameters": "{\"state\": \"on\"}",
      "isEnabled": false,
      "createdAt": "2023-01-01T00:00:00",
      "updatedAt": "2023-01-01T13:00:00"
    },
    "timestamp": 1678886400000
  }
  ```

#### 6.4.5 删除自动化规则
- **接口**：`DELETE /automation/rules/{id}`
- **描述**：删除自动化规则
- **路径参数**：
  - `id`: 规则ID
- **请求头**：`Authorization: Bearer {token}`
- **请求参数**：
  - `userId`: 用户ID
- **响应数据**：
  ```json
  {
    "code": 200,
    "message": "自动化规则删除成功",
    "data": null,
    "timestamp": 1678886400000
  }
  ```

#### 6.4.6 启用自动化规则
- **接口**：`PUT /automation/rules/{id}/enable`
- **描述**：启用自动化规则
- **路径参数**：
  - `id`: 规则ID
- **请求头**：`Authorization: Bearer {token}`
- **请求参数**：
  - `userId`: 用户ID
- **响应数据**：
  ```json
  {
    "code": 200,
    "message": "自动化规则已启用",
    "data": {
      "id": 1,
      "userId": 1,
      "ruleName": "温度过高自动开空调",
      "triggerType": "device_status",
      "triggerCondition": "{\"deviceType\": \"temperature_sensor\", \"field\": \"temperature\", \"conditionType\": \">\", \"value\": 26}",
      "targetDeviceId": "ac_001",
      "targetDeviceType": "air_conditioner",
      "commandType": "power",
      "commandParameters": "{\"state\": \"on\"}",
      "isEnabled": true,
      "createdAt": "2023-01-01T00:00:00",
      "updatedAt": "2023-01-01T13:00:00"
    },
    "timestamp": 1678886400000
  }
  ```

#### 6.4.7 禁用自动化规则
- **接口**：`PUT /automation/rules/{id}/disable`
- **描述**：禁用自动化规则
- **路径参数**：
  - `id`: 规则ID
- **请求头**：`Authorization: Bearer {token}`
- **请求参数**：
  - `userId`: 用户ID
- **响应数据**：
  ```json
  {
    "code": 200,
    "message": "自动化规则已禁用",
    "data": {
      "id": 1,
      "userId": 1,
      "ruleName": "温度过高自动开空调",
      "triggerType": "device_status",
      "triggerCondition": "{\"deviceType\": \"temperature_sensor\", \"field\": \"temperature\", \"conditionType\": \">\", \"value\": 26}",
      "targetDeviceId": "ac_001",
      "targetDeviceType": "air_conditioner",
      "commandType": "power",
      "commandParameters": "{\"state\": \"on\"}",
      "isEnabled": false,
      "createdAt": "2023-01-01T00:00:00",
      "updatedAt": "2023-01-01T13:00:00"
    },
    "timestamp": 1678886400000
  }
  ```

#### 6.4.8 批量启用自动化规则
- **接口**：`PUT /automation/rules/batch-enable`
- **描述**：批量启用自动化规则
- **请求头**：`Authorization: Bearer {token}`
- **请求参数**：
  - `userId`: 用户ID
- **请求体**：
  ```json
  {
    "ids": [1, 2, 3]
  }
  ```
- **响应数据**：
  ```json
  {
    "code": 200,
    "message": "批量启用自动化规则成功",
    "data": null,
    "timestamp": 1678886400000
  }
  ```

#### 6.4.9 批量禁用自动化规则
- **接口**：`PUT /automation/rules/batch-disable`
- **描述**：批量禁用自动化规则
- **请求头**：`Authorization: Bearer {token}`
- **请求参数**：
  - `userId`: 用户ID
- **请求体**：
  ```json
  {
    "ids": [1, 2, 3]
  }
  ```
- **响应数据**：
  ```json
  {
    "code": 200,
    "message": "批量禁用自动化规则成功",
    "data": null,
    "timestamp": 1678886400000
  }
  ```

#### 6.4.10 检查设备是否支持自动化
- **接口**：`GET /automation/check-device-support`
- **描述**：检查设备类型是否支持自动化
- **请求参数**：
  - `deviceType`: 设备类型
- **响应数据**：
  ```json
  {
    "code": 200,
    "message": "设备支持状态查询成功",
    "data": true,
    "timestamp": 1678886400000
  }
  ```

### 6.5 MQTT控制相关接口

#### 6.5.1 发送控制命令到设备
- **接口**：`POST /mqtt/send-control-command`
- **描述**：发送控制命令到设备
- **请求头**：`Authorization: Bearer {token}`
- **请求体**：
  ```json
  {
    "data": {
      "deviceType": "led",
      "deviceId": "led_001",
      "command": {
        "type": "switch",
        "parameters": {
          "state": "on"
        }
      }
    }
  }
  ```
- **响应数据**：
  ```json
  {
    "code": 200,
    "message": "控制命令发送成功",
    "data": null,
    "timestamp": 1678886400000
  }
  ```

#### 6.5.2 发送批量控制命令
- **接口**：`POST /mqtt/send-batch-command`
- **描述**：发送批量控制命令
- **请求头**：`Authorization: Bearer {token}`
- **请求体**：
  ```json
  {
    "commands": [
      {
        "deviceType": "led",
        "deviceId": "led_001",
        "command": {
          "type": "switch",
          "parameters": {
            "state": "off"
          }
        }
      },
      {
        "deviceType": "curtain",
        "deviceId": "curtain_001",
        "command": {
          "type": "position",
          "parameters": {
            "value": 50
          }
        }
      }
    ]
  }
  ```
- **响应数据**：
  ```json
  {
    "code": 200,
    "message": "批量控制命令发送成功",
    "data": null,
    "timestamp": 1678886400000
  }
  ```

### 6.6 连接状态相关接口

#### 6.6.1 检查设备是否在线
- **接口**：`GET /connection/is-online/{deviceId}`
- **描述**：检查设备是否在线
- **路径参数**：
  - `deviceId`: 设备ID
- **请求头**：`Authorization: Bearer {token}`
- **响应数据**：
  ```json
  {
    "code": 200,
    "message": "设备在线状态查询成功",
    "data": true,
    "timestamp": 1678886400000
  }
  ```

#### 6.6.2 获取设备连接状态详情
- **接口**：`GET /connection/status/{deviceId}`
- **描述**：获取设备连接状态详情
- **路径参数**：
  - `deviceId`: 设备ID
- **请求头**：`Authorization: Bearer {token}`
- **响应数据**：
  ```json
  {
    "code": 200,
    "message": "设备连接状态查询成功",
    "data": {
      "id": 1,
      "deviceId": "device_001",
      "connectionStatus": "online",
      "connectionTime": "2023-01-01T12:00:00",
      "disconnectionTime": null,
      "lastHeartbeat": "2023-01-01T12:30:00",
      "createdAt": "2023-01-01T00:00:00",
      "updatedAt": "2023-01-01T12:30:00"
    },
    "timestamp": 1678886400000
  }
  ```

## 7. WebSocket接口

### 7.1 设备状态实时更新
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

## 8. 错误码

- `200`：请求成功
- `400`：请求参数错误
- `401`：未授权
- `403`：权限不足
- `404`：资源不存在
- `422`：业务逻辑错误
- `500`：服务器内部错误

## 9. 更新记录

- `2023-01-01`：创建文档
- `2023-01-02`：根据前后端数据传输规范更新
- `2023-01-03`：添加设备绑定接口说明
- `2023-01-04`：添加设备详细状态接口说明
- `2023-01-05`：根据最新设计重新整理完整API文档
- `2023-01-06`：添加多用户对多设备关系说明，更新设备分享接口
- `2023-01-07`：简化权限模型，移除复杂的权限分级
- `2023-01-08`：更新自动化规则接口，支持设备触发条件和执行命令