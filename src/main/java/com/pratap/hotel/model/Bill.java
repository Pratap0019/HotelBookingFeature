package com.pratap.hotel.model;

import java.util.Map;

public class Bill {
    private double totalAmount;
    private Map<String, Double> breakdown;
    private Map<String, String> calculationDetails; // New field for showing calculations

    public Bill(double totalAmount, Map<String, Double> breakdown) {
        this.totalAmount = totalAmount;
        this.breakdown = breakdown;
        this.calculationDetails = new java.util.LinkedHashMap<>();
    }

    public Bill(double totalAmount, Map<String, Double> breakdown, Map<String, String> calculationDetails) {
        this.totalAmount = totalAmount;
        this.breakdown = breakdown;
        this.calculationDetails = calculationDetails;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public Map<String, Double> getBreakdown() {
        return breakdown;
    }

    public Map<String, String> getCalculationDetails() {
        return calculationDetails;
    }
}