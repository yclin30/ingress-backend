package com.yclin.ingressbackend.controller;

import com.yclin.ingressbackend.config.SecurityUser;
import com.yclin.ingressbackend.dto.user.UserDetailDto;
import com.yclin.ingressbackend.dto.user.UserUpdateRequestDto;
import com.yclin.ingressbackend.entity.domain.User;
import com.yclin.ingressbackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 获取当前登录用户的个人资料
     */
    @GetMapping("/me")
    public ResponseEntity<UserDetailDto> getCurrentUserProfile(@AuthenticationPrincipal SecurityUser securityUser) {
        // @AuthenticationPrincipal 现在注入的是 SecurityUser
        // 我们需要调用 .getUser() 来获取原始的 User 实体
        User currentUser = securityUser.getUser();
        UserDetailDto userProfile = userService.getUserProfile(currentUser);
        return ResponseEntity.ok(userProfile);
    }

    /**
     * 更新当前登录用户的个人资料
     */
    @PutMapping("/me")
    public ResponseEntity<UserDetailDto> updateCurrentUserProfile(
            @AuthenticationPrincipal SecurityUser securityUser,
            @RequestBody UserUpdateRequestDto requestDto
    ) {
        // 同样，从 SecurityUser 中获取原始的 User 实体
        User currentUser = securityUser.getUser();
        UserDetailDto updatedUserProfile = userService.updateUserProfile(currentUser, requestDto);
        return ResponseEntity.ok(updatedUserProfile);
    }
}