package com.agrifleet.agrifleet.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Booking State Machine
    // REQUESTED → APPROVED → IN_TRANSIT → ACTIVE → COMPLETED
    // REQUESTED → REJECTED
    // ACTIVE → CANCELLED
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status = BookingStatus.REQUESTED;

    // Timing
    @NotNull(message = "Start time is required")
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    // Pricing
    @Column(name = "total_price", nullable = false)
    private Double totalPrice;

    @Column(name = "price_per_hour")
    private Double pricePerHour;

    @Column(name = "total_hours")
    private Double totalHours;

    // Location of use
    @Column(name = "usage_village")
    private String usageVillage;

    @Column(name = "usage_district")
    private String usageDistrict;

    @Column(name = "usage_latitude")
    private Double usageLatitude;

    @Column(name = "usage_longitude")
    private Double usageLongitude;

    // Distance from equipment to usage location (km)
    @Column(name = "distance_km")
    private Double distanceKm;

    // Area to cover (for per-acre pricing)
    @Column(name = "area_acres")
    private Double areaAcres;

    // Notes
    @Column(name = "farmer_notes", length = 500)
    private String farmerNotes;

    @Column(name = "owner_notes", length = 500)
    private String ownerNotes;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    // Optimistic locking — prevents double booking
    @Version
    private Long version;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmer_id", nullable = false)
    private User farmer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment;

    // Timestamps
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum BookingStatus {
        REQUESTED,    // Farmer requested
        APPROVED,     // Owner approved
        IN_TRANSIT,   // Equipment moving to farm
        ACTIVE,       // Currently in use
        COMPLETED,    // Work done
        REJECTED,     // Owner rejected
        CANCELLED     // Farmer cancelled
    }
}
