package com.pratap.hotel.model;

public enum Extras {
    MATTRESS("Extra Mattress"),
    SPA("SPA"),
    GymPASS("Gym Access"),
    PoolPASS("Pool Access");

    private final String displayName;

    Extras(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}