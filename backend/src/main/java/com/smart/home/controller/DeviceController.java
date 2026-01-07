package com.smart.home.controller;

import com.smart.home.common.Result;
import com.smart.home.model.entity.Device;
import com.smart.home.model.dto.DeviceControlDTO;
import com.smart.home.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 设备控制器
 *
 * @author lingma
 */
@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    /**
     * 获取用户所有设备
     *
     * @param userId 用户ID
     * @return 设备列表
     */
    @GetMapping
    public Result<List<Device>> getUserDevices(@RequestParam Long userId) {
        List<Device> devices = deviceService.getDevicesByUserId(userId);
        return Result.success(devices);
    }

    /**
     * 获取特定设备信息
     *
     * @param id 设备ID
     * @return 设备信息
     */
    @GetMapping("/{id}")
    public Result<Device> getDevice(@PathVariable Long id) {
        Device device = deviceService.getDeviceById(id);
        if (device != null) {
            return Result.success(device);
        }
        return Result.error("设备不存在");
    }

    /**
     * 添加设备
     *
     * @param device 设备信息
     * @return 添加结果
     */
    @PostMapping
    public Result<Device> addDevice(@RequestBody Device device) {
        Device newDevice = deviceService.addDevice(device);
        return Result.success("设备添加成功", newDevice);
    }

    /**
     * 更新设备
     *
     * @param id 设备ID
     * @param device 设备信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public Result<Device> updateDevice(@PathVariable Long id, @RequestBody Device device) {
        device.setId(id);
        Device updatedDevice = deviceService.updateDevice(device);
        return Result.success("设备更新成功", updatedDevice);
    }

    /**
     * 删除设备
     *
     * @param id 设备ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteDevice(@PathVariable Long id) {
        deviceService.deleteDevice(id);
        return Result.success("设备删除成功", null);
    }

    /**
     * 发送控制命令到设备
     *
     * @param id 设备ID
     * @param controlDTO 控制命令DTO
     * @return 控制结果
     */
    @PostMapping("/{id}/control")
    public Result<String> controlDevice(@PathVariable String id, @RequestBody DeviceControlDTO controlDTO) {
        // 实现设备控制逻辑
        return Result.success("控制命令已发送", null);
    }
}