package com.smart.home.model.dto;

import lombok.Data;

/**
 * 登录请求DTO
 *
 * @author lingma
 */
@Data
public class LoginRequest {

    /**
     * 用户名
     */
    private String username;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 密码
     */
    private String password;

    /**
     * 验证码
     */
    private String verificationCode;

    /**
     * 登录类型 (password-密码登录, verification-验证码登录)
     */
    private String loginType;
}