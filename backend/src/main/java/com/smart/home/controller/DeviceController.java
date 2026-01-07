package com.smart.home.controller;

import com.smart.home.common.Result;
import com.smart.home.model.entity.Device;
import com.smart.home.model.entity.DeviceWithStatus;
import com.smart.home.model.dto.DeviceControlDTO;
import com.smart.home.service.DeviceService;
import com.smart.home.service.UserDeviceOwnershipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 设备控制器
 *
 * @author lingma
 */
@RestController
@RequestMapping("/api/v1/devices")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private UserDeviceOwnershipService userDeviceOwnershipService;

    /**
     * 获取用户有权限访问的设备列表
     *
     * @param userId 用户ID（从认证上下文获取）
     * @return 设备列表
     */
    @GetMapping
    public Result<List<Device>> getUserDevices(@RequestParam(required = false) Long userId) {
        if (userId == null) {
            // 在实际应用中，应该从JWT Token中获取用户ID
            // 这里暂时使用参数传入，后续会通过认证拦截器获取
            return Result.error("用户ID不能为空");
        }
        
        List<Device> devices = deviceService.getDevicesByUserId(userId);
        return Result.success("获取设备列表成功", devices);
    }

    /**
     * 获取用户有权限访问的设备列表及其在线状态
     *
     * @param userId 用户ID（从认证上下文获取）
     * @return 设备列表（包含在线状态）
     */
    @GetMapping("/with-status")
    public Result<List<Device>> getUserDevicesWithStatus(@RequestParam(required = false) Long userId) {
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }
        
        List<Device> devices = deviceService.getDevicesWithStatusByUserId(userId);
        return Result.success("获取设备及在线状态成功", devices);
    }

    /**
     * 获取用户有权限访问的设备列表及其详细状态
     *
     * @param userId 用户ID（从认证上下文获取）
     * @return 设备列表（包含详细状态）
     */
    @GetMapping("/with-detailed-status")
    public Result<List<DeviceWithStatus>> getUserDevicesWithDetailedStatus(@RequestParam(required = false) Long userId) {
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }
        
        List<DeviceWithStatus> devices = deviceService.getDevicesWithDetailedStatusByUserId(userId);
        return Result.success("获取设备及详细状态成功", devices);
    }

    /**
     * 获取特定设备信息
     *
     * @param id 设备ID
     * @param userId 用户ID（从认证上下文获取）
     * @return 设备信息
     */
    @GetMapping("/{id}")
    public Result<Device> getDevice(@PathVariable Long id, @RequestParam(required = false) Long userId) {
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }
        
        Device device = deviceService.getDeviceById(id);
        if (device != null) {
            // 检查用户是否有访问权限
            if (!deviceService.hasDevicePermission(userId, device.getDeviceId())) {
                return Result.error("没有访问此设备的权限");
            }
            
            // 设置用户的权限等级
            Boolean isOwner = userDeviceOwnershipService.isOwner(userId, device.getDeviceId());
            device.setPermissionLevel(isOwner ? "owner" : "shared");
            
            return Result.success("获取设备信息成功", device);
        }
        return Result.error("设备不存在");
    }

    /**
     * 添加设备
     *
     * @param device 设备信息
     * @param userId 用户ID（从认证上下文获取）
     * @return 添加结果
     */
    @PostMapping
    public Result<Device> addDevice(@RequestBody Device device, @RequestParam(required = false) Long userId) {
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }
        
        Device newDevice = deviceService.addDevice(device);
        
        // 为创建者添加所有者权限
        userDeviceOwnershipService.addOwnership(userId, newDevice.getDeviceId(), true);
        newDevice.setPermissionLevel("owner");
        
        return Result.success("设备添加成功", newDevice);
    }

    /**
     * 绑定设备到用户
     *
     * @param userId     用户ID（从认证上下文获取）
     * @param deviceId   设备ID
     * @param permissionLevel 权限等级
     * @return 绑定结果
     */
    @PostMapping("/bind")
    public Result<Device> bindDevice(
            @RequestParam(required = false) Long userId,
            @RequestParam String deviceId,
            @RequestParam(defaultValue = "false") Boolean isOwner) {
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }
        
        try {
            Device device = deviceService.bindDeviceToUser(userId, deviceId, isOwner);
            return Result.success("设备绑定成功", device);
        } catch (Exception e) {
            return Result.error("设备绑定失败: " + e.getMessage());
        }
    }

    /**
     * 设备分享给其他用户
     *
     * @param deviceId 设备ID
     * @param sourceUserId 分享者用户ID
     * @param targetUserId 目标用户ID
     * @return 分享结果
     */
    @PostMapping("/{deviceId}/share")
    public Result<String> shareDevice(
            @PathVariable String deviceId,
            @RequestParam Long sourceUserId,
            @RequestParam Long targetUserId) {
        try {
            boolean success = deviceService.shareDeviceToUser(deviceId, sourceUserId, targetUserId);
            if (success) {
                return Result.success("设备分享成功", null);
            } else {
                return Result.error("设备分享失败");
            }
        } catch (Exception e) {
            return Result.error("设备分享失败: " + e.getMessage());
        }
    }

    /**
     * 更新设备
     *
     * @param id 设备ID
     * @param device 设备信息
     * @param userId 用户ID（从认证上下文获取）
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public Result<Device> updateDevice(@PathVariable Long id, @RequestBody Device device, @RequestParam(required = false) Long userId) {
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }
        
        Device existingDevice = deviceService.getDeviceById(id);
        if (existingDevice == null) {
            return Result.error("设备不存在");
        }
        
        // 检查用户是否有更新权限（需要有访问权限）
        if (!deviceService.hasDevicePermission(userId, existingDevice.getDeviceId())) {
            return Result.error("没有更新此设备的权限");
        }
        
        device.setId(id);
        Device updatedDevice = deviceService.updateDevice(device);
        
        // 设置用户的权限等级
        Boolean isOwner = userDeviceOwnershipService.isOwner(userId, updatedDevice.getDeviceId());
        updatedDevice.setPermissionLevel(isOwner ? "owner" : "shared");
        
        return Result.success("设备更新成功", updatedDevice);
    }

    /**
     * 删除设备
     *
     * @param id 设备ID
     * @param userId 用户ID（从认证上下文获取）
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteDevice(@PathVariable Long id, @RequestParam(required = false) Long userId) {
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }
        
        Device device = deviceService.getDeviceById(id);
        if (device == null) {
            return Result.error("设备不存在");
        }
        
        // 检查用户是否有删除权限（需要是所有者）
        if (!userDeviceOwnershipService.isOwner(userId, device.getDeviceId())) {
            return Result.error("没有删除此设备的权限");
        }
        
        deviceService.deleteDevice(id);
        return Result.success("设备删除成功", null);
    }

    /**
     * 发送控制命令到设备
     *
     * @param id 设备ID
     * @param userId 用户ID（从认证上下文获取）
     * @param controlDTO 控制命令DTO
     * @return 控制结果
     */
    @PostMapping("/{id}/control")
    public Result<String> controlDevice(@PathVariable Long id, @RequestParam(required = false) Long userId, @RequestBody DeviceControlDTO controlDTO) {
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }
        
        Device device = deviceService.getDeviceById(id);
        if (device == null) {
            return Result.error("设备不存在");
        }
        
        // 检查用户是否有控制权限（需要有访问权限）
        if (!deviceService.hasDevicePermission(userId, device.getDeviceId())) {
            return Result.error("没有控制此设备的权限");
        }
        
        // 实现设备控制逻辑
        // 在实际实现中，这里会调用MQTT服务发送控制命令
        return Result.success("控制命令已发送", null);
    }
}