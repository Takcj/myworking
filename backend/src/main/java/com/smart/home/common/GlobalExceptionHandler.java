package com.smart.home.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

/**
 * 全局异常处理器
 *
 * @author lingma
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理运行时异常
     *
     * @param e 异常
     * @return Result
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<String> handleRuntimeException(RuntimeException e) {
        logger.error("运行时异常: ", e);
        return Result.error(500, "运行时异常: " + e.getMessage());
    }

    /**
     * 处理SQL异常
     *
     * @param e 异常
     * @return Result
     */
    @ExceptionHandler(SQLException.class)
    public Result<String> handleSqlException(SQLException e) {
        logger.error("数据库异常: ", e);
        return Result.error(500, "数据库异常: " + e.getMessage());
    }

    /**
     * 处理一般异常
     *
     * @param e 异常
     * @return Result
     */
    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {
        logger.error("系统异常: ", e);
        return Result.error(500, "系统异常: " + e.getMessage());
    }
}