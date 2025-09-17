package com.pratap.hotel.model;

import java.util.List;

public class Room {
    private int number;
    private RoomType roomType;
    private boolean booked;
    private boolean seaView;
    private List<Extras> extras;
    private Pet pet;

    public Room(int number, RoomType type, boolean booked, boolean seaView, List<Extras> extras, Pet pet) {
        this.number = number;
        this.roomType = roomType;
        this.booked = booked;
        this.seaView = seaView;
        this.extras = extras;
        this.pet = pet;
    }

    public int getRoomNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setType(RoomType type) {
        this.roomType = type;
    }

    public boolean isBooked() {
        return booked;
    }

    public void setBooked(boolean booked) {
        this.booked = booked;
    }

    public boolean hasSeaView() {
        return seaView;
    }

    public void setSeaView(boolean seaView) {
        this.seaView = seaView;
    }

    public List<Extras> getExtras() {
        return extras;
    }

    public void setExtras(List<Extras> extras) {
        this.extras = extras;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    @Override
    public String toString() {
        return "Room{" +
                "number=" + number +
                ", type=" + roomType +
                ", booked=" + booked +
                ", seaView=" + seaView +
                ", extras=" + extras +
                ", pet=" + pet +
                '}';
    }
}
