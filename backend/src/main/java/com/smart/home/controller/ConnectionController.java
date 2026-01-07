package com.smart.home.controller;

import com.smart.home.common.Result;
import com.smart.home.model.entity.ConnectionStatus;
import com.smart.home.service.ConnectionService;
import com.smart.home.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 连接控制器
 * 管理设备连接状态
 *
 * @author lingma
 */
@RestController
@RequestMapping("/api/v1/connection")
public class ConnectionController {

    @Autowired
    private ConnectionService connectionService;

    @Autowired
    private DeviceService deviceService;

    /**
     * 检查设备是否在线
     *
     * @param deviceId 设备ID
     * @param userId 用户ID（从认证上下文获取）
     * @return 检查结果
     */
    @GetMapping("/is-online/{deviceId}")
    public Result<Boolean> isDeviceOnline(@PathVariable String deviceId, @RequestParam(required = false) Long userId) {
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }

        // 检查用户是否有访问此设备的权限
        if (!deviceService.hasDevicePermission(userId, deviceId)) {
            return Result.error("没有访问此设备的权限");
        }

        Boolean isOnline = connectionService.isDeviceOnline(deviceId);
        return Result.success("设备在线状态查询成功", isOnline);
    }

    /**
     * 获取设备连接状态详情
     *
     * @param deviceId 设备ID
     * @param userId 用户ID（从认证上下文获取）
     * @return 状态详情
     */
    @GetMapping("/status/{deviceId}")
    public Result<Object> getDeviceStatus(@PathVariable String deviceId, @RequestParam(required = false) Long userId) {
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }

        // 检查用户是否有访问此设备的权限
        if (!deviceService.hasDevicePermission(userId, deviceId)) {
            return Result.error("没有访问此设备的权限");
        }

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
     * @param userId 用户ID（从认证上下文获取）
     * @param deviceId 设备ID
     * @return 设置结果
     */
    @PostMapping("/set-online/{userId}/{deviceId}")
    public Result<String> setDeviceOnline(@PathVariable Long userId, @PathVariable String deviceId) {
        // 检查用户是否有管理此设备连接状态的权限
        if (!deviceService.hasDevicePermission(userId, deviceId)) {
            return Result.error("没有管理此设备连接状态的权限");
        }

        try {
            connectionService.setDeviceOnline(String.valueOf(userId), deviceId);
            return Result.success("设备状态已设置为在线", null);
        } catch (Exception e) {
            return Result.error("设置设备在线状态失败: " + e.getMessage());
        }
    }

    /**
     * 手动设置设备为离线状态
     *
     * @param deviceId 设备ID
     * @param userId 用户ID（从认证上下文获取）
     * @return 设置结果
     */
    @PostMapping("/set-offline/{deviceId}")
    public Result<String> setDeviceOffline(@PathVariable String deviceId, @RequestParam(required = false) Long userId) {
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }

        // 检查用户是否有管理此设备连接状态的权限
        if (!deviceService.hasDevicePermission(userId, deviceId)) {
            return Result.error("没有管理此设备连接状态的权限");
        }

        try {
            connectionService.setDeviceOffline(deviceId);
            return Result.success("设备状态已设置为离线", null);
        } catch (Exception e) {
            return Result.error("设置设备离线状态失败: " + e.getMessage());
        }
    }
}