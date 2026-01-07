package com.smart.home.controller;

import com.smart.home.common.Result;
import com.smart.home.model.entity.ConnectionStatus;
import com.smart.home.service.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 连接状态管理控制器
 *
 * @author lingma
 */
@RestController
@RequestMapping("/api/connection")
public class ConnectionController {

    @Autowired
    private ConnectionService connectionService;

    /**
     * 检查设备是否在线
     *
     * @param deviceId 设备ID
     * @return 检查结果
     */
    @GetMapping("/is-online/{deviceId}")
    public Result<Boolean> isDeviceOnline(@PathVariable String deviceId) {
        boolean isOnline = connectionService.isDeviceOnline(deviceId);
        return Result.success("设备在线状态查询成功", isOnline);
    }

    /**
     * 获取设备连接状态详情
     *
     * @param deviceId 设备ID
     * @return 连接状态详情
     */
    @GetMapping("/status/{deviceId}")
    public Result<ConnectionStatus> getDeviceConnectionStatus(@PathVariable String deviceId) {
        ConnectionStatus status = connectionService.getConnectionStatus(deviceId);
        if (status != null) {
            return Result.success("设备连接状态查询成功", status);
        } else {
            return Result.error("设备连接状态不存在");
        }
    }

    /**
     * 手动设置设备为在线状态
     *
     * @param userId   用户ID
     * @param deviceId 设备ID
     * @return 设置结果
     */
    @PostMapping("/set-online/{userId}/{deviceId}")
    public Result<String> setDeviceOnline(@PathVariable String userId, @PathVariable String deviceId) {
        try {
            connectionService.setDeviceOnline(userId, deviceId);
            return Result.success("设备已设置为在线状态", null);
        } catch (Exception e) {
            return Result.error("设置设备在线状态失败: " + e.getMessage());
        }
    }

    /**
     * 手动设置设备为离线状态
     *
     * @param deviceId 设备ID
     * @return 设置结果
     */
    @PostMapping("/set-offline/{deviceId}")
    public Result<String> setDeviceOffline(@PathVariable String deviceId) {
        try {
            connectionService.setDeviceOffline(deviceId);
            return Result.success("设备已设置为离线状态", null);
        } catch (Exception e) {
            return Result.error("设置设备离线状态失败: " + e.getMessage());
        }
    }
}