package com.smart.home.controller;

import com.smart.home.common.Result;
import com.smart.home.model.dto.LoginRequest;
import com.smart.home.model.entity.User;
import com.smart.home.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 *
 * @author lingma
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     *
     * @param loginRequest 登录请求
     * @return 用户信息
     */
    @PostMapping("/login")
    public Result<User> login(@RequestBody LoginRequest loginRequest) {
        User user = userService.login(loginRequest);
        if (user != null) {
            return Result.success(user);
        }
        return Result.error("登录失败");
    }

    /**
     * 用户注册
     *
     * @param username 用户名
     * @param phone 手机号
     * @param password 密码
     * @return 用户信息
     */
    @PostMapping("/register")
    public Result<User> register(
            @RequestParam String username,
            @RequestParam String phone,
            @RequestParam String password) {
        User user = userService.register(username, phone, password);
        if (user != null) {
            return Result.success(user);
        }
        return Result.error("注册失败");
    }

    /**
     * 获取用户信息
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    public Result<User> getUserInfo(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user != null) {
            return Result.success(user);
        }
        return Result.error("用户不存在");
    }

    /**
     * 更新用户信息
     *
     * @param id 用户ID
     * @param user 用户信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public Result<User> updateUserInfo(@PathVariable Long id, @RequestBody User user) {
        // 实现更新用户信息的逻辑
        return Result.success("更新成功", user);
    }
}