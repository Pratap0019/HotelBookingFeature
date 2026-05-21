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
import com.pratap.hotel.model.BookingStatus;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class HotelBookingController {

    @GetMapping("/")
    public String showIndex(Model model) {
        addHomepageBaseAttributes(model);
        model.addAttribute("searchPerformed", false);
        model.addAttribute("searchedBookings", new ArrayList<BookingRecord>());
        return "homepage";
    }

    @PostMapping("/searchBookings")
    public String searchBookings(@RequestParam("mobileNumber") String mobileNumber, Model model) {
        addHomepageBaseAttributes(model);

        String normalizedInput = normalizeToTenDigits(mobileNumber);
        model.addAttribute("searchPerformed", true);
        model.addAttribute("searchedMobile", normalizedInput);

        List<BookingRecord> matchedBookings = buildAllBookingRecords().stream()
                .filter(record -> normalizeToTenDigits(record.getContactNumber()).equals(normalizedInput))
                .sorted(Comparator.comparing(BookingRecord::getCheckInDate).reversed())
                .collect(Collectors.toList());

        model.addAttribute("searchedBookings", matchedBookings);
        model.addAttribute("scrollTo", "searchResultsSection");
        return "homepage";
    }

    @GetMapping("/booking")
    public String bookingPage(Model model) {
        model.addAttribute("rooms", HotelData.ROOMS);
        model.addAttribute("roomRates", HotelData.ROOM_RATES);
        model.addAttribute("extrasRate", HotelData.EXTRAS_RATE);
        model.addAttribute("petFeeRates", HotelData.PET_FEE_RATES);
        model.addAttribute("roomTypeAvailability", new LinkedHashMap<RoomType, Long>());
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
    public String cancelBooking(@RequestParam("bookingId") String bookingId, HttpSession session) {
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        if (isAdmin == null || !isAdmin) {
            return "redirect:/admin/login";
        }

        updateBookingStatus(bookingId, BookingStatus.CANCELLED);

        // redirect back to bookings list
        return "redirect:/bookings#bookingsSection";
    }

    @PostMapping("/admin/checkIn")
    public String checkInBooking(@RequestParam("bookingId") String bookingId, HttpSession session) {
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        if (isAdmin == null || !isAdmin) {
            return "redirect:/admin/login";
        }

        updateBookingStatus(bookingId, BookingStatus.CHECKED_IN);

        // redirect back to bookings list
        return "redirect:/bookings#bookingsSection";
    }

    @PostMapping("/admin/checkOut")
    public String checkOutBooking(@RequestParam("bookingId") String bookingId, HttpSession session) {
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        if (isAdmin == null || !isAdmin) {
            return "redirect:/admin/login";
        }

        updateBookingStatus(bookingId, BookingStatus.CHECKED_OUT);

        // redirect back to bookings list
        return "redirect:/bookings#bookingsSection";
    }

    @PostMapping("/calculatePrice")
    public String calculatePrice(@RequestParam(value = "roomType", required = false) String roomType,
                                 @RequestParam("checkIn") String checkIn,
                                 @RequestParam("checkOut") String checkOut,
                                 @RequestParam("daysStayed") int daysStayed,
                                 @RequestParam(value = "extras", required = false) List<Extras> extras,
                                 @RequestParam(value = "petWeight", required = false) Double petWeight,
                                 @RequestParam(value = "spaSessions", required = false) Integer spaSessions,
                                  @RequestParam(value = "action", required = false) String action,
                                 Model model) {

        LocalDate checkInDate;
        LocalDate checkOutDate;
        try {
            checkInDate = LocalDate.parse(checkIn);
            checkOutDate = LocalDate.parse(checkOut);
        } catch (DateTimeParseException ex) {
            throw new RuntimeException("Invalid check-in/check-out date format.");
        }

        if (!checkOutDate.isAfter(checkInDate)) {
            throw new RuntimeException("Check-out must be after check-in.");
        }

        int computedDays = (int) java.time.temporal.ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        if (daysStayed != computedDays) {
            daysStayed = computedDays;
        }

        Map<RoomType, Long> roomTypeAvailability = getRoomTypeAvailability(checkInDate, checkOutDate);
        model.addAttribute("rooms", HotelData.ROOMS);
        model.addAttribute("roomRates", HotelData.ROOM_RATES);
        model.addAttribute("extrasRate", HotelData.EXTRAS_RATE);
        model.addAttribute("petFeeRates", HotelData.PET_FEE_RATES);
        model.addAttribute("roomTypeAvailability", roomTypeAvailability);
        model.addAttribute("spaSessions", spaSessions);
        model.addAttribute("checkIn", checkIn);
        model.addAttribute("checkOut", checkOut);
        model.addAttribute("daysStayed", daysStayed);
        model.addAttribute("petWeight", petWeight);
        model.addAttribute("selectedExtras", extras != null ? extras : new ArrayList<>());
        model.addAttribute("availabilityChecked", true);

        // First step: only compute date-based availability and let user pick room type after that.
        if ("availability".equalsIgnoreCase(action)) {
            model.addAttribute("scrollTo", "calculatePriceArea");
            return "booking";
        }

        if (roomType == null || roomType.isBlank()) {
            model.addAttribute("errorMessage", "Please select a room type after checking availability.");
            model.addAttribute("scrollTo", "bookingAlert");
            return "booking";
        }

        RoomType type = RoomType.valueOf(roomType);

        // Auto-assign the first room available for this specific date range.
        Room assignedRoom = HotelData.ROOMS.stream()
                .filter(r -> r.getRoomType() == type)
                .filter(r -> isRoomAvailableForRange(r.getRoomNumber(), checkInDate, checkOutDate))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No available rooms of type " + roomType + ". Please choose a different type."));

        Bill bill = SunMoonResort.calculateBill(assignedRoom.getRoomNumber(), daysStayed, extras, petWeight, spaSessions);


        model.addAttribute("bill", bill);

        model.addAttribute("assignedRoomNumber", assignedRoom.getRoomNumber());
        model.addAttribute("selectedRoomType", roomType);
        model.addAttribute("scrollTo", "priceBreakdown");

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
        LocalDate checkInDate = LocalDate.parse(checkIn);
        LocalDate checkOutDate = LocalDate.parse(checkOut);

        if (!isRoomAvailableForRange(roomNumber, checkInDate, checkOutDate)) {
            model.addAttribute("rooms", HotelData.ROOMS);
            model.addAttribute("roomRates", HotelData.ROOM_RATES);
            model.addAttribute("extrasRate", HotelData.EXTRAS_RATE);
            model.addAttribute("petFeeRates", HotelData.PET_FEE_RATES);
            model.addAttribute("roomTypeAvailability", getRoomTypeAvailability(checkInDate, checkOutDate));
            model.addAttribute("errorMessage", "This room is no longer available for the selected dates. Please calculate again.");
            model.addAttribute("scrollTo", "bookingAlert");
            return "booking";
        }

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
            BookingDetails bookingDetails = new BookingDetails(
                    UUID.randomUUID().toString(),
                    guest,
                    bill,
                    daysStayed,
                    checkIn,
                    checkOut,
                    BookingStatus.CONFIRMED
            );
            HotelData.BOOKINGS.computeIfAbsent(roomNumber, rn -> new ArrayList<>()).add(bookingDetails);
        }

        model.addAttribute("rooms", HotelData.ROOMS);
        model.addAttribute("roomRates", HotelData.ROOM_RATES);
        model.addAttribute("extrasRate", HotelData.EXTRAS_RATE);
        model.addAttribute("petFeeRates", HotelData.PET_FEE_RATES);
        model.addAttribute("roomTypeAvailability", new LinkedHashMap<RoomType, Long>());

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
        model.addAttribute("scrollTo", "bookingSuccess");

        return "booking";
    }

    /** Returns availability by type for a specific date range; if dates are null, returns full inventory counts. */
    private Map<RoomType, Long> getRoomTypeAvailability(LocalDate checkIn, LocalDate checkOut) {
        Map<RoomType, Long> availability = new LinkedHashMap<>();
        for (RoomType roomType : RoomType.values()) {
            availability.put(roomType, 0L);
        }

        HotelData.ROOMS.stream()
                .filter(r -> checkIn == null || checkOut == null || isRoomAvailableForRange(r.getRoomNumber(), checkIn, checkOut))
                .collect(Collectors.groupingBy(Room::getRoomType, Collectors.counting()))
                .forEach(availability::put);

        return availability;
    }

    /**
     * Builds a per-type summary map used on the homepage.
     * Each entry: RoomType → { available, total, hasBalcony, hasNatureView, hasWifi, hasMinifridge, rate }
     */
    private Map<RoomType, Map<String, Object>> buildRoomTypeSummary() {
        Map<RoomType, Map<String, Object>> summary = new java.util.LinkedHashMap<>();
        for (RoomType rt : new RoomType[]{RoomType.SINGLE, RoomType.DOUBLE, RoomType.SUITE}) {
            LocalDate today = LocalDate.now();
            LocalDate tomorrow = today.plusDays(1);
            long available = HotelData.ROOMS.stream()
                    .filter(r -> r.getRoomType() == rt)
                    .filter(r -> isRoomAvailableForRange(r.getRoomNumber(), today, tomorrow))
                    .count();
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

    private void addHomepageBaseAttributes(Model model) {
        model.addAttribute("rooms", HotelData.ROOMS);
        model.addAttribute("roomRates", getHomepageRoomRatesInOrder());
        model.addAttribute("extrasRate", getHomepageExtrasInOrder());
        model.addAttribute("petFeeRates", getHomepagePetFeesInOrder());
        model.addAttribute("roomTypeSummary", buildRoomTypeSummary());
    }

    @GetMapping("/bookings")
    public String bookingsList(Model model, HttpSession session) {
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        if (isAdmin == null || !isAdmin) {
            return "redirect:/admin/login";
        }

        List<BookingRecord> bookingRecords = new java.util.ArrayList<>();
        HotelData.BOOKINGS.forEach((roomNum, bookingDetailsList) -> {
            String roomType = HotelData.ROOMS.stream()
                    .filter(r -> r.getRoomNumber() == roomNum)
                    .findFirst()
                    .map(r -> r.getRoomType().name())
                    .orElse("UNKNOWN");
            bookingDetailsList.forEach(bookingDetails -> {
                Guest guest = bookingDetails.getGuest();
                Bill bill = bookingDetails.getBill();
                int daysStayed = bookingDetails.getDaysStayed();
                String checkInDate = bookingDetails.getCheckInDate();
                String checkOutDate = bookingDetails.getCheckOutDate();
                bookingRecords.add(new BookingRecord(
                        bookingDetails.getBookingId(),
                        roomNum,
                        guest.getName(),
                        guest.getContactNumber(),
                        roomType,
                        bill,
                        daysStayed,
                        checkInDate,
                        checkOutDate,
                        bookingDetails.getStatus()
                ));
            });
        });

        model.addAttribute("bookings", bookingRecords);
        // Pass full room inventory for admin-only detailed view
        model.addAttribute("rooms", HotelData.ROOMS);
        model.addAttribute("roomBookingRanges", buildRoomBookingRanges());
        return "admin-bookings";
    }

    private List<BookingRecord> buildAllBookingRecords() {
        List<BookingRecord> bookingRecords = new ArrayList<>();
        HotelData.BOOKINGS.forEach((roomNum, bookingDetailsList) -> {
            String roomType = HotelData.ROOMS.stream()
                    .filter(r -> r.getRoomNumber() == roomNum)
                    .findFirst()
                    .map(r -> r.getRoomType().name())
                    .orElse("UNKNOWN");
            bookingDetailsList.forEach(bookingDetails -> {
                Guest guest = bookingDetails.getGuest();
                Bill bill = bookingDetails.getBill();
                bookingRecords.add(new BookingRecord(
                        bookingDetails.getBookingId(),
                        roomNum,
                        guest.getName(),
                        guest.getContactNumber(),
                        roomType,
                        bill,
                        bookingDetails.getDaysStayed(),
                        bookingDetails.getCheckInDate(),
                        bookingDetails.getCheckOutDate(),
                        bookingDetails.getStatus()
                ));
            });
        });
        return bookingRecords;
    }

    private String normalizeToTenDigits(String rawMobile) {
        if (rawMobile == null) return "";
        String digits = rawMobile.replaceAll("\\D", "");
        if (digits.length() > 10) {
            return digits.substring(digits.length() - 10);
        }
        return digits;
    }

    private boolean isRoomAvailableForRange(int roomNumber, LocalDate checkIn, LocalDate checkOut) {
        List<BookingDetails> existingBookings = HotelData.BOOKINGS.getOrDefault(roomNumber, java.util.Collections.emptyList());
        return existingBookings.stream()
                .filter(existing -> existing.getStatus() == BookingStatus.CONFIRMED || existing.getStatus() == BookingStatus.CHECKED_IN)
                .noneMatch(existing -> hasOverlap(existing, checkIn, checkOut));
    }

    private boolean hasOverlap(BookingDetails existing, LocalDate requestedCheckIn, LocalDate requestedCheckOut) {
        LocalDate existingCheckIn = LocalDate.parse(existing.getCheckInDate());
        LocalDate existingCheckOut = LocalDate.parse(existing.getCheckOutDate());
        return requestedCheckIn.isBefore(existingCheckOut) && requestedCheckOut.isAfter(existingCheckIn);
    }

    private Map<Integer, String> buildRoomBookingRanges() {
        Map<Integer, String> ranges = new LinkedHashMap<>();
        HotelData.ROOMS.forEach(room -> {
            List<BookingDetails> bookings = HotelData.BOOKINGS.getOrDefault(room.getRoomNumber(), java.util.Collections.emptyList());
            String rangeText = bookings.stream()
                    .filter(b -> b.getStatus() == BookingStatus.CONFIRMED || b.getStatus() == BookingStatus.CHECKED_IN)
                    .map(b -> b.getCheckInDate() + " to " + b.getCheckOutDate())
                    .collect(Collectors.joining(" | "));
            if (rangeText.isBlank()) {
                rangeText = "-";
            }
            ranges.put(room.getRoomNumber(), rangeText);
        });
        return ranges;
    }

    private void updateBookingStatus(String bookingId, BookingStatus targetStatus) {
        HotelData.BOOKINGS.values().forEach(bookings -> bookings.stream()
                .filter(b -> b.getBookingId().equals(bookingId))
                .findFirst()
                .ifPresent(b -> {
                    if (targetStatus == BookingStatus.CANCELLED && b.getStatus() == BookingStatus.CONFIRMED) {
                        b.setStatus(targetStatus);
                    } else if (targetStatus == BookingStatus.CHECKED_IN && b.getStatus() == BookingStatus.CONFIRMED) {
                        b.setStatus(targetStatus);
                    } else if (targetStatus == BookingStatus.CHECKED_OUT && b.getStatus() == BookingStatus.CHECKED_IN) {
                        b.setStatus(targetStatus);
                    }
                }));
    }
}
