package com.agrifleet.agrifleet.dto;

import com.agrifleet.agrifleet.model.Equipment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class EquipmentRequest {

    @NotBlank(message = "Equipment name is required")
    private String name;

    @NotBlank(message = "Brand is required")
    private String brand;

    @NotNull(message = "Type is required")
    private Equipment.EquipmentType type;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Price per hour is required")
    @Positive(message = "Price must be positive")
    private Double pricePerHour;

    private Double pricePerAcre;

    @NotNull(message = "Latitude is required")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    private Double longitude;

    @NotBlank(message = "Village is required")
    private String village;

    @NotBlank(message = "District is required")
    private String district;

    @NotBlank(message = "State is required")
    private String state;

    private Integer manufactureYear;
    private Integer horsePower;
    private String imageUrl;
}