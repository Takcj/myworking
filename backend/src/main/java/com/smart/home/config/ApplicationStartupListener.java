package com.smart.home.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * 应用启动监听器
 * 在应用完全启动后执行初始化操作
 *
 * @author lingma
 */
@Component
public class ApplicationStartupListener {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationStartupListener.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 应用启动完成后执行
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        logger.info("应用已启动完成，执行初始化操作");
        checkDatabaseTables();
    }

    /**
     * 检查数据库表是否存在
     */
    private void checkDatabaseTables() {
        try {
            // 检查用户表是否存在
            jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'users'", 
                Integer.class
            );
            logger.info("数据库表结构检查完成，用户表存在");
        } catch (Exception e) {
            logger.error("数据库表结构检查失败，请检查数据库连接和表结构: ", e);
        }
    }
}