package com.pratap.hotel.model;

import java.util.List;

public class Room {
    private int roomNumber;
    private RoomType type;
    private boolean reserved;
    private boolean seaView;
    private List<Extras> extras;
    private Pet pet;

    public Room(int roomNumber, RoomType type, boolean reserved, boolean seaView, List<Extras> extras, Pet pet) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.reserved = reserved;
        this.seaView = seaView;
        this.extras = extras;
        this.pet = pet;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public RoomType getType() {
        return type;
    }

    public void setType(RoomType type) {
        this.type = type;
    }

    public boolean isReserved() {
        return reserved;
    }

    public void setReserved(boolean reserved) {
        this.reserved = reserved;
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
}

