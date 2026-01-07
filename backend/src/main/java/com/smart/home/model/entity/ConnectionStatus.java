package com.smart.home.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 连接状态实体类
 *
 * @author lingma
 */
@Data
@TableName("connection_status")
public class ConnectionStatus {

    /**
     * 记录唯一标识
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 连接状态（connected, disconnected）
     */
    private String connectionStatus;

    /**
     * 连接时间戳
     */
    private LocalDateTime connectionTime;

    /**
     * 断开连接时间戳
     */
    private LocalDateTime disconnectionTime;

    /**
     * 最后心跳时间
     */
    private LocalDateTime lastHeartbeat;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

}