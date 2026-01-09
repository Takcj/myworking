# 智能家居控制系统 - MQTT通信设计文档

## 1. 通信架构概述

智能家居控制系统采用 MQTT 协议实现后端与下位机设备之间的通信。MQTT 作为一种轻量级的发布/订阅消息传输协议，非常适合物联网设备通信场景，具有低带宽消耗、低网络流量和良好的可靠性。

## 2. MQTT 服务配置

### 2.1 服务端配置
- MQTT Broker: EMQ X 或 Mosquitto
- 服务器地址: 根据部署环境配置
- 端口: 1883 (非加密) 或 8883 (TLS加密)
- 认证: 用户名/密码认证
- QoS: 支持 QoS 0, 1, 2 三个等级

### 2.2 客户端配置
- 连接超时时间: 30秒
- 心跳间隔: 60秒
- 自动重连: 启用
- 消息持久化: 根据需要配置

## 3. 消息格式规范

### 3.1 消息结构
所有 MQTT 消息均采用 JSON 格式，统一的消息结构如下：

```json
{
  "timestamp": "时间戳(ISO 8601格式，带时区的完整日期时间)",
  "message_type": "消息类型",
  "data": {
    // 具体数据内容，根据消息类型变化
  }
}
```
message_type 如下
- device_status_message | 设备状态上报 | 设备向服务器发送设备状态信息 |
- device_control_message | 设备控制请求 | 服务器向设备发送控制指令 |
- device_connect_message | 设备连接 | 服务器向设备发送连接信息或设备向服务器发送连接信息 |
- heartbeat_message | 心跳保活 | 设备定期向服务器发送心跳保活信息 | 

### 3.2 消息字段说明

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| timestamp | String | 是 | 消息发送时间，ISO 8601格式 |
| message_type | String | 是 | 消息类型，见下表 |
| data | Object | 是 | 具体业务数据 |

### 3.3 消息类型定义

| 类型值 | 说明 | 发送方 | 接收方 |
|--------|------|--------|--------|
| device_status_message | 设备状态上报 | 设备 | 服务器 |
| device_control_message | 设备控制请求 | 服务器 | 设备 |
| device_response_message | 设备控制响应 | 设备 | 服务器 |
| device_connect_message | 设备连接 | 双向 |
| heartbeat_message | 心跳保活 | 设备 | 服务器 |

## 4. 主题(Topic)设计

### 4.1 主题层级结构
```
smart-home/{device_id}/{operation}
```

### 4.2 具体主题定义

| 操作 | 方向 | 主题示例 | 用途 |
|------|------|----------|------|
| status | 设备→服务器 | smart-home/{device_id}/status | 设备状态上报 |
| control | 服务器→设备 | smart-home/{device_id}/control | 设备控制指令 |
| response | 设备→服务器 | smart-home/{device_id}/response | 控制响应 |
| connect | 双向 | smart-home/{device_id}/connect | 设备连接 |
| heartbeat | 设备→服务器 | smart-home/{device_id}/heartbeat | 心跳保活 |

### 4.3 订阅模式
- 服务器订阅: `smart-home/+/+` 接收所有设备消息
- 设备订阅: `smart-home/{device_id}/+` 接收服务器消息

## 5. 具体消息类型实现

### 5.1 设备状态消息 (device_status_message)
  设备 ————> 服务器
```json
{
  "timestamp": "时间戳(ISO 8601格式)",
  "message_type": "device_status_message",
  "data": {
    "user_id": "用户ID",        // 通过设备ID查找的用户ID
    "area_id": "区域ID",
    "device_type": "设备类型",
    "device_id": "设备唯一ID",
    "data": {  
      // 具体设备状态，根据设备类型变化
    }
  }
}
```

### 5.2 设备控制数据 (device_control_message)
  服务器 ————> 设备
```json
{
  "timestamp": "时间戳(ISO 8601格式)",
  "message_type": "device_control_message",
  "data": {
    "user_id": "用户ID",        // 通过设备ID查找的用户ID
    "area_id": "区域ID",
    "device_type": "设备类型",
    "device_id": "设备唯一ID",
    "data": {
      // 具体控制指令，根据设备类型变化
    }
  }
}
```

### 5.3 设备控制响应数据 (device_response_message)
  设备 ————> 服务器
```json
{
  "timestamp": "时间戳(ISO 8601格式)",
  "message_type": "device_response_message",
  "data": {
    "user_id": "用户ID",        // 通过设备ID查找的用户ID
    "area_id": "区域ID",
    "device_type": "设备类型",
    "device_id": "设备唯一ID",
    "data": {
      "status": "执行结果"
    }
  }
}
```

### 5.4 设备连接数据 (device_connect_message)
  设备 <———> 服务器
```json
{
  "timestamp": "时间戳(ISO 8601格式)",
  "message_type": "device_connect_message",
  "data": {
    "connection_type": "连接类型",
    "data": {
      // 具体连接信息，根据连接类型变化
    }
  }
}
```

### 5.5 心跳保活 (heartbeat_message)
  设备 ———> 服务器
```json
{
  "timestamp": "时间戳(ISO 8601格式)",
  "message_type": "heartbeat_message",
  "data": {
    "device_type": "设备类型",
    "device_id": "设备唯一ID"
  }
}
```


## 6. 后端MQTT服务实现

### 6.1 MQTT配置类 (MqttConfig)
MqttConfig类负责配置MQTT连接参数、消息通道和消息处理流：
- 配置MQTT Broker连接参数，包括服务器地址、用户名密码等
- 设置客户端ID、QoS、心跳间隔、自动重连等参数
- 配置入站和出站消息通道
- 设置消息转换器（JSON格式）
- 配置订阅模式 `smart-home/+/+` 接收所有设备消息
- 配置消息处理器集成流

### 6.2 MQTT服务类 (MqttService)
MqttService类提供向设备发送控制指令的功能：
- 发送设备控制命令到 `smart-home/{device_id}/control` 主题
- 发送设备重启命令
- 发送固件更新命令
- 发送设备配置命令
- 提供统一的接口供Controller层或其他服务调用

### 6.3 MQTT消息处理器 (MqttMessageHandler)
MqttMessageHandler类处理从设备接收到的消息：
- 解析MQTT主题，提取设备ID和操作类型
- 通过设备ID获取用户ID信息
- 解析消息内容，根据消息类型进行相应处理
- 处理设备状态消息、响应消息、连接消息和心跳消息
- 更新设备状态到数据库
- 管理设备在线状态

## 7. 错误处理机制

### 7.1 连接错误处理
- 自动重连机制
- 连接状态监控
- 异常日志记录

### 7.2 消息错误处理
- 消息格式验证
- 消息重复处理
- 消息丢失补偿机制（QOS = 1）

### 7.3 设备离线处理
- 心跳检测
- 设备离线通知
- 离线消息缓存

## 8. 安全机制
- 设备认证：每个设备绑定唯一设备ID
- 用户隔离：通过设备ID关联用户ID实现数据隔离
- 权限控制：设备只能发布到自己的主题

## 9. 性能优化（预设计，实际暂无足量设备可测试）

### 9.1 批量处理
- 批量发送设备状态更新
- 减少网络请求次数

### 9.2 资源管理
- 连接池管理
- 内存使用优化
- 消息队列优化

## 10. 监控与调试

### 10.1 消息监控
- 消息收发统计
- 消息延迟监控
- 错误率统计

### 10.2 设备状态监控
- 设备在线状态
- 设备活跃度统计
- 设备异常告警