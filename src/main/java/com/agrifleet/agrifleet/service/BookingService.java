package com.agrifleet.agrifleet.service;

import com.agrifleet.agrifleet.dto.BookingRequest;
import com.agrifleet.agrifleet.model.Booking;
import com.agrifleet.agrifleet.model.Equipment;
import com.agrifleet.agrifleet.model.User;
import com.agrifleet.agrifleet.repository.BookingRepository;
import com.agrifleet.agrifleet.repository.EquipmentRepository;
import com.agrifleet.agrifleet.repository.UserRepository;
import com.agrifleet.agrifleet.util.HaversineUtil;
import com.agrifleet.agrifleet.util.PricingEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final EquipmentRepository equipmentRepository;
    private final UserRepository userRepository;
    private final PricingEngine pricingEngine;

    // Create new booking
    @Transactional
    public Booking createBooking(BookingRequest request,
                                 Long farmerId) {

        // Get farmer
        User farmer = userRepository.findById(farmerId)
                .orElseThrow(() ->
                        new RuntimeException("Farmer not found!"));

        // Get equipment
        Equipment equipment = equipmentRepository
                .findById(request.getEquipmentId())
                .orElseThrow(() ->
                        new RuntimeException("Equipment not found!"));

        // Check if equipment is available
        if (!equipment.getAvailable()) {
            throw new RuntimeException(
                    "Equipment is not available!");
        }

        // Double booking check
        Boolean hasOverlap = bookingRepository
                .existsOverlappingBooking(
                        equipment.getId(),
                        request.getStartTime(),
                        request.getEndTime());

        if (hasOverlap) {
            throw new RuntimeException(
                    "Equipment already booked for this time slot!");
        }

        // Calculate distance
        double distanceKm = HaversineUtil.calculateDistance(
                equipment.getLatitude(),
                equipment.getLongitude(),
                request.getUsageLatitude(),
                request.getUsageLongitude());

        // Get available equipment count for dynamic pricing
        long availableCount = equipmentRepository
                .countByDistrictAndAvailableTrue(
                        equipment.getDistrict());

        // Calculate dynamic price
        double dynamicPrice = pricingEngine.calculateDynamicPrice(
                equipment,
                request.getStartTime(),
                availableCount);

        // Calculate total price
        double totalPrice = pricingEngine.calculateTotalPrice(
                dynamicPrice,
                request.getStartTime(),
                request.getEndTime());

        // Create booking
        Booking booking = new Booking();
        booking.setFarmer(farmer);
        booking.setEquipment(equipment);
        booking.setStartTime(request.getStartTime());
        booking.setEndTime(request.getEndTime());
        booking.setPricePerHour(dynamicPrice);
        booking.setTotalPrice(totalPrice);
        booking.setDistanceKm(distanceKm);
        booking.setUsageLatitude(request.getUsageLatitude());
        booking.setUsageLongitude(request.getUsageLongitude());
        booking.setUsageVillage(request.getUsageVillage());
        booking.setUsageDistrict(request.getUsageDistrict());
        booking.setAreaAcres(request.getAreaAcres());
        booking.setFarmerNotes(request.getFarmerNotes());
        booking.setStatus(Booking.BookingStatus.REQUESTED);

        return bookingRepository.save(booking);
    }

    // Update booking status (state machine)
    @Transactional
    public Booking updateBookingStatus(Long bookingId,
                                       Booking.BookingStatus newStatus,
                                       Long userId,
                                       String notes) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new RuntimeException("Booking not found!"));

        // Validate state transitions
        validateStatusTransition(
                booking.getStatus(), newStatus);

        booking.setStatus(newStatus);

        if (newStatus == Booking.BookingStatus.APPROVED) {
            booking.setApprovedAt(LocalDateTime.now());
        }
        if (newStatus == Booking.BookingStatus.COMPLETED) {
            booking.setCompletedAt(LocalDateTime.now());
            booking.getEquipment().setAvailable(true);
            equipmentRepository.save(booking.getEquipment());
        }
        if (newStatus == Booking.BookingStatus.REJECTED) {
            booking.setRejectionReason(notes);
        }
        if (newStatus == Booking.BookingStatus.ACTIVE) {
            booking.getEquipment().setAvailable(false);
            equipmentRepository.save(booking.getEquipment());
        }
        if (notes != null && newStatus
                != Booking.BookingStatus.REJECTED) {
            booking.setOwnerNotes(notes);
        }

        return bookingRepository.save(booking);
    }

    // Get farmer bookings
    public List<Booking> getFarmerBookings(Long farmerId) {
        return bookingRepository.findByFarmerId(farmerId);
    }

    // Get owner bookings
    public List<Booking> getOwnerBookings(Long ownerId) {
        return bookingRepository.findByOwnerId(ownerId);
    }

    // Validate booking state machine transitions
    private void validateStatusTransition(
            Booking.BookingStatus current,
            Booking.BookingStatus next) {

        boolean valid = switch (current) {
            case REQUESTED -> next == Booking.BookingStatus.APPROVED
                    || next == Booking.BookingStatus.REJECTED;
            case APPROVED -> next == Booking.BookingStatus.IN_TRANSIT
                    || next == Booking.BookingStatus.CANCELLED;
            case IN_TRANSIT -> next == Booking.BookingStatus.ACTIVE;
            case ACTIVE -> next == Booking.BookingStatus.COMPLETED
                    || next == Booking.BookingStatus.CANCELLED;
            default -> false;
        };

        if (!valid) {
            throw new RuntimeException(
                    "Invalid status transition from "
                            + current + " to " + next);
        }
    }
}