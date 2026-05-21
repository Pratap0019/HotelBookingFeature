package com.pratap.hotel.controller;

import com.pratap.hotel.HotelData;
import com.pratap.hotel.SunMoonResort;
import com.pratap.hotel.model.Bill;
import com.pratap.hotel.model.Extras;
import com.pratap.hotel.model.Guest;
import com.pratap.hotel.model.BookingRecord;
import com.pratap.hotel.model.BookingDetails;
import com.pratap.hotel.model.Room;
import com.pratap.hotel.model.RoomType;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class HotelBookingController {

    @GetMapping("/")
    public String showIndex(Model model) {
        model.addAttribute("rooms", HotelData.ROOMS);
        model.addAttribute("roomRates", getHomepageRoomRatesInOrder());
        model.addAttribute("extrasRate", getHomepageExtrasInOrder());
        model.addAttribute("petFeeRates", getHomepagePetFeesInOrder());
        model.addAttribute("roomTypeSummary", buildRoomTypeSummary());
        return "homepage";
    }

    @GetMapping("/booking")
    public String bookingPage(Model model) {
        model.addAttribute("rooms", HotelData.ROOMS);
        model.addAttribute("roomRates", HotelData.ROOM_RATES);
        model.addAttribute("extrasRate", HotelData.EXTRAS_RATE);
        model.addAttribute("petFeeRates", HotelData.PET_FEE_RATES);
        model.addAttribute("roomTypeAvailability", getRoomTypeAvailability());
        return "booking";  // booking.html
    }

    @Value("${admin.password:admin123}")
    private String adminPassword;

    @GetMapping("/admin/login")
    public String adminLogin(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) model.addAttribute("error", error);
        return "admin-login";
    }

    @PostMapping("/admin/login")
    public String doAdminLogin(@RequestParam("password") String password, HttpSession session) {
        if (adminPassword != null && adminPassword.equals(password)) {
            session.setAttribute("isAdmin", true);
            return "redirect:/bookings";
        }
        return "redirect:/admin/login?error=Invalid%20password";
    }

    @GetMapping("/admin/logout")
    public String adminLogout(HttpSession session) {
        session.removeAttribute("isAdmin");
        return "redirect:/admin/login";
    }

    @PostMapping("/admin/cancelBooking")
    public String cancelBooking(@RequestParam("roomNumber") int roomNumber, HttpSession session, Model model) {
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        if (isAdmin == null || !isAdmin) {
            return "redirect:/admin/login";
        }

        // unbook room and remove booking
        HotelData.ROOMS.stream()
                .filter(r -> r.getRoomNumber() == roomNumber)
                .findFirst()
                .ifPresent(r -> r.setBooked(false));
        HotelData.BOOKINGS.remove(roomNumber);

        // redirect back to bookings list
        return "redirect:/bookings";
    }

    @PostMapping("/calculatePrice")
    public String calculatePrice(@RequestParam("roomType") String roomType,
                                 @RequestParam("checkIn") String checkIn,
                                 @RequestParam("checkOut") String checkOut,
                                 @RequestParam("daysStayed") int daysStayed,
                                 @RequestParam(value = "extras", required = false) List<Extras> extras,
                                 @RequestParam(value = "petWeight", required = false) Double petWeight,
                                 @RequestParam(value = "spaSessions", required = false) Integer spaSessions,
                                 Model model) {

        RoomType type = RoomType.valueOf(roomType);

        // Auto-assign the first available room of the requested type
        Room assignedRoom = HotelData.ROOMS.stream()
                .filter(r -> r.getRoomType() == type && !r.isBooked())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No available rooms of type " + roomType + ". Please choose a different type."));

        Bill bill = SunMoonResort.calculateBill(assignedRoom.getRoomNumber(), daysStayed, extras, petWeight, spaSessions);


        model.addAttribute("bill", bill);
        model.addAttribute("rooms", HotelData.ROOMS);
        model.addAttribute("roomRates", HotelData.ROOM_RATES);
        model.addAttribute("extrasRate", HotelData.EXTRAS_RATE);
        model.addAttribute("petFeeRates", HotelData.PET_FEE_RATES);
        model.addAttribute("roomTypeAvailability", getRoomTypeAvailability());
        model.addAttribute("spaSessions", spaSessions);

        model.addAttribute("assignedRoomNumber", assignedRoom.getRoomNumber());
        model.addAttribute("selectedRoomType", roomType);
        model.addAttribute("checkIn", checkIn);
        model.addAttribute("checkOut", checkOut);
        model.addAttribute("daysStayed", daysStayed);
        model.addAttribute("petWeight", petWeight);
        model.addAttribute("selectedExtras", extras != null ? extras : new java.util.ArrayList<>());

        return "booking";
    }

    @PostMapping("/confirmBooking")
    public String confirmBooking(@RequestParam("roomNumber") int roomNumber,
                                 @RequestParam(value = "guestName", required = false) String guestName,
                                 @RequestParam(value = "contactNumber", required = false) String contactNumber,
                                 @RequestParam("daysStayed") int daysStayed,
                                 @RequestParam("checkIn") String checkIn,
                                 @RequestParam("checkOut") String checkOut,
                                 @RequestParam("totalAmount") double totalAmount,
                                 @RequestParam Map<String, String> allParams,
                                 Model model) {
        // mark the room as booked
        HotelData.ROOMS.stream()
                .filter(r -> r.getRoomNumber() == roomNumber)
                .findFirst()
                .ifPresent(r -> r.setBooked(true));

        // store booking details with guest and bill
        if (guestName != null && !guestName.isBlank()) {
            Guest guest = new Guest(guestName, contactNumber);
            
            // Reconstruct Bill from form data (only on confirmation)
            Map<String, Double> breakdown = new LinkedHashMap<>();
            Map<String, String> calculationDetails = new LinkedHashMap<>();
            
            // Extract breakdown parameters (format: breakdown_ItemName=amount)
            for (String key : allParams.keySet()) {
                if (key.startsWith("breakdown_")) {
                    String itemName = key.substring(10); // Remove "breakdown_" prefix
                    try {
                        double amount = Double.parseDouble(allParams.get(key));
                        breakdown.put(itemName, amount);
                    } catch (NumberFormatException e) {
                        // Skip invalid entries
                    }
                }
            }
            
            // Extract calculation details (format: calculation_ItemName=details)
            for (String key : allParams.keySet()) {
                if (key.startsWith("calculation_")) {
                    String itemName = key.substring(12); // Remove "calculation_" prefix
                    calculationDetails.put(itemName, allParams.get(key));
                }
            }
            
            // Create Bill object with reconstructed data
            Bill bill = new Bill(totalAmount, breakdown, calculationDetails);
            
            // Create BookingDetails with guest and bill (only here, at confirmation)
            BookingDetails bookingDetails = new BookingDetails(guest, bill, daysStayed, checkIn, checkOut);
            HotelData.BOOKINGS.put(roomNumber, bookingDetails);
        }

        model.addAttribute("rooms", HotelData.ROOMS);
        model.addAttribute("roomRates", HotelData.ROOM_RATES);
        model.addAttribute("extrasRate", HotelData.EXTRAS_RATE);
        model.addAttribute("petFeeRates", HotelData.PET_FEE_RATES);
        model.addAttribute("roomTypeAvailability", getRoomTypeAvailability());

        // Determine the room type for a friendly message
        String roomTypeName = HotelData.ROOMS.stream()
                .filter(r -> r.getRoomNumber() == roomNumber)
                .findFirst()
                .map(r -> r.getRoomType().name())
                .orElse("Unknown");

        String msg = "Booking confirmed! You have been assigned Room " + roomNumber
                + " (" + roomTypeName + ").";
        if (guestName != null && !guestName.isBlank()) {
            msg += " Guest: " + guestName;
            if (contactNumber != null && !contactNumber.isBlank()) msg += " (" + contactNumber + ")";
        }
        model.addAttribute("successMessage", msg);

        return "booking";
    }

    /** Returns a map of RoomType → count of currently available (not booked) rooms */
    private Map<RoomType, Long> getRoomTypeAvailability() {
        return HotelData.ROOMS.stream()
                .filter(r -> !r.isBooked())
                .collect(Collectors.groupingBy(Room::getRoomType, Collectors.counting()));
    }

    /**
     * Builds a per-type summary map used on the homepage.
     * Each entry: RoomType → { available, total, hasBalcony, hasNatureView, hasWifi, hasMinifridge, rate }
     */
    private Map<RoomType, Map<String, Object>> buildRoomTypeSummary() {
        Map<RoomType, Map<String, Object>> summary = new java.util.LinkedHashMap<>();
        for (RoomType rt : new RoomType[]{RoomType.SINGLE, RoomType.DOUBLE, RoomType.SUITE}) {
            long available = HotelData.ROOMS.stream()
                    .filter(r -> r.getRoomType() == rt && !r.isBooked()).count();
            long total = HotelData.ROOMS.stream()
                    .filter(r -> r.getRoomType() == rt).count();
            Room sample = HotelData.ROOMS.stream()
                    .filter(r -> r.getRoomType() == rt).findFirst().orElse(null);
            Map<String, Object> info = new java.util.LinkedHashMap<>();
            info.put("available", available);
            info.put("total", total);
            info.put("hasBalcony", sample != null && sample.hasBalcony());
            info.put("hasNatureView", sample != null && sample.hasNatureView());
            // WiFi is free for ALL room types
            info.put("hasWifi", true);
            // Mini-fridge is free for DOUBLE and SUITE only
            info.put("hasMinifridge", rt == RoomType.DOUBLE || rt == RoomType.SUITE);
            info.put("rate", HotelData.ROOM_RATES.getOrDefault(rt, 0.0));
            summary.put(rt, info);
        }
        return summary;
    }

    private Map<RoomType, Double> getHomepageRoomRatesInOrder() {
        Map<RoomType, Double> ordered = new LinkedHashMap<>();
        ordered.put(RoomType.SUITE, HotelData.ROOM_RATES.get(RoomType.SUITE));
        ordered.put(RoomType.DOUBLE, HotelData.ROOM_RATES.get(RoomType.DOUBLE));
        ordered.put(RoomType.SINGLE, HotelData.ROOM_RATES.get(RoomType.SINGLE));
        return ordered;
    }

    private Map<Extras, Double> getHomepageExtrasInOrder() {
        Map<Extras, Double> ordered = new LinkedHashMap<>();
        ordered.put(Extras.SPA, HotelData.EXTRAS_RATE.get(Extras.SPA));
        ordered.put(Extras.PoolPASS, HotelData.EXTRAS_RATE.get(Extras.PoolPASS));
        ordered.put(Extras.GymPASS, HotelData.EXTRAS_RATE.get(Extras.GymPASS));
        ordered.put(Extras.MATTRESS, HotelData.EXTRAS_RATE.get(Extras.MATTRESS));
        return ordered;
    }

    private Map<String, Double> getHomepagePetFeesInOrder() {
        Map<String, Double> ordered = new LinkedHashMap<>();
        ordered.put("Large (above 15kg)", HotelData.PET_FEE_RATES.get("Large (above 15kg)"));
        ordered.put("Medium (below 15kg)", HotelData.PET_FEE_RATES.get("Medium (below 15kg)"));
        ordered.put("Small (below 8kg)", HotelData.PET_FEE_RATES.get("Small (below 8kg)"));
        return ordered;
    }

    @GetMapping("/bookings")
    public String bookingsList(Model model, HttpSession session) {
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        if (isAdmin == null || !isAdmin) {
            return "redirect:/admin/login";
        }

        List<BookingRecord> bookingRecords = new java.util.ArrayList<>();
        HotelData.BOOKINGS.forEach((roomNum, bookingDetails) -> {
            String roomType = HotelData.ROOMS.stream()
                    .filter(r -> r.getRoomNumber() == roomNum)
                    .findFirst()
                    .map(r -> r.getRoomType().name())
                    .orElse("UNKNOWN");
            Guest guest = bookingDetails.getGuest();
            Bill bill = bookingDetails.getBill();
            int daysStayed = bookingDetails.getDaysStayed();
            String checkInDate = bookingDetails.getCheckInDate();
            String checkOutDate = bookingDetails.getCheckOutDate();
            bookingRecords.add(new BookingRecord(
                    roomNum,
                    guest.getName(),
                    guest.getContactNumber(),
                    roomType,
                    bill,
                    daysStayed,
                    checkInDate,
                    checkOutDate
            ));
        });

        model.addAttribute("bookings", bookingRecords);
        // Pass full room inventory for admin-only detailed view
        model.addAttribute("rooms", HotelData.ROOMS);
        return "admin-bookings";
    }
}
