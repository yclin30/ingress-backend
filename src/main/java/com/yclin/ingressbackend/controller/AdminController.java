package com.yclin.ingressbackend.controller;

import com.yclin.ingressbackend.config.SecurityUser;
import com.yclin.ingressbackend.dto.ApiResponse;
import com.yclin.ingressbackend.dto.admin.AdminDtos.*;
import com.yclin.ingressbackend.dto.location.LocationDtos.LocationDetailDto;
import com.yclin.ingressbackend.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<UserPaginatedResponse>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        UserPaginatedResponse response = adminService.getUsers(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/users/{userId}/ban")
    public ResponseEntity<ApiResponse<Void>> banUser(@PathVariable Long userId) {
        adminService.banUser(userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/users/{userId}/unban")
    public ResponseEntity<ApiResponse<Void>> unbanUser(@PathVariable Long userId) {
        adminService.unbanUser(userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/locations")
    public ResponseEntity<ApiResponse<LocationDetailDto>> createLocation(
            @RequestBody LocationCreateRequest request,
            @AuthenticationPrincipal SecurityUser securityUser
    ) {
        LocationDetailDto newLocation = adminService.createLocation(request, securityUser.getUser());
        return new ResponseEntity<>(ApiResponse.created(newLocation), HttpStatus.CREATED);
    }

    @PostMapping("/announcements")
    public ResponseEntity<ApiResponse<Void>> createAnnouncement(
            @RequestBody AnnouncementCreateRequest request,
            @AuthenticationPrincipal SecurityUser securityUser
    ) {
        adminService.createAnnouncement(request.getContent(), securityUser.getUser());
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}