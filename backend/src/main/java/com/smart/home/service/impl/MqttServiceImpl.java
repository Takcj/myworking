package com.smart.home.service.impl;

import com.smart.home.common.Constants;
import com.smart.home.config.DeviceStatusWebSocket;
import com.smart.home.model.dto.DeviceDataDTO;
import com.smart.home.model.entity.AutomationRule;
import java.util.List;
import com.smart.home.service.AutomationService;
import com.smart.home.service.ConnectionService;
import com.smart.home.service.DeviceStatusService;
import com.smart.home.service.MqttService;
import com.smart.home.service.UserDeviceOwnershipService;
import com.smart.home.utils.MqttMessageParser;
import com.alibaba.fastjson.JSON;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * MQTT服务实现类
 *
 * @author lingma
 */
@Service
public class MqttServiceImpl implements MqttService {

    private static final Logger logger = LoggerFactory.getLogger(MqttServiceImpl.class);

    @Value("${mqtt.broker-url}")
    private String brokerUrl;

    @Value("${mqtt.username}")
    private String username;

    @Value("${mqtt.password}")
    private String password;

    @Value("${mqtt.client-id}")
    private String clientId;

    private MqttClient mqttClient;

    @Autowired
    private ConnectionService connectionService;

    @Autowired
    private AutomationService automationService;

    @Autowired
    private DeviceStatusService deviceStatusService;

    @Autowired
    private UserDeviceOwnershipService userDeviceOwnershipService;

    /**
     * 执行定时触发的自动化规则
     */
    @Scheduled(cron = "0 */1 * * * ?") // 每分钟执行一次
    public void executeScheduledAutomationRules() {
        try {
            // 获取所有定时触发类型的启用规则
            List<AutomationRule> scheduledRules = automationService.getAllScheduledRules();
            
            for (AutomationRule rule : scheduledRules) {
                // 解析定时条件
                String triggerCondition = rule.getTriggerCondition();
                if (isScheduledConditionMet(triggerCondition)) {
                    // 执行规则命令
                    executeScheduledRuleCommand(rule);
                }
            }
        } catch (Exception e) {
            logger.error("执行定时自动化规则失败", e);
        }
    }
    
    /**
     * 检查定时条件是否满足
     * 
     * @param triggerCondition 触发条件
     * @return 是否满足
     */
    private boolean isScheduledConditionMet(String triggerCondition) {
        try {
            // 解析JSON格式的触发条件
            com.alibaba.fastjson.JSONObject condition = com.alibaba.fastjson.JSONObject.parseObject(triggerCondition);
            String type = condition.getString("type");
            
            if ("time_range".equals(type)) {
                // 时间范围类型条件
                String startTime = condition.getString("start_time"); // HH:mm格式
                String endTime = condition.getString("end_time");   // HH:mm格式
                
                return isTimeInRange(startTime, endTime);
            } else if ("cron".equals(type)) {
                // Cron表达式类型条件
                String cronExpression = condition.getString("cron");
                return isCronMatch(cronExpression);
            }
            
            return false;
        } catch (Exception e) {
            logger.error("解析定时条件失败: {}", triggerCondition, e);
            return false;
        }
    }
    
    /**
     * 检查当前时间是否在指定时间范围内
     * 
     * @param startTime 开始时间(HH:mm)
     * @param endTime 结束时间(HH:mm)
     * @return 是否在范围内
     */
    private boolean isTimeInRange(String startTime, String endTime) {
        try {
            java.time.LocalTime now = java.time.LocalTime.now();
            java.time.LocalTime start = java.time.LocalTime.parse(startTime);
            java.time.LocalTime end = java.time.LocalTime.parse(endTime);
            
            if (start.isBefore(end)) {
                // 正常的时间段，如 08:00-20:00
                return !now.isBefore(start) && !now.isAfter(end);
            } else {
                // 跨天的时间段，如 22:00-06:00
                return !now.isBefore(start) || !now.isAfter(end);
            }
        } catch (Exception e) {
            logger.error("时间范围比较失败", e);
            return false;
        }
    }
    
    /**
     * 检查当前时间是否匹配Cron表达式
     * 
     * @param cronExpression Cron表达式
     * @return 是否匹配
     */
    private boolean isCronMatch(String cronExpression) {
        try {
            // 使用Spring的CronSequenceGenerator
            org.springframework.scheduling.support.CronSequenceGenerator generator = 
                new org.springframework.scheduling.support.CronSequenceGenerator(cronExpression);
            
            java.util.Date now = new java.util.Date();
            java.util.Date next = generator.next(now);
            
            // 如果下一次执行时间就是现在或过去，则认为匹配
            return next.getTime() <= now.getTime() + 60000; // 允许1分钟误差
        } catch (Exception e) {
            logger.error("Cron表达式匹配失败: {}", cronExpression, e);
            return false;
        }
    }
    
