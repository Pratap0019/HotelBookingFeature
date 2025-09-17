package com.pratap.hotel;

import com.pratap.hotel.model.*;

import java.util.*;

public class Hotel {

    /**
     * Calculate bill with user-selected extras and optional pet weight
     */
    public static Bill calculateBill(int roomNumber, int daysStayed, List<Extras> selectedExtras, Double petWeight) {
        Room room = getRoomDetails(roomNumber);

        double dailyRate = HotelData.ROOM_RATES.get(room.getRoomType());

        // Sea view surcharge
        if (room.hasSeaView()) {
            dailyRate += dailyRate * 0.2; // 20% surcharge
        }

        Map<String, Double> breakdown = new LinkedHashMap<>();

        // Room charge before extras
        double roomCharge = dailyRate * daysStayed;
        breakdown.put("Room Charge", roomCharge);

        double total = roomCharge;

        // Extras
        if (selectedExtras != null) {
            for (Extras extra : selectedExtras) {
                double extraCharge = HotelData.EXTRAS_RATE.get(extra) * daysStayed;
                breakdown.put(extra.name(), extraCharge);
                total += extraCharge;
            }
        }

        // Pet fees
        if (petWeight != null) {
            double petFee;
            if (petWeight <= 5) petFee = HotelData.PET_FEE_RATES.get("under5kg");
            else if (petWeight <= 10) petFee = HotelData.PET_FEE_RATES.get("under10kg");
            else petFee = HotelData.PET_FEE_RATES.get("over10kg");

            breakdown.put("Pet Fee", petFee);
            total += petFee;
        }

        // Service charge
        double serviceCharge = total * 0.1; // 10%
        breakdown.put("Service Charge", serviceCharge);
        total += serviceCharge;

        return new Bill(total, breakdown);
    }

    /**
     * Overloaded method for backward compatibility (uses room default extras and pet)
     */
    public static Bill calculateBill(int roomNumber, int daysStayed) {
        Room room = getRoomDetails(roomNumber);

        List<Extras> extras = room.getExtras();
        Double petWeight = Double.valueOf(room.getPet() != null ? room.getPet().getWeight() : null);

        return calculateBill(roomNumber, daysStayed, extras, petWeight);
    }

    /**
     * Helper: get room details by room number
     */
    private static Room getRoomDetails(int roomNumber) {
        return HotelData.ROOMS.stream()
                .filter(r -> r.getRoomNumber() == roomNumber)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Room not found."));
    }
}
