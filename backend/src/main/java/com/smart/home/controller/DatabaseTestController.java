package com.smart.home.controller;

import com.smart.home.common.Result;
import com.smart.home.service.impl.DatabaseTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 数据库测试控制器
 * 提供数据库连接测试接口
 *
 * @author lingma
 */
@RestController
@RequestMapping("/api/v1/database")
public class DatabaseTestController {

    @Autowired
    private DatabaseTestService databaseTestService;

    /**
     * 测试数据库连接
     *
     * @return 连接测试结果
     */
    @GetMapping("/test-connection")
    public Result<Map<String, Object>> testConnection() {
        Map<String, Object> result = databaseTestService.testConnection();
        if ("success".equals(result.get("status"))) {
            return Result.success("数据库连接测试", result);
        } else {
            return Result.error(500, (String) result.get("message"));
        }
    }

    /**
     * 检查数据库表结构
     *
     * @return 表结构检查结果
     */
    @GetMapping("/check-tables")
    public Result<Map<String, Object>> checkTables() {
        Map<String, Object> result = databaseTestService.checkTables();
        return Result.success("数据库表结构检查", result);
    }
}