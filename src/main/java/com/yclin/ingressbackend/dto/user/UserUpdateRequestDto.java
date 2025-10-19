package com.yclin.ingressbackend.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yclin.ingressbackend.entity.enums.UserGender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequestDto {
    private UserGender gender;
    private String introduction;

    @JsonProperty("faction_id")
    private Long factionId;
}