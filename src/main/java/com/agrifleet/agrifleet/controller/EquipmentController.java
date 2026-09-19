package com.agrifleet.agrifleet.controller;

import com.agrifleet.agrifleet.dto.EquipmentRequest;
import com.agrifleet.agrifleet.dto.EquipmentResponse;
import com.agrifleet.agrifleet.model.Equipment;
import com.agrifleet.agrifleet.security.JwtUtil;
import com.agrifleet.agrifleet.service.EquipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/equipment")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EquipmentController {

    private final EquipmentService equipmentService;
    private final JwtUtil jwtUtil;

    // Add new equipment (Owner only)
    @PostMapping("/add")
    public ResponseEntity<?> addEquipment(
            @Valid @RequestBody EquipmentRequest request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            Long ownerId = jwtUtil.extractUserId(token);

            Equipment equipment = equipmentService
                    .addEquipment(request, ownerId);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new java.util.HashMap<>() {{
                        put("message",
                                "Equipment added successfully!");
                        put("equipmentId", equipment.getId());
                        put("name", equipment.getName());
                    }});
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new java.util.HashMap<>() {{
                        put("error", e.getMessage());
                    }});
        }
    }

    // Search nearby equipment
    @GetMapping("/search")
    public ResponseEntity<?> searchNearby(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "25") Double radiusKm,
            @RequestParam(required = false) String type) {
        try {
            List<EquipmentResponse> equipment =
                    equipmentService.searchNearby(
                            latitude, longitude,
                            radiusKm, type);

            return ResponseEntity.ok(
                    new java.util.HashMap<>() {{
                        put("count", equipment.size());
                        put("equipment", equipment);
                        put("radiusKm", radiusKm);
                    }});
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new java.util.HashMap<>() {{
                        put("error", e.getMessage());
                    }});
        }
    }

    // Get owner's equipment list
    @GetMapping("/my-equipment")
    public ResponseEntity<?> getMyEquipment(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            Long ownerId = jwtUtil.extractUserId(token);

            List<EquipmentResponse> equipment =
                    equipmentService.getOwnerEquipment(ownerId);

            return ResponseEntity.ok(equipment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new java.util.HashMap<>() {{
                        put("error", e.getMessage());
                    }});
        }
    }

    // Update availability
    @PutMapping("/{id}/availability")
    public ResponseEntity<?> updateAvailability(
            @PathVariable Long id,
            @RequestParam Boolean available,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            Long ownerId = jwtUtil.extractUserId(token);

            equipmentService.updateAvailability(
                    id, available, ownerId);

            return ResponseEntity.ok(
                    new java.util.HashMap<>() {{
                        put("message",
                                "Availability updated!");
                        put("available", available);
                    }});
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new java.util.HashMap<>() {{
                        put("error", e.getMessage());
                    }});
        }
    }

    // Get dynamic price
    @GetMapping("/{id}/price")
    public ResponseEntity<?> getDynamicPrice(
            @PathVariable Long id,
            @RequestParam(required = false)
            String startTime) {
        try {
            LocalDateTime time = startTime != null
                    ? LocalDateTime.parse(startTime)
                    : LocalDateTime.now();

            double price = equipmentService
                    .getDynamicPrice(id, time);

            return ResponseEntity.ok(
                    new java.util.HashMap<>() {{
                        put("equipmentId", id);
                        put("pricePerHour", price);
                        put("startTime", time.toString());
                    }});
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new java.util.HashMap<>() {{
                        put("error", e.getMessage());
                    }});
        }
    }
}