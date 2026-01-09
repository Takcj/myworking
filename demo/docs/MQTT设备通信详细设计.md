# 智能家居控制系统 - MQTT设备支持与接入规范

## 1. 设备支持概述

本文档描述设备通过MQTT接入标准，以及支持设备列表和设备详细接入流程。

智能家居控制系统采用 MQTT 协议实现后端与下位机设备之间的通信。MQTT 作为一种轻量级的发布/订阅消息传输协议，非常适合物联网设备通信场景，具有低带宽消耗、低网络流量和良好的可靠性。

系统支持多种类型的智能设备接入，包括但不限于照明设备、传感器、环境控制设备等。所有设备都遵循统一的通信协议和消息格式，确保系统的一致性和可扩展性。

## 2. 支持的设备类型

### 2.1 LED灯
- **设备类型**: led
- **功能**: 可读取和控制开关、颜色、亮度

### 2.2 窗帘
- **设备类型**: curtain
- **功能**: 可读取和控制滚动位置

### 2.3 温度传感器
- **设备类型**: temperature_sensor
- **功能**: 可读取温度

### 2.4 湿度传感器
- **设备类型**: humidity_sensor
- **功能**: 可读取湿度

### 2.5 光敏传感器
- **设备类型**: light_sensor
- **功能**: 可读取光照强度

### 2.6 空调
- **设备类型**: air_conditioner
- **功能**: 可读取和控制空调配置，包含温度、运行模式、风速、出风模式、定时

### 2.7 扫地机
- **设备类型**: vacuum_cleaner
- **功能**: 可读取和控制扫地机的运行模式

## 3. 设备接入规范

### 3.1 设备认证要求
- 每个设备在出厂时或通过App配置后，应具备用户手机号和设备唯一ID
- 设备需要具备唯一的设备ID
- 设备认证需要携带手机号信息

### 3.2 连接要求
- 服务器地址：47.113.102.245
- 端口: 1883 (非加密) 或 8883 (TLS加密)
- 心跳间隔设置为60秒
- 自动重连机制（重试间隔5秒，最大重试次数10次）
- 支持QoS 0或1级别

### 3.3 设备连接流程

1. **设备启动后**，使用已配置的用户手机号和设备ID连接到MQTT Broker
2. **发送设备连接消息**:
   ```json
   {
     "timestamp": "2026-01-09T03:15:58Z",
     "message_type": "device_connect_message",
     "data": {
       "connection_type": "connection",
       "data": {
         "phone_number": "用户手机号",
         "device_id": "设备唯一ID",
         "device_type": "设备类型"
       }
     }
   }
   ```

3. **服务器验证**:
   - 服务器通过手机号查找对应的用户ID
   - 验证设备ID是否属于该用户
   - 验证通过后，设备成功接入系统

4. **订阅控制主题**:
   - 订阅 `smart-home/{device_id}/control` 接收控制指令
   - 订阅 `smart-home/{device_id}/connect` 接收连接指令

### 3.4 设备状态上报规范

#### 3.4.1 LED灯状态上报
```json
{
  "timestamp": "2026-01-09T03:15:58Z",
  "message_type": "device_status_message",
  "data": {
    "user_id": "用户ID",
    "area_id": "区域ID",
    "device_type": "led",
    "device_id": "设备唯一ID",
    "data": {
      "power": "on/off",
      "brightness": 0-100,
      "color": {
        "r": 0-255,
        "g": 0-255,
        "b": 0-255
      }
    }
  }
}
```

#### 3.4.2 窗帘状态上报
```json
{
  "timestamp": "2026-01-09T03:15:58Z",
  "message_type": "device_status_message",
  "data": {
    "user_id": "用户ID",
    "area_id": "区域ID",
    "device_type": "curtain",
    "device_id": "设备唯一ID",
    "data": {
      "position": 0-100,
      "moving_direction": "up/down/stop"
    }
  }
}
```

#### 3.4.3 温度传感器状态上报
```json
{
  "timestamp": "2026-01-09T03:15:58Z",
  "message_type": "device_status_message",
  "data": {
    "user_id": "用户ID",
    "area_id": "区域ID",
    "device_type": "temperature_sensor",
    "device_id": "设备唯一ID",
    "data": {
      "temperature": 23.5,
      "unit": "celsius"
    }
  }
}
```

#### 3.4.4 湿度传感器状态上报
```json
{
  "timestamp": "2026-01-09T03:15:58Z",
  "message_type": "device_status_message",
  "data": {
    "user_id": "用户ID",
    "area_id": "区域ID",
    "device_type": "humidity_sensor",
    "device_id": "设备唯一ID",
    "data": {
      "humidity": 60.0,
      "unit": "percentage"
    }
  }
}
```

#### 3.4.5 光敏传感器状态上报
```json
{
  "timestamp": "2026-01-09T03:15:58Z",
  "message_type": "device_status_message",
  "data": {
    "user_id": "用户ID",
    "area_id": "区域ID",
    "device_type": "light_sensor",
    "device_id": "设备唯一ID",
    "data": {
      "illuminance": 300,
      "unit": "lux"
    }
  }
}
```

