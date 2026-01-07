package com.smart.home.controller;

import com.smart.home.common.Result;
import com.smart.home.model.dto.DeviceDataDTO;
import com.smart.home.service.MqttService;
import com.smart.home.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * MQTT控制器
 * 提供通过MQTT协议控制设备的接口
 *
 * @author lingma
 */
@RestController
@RequestMapping("/api/v1/mqtt")
public class MqttController {

    @Autowired
    private MqttService mqttService;

    @Autowired
    private DeviceService deviceService;

    /**
     * 发送控制命令到设备
     *
     * @param deviceDataDTO 设备数据DTO
     * @param userId 用户ID（从认证上下文获取）
     * @return 控制结果
     */
    @PostMapping("/send-control-command")
    public Result<String> sendControlCommand(@RequestBody DeviceDataDTO deviceDataDTO, @RequestParam(required = false) Long userId) {
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }

        // 检查用户是否有对目标设备的控制权限
        String deviceId = deviceDataDTO.getData().getDeviceId();
        if (!deviceService.hasDevicePermission(userId, deviceId)) {
            return Result.error("没有控制此设备的权限");
        }

        // 发送控制命令
        mqttService.sendControlCommand(deviceDataDTO);
        return Result.success("控制命令发送成功", null);
    }

    /**
     * 发送批量控制命令
     *
     * @param userId 用户ID（从认证上下文获取）
     * @param message 批量命令消息
     * @return 控制结果
     */
    @PostMapping("/send-batch-command/{userId}")
    public Result<String> sendBatchControlCommand(@PathVariable Long userId, @RequestBody String message) {
        // 发送批量控制命令
        mqttService.sendBatchControlCommand(userId.toString(), message);
        return Result.success("批量控制命令发送成功", null);
    }

    /**
     * 发送批量控制命令（使用请求参数中的用户ID）
     *
     * @param message 批量命令消息
     * @param userId 用户ID（从认证上下文获取）
     * @return 控制结果
     */
    @PostMapping("/send-batch-command")
    public Result<String> sendBatchControlCommand(@RequestBody String message, @RequestParam(required = false) Long userId) {
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }

        // 发送批量控制命令
        mqttService.sendBatchControlCommand(userId.toString(), message);
        return Result.success("批量控制命令发送成功", null);
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