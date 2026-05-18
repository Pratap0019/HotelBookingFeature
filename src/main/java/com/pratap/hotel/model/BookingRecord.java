// Add BookingRecord model to represent a booking row for the bookings list template
package com.pratap.hotel.model;

public class BookingRecord {
    private final int roomNumber;
    private final String guestName;
    private final String contactNumber;
    private final String roomType;

    public BookingRecord(int roomNumber, String guestName, String contactNumber, String roomType) {
        this.roomNumber = roomNumber;
        this.guestName = guestName;
        this.contactNumber = contactNumber;
        this.roomType = roomType;
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
}

