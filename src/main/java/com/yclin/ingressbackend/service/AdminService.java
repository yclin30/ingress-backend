package com.yclin.ingressbackend.service;

import com.yclin.ingressbackend.dto.admin.AdminDtos.*;
import com.yclin.ingressbackend.dto.location.LocationDtos.CoordinateDto;
import com.yclin.ingressbackend.dto.location.LocationDtos.LocationDetailDto;
import com.yclin.ingressbackend.dto.websocket.WebSocketDtos.AnnouncementBroadcast;
import com.yclin.ingressbackend.entity.domain.Announcement;
import com.yclin.ingressbackend.entity.domain.Location;
import com.yclin.ingressbackend.entity.domain.User;
import com.yclin.ingressbackend.repository.AnnouncementRepository;
import com.yclin.ingressbackend.repository.LocationRepository;
import com.yclin.ingressbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final AnnouncementRepository announcementRepository;
    private final SimpMessagingTemplate messagingTemplate; // 用于 WebSocket 广播
    private final GeometryFactory geometryFactory = new GeometryFactory();

    /**
     * 分页获取用户列表
     */
    @Transactional(readOnly = true)
    public UserPaginatedResponse getUsers(Pageable pageable) {
        Page<User> userPage = userRepository.findAll(pageable);

        UserPaginatedResponse response = new UserPaginatedResponse();
        response.setUsers(userPage.getContent().stream()
                .map(this::convertToUserSummaryDto)
                .collect(Collectors.toList()));
        response.setCurrentPage(userPage.getNumber());
        response.setTotalPages(userPage.getTotalPages());
        response.setTotalElements(userPage.getTotalElements());

        return response;
    }

    /**
     * 封禁用户
     */
    @Transactional
    public void banUser(Long userId) {
        updateUserBanStatus(userId, true);
    }

    /**
     * 解封用户
     */
    @Transactional
    public void unbanUser(Long userId) {
        updateUserBanStatus(userId, false);
    }

    /**
     * 更新用户封禁状态的私有辅助方法
     */
    private void updateUserBanStatus(Long userId, boolean isBanned) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        user.setIsBanned(isBanned);
        // 事务提交时，更改会自动保存
    }

    /**
     * 创建一个新的游戏据点
     */
    @Transactional
    public LocationDetailDto createLocation(LocationCreateRequest request, User admin) {
        Point point = geometryFactory.createPoint(new Coordinate(request.getLongitude(), request.getLatitude()));
        point.setSRID(4326); // 确保设置正确的坐标系

        Location location = new Location();
        location.setName(request.getName());
        location.setCoordinate(point);
        location.setCreatedByAdmin(admin);

        Location savedLocation = locationRepository.save(location);
        return convertToLocationDetailDto(savedLocation);
    }

    /**
     * 创建并广播一条新的全局公告
     */
    @Transactional
    public void createAnnouncement(String content, User adminUser) {
        // 1. 将公告保存到数据库
        Announcement announcement = Announcement.builder()
                .content(content)
                .admin(adminUser)
                .build();
        Announcement savedAnnouncement = announcementRepository.save(announcement);

        // 2. 创建用于 WebSocket 广播的 DTO
        AnnouncementBroadcast broadcastMessage = new AnnouncementBroadcast(
                savedAnnouncement.getContent(),
                savedAnnouncement.getCreatedAt()
        );

        // 3. 通过 WebSocket 广播到 "/topic/announcements" 主题
        messagingTemplate.convertAndSend("/topic/announcements", broadcastMessage);
    }

    // --- DTO 转换辅助方法 ---

    private UserSummaryDto convertToUserSummaryDto(User user) {
        UserSummaryDto dto = new UserSummaryDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setIsBanned(user.getIsBanned());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }

    private LocationDetailDto convertToLocationDetailDto(Location location) {
        LocationDetailDto dto = new LocationDetailDto();
        dto.setId(location.getId());
        dto.setName(location.getName());
        dto.setImageUrl(location.getImageUrl());
        dto.setCoordinate(new CoordinateDto(location.getCoordinate().getY(), location.getCoordinate().getX()));
        if (location.getOwnerFaction() != null) {
            dto.setOwnerFactionId(location.getOwnerFaction().getId());
        }
        return dto;
    }
}