    /**
     * 执行定时规则命令
     * 
     * @param rule 规则
     */
    private void executeScheduledRuleCommand(AutomationRule rule) {
        try {
            // 从数据库获取用户ID
            String userId = rule.getUserId().toString();
            
            // 构建控制命令
            DeviceDataDTO deviceDataDTO = new DeviceDataDTO();
            deviceDataDTO.setUserId(userId);
            
            DeviceDataDTO.Data data = new DeviceDataDTO.Data();
            data.setDeviceId(rule.getTargetDeviceId());
            data.setDeviceType(rule.getTargetDeviceType());
            
            DeviceDataDTO.Command command = new DeviceDataDTO.Command();
            command.setType(rule.getCommandType());
            
            // 解析命令参数
            com.alibaba.fastjson.JSONObject commandParams = com.alibaba.fastjson.JSONObject.parseObject(rule.getCommandParameters());
            command.setParameters(commandParams);
            
            data.setCommand(command);
            deviceDataDTO.setData(data);
            
            // 发送控制命令
            sendControlCommand(deviceDataDTO);
        } catch (Exception e) {
            logger.error("执行定时规则命令失败", e);
        }
    }

    /**
     * 初始化MQTT服务
     */
    @PostConstruct
    public void init() throws MqttException {
        // 创建MQTT客户端
        mqttClient = new MqttClient(brokerUrl, clientId, new MemoryPersistence());
        
        // 设置连接选项
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(username);
        options.setPassword(password.toCharArray());
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(30);
        options.setKeepAliveInterval(60);

        // 连接服务器
        mqttClient.connect(options);
        logger.info("MQTT连接成功，服务器: {}", brokerUrl);
        
        // 订阅设备数据上报主题
        mqttClient.subscribe(Constants.MQTT.USER_DATA_TOPIC.replace("+", "#"), (topic, message) -> {
            CompletableFuture.runAsync(() -> {
                try {
                    // 解析用户ID
                    String[] topicParts = topic.split("/");
                    if (topicParts.length >= 3 && "device".equals(topicParts[2])) {
                        String userId = topicParts[1];
                        DeviceDataDTO deviceDataDTO = MqttMessageParser.parseDeviceData(message.toString());
                        if (deviceDataDTO != null) {
                            deviceDataDTO.setUserId(userId);
                            handleDeviceData(deviceDataDTO);
                        }
                    }
                } catch (Exception e) {
                    logger.error("处理设备数据消息时出错", e);
                }
            });
        });

        // 订阅控制命令响应主题
        mqttClient.subscribe(Constants.MQTT.USER_CONTROL_RESPONSE_TOPIC.replace("+", "#"), (topic, message) -> {
            CompletableFuture.runAsync(() -> {
                try {
                    String[] topicParts = topic.split("/");
                    if (topicParts.length >= 3) {
                        String userId = topicParts[1];
                        logger.debug("收到控制命令响应，用户ID: {}, 内容: {}", userId, message.toString());
                    }
                } catch (Exception e) {
                    logger.error("处理控制命令响应时出错", e);
                }
            });
        });
    }

    /**
     * 销毁MQTT服务
     */
    @PreDestroy
    public void destroy() throws MqttException {
        if (mqttClient != null && mqttClient.isConnected()) {
            mqttClient.disconnect();
            mqttClient.close();
            logger.info("MQTT连接已断开");
        }
    }

    /**
     * 发布消息
     *
     * @param topic   主题
     * @param message 消息内容
     * @param qos     QoS等级
     */
    @Override
    public void publish(String topic, String message, int qos) {
        try {
            if (mqttClient != null && mqttClient.isConnected()) {
                MqttMessage mqttMessage = new MqttMessage(message.getBytes());
                mqttMessage.setQos(qos);
                mqttMessage.setRetained(false);
                
                mqttClient.publish(topic, mqttMessage);
                logger.debug("发布MQTT消息，主题: {}，内容: {}", topic, message);
            } else {
                logger.error("MQTT客户端未连接，无法发布消息，主题: {}", topic);
            }
        } catch (MqttException e) {
            logger.error("发布MQTT消息失败，主题: {}", topic, e);
        }
    }

