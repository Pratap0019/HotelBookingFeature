package com.pratap.hotel;

import java.util.*;
import com.pratap.hotel.model.Extras;
import com.pratap.hotel.model.Pet;
import com.pratap.hotel.model.RoomType;
import com.pratap.hotel.model.Room;

public class HotelData {

    public static final List<Room> ROOMS = new ArrayList<>();
    public static final Map<RoomType, Double> ROOM_RATES = new HashMap<>();
    public static final Map<Extras, Double> EXTRAS_RATE = new HashMap<>();
    public static final Map<String, Double> PET_FEE_RATES = new HashMap<>();

    static {
        // Add available rooms
        ROOMS.add(new Room(101, RoomType.SINGLE, false, false, false));
        ROOMS.add(new Room(102, RoomType.DOUBLE, false, true, false));
        ROOMS.add(new Room(103, RoomType.SUITE, false, true, false));
        ROOMS.add(new Room(104, RoomType.SINGLE, false, false, false));
        ROOMS.add(new Room(105, RoomType.DOUBLE, false, false, false));
        ROOMS.add(new Room(106, RoomType.SUITE, false, true, false));
        ROOMS.add(new Room(201, RoomType.SINGLE, false, false, false));
        ROOMS.add(new Room(202, RoomType.DOUBLE, false, true, false));
        ROOMS.add(new Room(203, RoomType.SUITE, false, true, true));
        ROOMS.add(new Room(204, RoomType.SINGLE, false, false, false));
        ROOMS.add(new Room(205, RoomType.DOUBLE, false, false, true));
        ROOMS.add(new Room(206, RoomType.SUITE, false, true, true));

        // Room rates
        ROOM_RATES.put(RoomType.SINGLE, 800.0);
        ROOM_RATES.put(RoomType.DOUBLE, 1200.0);
        ROOM_RATES.put(RoomType.SUITE, 2500.0);

        // Extras
        EXTRAS_RATE.put(Extras.MINIFRIDGE, 200.0);
        EXTRAS_RATE.put(Extras.WIFI, 100.0);
        EXTRAS_RATE.put(Extras.MATTRESS, 200.0);

        // Pet fees
        PET_FEE_RATES.put("under8kg", 100.0);
        PET_FEE_RATES.put("under15kg", 150.0);
        PET_FEE_RATES.put("over15kg", 200.0);
    }
}
