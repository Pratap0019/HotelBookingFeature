package com.pratap.hotel;

import com.pratap.hotel.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.util.Arrays;

public class HotelTest {

    // Current flat rates (no surcharges):
    // SINGLE: 2000, DOUBLE: 3500, SUITE: 5000
    // Extras: MATTRESS=500/day, SPA=1500/session, GymPASS=500/day, PoolPASS=500/day
    // Pet fees: under8kg=200, under15kg=350, over15kg=500
    // Service charge: 10% of subtotal

    @Test
    void testSingleRoomNoExtrasNoPet() {
        Bill bill = SunMoonResort.calculateBill(101, 1, null, null);
        double base = 2000.0;
        double expected = base + base * 0.1;
        Assertions.assertEquals(expected, bill.getTotalAmount(), 0.01);
    }

    @Test
    void testDoubleRoomOneDay() {
        Bill bill = SunMoonResort.calculateBill(102, 1, null, null);
        double base = 3500.0;
        double expected = base + base * 0.1;
        Assertions.assertEquals(expected, bill.getTotalAmount(), 0.01);
    }

    @Test
    void testSuiteTwoDays() {
        Bill bill = SunMoonResort.calculateBill(203, 2, null, null);
        double roomCharge = 5000.0 * 2;
        double expected = roomCharge + roomCharge * 0.1;
        Assertions.assertEquals(expected, bill.getTotalAmount(), 0.01);
    }

    @Test
    void testRoomWithMattressExtra() {
        Bill bill = SunMoonResort.calculateBill(101, 1, Arrays.asList(Extras.MATTRESS), null);
        double base = 2000.0;
        double extra = 500.0;
        double subtotal = base + extra;
        double expected = subtotal + subtotal * 0.1;
        Assertions.assertEquals(expected, bill.getTotalAmount(), 0.01);
    }

    @Test
    void testRoomWithMultipleExtras() {
        Bill bill = SunMoonResort.calculateBill(101, 1, Arrays.asList(Extras.MATTRESS, Extras.GymPASS), null);
        double base = 2000.0;
        double extras = 500.0 + 500.0;
        double subtotal = base + extras;
        double expected = subtotal + subtotal * 0.1;
        Assertions.assertEquals(expected, bill.getTotalAmount(), 0.01);
    }

    @Test
    void testRoomWithPetSmall() {
        Bill bill = SunMoonResort.calculateBill(101, 1, null, 5.0);
        double base = 2000.0;
        double pet = 200.0;
        double subtotal = base + pet;
        double expected = subtotal + subtotal * 0.1;
        Assertions.assertEquals(expected, bill.getTotalAmount(), 0.01);
    }

    @Test
    void testRoomWithPetMedium() {
        Bill bill = SunMoonResort.calculateBill(101, 1, null, 12.0);
        double base = 2000.0;
        double pet = 350.0;
        double subtotal = base + pet;
        double expected = subtotal + subtotal * 0.1;
        Assertions.assertEquals(expected, bill.getTotalAmount(), 0.01);
    }

    @Test
    void testRoomWithPetLarge() {
        Bill bill = SunMoonResort.calculateBill(101, 1, null, 20.0);
        double base = 2000.0;
        double pet = 500.0;
        double subtotal = base + pet;
        double expected = subtotal + subtotal * 0.1;
        Assertions.assertEquals(expected, bill.getTotalAmount(), 0.01);
    }

    @Test
    void testFiveDayStay() {
        Bill bill = SunMoonResort.calculateBill(101, 5, null, null);
        double roomCharge = 2000.0 * 5;
        double expected = roomCharge + roomCharge * 0.1;
        Assertions.assertEquals(expected, bill.getTotalAmount(), 0.01);
    }

    @Test
    void testComplexScenario() {
        Bill bill = SunMoonResort.calculateBill(203, 2,
                Arrays.asList(Extras.MATTRESS, Extras.PoolPASS), 10.0);
        double roomCharge = 5000.0 * 2;
        double extras = (500.0 + 500.0) * 2;
        double pet = 350.0;
        double subtotal = roomCharge + extras + pet;
        double expected = subtotal + subtotal * 0.1;
        Assertions.assertEquals(expected, bill.getTotalAmount(), 0.01);
    }

    @Test
    void testSpaSessionsSingle() {
        Bill bill = SunMoonResort.calculateBill(101, 1, Arrays.asList(Extras.SPA), null, 2);
        double base = 2000.0;
        double spa = 1500.0 * 2;
        double subtotal = base + spa;
        double expected = subtotal + subtotal * 0.1;
        Assertions.assertEquals(expected, bill.getTotalAmount(), 0.01);
    }

    @Test
    void testInvalidRoomNumber() {
        Assertions.assertThrows(RuntimeException.class,
                () -> SunMoonResort.calculateBill(999, 1, null, null));
    }
}
