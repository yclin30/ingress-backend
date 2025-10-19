package com.yclin.ingressbackend.util;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Component;

@Component
public class GeometryUtil {
    
    private static final GeometryFactory geometryFactory = 
        new GeometryFactory(new PrecisionModel(), 4326);
    
    // 创建Point对象
    public static Point createPoint(double longitude, double latitude) {
        Coordinate coordinate = new Coordinate(longitude, latitude);
        Point point = geometryFactory.createPoint(coordinate);
        point.setSRID(4326);
        return point;
    }
    
    // 计算两点间距离（米）
    public static double calculateDistance(Point point1, Point point2) {
        // 使用Haversine公式计算大圆距离
        double lat1 = Math.toRadians(point1.getY());
        double lon1 = Math.toRadians(point1.getX());
        double lat2 = Math.toRadians(point2.getY());
        double lon2 = Math.toRadians(point2.getX());
        
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        
        double a = Math.pow(Math.sin(dlat / 2), 2)
                 + Math.cos(lat1) * Math.cos(lat2)
                 * Math.pow(Math.sin(dlon / 2), 2);
        
        double c = 2 * Math.asin(Math.sqrt(a));
        
        // 地球半径（米）
        double r = 6371000;
        
        return c * r;
    }
}