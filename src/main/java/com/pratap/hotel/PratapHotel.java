package com.pratap.hotel;

import com.pratap.hotel.model.*;

import java.util.*;

public class PratapHotel {

    /**
     * Calculate bill with user-selected extras and optional pet weight
     */
    public static Bill calculateBill(int roomNumber, int daysStayed, List<Extras> selectedExtras, Double petWeight) {
        Room room = getRoomDetails(roomNumber);

        double baseRate = HotelData.ROOM_RATES.get(room.getRoomType());
        double dailyRate = baseRate;

        // Apply surcharges to base rate
        if (room.hasSeaView()) {
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
                double extraCharge = HotelData.EXTRAS_RATE.get(extra) * daysStayed;
                breakdown.put(extra.name(), extraCharge);
                extrasCharge += extraCharge;
            }
        }

        // Pet fees
        double petFee = 0.0;
        if (petWeight != null) {
            if (petWeight <= 8) petFee = HotelData.PET_FEE_RATES.get("under8kg");
            else if (petWeight <= 15) petFee = HotelData.PET_FEE_RATES.get("under15kg");
            else petFee = HotelData.PET_FEE_RATES.get("over15kg");
            breakdown.put("Pet Fee", petFee * daysStayed);
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
