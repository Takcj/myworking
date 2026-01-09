package com.example.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

@Component
public class DatabaseConnectionValidator implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnectionValidator.class);

    private final DataSource dataSource;

    public DatabaseConnectionValidator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(5)) {
                DatabaseMetaData metaData = connection.getMetaData();
                
                logger.info("Database connection established successfully!");
                logger.info("Database Product Name: {}", metaData.getDatabaseProductName());
                logger.info("Database Version: {}", metaData.getDatabaseProductVersion());
                logger.info("Database Driver: {}", metaData.getDriverName());
                logger.info("Driver Version: {}", metaData.getDriverVersion());
                logger.info("JDBC URL: {}", metaData.getURL());
                logger.info("Username: {}", metaData.getUserName());
            } else {
                logger.error("Could not establish database connection");
            }
        } catch (SQLException e) {
            logger.error("Failed to validate database connection", e);
        }
    }
}