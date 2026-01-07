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
@RequestMapping("/api/auth")
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
    public Result<String> login(@RequestBody LoginRequest loginRequest) {
        User user = userService.login(loginRequest);
        if (user != null) {
            // 生成JWT token
            String token = com.smart.home.common.JwtUtil.generateToken(user.getId().toString());
            return Result.success("登录成功", token);
        }
        return Result.error("登录失败，用户名或密码错误");
    }

    /**
     * 用户注册
     *
     * @param loginRequest 注册请求
     * @return 注册结果
     */
    @PostMapping("/register")
    public Result<User> register(@RequestBody LoginRequest loginRequest) {
        User user = userService.register(
                loginRequest.getUsername(),
                loginRequest.getPhone(),
                loginRequest.getPassword()
        );
        if (user != null) {
            return Result.success("注册成功", user);
        }
        return Result.error("注册失败");
    }
}