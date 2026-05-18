package com.pratap.hotel.model;

public class Guest {
    private final String name;
    private final String contactNumber; // includes country code (e.g. +91-9876543210)

    public Guest(String name, String contactNumber) {
        this.name = name;
        this.contactNumber = contactNumber;
    }

    public String getName() {
        return name;
    }

    public String getContactNumber() {
        return contactNumber;
    }
}

