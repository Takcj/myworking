package com.smart.home.service;

import com.smart.home.model.entity.Device;
import com.smart.home.model.entity.DeviceWithStatus;

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
     * 获取用户有权限访问的设备列表
     *
     * @param userId 用户ID
     * @return 设备列表
     */
    List<Device> getUserAccessibleDevices(Long userId);

    /**
     * 获取用户设备及其在线状态
     *
     * @param userId 用户ID
     * @return 设备列表（包含在线状态）
     */
    List<Device> getDevicesWithStatusByUserId(Long userId);

    /**
     * 绑定设备到用户
     *
     * @param userId   用户ID
     * @param deviceId 设备ID
     * @param isOwner  是否为所有者
     * @return 绑定结果
     */
    Device bindDeviceToUser(Long userId, String deviceId, Boolean isOwner);

    /**
     * 获取用户设备及其详细状态
     *
     * @param userId 用户ID
     * @return 设备列表（包含详细状态）
     */
    List<DeviceWithStatus> getDevicesWithDetailedStatusByUserId(Long userId);

    /**
     * 设备分享给其他用户
     *
     * @param deviceId 设备ID
     * @param sourceUserId 分享者用户ID
     * @param targetUserId 目标用户ID
     * @return 分享结果
     */
    boolean shareDeviceToUser(String deviceId, Long sourceUserId, Long targetUserId);

    /**
     * 检查用户是否对设备有操作权限
     *
     * @param userId   用户ID
     * @param deviceId 设备ID
     * @return 是否有权限
     */
    boolean hasDevicePermission(Long userId, String deviceId);
}