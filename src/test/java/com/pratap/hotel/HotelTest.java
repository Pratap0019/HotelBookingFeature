package com.pratap.hotel;

import com.pratap.hotel.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.util.Arrays;

public class HotelTest {

    @Test
    void testSingleRoomNoExtrasNoPet() {
        Bill bill = PratapHotel.calculateBill(101, 1, null, null);
        double base = 800.0; // SINGLE
        double expected = base + base * 0.1; // service charge
        Assertions.assertEquals(expected, bill.getTotalAmount(), 0.01);
    }

    @Test
    void testDoubleRoomWithBalcony() {
        Bill bill = PratapHotel.calculateBill(102, 1, null, null);
        double base = 1200.0;
        base += base * 0.2; // seaview surcharge
        double expected = base + base * 0.1;
        Assertions.assertEquals(expected, bill.getTotalAmount(), 0.01);
    }

    @Test
    void testSuiteWithBalconyTwoDays() {
        Bill bill = PratapHotel.calculateBill(203, 2, null, null);
        double dailyPrice = 2500.0;
        double base = dailyPrice;
        base += dailyPrice * 0.1; // balcony surcharge
        base += dailyPrice * 0.2; // seaview surcharge
        double roomCharge = base * 2;
        double expected = roomCharge + roomCharge * 0.1;
        Assertions.assertEquals(expected, bill.getTotalAmount(), 0.01);
    }

    @Test
    void testRoomWithWifiExtra() {
        Bill bill = PratapHotel.calculateBill(101, 1, Arrays.asList(Extras.WIFI), null);
        double base = 800.0;
        double extra = 200.0;
        double subtotal = base + extra;
        double expected = subtotal + subtotal * 0.1;
        Assertions.assertEquals(expected, bill.getTotalAmount(), 0.01);
    }

    @Test
    void testRoomWithMultipleExtras() {
        Bill bill = PratapHotel.calculateBill(101, 1, Arrays.asList(Extras.MINIFRIDGE, Extras.MATTRESS), null);
        double base = 800.0;
        double extra = 1000.0 + 300.0;
        double subtotal = base + extra;
        double expected = subtotal + subtotal * 0.1;
        Assertions.assertEquals(expected, bill.getTotalAmount(), 0.01);
    }

    @Test
    void testRoomWithPetSmall() {
        Bill bill = PratapHotel.calculateBill(101, 1, null, 5.0);
        double base = 800.0;
        double pet = 200.0;
        double subtotal = base + pet;
        double expected = subtotal + subtotal * 0.1;
        Assertions.assertEquals(expected, bill.getTotalAmount(), 0.01);
    }

    @Test
    void testRoomWithPetMedium() {
        Bill bill = PratapHotel.calculateBill(101, 1, null, 12.0);
        double base = 800.0;
        double pet = 350.0;
        double subtotal = base + pet;
        double expected = subtotal + subtotal * 0.1;
        Assertions.assertEquals(expected, bill.getTotalAmount(), 0.01);
    }

    @Test
    void testRoomWithPetLarge() {
        Bill bill = PratapHotel.calculateBill(101, 1, null, 20.0);
        double base = 800.0;
        double pet = 500.0;
        double subtotal = base + pet;
        double expected = subtotal + subtotal * 0.1;
        Assertions.assertEquals(expected, bill.getTotalAmount(), 0.01);
    }

    @Test
    void testFiveDayStay() {
        Bill bill = PratapHotel.calculateBill(101, 5, null, null);
        double base = 800.0 * 5;
        double expected = base + base * 0.1;
        Assertions.assertEquals(expected, bill.getTotalAmount(), 0.01);
    }

    @Test
    void testComplexScenario() {
        Bill bill = PratapHotel.calculateBill(203, 2,
                Arrays.asList(Extras.WIFI, Extras.MATTRESS), 10.0);
        double dailyPrice = 2500.0;
        double base = dailyPrice;
        base += dailyPrice * 0.1; // balcony surcharge
        base += dailyPrice * 0.2; // seaview surcharge
        double roomCharge = base * 2;
        double extras = 200.0 * 2 + 300.0 * 2;
        double pet = 350.0;
        double subtotal = roomCharge + extras + pet;
        double expected = subtotal + subtotal * 0.1;
        Assertions.assertEquals(expected, bill.getTotalAmount(), 0.01);
    }

    @Test
    void testInvalidRoomNumber() {
        Assertions.assertThrows(RuntimeException.class,
                () -> PratapHotel.calculateBill(999, 1, null, null));
    }
}
