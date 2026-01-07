package com.smart.home.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smart.home.model.entity.Device;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 设备Mapper接口
 *
 * @author lingma
 */
@Mapper
public interface DeviceMapper extends BaseMapper<Device> {
    /**
     * 根据用户ID查询设备列表
     *
     * @param userId 用户ID
     * @return 设备列表
     */
    List<Device> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据设备ID查询设备
     *
     * @param deviceId 设备ID
     * @return 设备实体
     */
    Device selectByDeviceId(@Param("deviceId") String deviceId);

    /**
     * 根据用户ID和区域ID查询设备列表
     *
     * @param params 包含userId和areaId的参数
     * @return 设备列表
     */
    List<Device> selectByUserIdAndAreaId(@Param("userId") Long userId, @Param("areaId") Long areaId);
}