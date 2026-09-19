package com.agrifleet.agrifleet.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "equipment")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Equipment name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Brand is required")
    @Column(nullable = false)
    private String brand;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EquipmentType type;

    @NotBlank(message = "Description is required")
    @Column(nullable = false, length = 1000)
    private String description;

    // Pricing
    @NotNull(message = "Price per hour is required")
    @Positive(message = "Price must be positive")
    @Column(name = "price_per_hour", nullable = false)
    private Double pricePerHour;

    @Column(name = "price_per_acre")
    private Double pricePerAcre;

    // Location fields for geospatial search
    @NotNull(message = "Latitude is required")
    @Column(nullable = false)
    private Double latitude;

    @NotNull(message = "Longitude is required")
    @Column(nullable = false)
    private Double longitude;

    @NotBlank(message = "Village is required")
    @Column(nullable = false)
    private String village;

    @NotBlank(message = "District is required")
    @Column(nullable = false)
    private String district;

    @NotBlank(message = "State is required")
    @Column(nullable = false)
    private String state;

    // Equipment details
    @Column(name = "manufacture_year")
    private Integer manufactureYear;

    @Column(name = "horse_power")
    private Integer horsePower;

    @Column(name = "image_url")
    private String imageUrl;

    // Availability
    @Column(nullable = false)
    private Boolean available = true;

    // Dynamic pricing fields
    @Column(name = "base_price_per_hour", nullable = false)
    private Double basePricePerHour;

    @Column(name = "is_peak_season")
    private Boolean isPeakSeason = false;

    // Optimistic locking — prevents double booking
    @Version
    private Long version;

    // Owner relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        basePricePerHour = pricePerHour;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum EquipmentType {
        TRACTOR,
        HARVESTER,
        ROTAVATOR,
        CULTIVATOR,
        PLOUGH,
        SEEDER,
        SPRAYER,
        THRESHER,
        OTHER
    }
}
