package com.smart.home.model.dto;

import lombok.Data;

import java.util.Map;

/**
 * 自动化规则DTO
 *
 * @author lingma
 */
@Data
public class AutomationRuleDTO {

    /**
     * 用户ID（外键，关联用户表）
     */
    private Long userId;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 触发类型（device_status - 设备状态，time_based - 定时）
     */
    private String triggerType;

    /**
     * 触发条件（JSON格式存储）
     */
    private Map<String, Object> triggerCondition;

    /**
     * 目标设备ID（外键，关联设备表）
     */
    private String targetDeviceId;

    /**
     * 目标设备类型
     */
    private String targetDeviceType;

    /**
     * 命令类型
     */
    private String commandType;

    /**
     * 命令参数（JSON格式存储）
     */
    private Map<String, Object> commandParameters;

    /**
     * 规则是否启用
     */
    private Boolean isEnabled;
}