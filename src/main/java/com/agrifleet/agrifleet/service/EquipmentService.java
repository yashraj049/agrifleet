package com.agrifleet.agrifleet.service;

import com.agrifleet.agrifleet.dto.EquipmentRequest;
import com.agrifleet.agrifleet.dto.EquipmentResponse;
import com.agrifleet.agrifleet.model.Equipment;
import com.agrifleet.agrifleet.model.User;
import com.agrifleet.agrifleet.repository.EquipmentRepository;
import com.agrifleet.agrifleet.repository.UserRepository;
import com.agrifleet.agrifleet.util.HaversineUtil;
import com.agrifleet.agrifleet.util.PricingEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final UserRepository userRepository;
    private final PricingEngine pricingEngine;

    // Add new equipment
    @Transactional
    public Equipment addEquipment(EquipmentRequest request,
                                  Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() ->
                        new RuntimeException("Owner not found!"));

        Equipment equipment = new Equipment();
        equipment.setName(request.getName());
        equipment.setBrand(request.getBrand());
        equipment.setType(request.getType());
        equipment.setDescription(request.getDescription());
        equipment.setPricePerHour(request.getPricePerHour());
        equipment.setPricePerAcre(request.getPricePerAcre());
        equipment.setLatitude(request.getLatitude());
        equipment.setLongitude(request.getLongitude());
        equipment.setVillage(request.getVillage());
        equipment.setDistrict(request.getDistrict());
        equipment.setState(request.getState());
        equipment.setManufactureYear(request.getManufactureYear());
        equipment.setHorsePower(request.getHorsePower());
        equipment.setImageUrl(request.getImageUrl());
        equipment.setOwner(owner);
        equipment.setAvailable(true);

        return equipmentRepository.save(equipment);
    }

    // Search equipment within radius
    public List<EquipmentResponse> searchNearby(
            Double latitude,
            Double longitude,
            Double radiusKm,
            String type) {

        List<Equipment> equipmentList;

        if (type != null && !type.isEmpty()) {
            equipmentList = equipmentRepository
                    .findEquipmentByTypeWithinRadius(
                            latitude, longitude,
                            radiusKm, type);
        } else {
            equipmentList = equipmentRepository
                    .findEquipmentWithinRadius(
                            latitude, longitude, radiusKm);
        }

        return equipmentList.stream()
                .map(e -> mapToResponse(e, latitude, longitude))
                .collect(Collectors.toList());
    }

    // Get all equipment by owner
    public List<EquipmentResponse> getOwnerEquipment(Long ownerId) {
        return equipmentRepository.findByOwnerId(ownerId)
                .stream()
                .map(e -> mapToResponse(e, null, null))
                .collect(Collectors.toList());
    }

    // Update equipment availability
    @Transactional
    public Equipment updateAvailability(Long equipmentId,
                                        Boolean available,
                                        Long ownerId) {
        Equipment equipment = equipmentRepository
                .findById(equipmentId)
                .orElseThrow(() ->
                        new RuntimeException("Equipment not found!"));

        if (!equipment.getOwner().getId().equals(ownerId)) {
            throw new RuntimeException(
                    "You are not the owner of this equipment!");
        }

        equipment.setAvailable(available);
        return equipmentRepository.save(equipment);
    }

    // Apply dynamic pricing
    @Transactional
    public double getDynamicPrice(Long equipmentId,
                                  LocalDateTime startTime) {
        Equipment equipment = equipmentRepository
                .findById(equipmentId)
                .orElseThrow(() ->
                        new RuntimeException("Equipment not found!"));

        long availableCount = equipmentRepository
                .countByDistrictAndAvailableTrue(
                        equipment.getDistrict());

        double dynamicPrice = pricingEngine.calculateDynamicPrice(
                equipment, startTime, availableCount);

        equipment.setPricePerHour(dynamicPrice);
        equipmentRepository.save(equipment);

        return dynamicPrice;
    }

    // Map Equipment to EquipmentResponse
    private EquipmentResponse mapToResponse(
            Equipment equipment,
            Double searchLat,
            Double searchLng) {

        EquipmentResponse response = new EquipmentResponse();
        response.setId(equipment.getId());
        response.setName(equipment.getName());
        response.setBrand(equipment.getBrand());
        response.setType(equipment.getType());
        response.setDescription(equipment.getDescription());
        response.setPricePerHour(equipment.getPricePerHour());
        response.setPricePerAcre(equipment.getPricePerAcre());
        response.setLatitude(equipment.getLatitude());
        response.setLongitude(equipment.getLongitude());
        response.setVillage(equipment.getVillage());
        response.setDistrict(equipment.getDistrict());
        response.setState(equipment.getState());
        response.setManufactureYear(equipment.getManufactureYear());
        response.setHorsePower(equipment.getHorsePower());
        response.setImageUrl(equipment.getImageUrl());
        response.setAvailable(equipment.getAvailable());
        response.setIsPeakSeason(equipment.getIsPeakSeason());
        response.setOwnerName(equipment.getOwner().getName());
        response.setOwnerPhone(equipment.getOwner().getPhone());
        response.setCreatedAt(equipment.getCreatedAt());

        // Calculate distance if search location provided
        if (searchLat != null && searchLng != null) {
            double distance = HaversineUtil.calculateDistance(
                    searchLat, searchLng,
                    equipment.getLatitude(),
                    equipment.getLongitude());
            response.setDistanceKm(
                    Math.round(distance * 10.0) / 10.0);
        }

        return response;
    }
}