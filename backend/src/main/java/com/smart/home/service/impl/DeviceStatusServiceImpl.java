package com.smart.home.service.impl;

import com.smart.home.model.entity.DeviceStatus;
import com.smart.home.service.DeviceStatusService;
import com.smart.home.mapper.DeviceStatusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 设备状态服务实现类
 *
 * @author lingma
 */
@Service
public class DeviceStatusServiceImpl implements DeviceStatusService {

    @Autowired
    private DeviceStatusMapper deviceStatusMapper;

    /**
     * 根据设备ID获取设备状态
     *
     * @param deviceId 设备ID
     * @return 设备状态实体
     */
    @Override
    public DeviceStatus getDeviceStatusByDeviceId(String deviceId) {
        // 查询最新的设备状态记录
        return deviceStatusMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<DeviceStatus>()
                .eq("device_id", deviceId)
                .orderByDesc("created_at")
                .last("LIMIT 1")
        );
    }

    /**
     * 更新设备状态
     *
     * @param deviceId  设备ID
     * @param statusData 状态数据
     * @return 更新后的设备状态实体
     */
    @Override
    @Transactional
    public DeviceStatus updateDeviceStatus(String deviceId, String statusData) {
        // 先尝试获取设备的最新状态
        DeviceStatus existingStatus = getDeviceStatusByDeviceId(deviceId);
        
        if (existingStatus != null) {
            // 如果存在，则更新
            existingStatus.setStatusData(statusData);
            existingStatus.setUpdatedAt(LocalDateTime.now());
            deviceStatusMapper.updateById(existingStatus);
            return existingStatus;
        } else {
            // 如果不存在，则创建新的状态记录
            return createDeviceStatus(deviceId, statusData);
        }
    }

    /**
     * 创建设备状态记录
     *
     * @param deviceId  设备ID
     * @param statusData 状态数据
     * @return 创建的设备状态实体
     */
    @Override
    @Transactional
    public DeviceStatus createDeviceStatus(String deviceId, String statusData) {
        DeviceStatus deviceStatus = new DeviceStatus();
        deviceStatus.setDeviceId(deviceId);
        deviceStatus.setStatusData(statusData);
        deviceStatus.setCreatedAt(LocalDateTime.now());
        deviceStatus.setUpdatedAt(LocalDateTime.now());
        
        deviceStatusMapper.insert(deviceStatus);
        return deviceStatus;
    }
}