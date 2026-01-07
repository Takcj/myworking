package com.smart.home.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.IOException;

/**
 * 数据库初始化配置
 * 在应用启动时自动创建数据库表
 *
 * @author lingma
 */
@Configuration
public class DatabaseInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    /**
     * 初始化数据库表
     */
    @PostConstruct
    public void initializeDatabase() {
        try {
            // 检查表是否已经存在
            if (!tableExists()) {
                logger.info("开始初始化数据库表结构...");
                
                // 从资源文件加载SQL脚本并执行
                ClassPathResource resource = new ClassPathResource("init_database.sql");
                ScriptUtils.executeSqlScript(dataSource.getConnection(), resource);
                
                logger.info("数据库表结构初始化完成");
            } else {
                logger.info("数据库表已存在，跳过初始化");
            }
        } catch (Exception e) {
            logger.error("数据库初始化失败: ", e);
        }
    }

    /**
     * 检查表是否已存在
     *
     * @return 是否存在
     */
    private boolean tableExists() {
        try {
            // 尝试查询用户表是否存在
            jdbcTemplate.queryForObject("SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'users'", Integer.class);
            return true;
        } catch (Exception e) {
            logger.info("检测数据库表是否存在时出现异常: ", e);
            return false;
        }
    }
}