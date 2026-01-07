package com.smart.home.service;

import com.smart.home.model.entity.Device;
import java.util.List;

/**
 * 设备服务接口
 *
 * @author lingma
 */
public interface DeviceService {

    /**
     * 根据用户ID获取设备列表
     *
     * @param userId 用户ID
     * @return 设备列表
     */
    List<Device> getDevicesByUserId(Long userId);

    /**
     * 根据ID获取设备
     *
     * @param id 设备ID
     * @return 设备实体
     */
    Device getDeviceById(Long id);

    /**
     * 根据设备ID获取设备
     *
     * @param deviceId 设备ID字符串
     * @return 设备实体
     */
    Device getDeviceByDeviceId(String deviceId);

    /**
     * 添加设备
     *
     * @param device 设备实体
     * @return 设备实体
     */
    Device addDevice(Device device);

    /**
     * 更新设备
     *
     * @param device 设备实体
     * @return 设备实体
     */
    Device updateDevice(Device device);

    /**
     * 删除设备
     *
     * @param id 设备ID
     */
    void deleteDevice(Long id);

    /**
     * 根据用户ID和区域ID获取设备列表
     *
     * @param userId 用户ID
     * @param areaId 区域ID
     * @return 设备列表
     */
    List<Device> getDevicesByUserIdAndAreaId(Long userId, Long areaId);
}