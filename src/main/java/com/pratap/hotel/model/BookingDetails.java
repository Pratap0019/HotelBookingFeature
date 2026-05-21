package com.pratap.hotel.model;

/**
 * Represents complete booking information including guest details and bill breakdown.
 */
public class BookingDetails {
    private final Guest guest;
    private final Bill bill;
    private final int daysStayed;

    public BookingDetails(Guest guest, Bill bill, int daysStayed) {
        this.guest = guest;
        this.bill = bill;
        this.daysStayed = daysStayed;
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
}

