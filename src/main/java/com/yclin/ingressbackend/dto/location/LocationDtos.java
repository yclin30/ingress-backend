package com.yclin.ingressbackend.dto.location;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class LocationDtos {

    // 响应: GET /locations
    @Getter
    @Setter
    public static class LocationDetailDto {
        private Long id;
        private String name;
        @JsonProperty("image_url")
        private String imageUrl;
        private CoordinateDto coordinate;
        @JsonProperty("owner_faction_id")
        private Long ownerFactionId;
    }

    // 辅助: 坐标
    @Getter
    @Setter
    @AllArgsConstructor
    public static class CoordinateDto {
        private double latitude;
        private double longitude;
    }

    // 请求: POST /locations/{id}/resonators
    @Getter
    @Setter
    public static class ResonatorDeployRequest {
        @JsonProperty("slot_number")
        private int slotNumber;
        private int level;
    }

    // 响应: POST /locations/{id}/resonators
    @Getter
    @Setter
    public static class ResonatorDetailDto {
        private Long id;
        private int level;
        private int health;
        @JsonProperty("slot_number")
        private int slotNumber;
        @JsonProperty("location_id")
        private Long locationId;
        @JsonProperty("deployer_user_id")
        private Long deployerUserId;
    }
}