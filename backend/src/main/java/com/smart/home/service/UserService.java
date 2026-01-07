package com.smart.home.service;

import com.smart.home.model.entity.User;
import com.smart.home.model.dto.LoginRequest;

/**
 * 用户服务接口
 *
 * @author lingma
 */
public interface UserService {

    /**
     * 用户注册
     *
     * @param username 用户名
     * @param phone 手机号
     * @param password 密码
     * @return 用户实体
     */
    User register(String username, String phone, String password);

    /**
     * 用户登录
     *
     * @param loginRequest 登录请求
     * @return 用户实体
     */
    User login(LoginRequest loginRequest);

    /**
     * 根据ID获取用户
     *
     * @param id 用户ID
     * @return 用户实体
     */
    User getUserById(Long id);

    /**
     * 根据手机号获取用户
     *
     * @param phone 手机号
     * @return 用户实体
     */
    User getUserByPhone(String phone);

    /**
     * 更新用户最后登录时间
     *
     * @param userId 用户ID
     */
    void updateLastLoginTime(Long userId);
}