package com.yclin.ingressbackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 设置消息代理（Broker）的前缀
        registry.enableSimpleBroker("/topic", "/queue");
        // 设置应用程序目标（Application Destination）的前缀
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) { // <-- **核心修正: Endpoint -> Endpoints**
        // 注册一个 STOMP 端点，客户端将通过这个 URL 连接到 WebSocket 服务器
        registry.addEndpoint("/ws")
                // 允许所有来源的连接，以便于开发和跨域访问
                .setAllowedOriginPatterns("*");
    }
}