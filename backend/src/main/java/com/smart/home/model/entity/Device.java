package com.smart.home.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
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
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updatedAt;

    /**
     * 在线状态（true: 在线, false: 离线）
     * 用于表示设备当前是否在线
     */
    private Boolean onlineStatus;

    /**
     * 用户对设备的权限等级（仅在查询时使用）
     * 如：owner(拥有者), admin(管理员), member(成员), guest(访客)等
     */
    private String permissionLevel;
}