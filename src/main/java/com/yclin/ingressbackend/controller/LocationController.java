package com.yclin.ingressbackend.controller;

import com.yclin.ingressbackend.config.SecurityUser;
import com.yclin.ingressbackend.dto.ApiResponse;
import com.yclin.ingressbackend.dto.location.LocationDtos.*;
import com.yclin.ingressbackend.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<LocationDetailDto>>> getLocations(
            @RequestParam double minLat,
            @RequestParam double minLon,
            @RequestParam double maxLat,
            @RequestParam double maxLon
    ) {
        List<LocationDetailDto> locations = locationService.getLocationsInBounds(minLat, minLon, maxLat, maxLon);
        return ResponseEntity.ok(ApiResponse.success(locations));
    }

    @PostMapping("/{locationId}/resonators")
    public ResponseEntity<ApiResponse<ResonatorDetailDto>> deployResonator(
            @PathVariable Long locationId,
            @RequestBody ResonatorDeployRequest request,
            @AuthenticationPrincipal SecurityUser securityUser
    ) {
        ResonatorDetailDto deployedResonator = locationService.deployResonator(
                locationId,
                request,
                securityUser.getUser()
        );
        return new ResponseEntity<>(ApiResponse.created(deployedResonator), HttpStatus.CREATED);
    }
}