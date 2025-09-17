package com.pratap.hotel.model;

import java.util.List;

public class BookingForm {

    private Integer roomNumber;
    private Integer daysStayed;
    private List<String> extras;
    private Double petWeight;

    public BookingForm() {}

    public Integer getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(Integer roomNumber) {
        this.roomNumber = roomNumber;
    }

    public Integer getDaysStayed() {
        return daysStayed;
    }

    public void setDaysStayed(Integer daysStayed) {
        this.daysStayed = daysStayed;
    }

    public List<String> getExtras() {
        return extras;
    }

    public void setExtras(List<String> extras) {
        this.extras = extras;
    }

    public Double getPetWeight() {
        return petWeight;
    }

    public void setPetWeight(Double petWeight) {
        this.petWeight = petWeight;
    }
}
