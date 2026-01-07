package com.smart.home.service.impl;

import com.smart.home.model.entity.UserDeviceOwnership;
import com.smart.home.service.UserDeviceOwnershipService;
import com.smart.home.mapper.UserDeviceOwnershipMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户设备归属服务实现类
 *
 * @author lingma
 */
@Service
public class UserDeviceOwnershipServiceImpl implements UserDeviceOwnershipService {

    @Autowired
    private UserDeviceOwnershipMapper userDeviceOwnershipMapper;

    /**
     * 根据用户ID获取用户有权限访问的设备ID列表
     *
     * @param userId 用户ID
     * @return 设备ID列表
     */
    @Override
    public List<String> getDeviceIdsByUserId(Long userId) {
        return userDeviceOwnershipMapper.selectDeviceIdsByUserId(userId);
    }

    /**
     * 根据设备ID获取有权限访问的用户ID列表
     *
     * @param deviceId 设备ID
     * @return 用户ID列表
     */
    @Override
    public List<Long> getUserIdsByDeviceId(String deviceId) {
        return userDeviceOwnershipMapper.selectUserIdsByDeviceId(deviceId);
    }

    /**
     * 检查用户是否对设备有访问权限
     *
     * @param userId   用户ID
     * @param deviceId 设备ID
     * @return 归属记录
     */
    @Override
    public UserDeviceOwnership checkOwnership(Long userId, String deviceId) {
        return userDeviceOwnershipMapper.selectByUserIdAndDeviceId(userId, deviceId);
    }

    /**
     * 添加用户设备归属关系
     *
     * @param userId   用户ID
     * @param deviceId 设备ID
     * @param isOwner  是否为所有者
     * @return 归属记录
     */
    @Override
    @Transactional
    public UserDeviceOwnership addOwnership(Long userId, String deviceId, Boolean isOwner) {
        // 检查是否已存在归属关系
        UserDeviceOwnership existingOwnership = checkOwnership(userId, deviceId);
        if (existingOwnership != null) {
            throw new RuntimeException("用户对设备的归属关系已存在");
        }

        UserDeviceOwnership ownership = new UserDeviceOwnership();
        ownership.setUserId(userId);
        ownership.setDeviceId(deviceId);
        ownership.setIsOwner(isOwner);
        ownership.setCreatedAt(LocalDateTime.now());
        ownership.setUpdatedAt(LocalDateTime.now());

        userDeviceOwnershipMapper.insert(ownership);
        return ownership;
    }

    /**
     * 设置用户为设备所有者
     *
     * @param userId   用户ID
     * @param deviceId 设备ID
     * @return 归属记录
     */
    @Override
    @Transactional
    public UserDeviceOwnership setOwner(Long userId, String deviceId) {
        // 查找当前设备所有者，取消其所有者身份
        Long currentOwnerId = getOwnerIdByDeviceId(deviceId);
        if (currentOwnerId != null) {
            UserDeviceOwnership currentOwner = checkOwnership(currentOwnerId, deviceId);
            if (currentOwner != null) {
                currentOwner.setIsOwner(false);
                currentOwner.setUpdatedAt(LocalDateTime.now());
                userDeviceOwnershipMapper.updateById(currentOwner);
            }
        }

        // 检查目标用户是否已有归属关系
        UserDeviceOwnership ownership = checkOwnership(userId, deviceId);
        if (ownership == null) {
            // 如果没有归属关系，则创建
            ownership = new UserDeviceOwnership();
            ownership.setUserId(userId);
            ownership.setDeviceId(deviceId);
            ownership.setIsOwner(true);
            ownership.setCreatedAt(LocalDateTime.now());
            ownership.setUpdatedAt(LocalDateTime.now());
            userDeviceOwnershipMapper.insert(ownership);
        } else {
            // 如果已有归属关系，则更新为所有者
            ownership.setIsOwner(true);
            ownership.setUpdatedAt(LocalDateTime.now());
            userDeviceOwnershipMapper.updateById(ownership);
        }

        return ownership;
    }

    /**
     * 移除用户设备归属关系
     *
     * @param userId   用户ID
     * @param deviceId 设备ID
     */
    @Override
    @Transactional
    public void removeOwnership(Long userId, String deviceId) {
        UserDeviceOwnership ownership = checkOwnership(userId, deviceId);
        if (ownership != null) {
            userDeviceOwnershipMapper.deleteById(ownership.getId());
        }
    }

    /**
     * 获取设备所有者ID
     *
     * @param deviceId 设备ID
     * @return 所有者用户ID
     */
    @Override
    public Long getOwnerIdByDeviceId(String deviceId) {
        return userDeviceOwnershipMapper.selectOwnerIdByDeviceId(deviceId);
    }

    /**
     * 检查用户是否为设备所有者
     *
     * @param userId   用户ID
     * @param deviceId 设备ID
     * @return 是否为所有者
     */
    @Override
    public boolean isOwner(Long userId, String deviceId) {
        UserDeviceOwnership ownership = checkOwnership(userId, deviceId);
        return ownership != null && ownership.getIsOwner();
    }

    /**
     * 获取用户的设备归属关系列表
     *
     * @param userId 用户ID
     * @return 归属关系列表
     */
    @Override
    public List<UserDeviceOwnership> getUserOwnershipList(Long userId) {
        return userDeviceOwnershipMapper.selectByUserId(userId);
    }
}