package com.pratap.hotel.controller;

import com.pratap.hotel.HotelData;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
}