    /**
     * 订阅主题
     *
     * @param topic 主题
     * @param qos   QoS等级
     */
    @Override
    public void subscribe(String topic, int qos) {
        try {
            if (mqttClient != null && mqttClient.isConnected()) {
                mqttClient.subscribe(topic, qos);
                logger.debug("订阅MQTT主题: {}", topic);
            } else {
                logger.error("MQTT客户端未连接，无法订阅主题: {}", topic);
            }
        } catch (MqttException e) {
            logger.error("订阅MQTT主题失败: {}", topic, e);
        }
    }

    /**
     * 处理接收到的设备数据
     *
     * @param userId  用户ID
     * @param message 消息内容
     */
    /**
     * 处理设备数据上报
     *
     * @param deviceDataDTO 设备数据DTO
     */
    private void handleDeviceData(DeviceDataDTO deviceDataDTO) {
        try {
            String userId = deviceDataDTO.getUserId();
            String deviceId = deviceDataDTO.getData().getDeviceId();

            // 验证用户是否对设备有访问权限
            if (userDeviceOwnershipService.checkOwnership(Long.parseLong(userId), deviceId) == null) {
                logger.warn("用户 {} 对设备 {} 没有访问权限", userId, deviceId);
                return;
            }

            // 根据消息类型进行相应处理
            String messageType = deviceDataDTO.getMessageType();
            switch (messageType) {
                case Constants.MessageType.DEVICE_DATA:
                    handleDeviceDataMessage(deviceDataDTO);
                    break;
                case Constants.MessageType.CONNECTION:
                    handleConnectionMessage(deviceDataDTO);
                    break;
                case Constants.MessageType.HEARTBEAT:
                    handleHeartbeatMessage(deviceDataDTO);
                    break;
                case Constants.MessageType.CONTROL_COMMAND:
                    // 控制命令由设备发出响应，通常不需要在这里处理
                    logger.debug("收到控制命令响应，设备ID: {}", deviceId);
                    break;
                default:
                    logger.warn("未知的消息类型: {}", messageType);
                    break;
            }
        } catch (Exception e) {
            logger.error("处理设备数据时出错", e);
        }
    }

    /**
     * 处理设备数据消息
     */
    private void handleDeviceDataMessage(DeviceDataDTO deviceDataDTO) {
        try {
            String deviceId = deviceDataDTO.getData().getDeviceId();
            String deviceType = deviceDataDTO.getData().getDeviceType();
            Object status = deviceDataDTO.getData().getStatus();
            
            logger.info("处理设备数据消息，设备ID: {}，设备类型: {}，状态: {}", deviceId, deviceType, status);
            
            // 检查设备是否存在
            // TODO: 实现设备验证逻辑
            
            // 更新设备状态
            String statusData = JSON.toJSONString(status);
            deviceStatusService.updateDeviceStatus(deviceId, statusData);

            // 推送设备状态更新到WebSocket客户端
            DeviceStatusWebSocket.sendDeviceStatusUpdate(deviceId, deviceType, status);
            
            // 触发自动化规则
            automationService.checkAndTriggerRules(
                deviceDataDTO.getUserId(), 
                deviceId, 
                deviceType, 
                status
            );
        } catch (Exception e) {
            logger.error("处理设备数据消息时出错", e);
        }
    }

    /**
     * 处理控制命令消息
     */
    private void handleControlCommandMessage(DeviceDataDTO deviceDataDTO) {
        try {
            String deviceId = deviceDataDTO.getData().getDeviceId();
            DeviceDataDTO.Command command = deviceDataDTO.getData().getCommand();
            
            logger.info("处理控制命令消息，设备ID: {}，命令: {}", deviceId, command);
            
            // 处理控制命令
            // TODO: 实现控制命令处理逻辑
        } catch (Exception e) {
            logger.error("处理控制命令消息时出错", e);
        }
    }

    /**
     * 处理连接消息
     */
    private void handleConnectionMessage(DeviceDataDTO deviceDataDTO) {
        try {
            String deviceId = deviceDataDTO.getData().getDeviceId();
            String userId = deviceDataDTO.getUserId();
            
            logger.info("处理连接消息，用户ID: {}，设备ID: {}", userId, deviceId);
            
            // 验证用户是否对设备有访问权限
            if (userDeviceOwnershipService.checkOwnership(Long.parseLong(userId), deviceId) == null) {
                logger.warn("用户 {} 对设备 {} 没有连接上报权限", userId, deviceId);
                return;
            }

            // 记录连接状态为在线
            connectionService.setDeviceOnline(userId, deviceId);
            
            logger.info("设备 {} 已设置为在线状态", deviceId);
        } catch (Exception e) {
            logger.error("处理连接消息时出错", e);
        }
    }

