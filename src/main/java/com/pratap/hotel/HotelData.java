package com.pratap.hotel;

import java.util.*;
import com.pratap.hotel.model.Extras;
import com.pratap.hotel.model.Pet;
import com.pratap.hotel.model.RoomType;
import com.pratap.hotel.model.Room;
import com.pratap.hotel.model.Guest;

public class HotelData {

    public static final List<Room> ROOMS = new ArrayList<>();
    public static final Map<RoomType, Double> ROOM_RATES = new HashMap<>();
    public static final Map<Extras, Double> EXTRAS_RATE = new HashMap<>();
    public static final Map<String, Double> PET_FEE_RATES = new HashMap<>();
    // stores confirmed bookings: roomNumber -> Guest details
    public static final Map<Integer, Guest> BOOKINGS = new HashMap<>();

    static {
        // Add available rooms
        ROOMS.add(new Room(101, RoomType.SINGLE, false, false, false));
        ROOMS.add(new Room(102, RoomType.DOUBLE, false, false, true));
        ROOMS.add(new Room(103, RoomType.SUITE, false, true, true));
        ROOMS.add(new Room(104, RoomType.SINGLE, false, false, false));
        ROOMS.add(new Room(105, RoomType.DOUBLE, false, false, true));
        ROOMS.add(new Room(106, RoomType.SUITE, false, true, true));
        ROOMS.add(new Room(201, RoomType.SINGLE, false, false, false));
        ROOMS.add(new Room(202, RoomType.DOUBLE, false, false, true));
        ROOMS.add(new Room(203, RoomType.SUITE, false, true, true));
        ROOMS.add(new Room(204, RoomType.SINGLE, false, false, false));
        ROOMS.add(new Room(205, RoomType.DOUBLE, false, false, true));
        ROOMS.add(new Room(206, RoomType.SUITE, false, true, true));
        ROOMS.add(new Room(301, RoomType.SINGLE, false, false, false));
        ROOMS.add(new Room(302, RoomType.DOUBLE, false, false, true));
        ROOMS.add(new Room(303, RoomType.SUITE, false, true, true));
        ROOMS.add(new Room(304, RoomType.SINGLE, false, false, false));
        ROOMS.add(new Room(305, RoomType.DOUBLE, false, false, true));
        ROOMS.add(new Room(306, RoomType.SUITE, false, true, true));

        // Room rates
        ROOM_RATES.put(RoomType.SINGLE, 1500.0);
        ROOM_RATES.put(RoomType.DOUBLE, 2500.0);
        ROOM_RATES.put(RoomType.SUITE, 4000.0);

        // Extras
        EXTRAS_RATE.put(Extras.MINIFRIDGE, 500.0);
        EXTRAS_RATE.put(Extras.WIFI, 100.0);
        EXTRAS_RATE.put(Extras.MATTRESS, 500.0);
        EXTRAS_RATE.put(Extras.SPA, 1500.0);
        EXTRAS_RATE.put(Extras.GymPASS, 500.0);
        EXTRAS_RATE.put(Extras.PoolPASS, 500.0);

        // Pet fees
        PET_FEE_RATES.put("under8kg", 200.0);
        PET_FEE_RATES.put("under15kg", 350.0);
        PET_FEE_RATES.put("over15kg", 500.0);
        //a
    }
}
