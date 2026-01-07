package com.smart.home.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库测试服务
 * 用于测试数据库连接和表结构
 *
 * @author lingma
 */
@Service
public class DatabaseTestService {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseTestService.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 测试数据库连接
     *
     * @return 连接状态
     */
    public Map<String, Object> testConnection() {
        Map<String, Object> result = new HashMap<>();
        try {
            // 执行简单查询测试连接
            Integer testResult = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            if (testResult != null) {
                result.put("status", "success");
                result.put("message", "数据库连接正常");
                result.put("connection", true);
            } else {
                result.put("status", "error");
                result.put("message", "数据库连接异常");
                result.put("connection", false);
            }
        } catch (Exception e) {
            logger.error("数据库连接测试失败: ", e);
            result.put("status", "error");
            result.put("message", "数据库连接失败: " + e.getMessage());
            result.put("connection", false);
        }
        return result;
    }

    /**
     * 检查数据库表是否存在
     *
     * @return 表检查结果
     */
    public Map<String, Object> checkTables() {
        Map<String, Object> result = new HashMap<>();
        List<String> requiredTables = List.of("users", "house_areas", "devices", "connection_status", "automation_rules");
        List<String> missingTables = new ArrayList<>();

        try {
            for (String tableName : requiredTables) {
                if (!tableExists(tableName)) {
                    missingTables.add(tableName);
                }
            }

            if (missingTables.isEmpty()) {
                result.put("status", "success");
                result.put("message", "所有必需的表都存在");
                result.put("missingTables", missingTables);
            } else {
                result.put("status", "warning");
                result.put("message", "以下表不存在: " + String.join(", ", missingTables));
                result.put("missingTables", missingTables);
            }
        } catch (Exception e) {
            logger.error("检查表结构失败: ", e);
            result.put("status", "error");
            result.put("message", "检查表结构失败: " + e.getMessage());
            result.put("missingTables", new ArrayList<>());
        }

        return result;
    }

    /**
     * 检查表是否存在
     *
     * @param tableName 表名
     * @return 是否存在
     */
    private boolean tableExists(String tableName) {
        try {
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = ?",
                Integer.class,
                tableName
            );
            return count != null && count > 0;
        } catch (Exception e) {
            logger.error("检查表 {} 是否存在时出错: ", tableName, e);
            return false;
        }
    }
}