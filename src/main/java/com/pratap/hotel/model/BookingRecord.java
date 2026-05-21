// Add BookingRecord model to represent a booking row for the bookings list template
package com.pratap.hotel.model;

public class BookingRecord {
    private final String bookingId;
    private final int roomNumber;
    private final String guestName;
    private final String contactNumber;
    private final String roomType;
    private final Bill bill;
    private final int daysStayed;
    private final String checkInDate;
    private final String checkOutDate;
    private final BookingStatus status;

    public BookingRecord(String bookingId, int roomNumber, String guestName, String contactNumber, String roomType, Bill bill, int daysStayed,
                         String checkInDate, String checkOutDate, BookingStatus status) {
        this.bookingId = bookingId;
        this.roomNumber = roomNumber;
        this.guestName = guestName;
        this.contactNumber = contactNumber;
        this.roomType = roomType;
        this.bill = bill;
        this.daysStayed = daysStayed;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.status = status;
    }

    public String getBookingId() {
        return bookingId;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getRoomType() {
        return roomType;
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

    public BookingStatus getStatus() {
        return status;
    }
}

