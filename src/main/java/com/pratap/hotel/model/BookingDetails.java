package com.pratap.hotel.model;

/**
 * Represents complete booking information including guest details and bill breakdown.
 */
public class BookingDetails {
    private final String bookingId;
    private final Guest guest;
    private final Bill bill;
    private final int daysStayed;
    private final String checkInDate;
    private final String checkOutDate;
    private com.pratap.hotel.model.BookingStatus status;

    public BookingDetails(String bookingId, Guest guest, Bill bill, int daysStayed, String checkInDate, String checkOutDate, com.pratap.hotel.model.BookingStatus status) {
        this.bookingId = bookingId;
        this.guest = guest;
        this.bill = bill;
        this.daysStayed = daysStayed;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.status = status;
    }

    public String getBookingId() {
        return bookingId;
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

    public com.pratap.hotel.model.BookingStatus getStatus() {
        return status;
    }

    public void setStatus(com.pratap.hotel.model.BookingStatus status) {
        this.status = status;
    }
}

