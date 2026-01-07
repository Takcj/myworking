package com.smart.home.controller;

import com.smart.home.common.Result;
import com.smart.home.model.dto.LoginRequest;
import com.smart.home.model.entity.User;
import com.smart.home.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 *
 * @author lingma
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     *
     * @param loginRequest 登录请求
     * @return 登录结果
     */
    @PostMapping("/login")
    public Result<Object> login(@RequestBody LoginRequest loginRequest) {
        User user = userService.login(loginRequest);
        if (user != null) {
            // 这里应该生成JWT token，为了简化暂时返回用户信息
            Object result = new Object() {
                public String token = "fake-jwt-token";
                public User user = user;
            };
            return Result.success("登录成功", result);
        }
        return Result.error("用户名或密码错误");
    }

    /**
     * 用户注册
     *
     * @param username 用户名
     * @param phone 手机号
     * @param password 密码
     * @return 注册结果
     */
    @PostMapping("/register")
    public Result<User> register(
            @RequestParam String username,
            @RequestParam String phone,
            @RequestParam String password) {
        User user = userService.register(username, phone, password);
        if (user != null) {
            return Result.success("注册成功", user);
        }
        return Result.error("注册失败");
    }
}