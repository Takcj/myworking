package com.smart.home.service.impl;

import com.smart.home.model.entity.Device;
import com.smart.home.model.entity.DeviceWithStatus;
import com.smart.home.service.DeviceService;
import com.smart.home.mapper.DeviceMapper;
import com.smart.home.service.ConnectionService;
import com.smart.home.service.DeviceStatusService;
import com.smart.home.model.entity.DeviceStatus;
import com.smart.home.service.UserDeviceOwnershipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 设备服务实现类
 *
 * @author lingma
 */
@Service
public class DeviceServiceImpl implements DeviceService {

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private ConnectionService connectionService;

    @Autowired
    private DeviceStatusService deviceStatusService;

    @Autowired
    private UserDeviceOwnershipService userDeviceOwnershipService;

    /**
     * 根据用户ID获取设备列表
     *
     * @param userId 用户ID
     * @return 设备列表
     */
    @Override
    public List<Device> getDevicesByUserId(Long userId) {
        // 获取用户有权限访问的设备ID列表
        List<String> deviceIds = userDeviceOwnershipService.getDeviceIdsByUserId(userId);
        return deviceMapper.selectByDeviceIds(deviceIds);
    }

    /**
     * 根据ID获取设备
     *
     * @param id 设备ID
     * @return 设备实体
     */
    @Override
    public Device getDeviceById(Long id) {
        return deviceMapper.selectById(id);
    }

    /**
     * 根据设备ID获取设备
     *
     * @param deviceId 设备ID字符串
     * @return 设备实体
     */
    @Override
    public Device getDeviceByDeviceId(String deviceId) {
        return deviceMapper.selectByDeviceId(deviceId);
    }

    /**
     * 添加设备
     *
     * @param device 设备实体
     * @return 设备实体
     */
    @Override
    @Transactional
    public Device addDevice(Device device) {
        device.setCreatedAt(LocalDateTime.now());
        device.setUpdatedAt(LocalDateTime.now());
        deviceMapper.insert(device);
        return device;
    }

    /**
     * 更新设备
     *
     * @param device 设备实体
     * @return 设备实体
     */
    @Override
    @Transactional
    public Device updateDevice(Device device) {
        device.setUpdatedAt(LocalDateTime.now());
        deviceMapper.updateById(device);
        return device;
    }

    /**
     * 删除设备
     *
     * @param id 设备ID
     */
    @Override
    @Transactional
    public void deleteDevice(Long id) {
        Device device = deviceMapper.selectById(id);
        if (device != null) {
            deviceMapper.deleteById(id);
        }
    }

    /**
     * 获取用户有权限访问的设备列表
     *
     * @param userId 用户ID
     * @return 设备列表
     */
    @Override
    public List<Device> getUserAccessibleDevices(Long userId) {
        List<String> deviceIds = userDeviceOwnershipService.getDeviceIdsByUserId(userId);
        return deviceMapper.selectByDeviceIds(deviceIds);
    }

    /**
     * 获取用户设备及其在线状态
     *
     * @param userId 用户ID
     * @return 设备列表（包含在线状态）
     */
    @Override
    public List<Device> getDevicesWithStatusByUserId(Long userId) {
        List<Device> devices = getDevicesByUserId(userId);
        return devices.stream()
                .map(device -> {
                    // 为设备添加在线状态
                    Boolean isOnline = connectionService.isDeviceOnline(device.getDeviceId());
                    device.setOnlineStatus(isOnline);
                    
                    // 为设备添加是否为所有者标识
                    Boolean isOwner = userDeviceOwnershipService.isOwner(userId, device.getDeviceId());
                    device.setPermissionLevel(isOwner ? "owner" : "shared");
                    
                    // 为设备添加详细状态信息
                    DeviceStatus deviceStatus = deviceStatusService.getDeviceStatusByDeviceId(device.getDeviceId());
                    if (deviceStatus != null) {
                        // 可以将详细状态信息附加到设备对象中
                        // 由于Device实体不直接包含状态数据，这里只是设置在线状态
                    }
                    return device;
                })
                .collect(Collectors.toList());
    }