#### 3.4.6 空调状态上报
```json
{
  "timestamp": "2026-01-09T03:15:58Z",
  "message_type": "device_status_message",
  "data": {
    "user_id": "用户ID",
    "area_id": "区域ID",
    "device_type": "air_conditioner",
    "device_id": "设备唯一ID",
    "data": {
      "power": "on/off",
      "temperature": 24,
      "mode": "cool/heat/fan/dry/auto",
      "fan_speed": "low/medium/high/auto",
      "swing_mode": "vertical/horizontal/both/off",
      "timer_on": 0,
      "timer_off": 0
    }
  }
}
```

#### 3.4.7 扫地机状态上报
```json
{
  "timestamp": "2026-01-09T03:15:58Z",
  "message_type": "device_status_message",
  "data": {
    "user_id": "用户ID",
    "area_id": "区域ID",
    "device_type": "vacuum_cleaner",
    "device_id": "设备唯一ID",
    "data": {
      "status": "idle/charging/cleaning/returning/error",
      "mode": "auto/spot/edge/single_room",
      "battery_level": 85,
      "cleaning_area": 15.5,
      "cleaning_time": 30
    }
  }
}
```

### 3.5 设备控制指令规范

#### 3.5.1 LED灯控制
```json
{
  "timestamp": "2026-01-09T03:15:58Z",
  "message_type": "device_control_message",
  "data": {
    "user_id": "用户ID",
    "area_id": "区域ID",
    "device_type": "led",
    "device_id": "设备唯一ID",
    "data": {
      "command": "set_power",
      "parameters": {
        "power": "on/off"
      }
    }
  }
}
```

支持的LED灯命令:
- `set_power`: 设置开关状态
- `set_brightness`: 设置亮度
- `set_color`: 设置颜色
- `toggle`: 切换开关状态

#### 3.5.2 窗帘控制
```json
{
  "timestamp": "2026-01-09T03:15:58Z",
  "message_type": "device_control_message",
  "data": {
    "user_id": "用户ID",
    "area_id": "区域ID",
    "device_type": "curtain",
    "device_id": "设备唯一ID",
    "data": {
      "command": "set_position",
      "parameters": {
        "position": 50
      }
    }
  }
}
```

支持的窗帘命令:
- `set_position`: 设置位置
- `open`: 完全打开
- `close`: 完全关闭
- `stop`: 停止运动

#### 3.5.3 空调控制
```json
{
  "timestamp": "2026-01-09T03:15:58Z",
  "message_type": "device_control_message",
  "data": {
    "user_id": "用户ID",
    "area_id": "区域ID",
    "device_type": "air_conditioner",
    "device_id": "设备唯一ID",
    "data": {
      "command": "set_power",
      "parameters": {
        "power": "on/off"
      }
    }
  }
}
```

支持的空调命令:
- `set_power`: 设置开关状态
- `set_temperature`: 设置温度
- `set_mode`: 设置运行模式
- `set_fan_speed`: 设置风速
- `set_swing_mode`: 设置摆风模式
- `set_timer_on`: 设置开机定时
- `set_timer_off`: 设置关机定时

### 3.6 设备控制响应规范

```json
{
  "timestamp": "2026-01-09T03:15:59Z",
  "message_type": "device_response_message",
  "data": {
    "user_id": "用户ID",
    "area_id": "区域ID",
    "device_type": "led",
    "device_id": "设备唯一ID",
    "data": {
      "status": "success / failure"
    }
  }
}
```

### 3.7 心跳保活机制

设备需要定期发送心跳消息以维持连接状态，建议频率为每分钟一次：
```json
{
  "timestamp": "2026-01-09T03:15:58Z",
  "message_type": "heartbeat_message",
  "data": {
    "device_type": "led",
    "device_id": "设备唯一ID",
  }
}
```

## 4. 设备接入示例

### 4.1 LED灯接入示例

1. **设备连接**:
   - 连接到MQTT Broker
   - 发送设备连接消息

2. **订阅主题**:
   - 订阅 `smart-home/{device_id}/control` 接收控制指令
   - 订阅 `smart-home/{device_id}/connect` 接收连接指令

3. **上报状态**:
   - 当设备状态改变时，发送设备状态消息到 `smart-home/{device_id}/status`

4. **处理控制指令**:
   - 接收控制指令并执行相应操作
   - 发送响应消息到 `smart-home/{device_id}/response`

### 4.2 传感器设备接入示例

1. **设备连接**:
   - 连接到MQTT Broker
   - 发送设备连接消息

2. **订阅主题**:
   - 订阅 `smart-home/{device_id}/connect` 接收连接指令

3. **周期性上报**:
   - 根据配置的采样频率，定期发送设备状态消息到 `smart-home/{device_id}/status`

## 5. 设备管理

### 5.1 设备配置
- 用户通过手机App将设备与账户绑定
- App将用户手机号和认证信息配置到设备
- 服务器通过手机号查找用户ID，完成设备与用户关联

### 5.2 设备状态监控
- 通过心跳消息监控设备在线状态
- 通过状态上报监控设备工作状态
- 设备长时间无响应视为离线

## 6. 故障处理

### 6.1 连接故障
- 设备检测到连接断开时，执行自动重连
- 重连失败超过阈值时，上报离线状态

### 6.2 消息处理故障
- 消息格式错误时，记录日志并忽略
- 指令执行失败时，返回错误响应

### 6.3 设备故障
- 设备检测到硬件故障时，上报故障状态
- 系统记录故障日志并通知用户