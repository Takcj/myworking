package com.smart.home.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smart.home.model.entity.DeviceStatus;
import org.apache.ibatis.annotations.Mapper;

/**
 * 设备状态Mapper接口
 *
 * @author lingma
 */
@Mapper
public interface DeviceStatusMapper extends BaseMapper<DeviceStatus> {
}