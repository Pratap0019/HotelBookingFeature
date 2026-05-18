package com.pratap.hotel.controller;

import com.pratap.hotel.HotelData;
import com.pratap.hotel.SunMoonResort;
import com.pratap.hotel.model.Bill;
import com.pratap.hotel.model.Extras;
import com.pratap.hotel.model.Guest;
import com.pratap.hotel.model.BookingRecord;
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
    public String bookingsList(Model model) {
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
        return "bookings";
    }
}
