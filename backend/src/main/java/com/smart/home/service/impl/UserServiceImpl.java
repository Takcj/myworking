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
        // 检查手机号是否已存在
        User existingUser = userMapper.selectByPhone(phone);
        if (existingUser != null) {
            throw new RuntimeException("该手机号已被注册");
        }

        // 创建新用户
        User user = new User();
        user.setUsername(username);
        user.setPhone(phone);
        // 加密密码
        user.setPassword(DigestUtils.md5DigestAsHex(password.getBytes()));

        // 保存用户信息
        userMapper.insert(user);
        return user;
    }

    /**
     * 用户登录
     *
     * @param loginRequest 登录请求
     * @return 用户实体
     */
    @Override
    public User login(LoginRequest loginRequest) {
        User user;
        if (loginRequest.getPhone() != null) {
            // 使用手机号登录
            user = userMapper.selectByPhone(loginRequest.getPhone());
        } else {
            // 使用用户名登录
            user = userMapper.selectOne(
                com.baomidou.mybatisplus.core.conditions.query.QueryWrapper.<User>new().eq("username", loginRequest.getUsername())
            );
        }

        if (user != null) {
            // 验证密码
            String encryptedPassword = DigestUtils.md5DigestAsHex(loginRequest.getPassword().getBytes());
            if (encryptedPassword.equals(user.getPassword())) {
                // 更新最后登录时间
                updateLastLoginTime(user.getId());
                return user;
            }
        }

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
        return userMapper.selectById(id);
    }

    /**
     * 根据手机号获取用户
     *
     * @param phone 手机号
     * @return 用户实体
     */
    @Override
    public User getUserByPhone(String phone) {
        return userMapper.selectByPhone(phone);
    }

    /**
     * 更新用户最后登录时间
     *
     * @param userId 用户ID
     */
    @Override
    public void updateLastLoginTime(Long userId) {
        userMapper.updateLastLoginTime(userId);
    }
}