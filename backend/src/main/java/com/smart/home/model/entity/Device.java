package com.smart.home.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 设备实体类
 *
 * @author lingma
 */
@Data
@TableName("devices")
public class Device {

    /**
     * 设备唯一标识
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID（外键，关联用户表）
     */
    private Long userId;

    /**
     * 区域ID（外键，关联房屋区域表）
     */
    private Long areaId;

    /**
     * 设备唯一ID（由下位机提供）
     */
    private String deviceId;

    /**
     * 设备类型（temperature_sensor, humidity_sensor, light_sensor, led, curtain等）
     */
    private String deviceType;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 状态名称（用于前端显示框架）
     */
    private String statusName;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

}