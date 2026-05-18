package com.pratap.hotel.model;

public class Room {
    private final int number;
    private final RoomType roomType;
    private boolean booked;
    private final boolean natureView;
    private final boolean balcony;


    public Room(int number, RoomType type, boolean booked, boolean natureView, boolean balcony) {
        this.number = number;
        this.booked = booked;
        this.natureView = natureView;
        this.roomType = type;
        this.balcony = balcony;
    }

    public int getRoomNumber() {
        return number;
    }

    public RoomType getRoomType() {
        return roomType;
    }


    public boolean isBooked() {
        return booked;
    }

    public void setBooked(boolean booked) {
        this.booked = booked;
    }

    public boolean hasNatureView() {
        return natureView;
    }
    public boolean hasBalcony() {
        return balcony;
    }


    @Override
    public String toString() {
        return "Room{" +
                "number=" + number +
                ", type=" + roomType +
                ", booked=" + booked +
                ", seaView=" + natureView +
                ", balcony=" + balcony +
                '}';
    }
}