package com.smart.home.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.smart.home.common.Constants;
import com.smart.home.model.dto.DeviceDataDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * MQTT消息解析器
 * 解析来自下位机设备的MQTT消息
 *
 * @author lingma
 */
public class MqttMessageParser {
    
    private static final Logger logger = LoggerFactory.getLogger(MqttMessageParser.class);
    
    /**
     * 解析设备数据消息
     *
     * @param message 消息内容
     * @return DeviceDataDTO
     */
    public static DeviceDataDTO parseDeviceData(String message) {
        try {
            JSONObject json = JSON.parseObject(message);
            
            // 验证消息结构是否符合四层结构：用户ID → 时间戳 → 消息类型 → 数据体
            if (!isValidMessageStructure(json)) {
                throw new IllegalArgumentException("消息格式不符合规范");
            }
            
            DeviceDataDTO dto = new DeviceDataDTO();
            dto.setUserId(json.getString("user_id"));
            dto.setTimestamp(json.getLong("timestamp"));
            dto.setMessageType(json.getString("message_type"));
            
            // 解析数据体
            JSONObject dataObj = json.getJSONObject("data");
            if (dataObj != null) {
                DeviceDataDTO.DataBody dataBody = new DeviceDataDTO.DataBody();
                dataBody.setArea(dataObj.getString("area"));
                dataBody.setDeviceType(dataObj.getString("device_type"));
                dataBody.setDeviceId(dataObj.getString("device_id"));
                
                // 解析状态数据，将其作为一个JSON对象存储
                if (dataObj.containsKey("status")) {
                    dataBody.setStatus(dataObj.getJSONObject("status"));
                }
                
                // 解析命令数据
                if (dataObj.containsKey("command")) {
                    dataBody.setCommand(dataObj.getObject("command", DeviceDataDTO.Command.class));
                }
                
                // 其他通用字段
                if (dataObj.containsKey("timestamp")) {
                    dataBody.setTimestamp(dataObj.getLong("timestamp"));
                }
                
                dto.setData(dataBody);
            }
            
            return dto;
        } catch (Exception e) {
            logger.error("解析设备数据消息失败: {}", message, e);
            throw new RuntimeException("解析设备数据消息失败: " + e.getMessage(), e);
        }
    }
    
    
    /**
     * 验证消息结构是否符合规范
     *
     * @param json JSON对象
     * @return 是否符合规范
     */
    private static boolean isValidMessageStructure(JSONObject json) {
        // 检查必需字段
        return json.containsKey("user_id") &&
               json.containsKey("timestamp") &&
               json.containsKey("message_type") &&
               json.containsKey("data");
    }

    /**
     * 构造控制命令消息
     *
     * @param userId     用户ID
     * @param area       区域
     * @param deviceType 设备类型
     * @param deviceId   设备ID
     * @param command    命令
     * @return 消息字符串
     */
    public static String buildControlCommand(String userId, String area, String deviceType, String deviceId, Map<String, Object> command) {
        Map<String, Object> message = new HashMap<>();
        message.put("user_id", userId);
        message.put("timestamp", System.currentTimeMillis());
        message.put("message_type", Constants.MESSAGE_TYPE_CONTROL_COMMAND);
        
        Map<String, Object> data = new HashMap<>();
        data.put("area", area);
        data.put("device_type", deviceType);
        data.put("device_id", deviceId);
        data.put("command", command);
        
        message.put("data", data);
        
        return JSON.toJSONString(message);
    }
}