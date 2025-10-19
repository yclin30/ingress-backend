package com.yclin.ingressbackend.repository;

import com.yclin.ingressbackend.entity.domain.Location;
import org.locationtech.jts.geom.Polygon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Long> {

    @Query(value = "SELECT l FROM Location l WHERE ST_Contains(:boundingBox, l.coordinate) = true")
    List<Location> findLocationsWithin(@Param("boundingBox") Polygon boundingBox);
}