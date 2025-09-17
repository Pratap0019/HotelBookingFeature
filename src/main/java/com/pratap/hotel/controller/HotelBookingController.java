package com.pratap.hotel.controller;

import com.pratap.hotel.HotelData;
import com.pratap.hotel.PratapHotel;
import com.pratap.hotel.model.Bill;
import com.pratap.hotel.model.Extras;
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
                                 Model model) {

        Bill bill = PratapHotel.calculateBill(roomNumber, daysStayed, extras, petWeight);

        model.addAttribute("bill", bill);
        model.addAttribute("rooms", HotelData.ROOMS);
        model.addAttribute("roomRates", HotelData.ROOM_RATES);
        model.addAttribute("extras", HotelData.EXTRAS_RATE);
        model.addAttribute("petFees", HotelData.PET_FEE_RATES);

        model.addAttribute("roomNumber", roomNumber);
        model.addAttribute("daysStayed", daysStayed);
        model.addAttribute("petWeight", petWeight);

        return "booking";
    }

    @PostMapping("/confirmBooking")
    public String confirmBooking(@RequestParam("roomNumber") int roomNumber,
                                 Model model) {
        // mark the room as booked
        HotelData.ROOMS.stream()
                .filter(r -> r.getRoomNumber() == roomNumber)
                .findFirst()
                .ifPresent(r -> r.setBooked(true));

        model.addAttribute("rooms", HotelData.ROOMS);
        model.addAttribute("roomRates", HotelData.ROOM_RATES);
        model.addAttribute("extrasRate", HotelData.EXTRAS_RATE);
        model.addAttribute("petFeeRates", HotelData.PET_FEE_RATES);

        model.addAttribute("successMessage", "Room " + roomNumber + " has been successfully booked!");

        return "booking";
    }
}
