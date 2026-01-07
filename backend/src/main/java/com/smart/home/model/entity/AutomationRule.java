package com.smart.home.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 自动化规则实体类
 *
 * @author lingma
 */
@Data
@TableName("automation_rules")
public class AutomationRule {

    /**
     * 规则唯一标识
     */
    @TableId(type = IdType.AUTO)
    private Long id;

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
    private String triggerCondition;

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
    private String commandParameters;

    /**
     * 规则是否启用
     */
    private Boolean isEnabled;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

}