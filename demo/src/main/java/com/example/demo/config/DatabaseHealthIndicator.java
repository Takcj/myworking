package com.example.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class DatabaseHealthIndicator implements HealthIndicator {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseHealthIndicator.class);
    
    private final DataSource dataSource;
    
    public DatabaseHealthIndicator(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    @Override
    public Health health() {
        try (Connection connection = dataSource.getConnection()) {
            // 尝试建立连接
            if (connection.isValid(5)) { // 5秒超时
                logger.info("Database connection is healthy");
                return Health.up()
                        .withDetail("database", "MySQL")
                        .withDetail("validationQuery", "SELECT 1")
                        .build();
            } else {
                logger.warn("Database connection validation failed");
                return Health.down()
                        .withDetail("error", "Connection validation failed")
                        .build();
            }
        } catch (SQLException e) {
            logger.error("Failed to connect to database", e);
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
    
}