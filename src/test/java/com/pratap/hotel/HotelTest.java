package com.pratap.hotel;

import org.junit.jupiter.api.Test;

import com.pratap.hotel.model.Bill;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class HotelTest {

    @Test
    void test1() {
        int roomNumber = 102; // Room with a pet (4kg)
        int daysStayed = 5;
        Bill bill = Hotel.calculateBill(roomNumber, daysStayed);

        double expectedTotalAmount = 555.5;
        double expectedRoomCharge = 505.0;
        double expectedServiceCharge = 50.5;

        assertEquals(expectedTotalAmount, bill.getTotalAmount());
        assertEquals(expectedRoomCharge, bill.getBreakdown().get("Room Charge"));
        assertEquals(expectedServiceCharge, bill.getBreakdown().get("Service Charge"));
    }

    @Test
    void test2() {
        int roomNumber = 103; // Room with a pet (12kg)
        int daysStayed = 3;
        Bill bill = Hotel.calculateBill(roomNumber, daysStayed);

        double expectedTotalAmount = 709.5;
        assertEquals(expectedTotalAmount, bill.getTotalAmount());

        double expectedRoomCharge = 645.0;
        double expectedServiceCharge = 64.5;

        assertEquals(expectedRoomCharge, bill.getBreakdown().get("Room Charge"));
        assertEquals(expectedServiceCharge, bill.getBreakdown().get("Service Charge"));
    }

    @Test
    void test3() {
        int nonExistingRoomNumber = 999;
        int daysStayed = 5;

        Exception exception = assertThrows(RuntimeException.class,
                () -> Hotel.calculateBill(nonExistingRoomNumber, daysStayed));

        String expectedErrorMessage = "Room not found.";
        assertEquals(expectedErrorMessage, exception.getMessage());
    }
}