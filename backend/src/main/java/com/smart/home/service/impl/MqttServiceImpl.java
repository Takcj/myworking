package com.smart.home.service.impl;

import com.smart.home.common.Constants;
import com.smart.home.model.dto.DeviceDataDTO;
import com.smart.home.service.AutomationService;
import com.smart.home.service.ConnectionService;
import com.smart.home.service.DeviceService;
import com.smart.home.service.MqttService;
import com.smart.home.utils.MqttMessageParser;
import com.smart.home.utils.ValidationUtils;
import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Map;

/**
 * MQTT服务实现类
 *
 * @author lingma
 */
@Service
public class MqttServiceImpl implements MqttService {

    private static final Logger logger = LoggerFactory.getLogger(MqttServiceImpl.class);

    @Value("${mqtt.broker}")
    private String brokerUrl;

    @Value("${mqtt.username}")
    private String username;

    @Value("${mqtt.password}")
    private String password;

    @Value("${mqtt.client-id}")
    private String clientId;

    @Value("${mqtt.default-qos:1}")
    private int defaultQos;

    private MqttClient mqttClient;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private ConnectionService connectionService;

    @Autowired
    private AutomationService automationService;

    /**
     * 连接MQTT服务器
     */
    @PostConstruct
    public void connect() {
        try {
            // 创建MQTT客户端
            mqttClient = new MqttClient(brokerUrl, clientId);
            
            // 设置连接选项
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(username);
            options.setPassword(password.toCharArray());
            options.setConnectionTimeout(30);
            options.setKeepAliveInterval(60);
            options.setCleanSession(true);
            options.setAutomaticReconnect(true);

            // 设置回调处理器
            mqttClient.setCallback(new MqttCallbackExtended() {
                @Override
                public void connectComplete(boolean reconnect, String serverURI) {
                    logger.info("MQTT连接完成，reconnect: {}, serverURI: {}", reconnect, serverURI);
                    
                    // 连接成功后订阅用户数据主题
                    subscribe(String.format(Constants.MQTT.USER_DATA_TOPIC, "+"), defaultQos);
                }

                @Override
                public void connectionLost(Throwable cause) {
                    logger.error("MQTT连接丢失: ", cause);
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    logger.debug("收到MQTT消息，主题: {}，内容: {}", topic, message.toString());
                    
                    // 解析用户ID
                    String[] topicParts = topic.split("/");
                    if (topicParts.length >= 3 && "device".equals(topicParts[2])) {
                        String userId = topicParts[1];
                        
                        // 处理设备数据
                        handleDeviceData(userId, message.toString());
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    logger.debug("MQTT消息发送完成");
                }
            });

            // 连接服务器
            mqttClient.connect(options);
            logger.info("MQTT连接成功，服务器: {}", brokerUrl);
        } catch (MqttException e) {
            logger.error("MQTT连接失败: ", e);
        }
    }

    /**
     * 断开MQTT连接
     */
    @PreDestroy
    public void disconnect() {
        try {
            if (mqttClient != null && mqttClient.isConnected()) {
                mqttClient.disconnect();
                logger.info("MQTT连接已断开");
            }
        } catch (MqttException e) {
            logger.error("断开MQTT连接时出错: ", e);
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
    @Override
    public void handleDeviceData(String userId, String message) {
        try {
            // 验证消息格式
            if (!MqttMessageParser.validateMessageFormat(message)) {
                logger.error("消息格式无效: {}", message);
                return;
            }
            
            // 解析消息
            DeviceDataDTO deviceDataDTO = MqttMessageParser.parseDeviceData(message);
            if (deviceDataDTO == null) {
                logger.error("消息解析失败: {}", message);
                return;
            }
            
            // 根据消息类型进行相应处理
            String messageType = deviceDataDTO.getMessageType();
            switch (messageType) {
                case Constants.MessageType.DEVICE_DATA:
                    handleDeviceDataMessage(deviceDataDTO);
                    break;
                case Constants.MessageType.CONTROL_COMMAND:
                    handleControlCommandMessage(deviceDataDTO);
                    break;
                case Constants.MessageType.CONNECTION:
                    handleConnectionMessage(deviceDataDTO);
                    break;
                case Constants.MessageType.HEARTBEAT:
                    handleHeartbeatMessage(deviceDataDTO);
                    break;
                default:
                    logger.warn("未知的消息类型: {}", messageType);
                    break;
            }
        } catch (Exception e) {
            logger.error("处理设备数据时出错，用户ID: {}，消息: {}", userId, message, e);
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
            
            // 更新设备状态
            // TODO: 实现更新设备状态的逻辑
            
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
            
            logger.info("处理连接消息，设备ID: {}", deviceId);
            
            // 记录连接状态
            connectionService.setDeviceOnline(deviceDataDTO.getUserId(), deviceId);
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
            
            logger.info("处理心跳消息，设备ID: {}", deviceId);
            
            // 更新心跳时间
            connectionService.updateHeartbeat(deviceId);
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
            String deviceId = deviceDataDTO.getData().getDeviceId();
            
            // 构建控制命令主题
            String topic = String.format(Constants.MQTT.USER_CONTROL_TOPIC, userId);
            
            // 构建控制命令消息
            String message = buildControlCommandMessage(deviceDataDTO);
            
            // 发布消息
            publish(topic, message, defaultQos);
            
            logger.info("发送控制命令到设备，用户ID: {}，设备ID: {}，命令: {}", 
                      userId, deviceId, deviceDataDTO.getData().getCommand());
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
            // 构建控制命令主题
            String topic = String.format(Constants.MQTT.USER_CONTROL_TOPIC, userId);
            
            // 发布消息
            publish(topic, message, defaultQos);
            
            logger.info("发送批量控制命令，用户ID: {}，命令: {}", userId, message);
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
        // 在实际实现中，可以发送一个心跳响应消息给设备
        logger.debug("发送心跳响应，用户ID: {}，设备ID: {}", userId, deviceId);
    }

    /**
     * 构建控制命令消息
     *
     * @param deviceDataDTO 设备数据DTO
     * @return 控制命令消息
     */
    private String buildControlCommandMessage(DeviceDataDTO deviceDataDTO) {
        // 实际实现中需要将DeviceDataDTO转换为JSON格式的控制命令
        // 这里暂时返回一个简单的JSON字符串
        return "{ \"command\": \"control_command\", \"data\": " + deviceDataDTO.toString() + " }";
    }
}