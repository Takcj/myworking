package com.smart.home.service;

import com.smart.home.model.entity.UserDeviceOwnership;

import java.util.List;

/**
 * 用户设备归属服务接口
 *
 * @author lingma
 */
public interface UserDeviceOwnershipService {

    /**
     * 根据用户ID获取用户有权限访问的设备ID列表
     *
     * @param userId 用户ID
     * @return 设备ID列表
     */
    List<String> getDeviceIdsByUserId(Long userId);

    /**
     * 根据设备ID获取有权限访问的用户ID列表
     *
     * @param deviceId 设备ID
     * @return 用户ID列表
     */
    List<Long> getUserIdsByDeviceId(String deviceId);

    /**
     * 检查用户是否对设备有访问权限
     *
     * @param userId   用户ID
     * @param deviceId 设备ID
     * @return 归属记录
     */
    UserDeviceOwnership checkOwnership(Long userId, String deviceId);

    /**
     * 添加用户设备归属关系
     *
     * @param userId   用户ID
     * @param deviceId 设备ID
     * @param isOwner  是否为所有者
     * @return 归属记录
     */
    UserDeviceOwnership addOwnership(Long userId, String deviceId, Boolean isOwner);

    /**
     * 设置用户为设备所有者
     *
     * @param userId   用户ID
     * @param deviceId 设备ID
     * @return 归属记录
     */
    UserDeviceOwnership setOwner(Long userId, String deviceId);

    /**
     * 移除用户设备归属关系
     *
     * @param userId   用户ID
     * @param deviceId 设备ID
     */
    void removeOwnership(Long userId, String deviceId);

    /**
     * 获取设备所有者ID
     *
     * @param deviceId 设备ID
     * @return 所有者用户ID
     */
    Long getOwnerIdByDeviceId(String deviceId);

    /**
     * 检查用户是否为设备所有者
     *
     * @param userId   用户ID
     * @param deviceId 设备ID
     * @return 是否为所有者
     */
    boolean isOwner(Long userId, String deviceId);

    /**
     * 获取用户的设备归属关系列表
     *
     * @param userId 用户ID
     * @return 归属关系列表
     */
    List<UserDeviceOwnership> getUserOwnershipList(Long userId);
}