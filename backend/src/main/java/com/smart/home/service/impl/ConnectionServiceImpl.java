package com.smart.home.service.impl;

import com.smart.home.model.entity.ConnectionStatus;
import com.smart.home.service.ConnectionService;
import com.smart.home.mapper.ConnectionStatusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 连接状态服务实现类
 *
 * @author lingma
 */
@Service
public class ConnectionServiceImpl implements ConnectionService {

    @Autowired
    private ConnectionStatusMapper connectionStatusMapper;

    /**
     * 记录设备连接状态
     *
     * @param userId           用户ID
     * @param deviceId         设备ID
     * @param connectionStatus 连接状态
     * @return 连接状态实体
     */
    @Override
    public ConnectionStatus recordConnectionStatus(String userId, String deviceId, String connectionStatus) {
        // 先尝试获取现有的连接状态记录
        ConnectionStatus existingStatus = connectionStatusMapper.selectByDeviceId(deviceId);
        
        ConnectionStatus status;
        if (existingStatus != null) {
            // 更新现有记录
            existingStatus.setUserId(userId);
            existingStatus.setConnectionStatus(connectionStatus);
            if ("connected".equals(connectionStatus)) {
                existingStatus.setConnectionTime(LocalDateTime.now());
            } else if ("disconnected".equals(connectionStatus)) {
                existingStatus.setDisconnectionTime(LocalDateTime.now());
            }
            existingStatus.setUpdatedAt(LocalDateTime.now());
            
            connectionStatusMapper.updateById(existingStatus);
            status = existingStatus;
        } else {
            // 创建新记录
            status = new ConnectionStatus();
            status.setUserId(userId);
            status.setDeviceId(deviceId);
            status.setConnectionStatus(connectionStatus);
            if ("connected".equals(connectionStatus)) {
                status.setConnectionTime(LocalDateTime.now());
            } else if ("disconnected".equals(connectionStatus)) {
                status.setDisconnectionTime(LocalDateTime.now());
            }
            status.setCreatedAt(LocalDateTime.now());
            status.setUpdatedAt(LocalDateTime.now());

            connectionStatusMapper.insert(status);
        }

        return status;
    }

    /**
     * 更新最后心跳时间
     *
     * @param deviceId 设备ID
     */
    @Override
    public void updateHeartbeat(String deviceId) {
        connectionStatusMapper.updateHeartbeat(deviceId);
    }

    /**
     * 获取设备连接状态
     *
     * @param deviceId 设备ID
     * @return 连接状态实体
     */
    @Override
    public ConnectionStatus getConnectionStatus(String deviceId) {
        return connectionStatusMapper.selectByDeviceId(deviceId);
    }

    /**
     * 设置设备为在线状态
     *
     * @param userId   用户ID
     * @param deviceId 设备ID
     */
    @Override
    public void setDeviceOnline(String userId, String deviceId) {
        // 先尝试获取现有的连接状态记录
        ConnectionStatus existingStatus = connectionStatusMapper.selectByDeviceId(deviceId);
        
        if (existingStatus != null) {
            // 更新现有记录
            connectionStatusMapper.setDeviceOnline(userId, deviceId);
        } else {
            // 创建新记录
            recordConnectionStatus(userId, deviceId, "connected");
        }
    }

    /**
     * 设置设备为离线状态
     *
     * @param deviceId 设备ID
     */
    @Override
    public void setDeviceOffline(String deviceId) {
        connectionStatusMapper.setDeviceOffline(deviceId);
    }

    /**
     * 检查设备是否在线
     *
     * @param deviceId 设备ID
     * @return 是否在线
     */
    @Override
    public boolean isDeviceOnline(String deviceId) {
        ConnectionStatus status = getConnectionStatus(deviceId);
        if (status == null) {
            return false;
        }
        
        // 判断连接状态是否为connected，且没有超过心跳超时时间
        // 假设心跳超时时间为5分钟
        if ("connected".equals(status.getConnectionStatus())) {
            if (status.getLastHeartbeat() != null) {
                // 检查最后心跳时间是否在5分钟内
                long currentTime = System.currentTimeMillis();
                long lastHeartbeatTime = status.getLastHeartbeat().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
                long timeout = 5 * 60 * 1000; // 5分钟超时
                return (currentTime - lastHeartbeatTime) < timeout;
            } else {
                // 如果没有心跳记录，只检查连接时间
                long connectionTime = status.getConnectionTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
                long currentTime = System.currentTimeMillis();
                long timeout = 5 * 60 * 1000; // 5分钟超时
                return (currentTime - connectionTime) < timeout;
            }
        }
        
        return false;
    }
}