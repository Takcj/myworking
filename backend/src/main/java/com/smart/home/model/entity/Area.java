package com.smart.home.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 区域实体类
 *
 * @author lingma
 */
@Data
@TableName("house_areas")
public class Area {

    /**
     * 区域唯一标识
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID（外键，关联用户表）
     */
    private Long userId;

    /**
     * 区域名称（客厅、卧室、厨房、卫生间、阳台、通用区域等）
     */
    private String areaName;

    /**
     * 区域类型（fixed - 固定区域，general - 通用区域）
     */
    private String areaType;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

}