package com.pratap.hotel.controller;

import com.pratap.hotel.Hotel;
import com.pratap.hotel.HotelData;
import com.pratap.hotel.model.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class HotelBookingController {

    @GetMapping("/")
    public String showIndex(Model model) {
        model.addAttribute("rooms", HotelData.ROOMS);
        model.addAttribute("bookingForm", new BookingForm()); // if you have a form object
        return "index";
    }


    @PostMapping("/searchRooms")
    public String searchRooms(@RequestParam("checkIn") String checkIn,
                              @RequestParam("checkOut") String checkOut,
                              @RequestParam("guestCount") int guestCount,
                              @RequestParam("roomType") String roomType,
                              Model model) {
        model.addAttribute("checkIn", checkIn);
        model.addAttribute("checkOut", checkOut);
        model.addAttribute("guestCount", guestCount);
        model.addAttribute("roomType", roomType);
        model.addAttribute("bookingForm", new BookingForm());
        model.addAttribute("bookingMessage", "Room search submitted successfully!");
        prepareRoomFormData(model);
        return "index";
    }

    @PostMapping("/calculateBill")
    public String calculateBill(@ModelAttribute("bookingForm") BookingForm bookingForm, Model model) {
        try {
            List<Extras> selectedExtras = null;
            if (bookingForm.getExtras() != null) {
                selectedExtras = bookingForm.getExtras().stream()
                        .map(Extras::valueOf)
                        .toList();
            }
            Bill bill = Hotel.calculateBill(
                    bookingForm.getRoomNumber(),
                    bookingForm.getDaysStayed(),
                    selectedExtras,
                    bookingForm.getPetWeight()
            );
            model.addAttribute("bill", bill);
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
        }
        prepareRoomFormData(model);
        return "index";
    }

    private void prepareRoomFormData(Model model) {
        model.addAttribute("availableRooms", HotelData.ROOMS);
        Map<Integer, List<Extras>> roomExtrasMap = new HashMap<>();
        Map<Integer, Pet> roomPetMap = new HashMap<>();
        for (Room r : HotelData.ROOMS) {
            roomExtrasMap.put(r.getRoomNumber(), r.getExtras());
            roomPetMap.put(r.getRoomNumber(), r.getPet());
        }
        model.addAttribute("roomExtrasMap", roomExtrasMap);
        model.addAttribute("roomPetMap", roomPetMap);
    }
}