    /**
     * 处理心跳消息
     */
    private void handleHeartbeatMessage(DeviceDataDTO deviceDataDTO) {
        try {
            String deviceId = deviceDataDTO.getData().getDeviceId();
            String userId = deviceDataDTO.getUserId();
            
            logger.info("处理心跳消息，用户ID: {}，设备ID: {}", userId, deviceId);
            
            // 验证用户是否对设备有访问权限
            if (userDeviceOwnershipService.checkOwnership(Long.parseLong(userId), deviceId) == null) {
                logger.warn("用户 {} 对设备 {} 没有心跳上报权限", userId, deviceId);
                return;
            }

            // 更新心跳时间
            connectionService.updateHeartbeat(deviceId);
            
            // 确保设备状态为在线
            connectionService.setDeviceOnline(userId, deviceId);
            
            logger.debug("设备 {} 心跳时间已更新", deviceId);
        } catch (Exception e) {
            logger.error("处理心跳消息时出错", e);
        }
    }

    /**
     * 发送控制命令到设备
     *
     * @param deviceDataDTO 设备数据DTO
     */
    @Override
    public void sendControlCommand(DeviceDataDTO deviceDataDTO) {
        try {
            String userId = deviceDataDTO.getUserId();
            String area = deviceDataDTO.getData().getArea();
            String deviceType = deviceDataDTO.getData().getDeviceType();
            String deviceId = deviceDataDTO.getData().getDeviceId();
            DeviceDataDTO.Command command = deviceDataDTO.getData().getCommand();

            // 检查设备是否在线
            if (!connectionService.isDeviceOnline(deviceId)) {
                logger.warn("设备 {} 不在线，无法发送控制命令", deviceId);
                return;
            }

            // 验证用户是否对设备有控制权限
            if (userDeviceOwnershipService.checkOwnership(Long.parseLong(userId), deviceId) == null) {
                logger.warn("用户 {} 对设备 {} 没有控制权限", userId, deviceId);
                return;
            }

            // 构造控制命令消息
            Map<String, Object> commandMap = Map.of(
                "type", command.getType(),
                "parameters", command.getParameters()
            );
            
            String message = MqttMessageParser.buildControlCommand(userId, area, deviceType, deviceId, commandMap);

            // 发送控制命令
            String topic = String.format(Constants.MQTT.USER_CONTROL_TOPIC, userId);
            MqttMessage mqttMessage = new MqttMessage(message.getBytes());
            mqttMessage.setQos(1);
            mqttClient.publish(topic, mqttMessage);

            logger.info("控制命令发送成功: 用户ID={}, 设备ID={}, 命令={}", userId, deviceId, command);
        } catch (Exception e) {
            logger.error("发送控制命令失败", e);
        }
    }

    /**
     * 发送批量控制命令到设备
     *
     * @param userId  用户ID
     * @param message 批量命令消息
     */
    @Override
    public void sendBatchControlCommand(String userId, String message) {
        try {
            // 发送批量控制命令
            String topic = String.format(Constants.MQTT.USER_BATCH_CONTROL_TOPIC, userId);
            MqttMessage mqttMessage = new MqttMessage(message.getBytes());
            mqttMessage.setQos(1);
            mqttClient.publish(topic, mqttMessage);

            logger.info("批量控制命令发送成功: 用户ID={}, 命令={}", userId, message);
        } catch (Exception e) {
            logger.error("发送批量控制命令失败，用户ID: {}", userId, e);
        }
    }

    /**
     * 发送心跳响应
     *
     * @param userId   用户ID
     * @param deviceId 设备ID
     */
    @Override
    public void sendHeartbeatResponse(String userId, String deviceId) {
        try {
            // 构造心跳响应消息
            String message = String.format("{\"user_id\":\"%s\",\"device_id\":\"%s\",\"timestamp\":%d,\"message_type\":\"heartbeat_response\"}", 
                userId, deviceId, System.currentTimeMillis());

            // 发送心跳响应
            String topic = String.format(Constants.MQTT.USER_HEARTBEAT_RESPONSE_TOPIC, userId, deviceId);
            MqttMessage mqttMessage = new MqttMessage(message.getBytes());
            mqttMessage.setQos(0);
            mqttClient.publish(topic, mqttMessage);

            logger.debug("心跳响应发送成功: 用户ID={}, 设备ID={}", userId, deviceId);
        } catch (Exception e) {
            logger.error("发送心跳响应失败", e);
        }
    }
}