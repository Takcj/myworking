package com.smart.home.service;

import com.smart.home.model.entity.ConnectionStatus;

/**
 * 连接状态服务接口
 *
 * @author lingma
 */
public interface ConnectionService {

    /**
     * 记录设备连接状态
     *
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @param connectionStatus 连接状态
     * @return 连接状态实体
     */
    ConnectionStatus recordConnectionStatus(String userId, String deviceId, String connectionStatus);

    /**
     * 更新最后心跳时间
     *
     * @param deviceId 设备ID
     */
    void updateHeartbeat(String deviceId);

    /**
     * 获取设备连接状态
     *
     * @param deviceId 设备ID
     * @return 连接状态实体
     */
    ConnectionStatus getConnectionStatus(String deviceId);

    /**
     * 设置设备为在线状态
     *
     * @param userId 用户ID
     * @param deviceId 设备ID
     */
    void setDeviceOnline(String userId, String deviceId);

    /**
     * 设置设备为离线状态
     *
     * @param deviceId 设备ID
     */
    void setDeviceOffline(String deviceId);

    /**
     * 检查设备是否在线
     *
     * @param deviceId 设备ID
     * @return 是否在线
     */
    boolean isDeviceOnline(String deviceId);
}