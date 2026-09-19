package com.agrifleet.agrifleet.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BookingRequest {

    @NotNull(message = "Equipment ID is required")
    private Long equipmentId;

    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    private LocalDateTime endTime;

    @NotNull(message = "Usage latitude is required")
    private Double usageLatitude;

    @NotNull(message = "Usage longitude is required")
    private Double usageLongitude;

    private String usageVillage;
    private String usageDistrict;
    private Double areaAcres;
    private String farmerNotes;
}