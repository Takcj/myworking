package com.smart.home.model.dto;

import lombok.Data;

import java.util.Map;

/**
 * 设备数据传输对象
 *
 * @author lingma
 */
@Data
public class DeviceDataDTO {

    /**
     * 用户唯一标识
     */
    private String userId;

    /**
     * 时间戳
     */
    private Long timestamp;

    /**
     * 消息类型
     */
    private String messageType;

    /**
     * 数据体
     */
    private DataBody data;

    /**
     * 数据体内部类
     */
    @Data
    public static class DataBody {
        /**
         * 区域标识
         */
        private String area;

        /**
         * 设备类型
         */
        private String deviceType;

        /**
         * 设备唯一标识
         */
        private String deviceId;

        /**
         * 设备状态
         */
        private Map<String, Object> status;

        /**
         * 设备数据采集时间
         */
        private Long timestamp;

        /**
         * 控制命令
         */
        private Command command;
    }

    /**
     * 控制命令内部类
     */
    @Data
    public static class Command {
        /**
         * 操作类型
         */
        private String type;

        /**
         * 操作参数
         */
        private Map<String, Object> parameters;
    }
}