package com.smart.home.service;

import com.smart.home.model.dto.DeviceDataDTO;

/**
 * MQTT服务接口
 *
 * @author lingma
 */
public interface MqttService {

    /**
     * 连接MQTT服务器
     */
    void connect();

    /**
     * 断开MQTT连接
     */
    void disconnect();

    /**
     * 发布消息
     *
     * @param topic 主题
     * @param message 消息内容
     * @param qos QoS等级
     */
    void publish(String topic, String message, int qos);

    /**
     * 订阅主题
     *
     * @param topic 主题
     * @param qos QoS等级
     */
    void subscribe(String topic, int qos);

    /**
     * 处理接收到的设备数据
     *
     * @param userId 用户ID
     * @param message 消息内容
     */
    void handleDeviceData(String userId, String message);

    /**
     * 发送控制命令到设备
     *
     * @param deviceDataDTO 设备数据DTO
     */
    void sendControlCommand(DeviceDataDTO deviceDataDTO);

    /**
     * 发送批量控制命令到设备
     *
     * @param userId 用户ID
     * @param message 批量命令消息
     */
    void sendBatchControlCommand(String userId, String message);

    /**
     * 发送心跳响应
     *
     * @param userId 用户ID
     * @param deviceId 设备ID
     */
    void sendHeartbeatResponse(String userId, String deviceId);
}