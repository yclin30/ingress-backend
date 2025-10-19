package com.yclin.ingressbackend.service;

import com.yclin.ingressbackend.dto.location.LocationDtos.*;
import com.yclin.ingressbackend.entity.domain.Location;
import com.yclin.ingressbackend.entity.domain.Resonator;
import com.yclin.ingressbackend.entity.domain.User;
import com.yclin.ingressbackend.repository.LocationRepository;
import com.yclin.ingressbackend.repository.ResonatorRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;
    private final ResonatorRepository resonatorRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory();

    @Transactional(readOnly = true)
    public List<LocationDetailDto> getLocationsInBounds(double minLat, double minLon, double maxLat, double maxLon) {
        Coordinate[] coords = new Coordinate[]{
                new Coordinate(minLon, minLat),
                new Coordinate(maxLon, minLat),
                new Coordinate(maxLon, maxLat),
                new Coordinate(minLon, maxLat),
                new Coordinate(minLon, minLat) // 闭合环
        };
        Polygon boundingBox = geometryFactory.createPolygon(coords);

        // **核心修正**:
        // 为我们创建的查询多边形设置正确的 SRID (Spatial Reference System Identifier)。
        // 必须与数据库中存储的点的 SRID (4326) 保持一致。
        boundingBox.setSRID(4326);

        List<Location> locations = locationRepository.findLocationsWithin(boundingBox);

        return locations.stream()
                .map(this::convertToLocationDetailDto)
                .collect(Collectors.toList());
    }

    // deployResonator 和 DTO 转换方法保持不变
    @Transactional
    public ResonatorDetailDto deployResonator(Long locationId, ResonatorDeployRequest request, User currentUser) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new IllegalArgumentException("Location not found with id: " + locationId));

        Resonator resonator = Resonator.builder()
                .location(location)
                .deployer(currentUser)
                .level(request.getLevel())
                .slotNumber(request.getSlotNumber())
                .build();

        Resonator savedResonator = resonatorRepository.save(resonator);
        return convertToResonatorDetailDto(savedResonator);
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

    private ResonatorDetailDto convertToResonatorDetailDto(Resonator resonator) {
        ResonatorDetailDto dto = new ResonatorDetailDto();
        dto.setId( resonator.getId() );
        dto.setLevel( resonator.getLevel() );
        dto.setHealth( resonator.getHealth() );
        dto.setSlotNumber( resonator.getSlotNumber() );
        dto.setLocationId( resonator.getLocation().getId() );
        dto.setDeployerUserId( resonator.getDeployer().getId() );
        return dto;
    }
}