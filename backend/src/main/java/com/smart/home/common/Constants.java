package com.smart.home.common;

/**
 * 系统常量类
 *
 * @author lingma
 */
public class Constants {

    /**
     * MQTT相关常量
     */
    public static final class MQTT {
        // 用户数据主题格式，用于下位机上报数据
        public static final String USER_DATA_TOPIC = "user/%s/device/data";
        // 用户控制主题格式，用于向用户设备发送控制命令
        public static final String USER_CONTROL_TOPIC = "user/%s/device/control";
        // 用户心跳主题格式
        public static final String USER_HEARTBEAT_TOPIC = "user/%s/device/heartbeat";
        // 用户连接状态主题格式
        public static final String USER_CONNECTION_TOPIC = "user/%s/device/connection";
    }

    /**
     * 消息类型常量
     */
    public static final class MessageType {
        public static final String DEVICE_DATA = "device_data";
        public static final String CONTROL_COMMAND = "control_command";
        public static final String CONNECTION = "connection";
        public static final String HEARTBEAT = "heartbeat";
    }

    /**
     * 设备类型常量
     */
    public static final class DeviceType {
        // 传感器类型
        public static final String TEMPERATURE_SENSOR = "temperature_sensor";
        public static final String HUMIDITY_SENSOR = "humidity_sensor";
        public static final String LIGHT_SENSOR = "light_sensor";
        
        // 执行器类型
        public static final String LED = "led";
        public static final String CURTAIN = "curtain";
        public static final String AIR_CONDITIONER = "air_conditioner";
        public static final String DOOR_WINDOW_SENSOR = "door_window_sensor";
    }

    /**
     * 区域类型常量
     */
    public static final class AreaType {
        public static final String FIXED = "fixed";
        public static final String GENERAL = "general";
    }

    /**
     * 连接状态常量
     */
    public static final class ConnectionStatus {
        public static final String CONNECTED = "connected";
        public static final String DISCONNECTED = "disconnected";
        public static final String ONLINE = "online";
    }

    /**
     * 触发类型常量
     */
    public static final class TriggerType {
        public static final String DEVICE_STATUS = "device_status";
        public static final String TIME_BASED = "time_based";
    }

    /**
     * 状态码常量
     */
    public static final class StatusCode {
        public static final int SUCCESS = 200;
        public static final int ERROR = 500;
        public static final int UNAUTHORIZED = 401;
        public static final int NOT_FOUND = 404;
    }

    /**
     * 通用常量
     */
    public static final String SUCCESS = "success";
    public static final String ERROR = "error";
    public static final String ENABLED = "enabled";
    public static final String DISABLED = "disabled";
}