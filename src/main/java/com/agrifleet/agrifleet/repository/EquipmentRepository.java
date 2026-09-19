package com.agrifleet.agrifleet.repository;

import com.agrifleet.agrifleet.model.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    // Haversine formula — finds equipment within radius (km)
    @Query(value = """
            SELECT e.* FROM equipment e
            WHERE e.available = true
            AND (6371 * acos(
                cos(radians(:lat)) * cos(radians(e.latitude))
                * cos(radians(e.longitude) - radians(:lng))
                + sin(radians(:lat)) * sin(radians(e.latitude))
            )) <= :radiusKm
            ORDER BY (6371 * acos(
                cos(radians(:lat)) * cos(radians(e.latitude))
                * cos(radians(e.longitude) - radians(:lng))
                + sin(radians(:lat)) * sin(radians(e.latitude))
            )) ASC
            """, nativeQuery = true)
    List<Equipment> findEquipmentWithinRadius(
            @Param("lat") Double latitude,
            @Param("lng") Double longitude,
            @Param("radiusKm") Double radiusKm
    );

    // Find by type within radius
    @Query(value = """
            SELECT e.* FROM equipment e
            WHERE e.available = true
            AND e.type = :type
            AND (6371 * acos(
                cos(radians(:lat)) * cos(radians(e.latitude))
                * cos(radians(e.longitude) - radians(:lng))
                + sin(radians(:lat)) * sin(radians(e.latitude))
            )) <= :radiusKm
            ORDER BY (6371 * acos(
                cos(radians(:lat)) * cos(radians(e.latitude))
                * cos(radians(e.longitude) - radians(:lng))
                + sin(radians(:lat)) * sin(radians(e.latitude))
            )) ASC
            """, nativeQuery = true)
    List<Equipment> findEquipmentByTypeWithinRadius(
            @Param("lat") Double latitude,
            @Param("lng") Double longitude,
            @Param("radiusKm") Double radiusKm,
            @Param("type") String type
    );

    // Find all equipment by owner
    List<Equipment> findByOwnerId(Long ownerId);

    // Count available equipment in district
    Long countByDistrictAndAvailableTrue(String district);
}