package com.smart.home.service;

import com.smart.home.model.entity.DeviceStatus;

/**
 * 设备状态服务接口
 *
 * @author lingma
 */
public interface DeviceStatusService {

    /**
     * 根据设备ID获取设备状态
     *
     * @param deviceId 设备ID
     * @return 设备状态实体
     */
    DeviceStatus getDeviceStatusByDeviceId(String deviceId);

    /**
     * 更新设备状态
     *
     * @param deviceId  设备ID
     * @param statusData 状态数据
     * @return 更新后的设备状态实体
     */
    DeviceStatus updateDeviceStatus(String deviceId, String statusData);

    /**
     * 创建设备状态记录
     *
     * @param deviceId  设备ID
     * @param statusData 状态数据
     * @return 创建的设备状态实体
     */
    DeviceStatus createDeviceStatus(String deviceId, String statusData);
}