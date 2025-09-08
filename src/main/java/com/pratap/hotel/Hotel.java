package com.pratap.hotel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.pratap.hotel.model.Bill;
import com.pratap.hotel.model.Extras;
import com.pratap.hotel.model.Pet;
import com.pratap.hotel.model.Room;
import com.pratap.hotel.model.RoomType;

public class Hotel {
    private static List<Room> rooms = new ArrayList<>();
    private static Map<RoomType, Double> roomRates = new HashMap<>();
    private static Map<Extras, Double> extrasRate = new HashMap<>();
    private static Map<String, Double> petFeeRates = new HashMap<>();

    static {

        rooms.add(new Room(101, RoomType.SINGLE, false, false, Arrays.asList(Extras.WIFI), null));
        rooms.add(new Room(102, RoomType.DOUBLE, false, true, Collections.emptyList(), new Pet(4)));
        rooms.add(new Room(103, RoomType.SUITE, false, true, Arrays.asList(Extras.MINIBAR, Extras.WIFI), new Pet(12)));

        roomRates.put(RoomType.SINGLE, 50.0);
        roomRates.put(RoomType.DOUBLE, 80.0);
        roomRates.put(RoomType.SUITE, 150.0);

        extrasRate.put(Extras.MINIBAR, 15.0);
        extrasRate.put(Extras.WIFI, 5.0);

        petFeeRates.put("under5kg", 5.0);
        petFeeRates.put("under10kg", 10.0);
        petFeeRates.put("over10kg", 15.0);
    }

    public static Bill calculateBill(int roomNumber, int daysStayed) {
        Room roomDetails = getRoomDetails(roomNumber);

        double dailyRate = roomRates.get(roomDetails.getType());

        if (roomDetails.hasSeaView()) {
            dailyRate += dailyRate * 0.2; // 20% surcharge for sea view
        }

        for (Extras extra : roomDetails.getExtras()) {
            dailyRate += extrasRate.get(extra);
        }

        if (roomDetails.getPet() != null) {
            Pet pet = roomDetails.getPet();
            if (pet.getWeight() <= 5) {
                dailyRate += petFeeRates.get("under5kg");
            } else if (pet.getWeight() <= 10) {
                dailyRate += petFeeRates.get("under10kg");
            } else {
                dailyRate += petFeeRates.get("over10kg");
            }
        }

        double totalAmount = dailyRate * daysStayed;
        double serviceCharge = totalAmount * 0.1; // 10% service charge
        double finalAmount = totalAmount + serviceCharge;

        Map<String, Double> breakdown = new HashMap<>();
        breakdown.put("Room Charge", totalAmount);
        breakdown.put("Service Charge", serviceCharge);

        return new Bill(finalAmount, breakdown);
    }

    private static Room getRoomDetails(int roomNumber) {
        Optional<Room> room = rooms.stream()
                .filter(r -> r.getRoomNumber() == roomNumber)
                .findFirst();
        if (room.isPresent()) {
            return room.get();
        } else {
            throw new RuntimeException("Room not found.");
        }
    }

    public static void main(String[] args) {
        // Example usage of calculateBill method
        int roomNumber = 102; // Change to the desired room number
        int daysStayed = 5; // Change to the number of days stayed
        Bill bill = calculateBill(roomNumber, daysStayed);

        System.out.println("Total Amount: INR" + bill.getTotalAmount());
        System.out.println("Breakdown:");
        for (Map.Entry<String, Double> entry : bill.getBreakdown().entrySet()) {
            System.out.println(entry.getKey() + ": INR" + entry.getValue());
        }
    }
}