package com.yclin.ingressbackend.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yclin.ingressbackend.entity.enums.UserGender;
import com.yclin.ingressbackend.entity.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant; // 确认 import 的是 Instant

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailDto {
    private Long id;
    private String username;
    private String email;
    private UserRole role;
    private UserGender gender;
    private String introduction;
    private Integer level;
    private Long experience;

    // **关键点**: 使用 @JsonProperty 来确保 JSON 输出的字段名为 is_banned
    @JsonProperty("is_banned")
    private Boolean isBanned;

    private FactionDto faction;

    // **关键点**: 使用 @JsonProperty 来确保 JSON 输出的字段名为 created_at
    @JsonProperty("created_at")
    private Instant createdAt;
}