package com.smart.home.service;

import com.smart.home.model.dto.DeviceDataDTO;

/**
 * MQTT服务接口
 * 定义与下位机设备通信的接口方法
 *
 * @author lingma
 */
public interface MqttService {

    /**
     * 发送控制命令到设备
     *
     * @param deviceDataDTO 设备数据DTO
     */
    void sendControlCommand(DeviceDataDTO deviceDataDTO);

    /**
     * 发送批量控制命令
     *
     * @param userId  用户ID
     * @param message 批量命令消息
     */
    void sendBatchControlCommand(String userId, String message);

    /**
     * 发送心跳响应
     *
     * @param userId   用户ID
     * @param deviceId 设备ID
     */
    void sendHeartbeatResponse(String userId, String deviceId);
}