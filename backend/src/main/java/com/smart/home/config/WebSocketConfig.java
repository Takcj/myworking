package com.smart.home.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * WebSocket配置类
 *
 * @author lingma
 */
@Configuration
public class WebSocketConfig {

    /**
     * ServerEndpointExporter
     * 自动注册使用@ServerEndpoint注解的Bean
     *
     * @return ServerEndpointExporter
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}