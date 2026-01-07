package com.smart.home.common;

/**
 * 设备类型枚举
 * 
 * @author lingma
 */
public enum DeviceType {
    // 传感器类型
    TEMPERATURE_SENSOR("temperature_sensor", "温度传感器"),
    HUMIDITY_SENSOR("humidity_sensor", "湿度传感器"),
    LIGHT_SENSOR("light_sensor", "光照传感器"),
    
    // 执行器类型
    LED("led", "LED灯"),
    CURTAIN("curtain", "窗帘"),
    AIR_CONDITIONER("air_conditioner", "空调"),
    FAN("fan", "风扇"),
    SOCKET("socket", "插座");

    private final String code;
    private final String description;

    DeviceType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据代码获取设备类型
     *
     * @param code 设备类型代码
     * @return 设备类型枚举
     */
    public static DeviceType fromCode(String code) {
        for (DeviceType type : DeviceType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 检查设备类型代码是否有效
     *
     * @param code 设备类型代码
     * @return 是否有效
     */
    public static boolean isValidCode(String code) {
        return fromCode(code) != null;
    }
}