package com.yclin.ingressbackend.config;

import com.yclin.ingressbackend.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        // 1. 如果请求头不存在或格式不正确，则直接放行，让后续的过滤器处理
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. 提取 JWT
        final String jwt = authHeader.substring(7); // "Bearer " 是 7 个字符
        final String username = jwtService.extractUsername(jwt);

        // 3. 如果 Token 中有用户名，并且当前安全上下文中没有认证信息
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 从数据库加载用户信息
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 4. 验证 Token 是否有效
            if (jwtService.isTokenValid(jwt, userDetails)) {
                // 5. 如果有效，构建一个认证令牌
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // 我们是 JWT 认证，不需要凭证（密码）
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                // 6. 将认证令牌更新到安全上下文中
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 7. 无论如何都放行，让请求继续传递
        filterChain.doFilter(request, response);
    }
}