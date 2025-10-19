package com.yclin.ingressbackend.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yclin.ingressbackend.entity.enums.UserRole;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;
import java.util.List;

public class AdminDtos {

    @Getter
    @Setter
    public static class UserPaginatedResponse {
        private List<UserSummaryDto> users;
        @JsonProperty("current_page")
        private int currentPage;
        @JsonProperty("total_pages")
        private int totalPages;
        @JsonProperty("total_elements")
        private long totalElements;
    }

    @Getter
    @Setter
    public static class UserSummaryDto {
        private Long id;
        private String username;
        private String email;
        private UserRole role;
        @JsonProperty("is_banned")
        private Boolean isBanned;
        @JsonProperty("created_at")
        private Instant createdAt;
    }
    
    @Getter
    @Setter
    public static class LocationCreateRequest {
        private String name;
        private double latitude;
        private double longitude;
    }

    @Getter
    @Setter
    public static class AnnouncementCreateRequest {
        private String content;
    }
}