package com.example.demo.config;

import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import jakarta.annotation.PostConstruct;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@EnableConfigurationProperties(JpaProperties.class)
public class DatabaseInitializationConfig implements ApplicationListener<ContextRefreshedEvent> {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializationConfig.class);
    
    @Autowired
    private DataSource dataSource;
    
    private boolean dbReady = false;
    
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (!dbReady) {
            verifyDatabaseConnection();
        }
    }
    
    @PostConstruct
    public void verifyDatabaseConnection() {
        try {
            var connection = dataSource.getConnection();
            if (connection.isValid(10)) { // 10秒超时
                logger.info("Successfully connected to database");
                logger.info("Database URL: {}", connection.getMetaData().getURL());
                logger.info("Database Product Name: {}", connection.getMetaData().getDatabaseProductName());
                logger.info("Database Version: {} {}", 
                    connection.getMetaData().getDatabaseMajorVersion(), 
                    connection.getMetaData().getDatabaseMinorVersion());
                logger.info("Driver Name: {}", connection.getMetaData().getDriverName());
                logger.info("Driver Version: {}", connection.getMetaData().getDriverVersion());
                
                dbReady = true;
            } else {
                throw new RuntimeException("Could not establish database connection");
            }
            connection.close();
        } catch (Exception e) {
            logger.error("Failed to connect to database", e);
            throw new RuntimeException("Database connection failed: " + e.getMessage(), e);
        }
    }
    
    public boolean isDbReady() {
        return dbReady;
    }
}