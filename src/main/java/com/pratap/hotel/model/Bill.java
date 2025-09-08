package com.pratap.hotel.model;

import java.util.Map;

public class Bill {
    private double totalAmount;
    private Map<String, Double> breakdown;

    public Bill(double totalAmount, Map<String, Double> breakdown) {
        this.totalAmount = totalAmount;
        this.breakdown = breakdown;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public Map<String, Double> getBreakdown() {
        return breakdown;
    }
}
