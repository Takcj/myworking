package com.smart.home.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smart.home.common.Constants;
import com.smart.home.model.dto.DeviceDataDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MQTT消息解析器
 *
 * @author lingma
 */
public class MqttMessageParser {
    
    private static final Logger logger = LoggerFactory.getLogger(MqttMessageParser.class);
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 解析设备数据消息
     *
     * @param message 消息内容
     * @return DeviceDataDTO
     */
    public static DeviceDataDTO parseDeviceData(String message) {
        try {
            JsonNode rootNode = objectMapper.readTree(message);
            
            DeviceDataDTO dto = new DeviceDataDTO();
            dto.setUserId(rootNode.get("user_id").asText());
            dto.setTimestamp(rootNode.get("timestamp").asLong());
            dto.setMessageType(rootNode.get("message_type").asText());
            
            // 解析数据体
            if (rootNode.has("data") && rootNode.get("data").isObject()) {
                JsonNode dataNode = rootNode.get("data");
                DeviceDataDTO.DataBody dataBody = new DeviceDataDTO.DataBody();
                
                // 根据消息类型解析数据体
                String messageType = rootNode.get("message_type").asText();
                switch (messageType) {
                    case Constants.MessageType.DEVICE_DATA:
                        parseDeviceDataBody(dataNode, dataBody);
                        break;
                    case Constants.MessageType.CONTROL_COMMAND:
                        parseControlCommandDataBody(dataNode, dataBody);
                        break;
                    case Constants.MessageType.CONNECTION:
                        parseConnectionDataBody(dataNode, dataBody);
                        break;
                    case Constants.MessageType.HEARTBEAT:
                        parseHeartbeatDataBody(dataNode, dataBody);
                        break;
                    default:
                        logger.warn("未知的消息类型: {}", messageType);
                        break;
                }
                
                dto.setData(dataBody);
            }
            
            return dto;
        } catch (JsonProcessingException e) {
            logger.error("解析MQTT消息失败: {}", message, e);
            return null;
        } catch (Exception e) {
            logger.error("解析MQTT消息时发生异常: {}", message, e);
            return null;
        }
    }
    
    /**
     * 解析设备数据体
     */
    private static void parseDeviceDataBody(JsonNode dataNode, DeviceDataDTO.DataBody dataBody) {
        if (dataNode.has("area")) {
            dataBody.setArea(dataNode.get("area").asText());
        }
        
        if (dataNode.has("device_type")) {
            dataBody.setDeviceType(dataNode.get("device_type").asText());
        }
        
        if (dataNode.has("device_id")) {
            dataBody.setDeviceId(dataNode.get("device_id").asText());
        }
        
        if (dataNode.has("status")) {
            // 将status节点转换为Map
            try {
                dataBody.setStatus(objectMapper.convertValue(dataNode.get("status"), java.util.Map.class));
            } catch (Exception e) {
                logger.error("解析设备状态失败", e);
            }
        }
        
        if (dataNode.has("timestamp")) {
            dataBody.setTimestamp(dataNode.get("timestamp").asLong());
        }
    }
    
    /**
     * 解析控制命令数据体
     */
    private static void parseControlCommandDataBody(JsonNode dataNode, DeviceDataDTO.DataBody dataBody) {
        if (dataNode.has("area")) {
            dataBody.setArea(dataNode.get("area").asText());
        }
        
        if (dataNode.has("device_type")) {
            dataBody.setDeviceType(dataNode.get("device_type").asText());
        }
        
        if (dataNode.has("device_id")) {
            dataBody.setDeviceId(dataNode.get("device_id").asText());
        }
        
        if (dataNode.has("command")) {
            JsonNode commandNode = dataNode.get("command");
            DeviceDataDTO.Command command = new DeviceDataDTO.Command();
            
            if (commandNode.has("type")) {
                command.setType(commandNode.get("type").asText());
            }
            
            if (commandNode.has("parameters")) {
                try {
                    command.setParameters(objectMapper.convertValue(commandNode.get("parameters"), java.util.Map.class));
                } catch (Exception e) {
                    logger.error("解析命令参数失败", e);
                }
            }
            
            dataBody.setCommand(command);
        }
    }
    
    /**
     * 解析连接数据体
     */
    private static void parseConnectionDataBody(JsonNode dataNode, DeviceDataDTO.DataBody dataBody) {
        // 连接数据的特殊处理
        if (dataNode.has("device_id")) {
            dataBody.setDeviceId(dataNode.get("device_id").asText());
        }
        
        // 其他连接相关信息可以按需解析
    }
    
    /**
     * 解析心跳数据体
     */
    private static void parseHeartbeatDataBody(JsonNode dataNode, DeviceDataDTO.DataBody dataBody) {
        if (dataNode.has("device_id")) {
            dataBody.setDeviceId(dataNode.get("device_id").asText());
        }
        
        // 其他心跳相关信息可以按需解析
    }
    
    /**
     * 验证消息格式
     */
    public static boolean validateMessageFormat(String message) {
        try {
            JsonNode rootNode = objectMapper.readTree(message);
            
            // 验证必需字段
            return rootNode.has("user_id") && 
                   rootNode.has("timestamp") && 
                   rootNode.has("message_type") && 
                   rootNode.has("data");
        } catch (Exception e) {
            logger.error("验证消息格式失败: {}", message, e);
            return false;
        }
    }
}