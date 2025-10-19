package com.yclin.ingressbackend.service;

import com.yclin.ingressbackend.config.SecurityUser; // <-- 新增 import
import com.yclin.ingressbackend.dto.auth.AuthenticationResponse;
import com.yclin.ingressbackend.dto.auth.LoginRequest;
import com.yclin.ingressbackend.dto.auth.RegisterRequest;
import com.yclin.ingressbackend.entity.domain.User;
import com.yclin.ingressbackend.entity.enums.UserRole;
import com.yclin.ingressbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * 用户注册逻辑
     */
    public AuthenticationResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username is already taken!");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already in use!");
        }

        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.ROLE_USER)
                .level(1)
                .experience(0L)
                .isBanned(false)
                .createdAt(Instant.now())
                .build();

        userRepository.save(user);

        // **关键修正**: 在生成 Token 前，将 User 对象包装成 SecurityUser
        var jwtToken = jwtService.generateToken(new SecurityUser(user));

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    /**
     * 用户登录逻辑
     */
    public AuthenticationResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // **关键修正**: 在生成 Token 前，将 User 对象包装成 SecurityUser
        var jwtToken = jwtService.generateToken(new SecurityUser(user));

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}