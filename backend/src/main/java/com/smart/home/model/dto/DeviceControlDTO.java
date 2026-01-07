package com.smart.home.model.dto;

import lombok.Data;

import java.util.Map;

/**
 * 设备控制DTO
 *
 * @author lingma
 */
@Data
public class DeviceControlDTO {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 区域标识
     */
    private String area;

    /**
     * 设备类型
     */
    private String deviceType;

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 命令类型
     */
    private String commandType;

    /**
     * 命令参数
     */
    private Map<String, Object> parameters;
}