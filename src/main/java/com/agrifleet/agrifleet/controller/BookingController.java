package com.agrifleet.agrifleet.controller;

import com.agrifleet.agrifleet.dto.BookingRequest;
import com.agrifleet.agrifleet.model.Booking;
import com.agrifleet.agrifleet.security.JwtUtil;
import com.agrifleet.agrifleet.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BookingController {

    private final BookingService bookingService;
    private final JwtUtil jwtUtil;

    // Create booking (Farmer)
    @PostMapping("/create")
    public ResponseEntity<?> createBooking(
            @Valid @RequestBody BookingRequest request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            Long farmerId = jwtUtil.extractUserId(token);

            Booking booking = bookingService
                    .createBooking(request, farmerId);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new java.util.HashMap<>() {{
                        put("message",
                                "Booking created successfully!");
                        put("bookingId", booking.getId());
                        put("status", booking.getStatus());
                        put("totalPrice",
                                booking.getTotalPrice());
                        put("pricePerHour",
                                booking.getPricePerHour());
                        put("distanceKm",
                                booking.getDistanceKm());
                    }});
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new java.util.HashMap<>() {{
                        put("error", e.getMessage());
                    }});
        }
    }

    // Update booking status (Owner/Farmer)
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestParam Booking.BookingStatus status,
            @RequestParam(required = false) String notes,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            Long userId = jwtUtil.extractUserId(token);

            Booking booking = bookingService
                    .updateBookingStatus(
                            id, status, userId, notes);

            return ResponseEntity.ok(
                    new java.util.HashMap<>() {{
                        put("message",
                                "Booking status updated!");
                        put("bookingId", booking.getId());
                        put("newStatus", booking.getStatus());
                    }});
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new java.util.HashMap<>() {{
                        put("error", e.getMessage());
                    }});
        }
    }

    // Get farmer's bookings
    @GetMapping("/my-bookings")
    public ResponseEntity<?> getMyBookings(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            Long farmerId = jwtUtil.extractUserId(token);

            List<Booking> bookings = bookingService
                    .getFarmerBookings(farmerId);

            return ResponseEntity.ok(
                    new java.util.HashMap<>() {{
                        put("count", bookings.size());
                        put("bookings", bookings);
                    }});
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new java.util.HashMap<>() {{
                        put("error", e.getMessage());
                    }});
        }
    }

    // Get owner's incoming bookings
    @GetMapping("/incoming")
    public ResponseEntity<?> getIncomingBookings(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            Long ownerId = jwtUtil.extractUserId(token);

            List<Booking> bookings = bookingService
                    .getOwnerBookings(ownerId);

            return ResponseEntity.ok(
                    new java.util.HashMap<>() {{
                        put("count", bookings.size());
                        put("bookings", bookings);
                    }});
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new java.util.HashMap<>() {{
                        put("error", e.getMessage());
                    }});
        }
    }
}