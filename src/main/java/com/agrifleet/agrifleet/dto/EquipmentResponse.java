package com.agrifleet.agrifleet.dto;

import com.agrifleet.agrifleet.model.Equipment;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EquipmentResponse {

    private Long id;
    private String name;
    private String brand;
    private Equipment.EquipmentType type;
    private String description;
    private Double pricePerHour;
    private Double pricePerAcre;
    private Double latitude;
    private Double longitude;
    private String village;
    private String district;
    private String state;
    private Integer manufactureYear;
    private Integer horsePower;
    private String imageUrl;
    private Boolean available;
    private Boolean isPeakSeason;
    private Double distanceKm; // distance from search location
    private String ownerName;
    private String ownerPhone;
    private LocalDateTime createdAt;
}