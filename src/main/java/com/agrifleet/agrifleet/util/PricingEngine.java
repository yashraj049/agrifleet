package com.agrifleet.agrifleet.util;

import com.agrifleet.agrifleet.model.Equipment;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;

@Component
public class PricingEngine {

    // Peak season price increase percentage
    private static final double PEAK_SEASON_MULTIPLIER = 1.15; // 15% increase

    // Low availability threshold
    private static final long LOW_AVAILABILITY_THRESHOLD = 3;

    // Low availability price increase
    private static final double LOW_AVAILABILITY_MULTIPLIER = 1.10; // 10% increase

    /**
     * Calculates dynamic price based on:
     * 1. Peak harvest season (Rabi/Kharif)
     * 2. Local equipment availability
     *
     * @param equipment         The equipment being booked
     * @param startTime         Booking start time
     * @param availableCount    Available equipment count in district
     * @return                  Final price per hour
     */
    public double calculateDynamicPrice(
            Equipment equipment,
            LocalDateTime startTime,
            long availableCount) {

        double basePrice = equipment.getBasePricePerHour();
        double finalPrice = basePrice;

        // Check peak harvest season
        if (isPeakSeason(startTime)) {
            finalPrice *= PEAK_SEASON_MULTIPLIER;
            equipment.setIsPeakSeason(true);
        } else {
            equipment.setIsPeakSeason(false);
        }

        // Check local availability
        if (availableCount <= LOW_AVAILABILITY_THRESHOLD) {
            finalPrice *= LOW_AVAILABILITY_MULTIPLIER;
        }

        // Round to 2 decimal places
        return Math.round(finalPrice * 100.0) / 100.0;
    }

    /**
     * Checks if booking is during peak harvest season
     * Kharif: June - October (Paddy, Cotton, Soybean)
     * Rabi:   October - March (Wheat, Gram, Mustard)
     *
     * @param dateTime Booking date
     * @return true if peak season
     */
    public boolean isPeakSeason(LocalDateTime dateTime) {
        Month month = dateTime.getMonth();
        return month == Month.JUNE
                || month == Month.JULY
                || month == Month.AUGUST
                || month == Month.SEPTEMBER
                || month == Month.OCTOBER
                || month == Month.NOVEMBER
                || month == Month.FEBRUARY
                || month == Month.MARCH;
    }

    /**
     * Calculates total booking price
     *
     * @param pricePerHour  Price per hour
     * @param startTime     Booking start time
     * @param endTime       Booking end time
     * @return              Total price
     */
    public double calculateTotalPrice(
            double pricePerHour,
            LocalDateTime startTime,
            LocalDateTime endTime) {

        long minutes = ChronoUnit.MINUTES.between(startTime, endTime);
        double hours = minutes / 60.0;
        double total = pricePerHour * hours;

        // Round to 2 decimal places
        return Math.round(total * 100.0) / 100.0;
    }
}