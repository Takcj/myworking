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
  "user_id": "用户ID",
  "device_id": "设备唯一ID",
  "timestamp": "时间戳(ISO 8601格式)",
  "message_type": "消息类型",
  "data": {
    // 具体数据内容，根据消息类型变化
  }
}
```

### 3.2 消息字段说明

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| user_id | String | 是 | 用户唯一标识 |
| device_id | String | 是 | 设备唯一标识 |
| timestamp | String | 是 | 消息发送时间，ISO 8601格式 |
| message_type | String | 是 | 消息类型，见下表 |
| data | Object | 是 | 具体业务数据 |

### 3.3 消息类型定义

| 类型值 | 说明 | 发送方 | 接收方 |
|--------|------|--------|--------|
| device_status_report | 设备状态上报 | 设备 | 服务器 |
| device_control_request | 设备控制请求 | 服务器 | 设备 |
| device_control_response | 设备控制响应 | 设备 | 服务器 |
| device_registration | 设备注册 | 设备 | 服务器 |
| device_deregistration | 设备注销 | 设备 | 服务器 |
| heartbeat | 心跳保活 | 设备 | 服务器 |
| sync_time | 时间同步 | 服务器 | 设备 |

## 4. 主题(Topic)设计

### 4.1 主题层级结构
```
smart-home/{user_id}/{device_id}/{operation}
```

### 4.2 具体主题定义

| 操作 | 方向 | 主题示例 | 用途 |
|------|------|----------|------|
| status | 设备→服务器 | smart-home/12345/001/status | 设备状态上报 |
| control | 服务器→设备 | smart-home/12345/001/control | 设备控制指令 |
| response | 设备→服务器 | smart-home/12345/001/response | 控制响应 |
| register | 设备→服务器 | smart-home/+/+/register | 设备注册 |
| heartbeat | 设备→服务器 | smart-home/12345/001/heartbeat | 心跳保活 |
| time-sync | 服务器→设备 | smart-home/12345/001/time-sync | 时间同步 |

### 4.3 订阅模式
- 服务器订阅: `smart-home/+/+/+` 接收所有设备消息
- 设备订阅: `smart-home/{user_id}/{device_id}/control` 接收控制指令

## 5. 具体消息类型实现

### 5.1 设备状态上报 (device_status_report)

```json
{
  "user_id": "12345",
  "device_id": "001",
  "timestamp": "2026-01-09T03:15:58Z",
  "message_type": "device_status_report",
  "data": {
    "temperature": 23.5,
    "humidity": 60,
    "power_state": "on",
    "brightness": 80
  }
}
```

### 5.2 设备控制请求 (device_control_request)

```json
{
  "user_id": "12345",
  "device_id": "001",
  "timestamp": "2026-01-09T03:15:58Z",
  "message_type": "device_control_request",
  "data": {
    "command": "turn_on",
    "parameters": {
      "brightness": 100
    }
  }
}
```

### 5.3 设备控制响应 (device_control_response)

```json
{
  "user_id": "12345",
  "device_id": "001",
  "timestamp": "2026-01-09T03:15:59Z",
  "message_type": "device_control_response",
  "data": {
    "request_id": "req_abc123",
    "status": "success",
    "message": "Command executed successfully"
  }
}
```

### 5.4 设备注册 (device_registration)

```json
{
  "user_id": "12345",
  "device_id": "001",
  "timestamp": "2026-01-09T03:15:58Z",
  "message_type": "device_registration",
  "data": {
    "device_type": "light",
    "firmware_version": "1.2.3",
    "manufacturer": "SmartHome Inc."
  }
}
```

### 5.5 心跳保活 (heartbeat)

```json
{
  "user_id": "12345",
  "device_id": "001",
  "timestamp": "2026-01-09T03:15:58Z",
  "message_type": "heartbeat",
  "data": {
    "battery_level": 85,
    "signal_strength": -65
  }
}
```

## 6. 后端MQTT服务实现

### 6.1 MQTT配置类 (MqttConfig)

```java
@Configuration
@EnableIntegration
public class MqttConfig {
    
    @Value("${mqtt.server.url}")
    private String url;
    
    @Value("${mqtt.username}")
    private String username;
    
    @Value("${mqtt.password}")
    private String password;
    
