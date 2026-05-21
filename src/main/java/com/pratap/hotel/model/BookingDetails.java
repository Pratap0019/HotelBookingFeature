package com.pratap.hotel.model;

/**
 * Represents complete booking information including guest details and bill breakdown.
 */
public class BookingDetails {
    private final Guest guest;
    private final Bill bill;
    private final int daysStayed;
    private final String checkInDate;
    private final String checkOutDate;

    public BookingDetails(Guest guest, Bill bill, int daysStayed, String checkInDate, String checkOutDate) {
        this.guest = guest;
        this.bill = bill;
        this.daysStayed = daysStayed;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
    }

    public Guest getGuest() {
        return guest;
    }

    public Bill getBill() {
        return bill;
    }

    public int getDaysStayed() {
        return daysStayed;
    }

    public String getCheckInDate() {
        return checkInDate;
    }

    public String getCheckOutDate() {
        return checkOutDate;
    }
}

