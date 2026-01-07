package com.smart.home.utils;

import java.util.regex.Pattern;

/**
 * 验证工具类
 *
 * @author lingma
 */
public class ValidationUtils {

    /**
     * 手机号正则表达式
     */
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    /**
     * 验证手机号格式
     *
     * @param phone 手机号
     * @return 是否有效
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * 验证用户名格式（4-20位字母数字下划线）
     *
     * @param username 用户名
     * @return 是否有效
     */
    public static boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return username.matches("^[a-zA-Z0-9_]{4,20}$");
    }

    /**
     * 验证密码格式（6-20位任意字符）
     *
     * @param password 密码
     * @return 是否有效
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            return false;
        }
        return password.length() >= 6 && password.length() <= 20;
    }

    /**
     * 验证设备ID格式（8-32位字母数字下划线横线）
     *
     * @param deviceId 设备ID
     * @return 是否有效
     */
    public static boolean isValidDeviceId(String deviceId) {
        if (deviceId == null || deviceId.trim().isEmpty()) {
            return false;
        }
        return deviceId.matches("^[a-zA-Z0-9_-]{8,32}$");
    }
}