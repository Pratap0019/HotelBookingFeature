package com.pratap.hotel;

import com.pratap.hotel.model.*;

import java.util.*;

public class SunMoonResort {

    /**
     * Calculate bill with user-selected extras and optional pet weight
     */
    public static Bill calculateBill(int roomNumber, int daysStayed, List<Extras> selectedExtras, Double petWeight) {
        // Delegate to the extended method with null spaSessions for backward compatibility
        return calculateBill(roomNumber, daysStayed, selectedExtras, petWeight, null);
    }

    /**
     * Extended calculateBill which accepts spaSessions (1-3). If spaSessions is null and SPA is selected,
     * it defaults to 1 session.
     */
    public static Bill calculateBill(int roomNumber, int daysStayed, List<Extras> selectedExtras, Double petWeight, Integer spaSessions) {
        Room room = getRoomDetails(roomNumber);

        double baseRate = HotelData.ROOM_RATES.get(room.getRoomType());
        double dailyRate = baseRate;
        // No surcharges — flat rate per room type

        Map<String, Double> breakdown = new LinkedHashMap<>();
        Map<String, String> calculationDetails = new LinkedHashMap<>();

        // Room charge for all days
        double roomCharge = dailyRate * daysStayed;
        breakdown.put("Room Charge", roomCharge);
        calculationDetails.put("Room Charge", String.format("₹%.2f/day × %d days", dailyRate, daysStayed));

        // Extras
        double extrasCharge = 0.0;
        if (selectedExtras != null) {
            for (Extras extra : selectedExtras) {
                if (extra == Extras.SPA) {
                    int sessions = (spaSessions != null && spaSessions >= 1) ? spaSessions : 1;
                    double ratePerSession = HotelData.EXTRAS_RATE.get(extra);
                    double extraCharge = ratePerSession * sessions;
                    String key = extra.getDisplayName() + " (" + sessions + " session" + (sessions > 1 ? "s" : "") + ")";
                    breakdown.put(key, extraCharge);
                    calculationDetails.put(key, String.format("₹%.2f × %d session%s", ratePerSession, sessions, sessions > 1 ? "s" : ""));
                    extrasCharge += extraCharge;
                } else {
                    double ratePerDay = HotelData.EXTRAS_RATE.get(extra);
                    double extraCharge = ratePerDay * daysStayed;
                    breakdown.put(extra.getDisplayName(), extraCharge);
                    calculationDetails.put(extra.getDisplayName(), String.format("₹%.2f/day × %d days", ratePerDay, daysStayed));
                    extrasCharge += extraCharge;
                }
            }
        }

        // Pet fees
        double petFee = 0.0;
        if (petWeight != null) {
            if (petWeight <= 8) petFee = HotelData.PET_FEE_RATES.get("Small (below 8kg)");
            else if (petWeight <= 15) petFee = HotelData.PET_FEE_RATES.get("Medium (below 15kg)");
            else petFee = HotelData.PET_FEE_RATES.get("Large (above 15kg)");
            // Pet fee is applied per stay in this implementation (as per project conventions)
            breakdown.put("Pet Fee", petFee);
            calculationDetails.put("Pet Fee", String.format("₹%.2f (one-time charge)", petFee));
        }

        // Subtotal before service charge
        double subtotal = roomCharge + extrasCharge + petFee;

        // Service charge (10% of subtotal)
        double serviceCharge = subtotal * 0.1;
        breakdown.put("Service Charge", serviceCharge);
        calculationDetails.put("Service Charge", String.format("10%% of subtotal = ₹%.2f", serviceCharge));

        double total = subtotal + serviceCharge;

        return new Bill(total, breakdown, calculationDetails);
    }



    private static Room getRoomDetails(int roomNumber) {
        return HotelData.ROOMS.stream()
                .filter(r -> r.getRoomNumber() == roomNumber)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Room not found."));
    }
}
