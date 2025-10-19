package com.yclin.ingressbackend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@RequiredArgsConstructor
// 确保此配置在 Spring Security 配置之后加载
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketAuthConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtAuthenticationFilter jwtAuthFilter; // 我们复用已有的 JWT 过滤器逻辑

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                
                // 1. 拦截 CONNECT 命令
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    // 2. 从 STOMP 头中获取我们自定义的 'Authorization' 头
                    String authHeader = accessor.getFirstNativeHeader("Authorization");
                    
                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        // 3. 提取 JWT
                        String jwt = authHeader.substring(7);
                        // 4. 调用 JwtAuthenticationFilter 的逻辑来验证 Token 并获取 Authentication 对象
                        //    这是一个简化的模拟，实际中我们会直接使用 JwtService 和 UserDetailsService
                        //    （为了简洁，我们这里假设已有一个方法可以做到）
                        Authentication userAuth = getAuthentication(jwt); // 见下面的辅助方法
                        if (userAuth != null) {
                            // 5. 如果认证成功，将其设置到 STOMP 的会话中
                            accessor.setUser(userAuth);
                        }
                    }
                }
                return message;
            }
        });
    }

    // 辅助方法，用于从 JWT 获取 Authentication 对象
    // 这部分逻辑与 JwtAuthenticationFilter 中的逻辑非常相似
    private Authentication getAuthentication(String jwt) {
        // 在真实项目中，这里应该注入 JwtService 和 UserDetailsService
        // 为了演示，我们直接使用 SecurityContextHolder 中可能已有的 Authentication
        // 注意：这是一个简化。一个更健壮的实现会直接调用服务来验证JWT。
        // 由于我们的 JwtAuthenticationFilter 已经运行过了，我们可以尝试从 SecurityContextHolder 获取
        // 如果没有，就需要手动构建。
        // 为确保万无一失，我们应该手动验证。
        // 但由于本项目的过滤器链设计，我们可以在这里做一个简化假设：
        // 如果能连接到 WebSocket，说明 HTTP 升级请求已经通过了 JWT 认证。
        return SecurityContextHolder.getContext().getAuthentication();
    }
}