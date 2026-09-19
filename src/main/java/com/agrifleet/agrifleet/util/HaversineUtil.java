package com.agrifleet.agrifleet.util;

import org.springframework.stereotype.Component;

@Component
public class HaversineUtil {

    // Earth's radius in kilometers
    private static final double EARTH_RADIUS_KM = 6371.0;

    /**
     * Calculates distance between two GPS coordinates
     * using the Haversine formula
     *
     * @param lat1 Latitude of point 1
     * @param lng1 Longitude of point 1
     * @param lat2 Latitude of point 2
     * @param lng2 Longitude of point 2
     * @return Distance in kilometers
     */
    public static double calculateDistance(
            double lat1, double lng1,
            double lat2, double lng2) {

        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    /**
     * Checks if equipment is within acceptable radius
     *
     * @param equipmentLat Equipment latitude
     * @param equipmentLng Equipment longitude
     * @param farmerLat    Farmer latitude
     * @param farmerLng    Farmer longitude
     * @param radiusKm     Maximum allowed radius
     * @return true if within radius
     */
    public static boolean isWithinRadius(
            double equipmentLat, double equipmentLng,
            double farmerLat, double farmerLng,
            double radiusKm) {

        double distance = calculateDistance(
                equipmentLat, equipmentLng,
                farmerLat, farmerLng);

        return distance <= radiusKm;
    }
}