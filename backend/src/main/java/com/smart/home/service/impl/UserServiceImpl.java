package com.smart.home.service.impl;

import com.smart.home.model.entity.User;
import com.smart.home.model.dto.LoginRequest;
import com.smart.home.service.UserService;
import com.smart.home.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

/**
 * 用户服务实现类
 *
 * @author lingma
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 用户注册
     *
     * @param username 用户名
     * @param phone    手机号
     * @param password 密码
     * @return 用户实体
     */
    @Override
    public User register(String username, String phone, String password) {
        // TODO: 实现用户注册的逻辑
        // 1. 检查用户名或手机号是否已存在
        // 2. 对密码进行加密
        // 3. 保存用户信息
        // 4. 返回用户实体
        return null;
    }

    /**
     * 用户登录
     *
     * @param loginRequest 登录请求
     * @return 用户实体
     */
    @Override
    public User login(LoginRequest loginRequest) {
        // TODO: 实现用户登录的逻辑
        // 1. 根据用户名或手机号获取用户
        // 2. 验证密码
        // 3. 更新最后登录时间
        // 4. 返回用户实体
        return null;
    }

    /**
     * 根据ID获取用户
     *
     * @param id 用户ID
     * @return 用户实体
     */
    @Override
    public User getUserById(Long id) {
        // TODO: 实现根据ID获取用户的逻辑
        return null;
    }

    /**
     * 根据手机号获取用户
     *
     * @param phone 手机号
     * @return 用户实体
     */
    @Override
    public User getUserByPhone(String phone) {
        // TODO: 实现根据手机号获取用户的逻辑
        return null;
    }

    /**
     * 更新用户最后登录时间
     *
     * @param userId 用户ID
     */
    @Override
    public void updateLastLoginTime(Long userId) {
        // TODO: 实现更新用户最后登录时间的逻辑
    }
}