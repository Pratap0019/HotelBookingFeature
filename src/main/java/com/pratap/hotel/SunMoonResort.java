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

        // Apply surcharges to base rate
        if (room.hasNatureView()) {
            dailyRate += baseRate * 0.2;
        }
        if (room.hasBalcony()) {
            dailyRate += baseRate * 0.1;
        }

        Map<String, Double> breakdown = new LinkedHashMap<>();

        // Room charge for all days
        double roomCharge = dailyRate * daysStayed;
        breakdown.put("Room Charge", roomCharge);

        // Extras
        double extrasCharge = 0.0;
        if (selectedExtras != null) {
            for (Extras extra : selectedExtras) {
                if (extra == Extras.SPA) {
                    int sessions = (spaSessions != null && spaSessions >= 1) ? spaSessions : 1;
                    double extraCharge = HotelData.EXTRAS_RATE.get(extra) * sessions;
                    breakdown.put(extra.name() + " (" + sessions + " session" + (sessions > 1 ? "s" : "") + ")", extraCharge);
                    extrasCharge += extraCharge;
                } else {
                    double extraCharge = HotelData.EXTRAS_RATE.get(extra) * daysStayed;
                    breakdown.put(extra.name(), extraCharge);
                    extrasCharge += extraCharge;
                }
            }
        }

        // Pet fees
        double petFee = 0.0;
        if (petWeight != null) {
            if (petWeight <= 8) petFee = HotelData.PET_FEE_RATES.get("under8kg");
            else if (petWeight <= 15) petFee = HotelData.PET_FEE_RATES.get("under15kg");
            else petFee = HotelData.PET_FEE_RATES.get("over15kg");
            // Pet fee is applied per stay in this implementation (as per project conventions)
            breakdown.put("Pet Fee", petFee);
        }

        // Subtotal before service charge
        double subtotal = roomCharge + extrasCharge + petFee;

        // Service charge (10% of subtotal)
        double serviceCharge = subtotal * 0.1;
        breakdown.put("Service Charge", serviceCharge);

        double total = subtotal + serviceCharge;

        return new Bill(total, breakdown);
    }



    private static Room getRoomDetails(int roomNumber) {
        return HotelData.ROOMS.stream()
                .filter(r -> r.getRoomNumber() == roomNumber)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Room not found."));
    }
}
