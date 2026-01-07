package com.smart.home.service.impl;

import com.smart.home.model.entity.Device;
import com.smart.home.service.DeviceService;
import com.smart.home.mapper.DeviceMapper;
import com.smart.home.service.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    /**
     * 根据用户ID获取设备列表
     *
     * @param userId 用户ID
     * @return 设备列表
     */
    @Override
    public List<Device> getDevicesByUserId(Long userId) {
        List<Device> devices = deviceMapper.selectByUserId(userId);
        // 可以在这里为每个设备添加连接状态信息
        return devices;
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
    public void deleteDevice(Long id) {
        deviceMapper.deleteById(id);
    }

    /**
     * 根据用户ID和区域ID获取设备列表
     *
     * @param userId 用户ID
     * @param areaId 区域ID
     * @return 设备列表
     */
    @Override
    public List<Device> getDevicesByUserIdAndAreaId(Long userId, Long areaId) {
        return deviceMapper.selectByUserIdAndAreaId(userId, areaId);
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
                    return device;
                })
                .collect(Collectors.toList());
    }
}