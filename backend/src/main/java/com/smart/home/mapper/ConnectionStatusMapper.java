package com.smart.home.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smart.home.model.entity.ConnectionStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 连接状态Mapper接口
 *
 * @author lingma
 */
@Mapper
public interface ConnectionStatusMapper extends BaseMapper<ConnectionStatus> {
    /**
     * 根据设备ID查询连接状态
     *
     * @param deviceId 设备ID
     * @return 连接状态实体
     */
    ConnectionStatus selectByDeviceId(@Param("deviceId") String deviceId);

    /**
     * 更新最后心跳时间
     *
     * @param deviceId 设备ID
     */
    void updateHeartbeat(@Param("deviceId") String deviceId);

    /**
     * 设置设备为在线状态
     *
     * @param params 包含userId和deviceId的参数
     */
    void setDeviceOnline(@Param("userId") String userId, @Param("deviceId") String deviceId);

    /**
     * 设置设备为离线状态
     *
     * @param deviceId 设备ID
     */
    void setDeviceOffline(@Param("deviceId") String deviceId);
}