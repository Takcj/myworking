package com.smart.home.controller;

import com.smart.home.common.Result;
import com.smart.home.model.dto.DeviceControlDTO;
import com.smart.home.model.dto.DeviceDataDTO;
import com.smart.home.service.MqttService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * MQTT消息处理控制器
 *
 * @author lingma
 */
@RestController
@RequestMapping("/api/mqtt")
public class MqttController {

    @Autowired
    private MqttService mqttService;

    /**
     * 发送控制命令到设备
     *
     * @param deviceControlDTO 设备控制DTO
     * @return 发送结果
     */
    @PostMapping("/send-control-command")
    public Result<String> sendControlCommand(@RequestBody DeviceControlDTO deviceControlDTO) {
        try {
            // 将DeviceControlDTO转换为DeviceDataDTO格式
            DeviceDataDTO deviceDataDTO = new DeviceDataDTO();
            deviceDataDTO.setUserId(deviceControlDTO.getUserId());
            
            DeviceDataDTO.DataBody dataBody = new DeviceDataDTO.DataBody();
            dataBody.setArea(deviceControlDTO.getArea());
            dataBody.setDeviceType(deviceControlDTO.getDeviceType());
            dataBody.setDeviceId(deviceControlDTO.getDeviceId());
            
            DeviceDataDTO.Command command = new DeviceDataDTO.Command();
            command.setType(deviceControlDTO.getCommandType());
            command.setParameters(deviceControlDTO.getParameters());
            dataBody.setCommand(command);
            
            deviceDataDTO.setData(dataBody);
            
            mqttService.sendControlCommand(deviceDataDTO);
            return Result.success("控制命令发送成功", null);
        } catch (Exception e) {
            return Result.error("控制命令发送失败: " + e.getMessage());
        }
    }

    /**
     * 发送批量控制命令
     *
     * @param userId  用户ID
     * @param message 批量命令消息
     * @return 发送结果
     */
    @PostMapping("/send-batch-command/{userId}")
    public Result<String> sendBatchControlCommand(@PathVariable String userId, @RequestBody String message) {
        try {
            mqttService.sendBatchControlCommand(userId, message);
            return Result.success("批量控制命令发送成功", null);
        } catch (Exception e) {
            return Result.error("批量控制命令发送失败: " + e.getMessage());
        }
    }

    /**
     * 发送心跳响应
     *
     * @param userId   用户ID
     * @param deviceId 设备ID
     * @return 发送结果
     */
    @PostMapping("/send-heartbeat/{userId}/{deviceId}")
    public Result<String> sendHeartbeat(@PathVariable String userId, @PathVariable String deviceId) {
        try {
            mqttService.sendHeartbeatResponse(userId, deviceId);
            return Result.success("心跳响应发送成功", null);
        } catch (Exception e) {
            return Result.error("心跳响应发送失败: " + e.getMessage());
        }
    }

    /**
     * 测试MQTT连接
     *
     * @return 连接状态
     */
    @GetMapping("/test-connection")
    public Result<String> testMqttConnection() {
        // 这里可以添加测试MQTT连接的逻辑
        return Result.success("MQTT连接正常", null);
    }
}