    /**
     * 获取用户设备及其详细状态
     *
     * @param userId 用户ID
     * @return 设备列表（包含详细状态）
     */
    @Override
    public List<DeviceWithStatus> getDevicesWithDetailedStatusByUserId(Long userId) {
        List<Device> devices = getDevicesByUserId(userId);
        return devices.stream()
                .map(device -> {
                    DeviceWithStatus deviceWithStatus = new DeviceWithStatus();
                    deviceWithStatus.setId(device.getId());
                    deviceWithStatus.setDeviceId(device.getDeviceId());
                    deviceWithStatus.setDeviceType(device.getDeviceType());
                    deviceWithStatus.setDeviceName(device.getDeviceName());
                    deviceWithStatus.setCreatedAt(device.getCreatedAt());
                    deviceWithStatus.setUpdatedAt(device.getUpdatedAt());
                    
                    // 设置在线状态
                    Boolean isOnline = connectionService.isDeviceOnline(device.getDeviceId());
                    deviceWithStatus.setOnlineStatus(isOnline);
                    
                    // 设置权限等级
                    Boolean isOwner = userDeviceOwnershipService.isOwner(userId, device.getDeviceId());
                    deviceWithStatus.setPermissionLevel(isOwner ? "owner" : "shared");
                    
                    // 获取并设置详细状态数据
                    DeviceStatus deviceStatus = deviceStatusService.getDeviceStatusByDeviceId(device.getDeviceId());
                    if (deviceStatus != null) {
                        deviceWithStatus.setStatusData(deviceStatus.getStatusData());
                    }
                    
                    return deviceWithStatus;
                })
                .collect(Collectors.toList());
    }

    /**
     * 绑定设备到用户
     *
     * @param userId   用户ID
     * @param deviceId 设备ID
     * @param permissionLevel 权限等级
     * @return 绑定结果
     */
    @Override
    @Transactional
    public Device bindDeviceToUser(Long userId, String deviceId, Boolean isOwner) {
        // 检查设备是否已存在
        Device device = getDeviceByDeviceId(deviceId);
        if (device == null) {
            throw new RuntimeException("设备不存在");
        }

        // 检查用户是否已经有对设备的归属关系
        if (userDeviceOwnershipService.checkOwnership(userId, deviceId) != null) {
            // 如果用户已经有权限，直接返回设备信息
            Boolean isOwnerCheck = userDeviceOwnershipService.isOwner(userId, device.getDeviceId());
            device.setPermissionLevel(isOwnerCheck ? "owner" : "shared");
            return device;
        }

        // 为用户添加设备归属关系
        userDeviceOwnershipService.addOwnership(userId, deviceId, isOwner);
        
        // 返回设备信息
        device.setPermissionLevel(isOwner ? "owner" : "shared");
        return device;
    }

    /**
     * 设备分享给其他用户
     *
     * @param deviceId 设备ID
     * @param sourceUserId 分享者用户ID
     * @param targetUserId 目标用户ID
     * @param permissionLevel 权限等级
     * @return 分享结果
     */
    @Override
    @Transactional
    public boolean shareDeviceToUser(String deviceId, Long sourceUserId, Long targetUserId) {
        // 验证分享者权限，只有所有者才能分享设备
        if (!userDeviceOwnershipService.isOwner(sourceUserId, deviceId)) {
            throw new RuntimeException("只有设备所有者才能分享设备");
        }

        // 检查目标用户是否已经对设备有归属关系
        if (userDeviceOwnershipService.checkOwnership(targetUserId, deviceId) != null) {
            throw new RuntimeException("目标用户已经对设备有归属关系");
        }

        // 为目标用户添加设备归属关系（非所有者）
        userDeviceOwnershipService.addOwnership(targetUserId, deviceId, false);
        return true;
    }

    /**
     * 检查用户是否对设备有操作权限
     *
     * @param userId   用户ID
     * @param deviceId 设备ID
     * @param requiredLevel 需要的权限等级
     * @return 是否有权限
     */
    @Override
    public boolean hasDevicePermission(Long userId, String deviceId) {
        return userDeviceOwnershipService.checkOwnership(userId, deviceId) != null;
    }
}