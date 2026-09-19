package com.agrifleet.agrifleet.repository;

import com.agrifleet.agrifleet.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Find all bookings by farmer
    List<Booking> findByFarmerId(Long farmerId);

    // Find all bookings by equipment
    List<Booking> findByEquipmentId(Long equipmentId);

    // Double booking check — checks if equipment is already booked
    // for the requested time slot
    @Query("""
            SELECT COUNT(b) > 0 FROM Booking b
            WHERE b.equipment.id = :equipmentId
            AND b.status IN ('REQUESTED', 'APPROVED', 'IN_TRANSIT', 'ACTIVE')
            AND (
                (b.startTime <= :endTime AND b.endTime >= :startTime)
            )
            """)
    Boolean existsOverlappingBooking(
            @Param("equipmentId") Long equipmentId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    // Find bookings by owner (through equipment)
    @Query("""
            SELECT b FROM Booking b
            WHERE b.equipment.owner.id = :ownerId
            ORDER BY b.createdAt DESC
            """)
    List<Booking> findByOwnerId(@Param("ownerId") Long ownerId);

    // Find active bookings for equipment
    @Query("""
            SELECT b FROM Booking b
            WHERE b.equipment.id = :equipmentId
            AND b.status IN ('APPROVED', 'IN_TRANSIT', 'ACTIVE')
            """)
    List<Booking> findActiveBookingsByEquipmentId(
            @Param("equipmentId") Long equipmentId
    );
}