    @Bean
    public MqttClientFactory mqttClientFactory() {
        DefaultMqttClientFactory factory = new DefaultMqttClientFactory();
        MqttConnectionOptions options = new MqttConnectionOptions();
        options.setServerURIs(new String[]{url});
        options.setUserName(username);
        options.setPassword(password.toCharArray());
        options.setAutomaticReconnect(true);
        options.setKeepAliveInterval(60);
        factory.setConnectionOptions(options);
        return factory;
    }
    
    // 发布通道
    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }
    
    // 订阅通道
    @Bean
    public MessageChannel mqttInboundChannel() {
        return new DirectChannel();
    }
}
```

### 6.2 MQTT服务类 (MqttService)

```java
@Service
@Slf4j
public class MqttService {
    
    @Autowired
    private MessageChannel mqttOutboundChannel;
    
    @Autowired
    private UserDeviceService userDeviceService;
    
    /**
     * 发送设备控制命令
     */
    public boolean sendControlCommand(String userId, String deviceId, String command, Map<String, Object> parameters) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("user_id", userId);
            message.put("device_id", deviceId);
            message.put("timestamp", Instant.now().toString());
            message.put("message_type", "device_control_request");
            
            Map<String, Object> data = new HashMap<>();
            data.put("command", command);
            data.put("parameters", parameters);
            message.put("data", data);
            
            String topic = String.format("smart-home/%s/%s/control", userId, deviceId);
            
            Message<String> mqttMessage = MessageBuilder
                .withPayload(objectMapper.writeValueAsString(message))
                .setHeader(MqttHeaders.TOPIC, topic)
                .setHeader(MqttHeaders.QOS, 1)
                .build();
                
            return mqttOutboundChannel.send(mqttMessage, 5000);
        } catch (Exception e) {
            log.error("发送MQTT消息失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 处理设备状态上报
     */
    @ServiceActivator(inputChannel = "mqttInboundChannel")
    public void handleStatusReport(Message<byte[]> message) {
        try {
            String topic = (String) message.getHeaders().get(MqttHeaders.TOPIC);
            String payload = new String(message.getPayload());
            
            // 解析消息
            JsonNode jsonNode = objectMapper.readTree(payload);
            String messageType = jsonNode.get("message_type").asText();
            String userId = jsonNode.get("user_id").asText();
            String deviceId = jsonNode.get("device_id").asText();
            
            switch (messageType) {
                case "device_status_report":
                    handleDeviceStatus(jsonNode, userId, deviceId);
                    break;
                case "device_control_response":
                    handleControlResponse(jsonNode, userId, deviceId);
                    break;
                case "device_registration":
                    handleDeviceRegistration(jsonNode, userId, deviceId);
                    break;
                case "heartbeat":
                    handleHeartbeat(jsonNode, userId, deviceId);
                    break;
                default:
                    log.warn("未知消息类型: {}", messageType);
            }
        } catch (Exception e) {
            log.error("处理MQTT消息失败: {}", e.getMessage(), e);
        }
    }
    
    private void handleDeviceStatus(JsonNode jsonNode, String userId, String deviceId) {
        // 更新设备状态到数据库
        DeviceStatus status = objectMapper.treeToValue(
            jsonNode.get("data"), DeviceStatus.class);
        userDeviceService.updateDeviceStatus(userId, deviceId, status);
    }
    
    // 其他处理方法...
}
```

## 7. 错误处理机制

### 7.1 连接错误处理
- 自动重连机制
- 连接状态监控
- 异常日志记录

### 7.2 消息错误处理
- 消息格式验证
- 消息重复处理
- 消息丢失补偿机制

### 7.3 设备离线处理
- 心跳检测
- 设备离线通知
- 离线消息缓存

## 8. 安全机制

### 8.1 认证授权
- 设备认证：每个设备使用唯一的用户名/密码
- 用户隔离：不同用户的设备消息相互隔离
- 权限控制：设备只能发布到自己的主题

### 8.2 数据安全
- 传输加密：使用 TLS 加密传输
- 敏感数据：对敏感信息进行加密存储
- 访问控制：限制设备对主题的访问权限

## 9. 性能优化

### 9.1 消息压缩
- 对大数据量消息进行压缩
- 减少网络传输开销

### 9.2 批量处理
- 批量发送设备状态更新
- 减少网络请求次数

### 9.3 资源管理
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