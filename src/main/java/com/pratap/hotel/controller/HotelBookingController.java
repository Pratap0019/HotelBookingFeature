package com.pratap.hotel.controller;

import com.pratap.hotel.HotelData;
import com.pratap.hotel.SunMoonResort;
import com.pratap.hotel.model.Bill;
import com.pratap.hotel.model.Extras;
import com.pratap.hotel.model.Guest;
import com.pratap.hotel.model.BookingRecord;
import com.pratap.hotel.model.Room;
import com.pratap.hotel.model.RoomType;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class HotelBookingController {

    @GetMapping("/")
    public String showIndex(Model model) {
        model.addAttribute("rooms", HotelData.ROOMS);
        model.addAttribute("roomRates", HotelData.ROOM_RATES);
        model.addAttribute("extrasRate", HotelData.EXTRAS_RATE);
        model.addAttribute("petFeeRates", HotelData.PET_FEE_RATES);
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
                                 Model model) {
        // mark the room as booked
        HotelData.ROOMS.stream()
                .filter(r -> r.getRoomNumber() == roomNumber)
                .findFirst()
                .ifPresent(r -> r.setBooked(true));

        // store guest details for future use
        if (guestName != null && !guestName.isBlank()) {
            HotelData.BOOKINGS.put(roomNumber, new Guest(guestName, contactNumber));
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

    @GetMapping("/bookings")
    public String bookingsList(Model model, HttpSession session) {
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        if (isAdmin == null || !isAdmin) {
            return "redirect:/admin/login";
        }

        List<BookingRecord> bookingRecords = new java.util.ArrayList<>();
        HotelData.BOOKINGS.forEach((roomNum, guest) -> {
            String roomType = HotelData.ROOMS.stream()
                    .filter(r -> r.getRoomNumber() == roomNum)
                    .findFirst()
                    .map(r -> r.getRoomType().name())
                    .orElse("UNKNOWN");
            bookingRecords.add(new BookingRecord(roomNum, guest.getName(), guest.getContactNumber(), roomType));
        });

        model.addAttribute("bookings", bookingRecords);
        // Pass full room inventory for admin-only detailed view
        model.addAttribute("rooms", HotelData.ROOMS);
        return "admin-bookings";
    }
}
