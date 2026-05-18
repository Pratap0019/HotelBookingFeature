package com.pratap.hotel.controller;

import com.pratap.hotel.HotelData;
import com.pratap.hotel.SunMoonResort;
import com.pratap.hotel.model.Bill;
import com.pratap.hotel.model.Extras;
import com.pratap.hotel.model.Guest;
import com.pratap.hotel.model.BookingRecord;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class HotelBookingController {

    @GetMapping("/")
    public String showIndex(Model model) {
        // Add data to the model
        model.addAttribute("rooms", HotelData.ROOMS);
        model.addAttribute("roomRates", HotelData.ROOM_RATES);
        model.addAttribute("extrasRate", HotelData.EXTRAS_RATE);
        model.addAttribute("petFeeRates", HotelData.PET_FEE_RATES);

        return "homepage";
    }

    @GetMapping("/booking")
    public String bookingPage(Model model) {
        model.addAttribute("rooms", HotelData.ROOMS);
        model.addAttribute("roomRates", HotelData.ROOM_RATES);
        model.addAttribute("extrasRate", HotelData.EXTRAS_RATE);
        model.addAttribute("petFeeRates", HotelData.PET_FEE_RATES);
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
    public String calculatePrice(@RequestParam("roomNumber") int roomNumber,
                                 @RequestParam("daysStayed") int daysStayed,
                                 @RequestParam(value = "extras", required = false) List<Extras> extras,
                                 @RequestParam(value = "petWeight", required = false) Double petWeight,
                                 @RequestParam(value = "spaSessions", required = false) Integer spaSessions,
                                 Model model) {

        Bill bill = SunMoonResort.calculateBill(roomNumber, daysStayed, extras, petWeight, spaSessions);

        model.addAttribute("bill", bill);
        model.addAttribute("rooms", HotelData.ROOMS);
        model.addAttribute("roomRates", HotelData.ROOM_RATES);
        model.addAttribute("extrasRate", HotelData.EXTRAS_RATE);
        model.addAttribute("petFeeRates", HotelData.PET_FEE_RATES);
        model.addAttribute("spaSessions", spaSessions);

        model.addAttribute("roomNumber", roomNumber);
        model.addAttribute("daysStayed", daysStayed);
        model.addAttribute("petWeight", petWeight);

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

        String msg = "Room " + roomNumber + " has been successfully booked!";
        if (guestName != null && !guestName.isBlank()) {
            msg += " Guest: " + guestName;
            if (contactNumber != null && !contactNumber.isBlank()) msg += " (" + contactNumber + ")";
        }
        model.addAttribute("successMessage", msg);

        return "booking";
    }

    @GetMapping("/bookings")
    public String bookingsList(Model model, HttpSession session) {
        // admin-only: check session
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        if (isAdmin == null || !isAdmin) {
            return "redirect:/admin/login";
        }

        // Build a simple list of BookingRecord for display
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
        return "admin-bookings";
    }
}
