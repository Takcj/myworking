package com.smart.home.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smart.home.model.entity.UserDeviceOwnership;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户设备归属Mapper接口
 *
 * @author lingma
 */
@Mapper
public interface UserDeviceOwnershipMapper extends BaseMapper<UserDeviceOwnership> {

    /**
     * 根据用户ID获取用户有权限访问的设备ID列表
     *
     * @param userId 用户ID
     * @return 设备ID列表
     */
    List<String> selectDeviceIdsByUserId(@Param("userId") Long userId);

    /**
     * 根据设备ID获取有权限访问的用户ID列表
     *
     * @param deviceId 设备ID
     * @return 用户ID列表
     */
    List<Long> selectUserIdsByDeviceId(@Param("deviceId") String deviceId);

    /**
     * 检查用户是否对设备有访问权限
     *
     * @param userId   用户ID
     * @param deviceId 设备ID
     * @return 归属记录
     */
    UserDeviceOwnership selectByUserIdAndDeviceId(@Param("userId") Long userId, @Param("deviceId") String deviceId);

    /**
     * 获取设备所有者ID
     *
     * @param deviceId 设备ID
     * @return 所有者用户ID
     */
    Long selectOwnerIdByDeviceId(@Param("deviceId") String deviceId);

    /**
     * 获取用户的设备归属关系列表
     *
     * @param userId 用户ID
     * @return 归属关系列表
     */
    List<UserDeviceOwnership> selectByUserId(@Param("userId") Long userId);
}