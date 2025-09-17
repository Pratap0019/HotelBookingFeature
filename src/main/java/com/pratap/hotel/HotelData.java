package com.pratap.hotel;

import com.pratap.hotel.model.Extras;
import com.pratap.hotel.model.Pet;
import com.pratap.hotel.model.Room;
import com.pratap.hotel.model.RoomType;

import java.util.*;

public class HotelData {

    public static final List<Room> ROOMS = new ArrayList<>();
    public static final Map<RoomType, Double> ROOM_RATES = new HashMap<>();
    public static final Map<Extras, Double> EXTRAS_RATE = new HashMap<>();
    public static final Map<String, Double> PET_FEE_RATES = new HashMap<>();

    static {
        // Add multiple rooms
        ROOMS.add(new Room(101, RoomType.SINGLE, false, false, Arrays.asList(Extras.WIFI), null));
        ROOMS.add(new Room(102, RoomType.DOUBLE, false, true, Collections.emptyList(), new Pet(4)));
        ROOMS.add(new Room(103, RoomType.SUITE, false, true, Arrays.asList(Extras.MINIBAR, Extras.WIFI), new Pet(12)));
        ROOMS.add(new Room(104, RoomType.SINGLE, true, false, Arrays.asList(Extras.WIFI), null));
        ROOMS.add(new Room(105, RoomType.DOUBLE, true, false, Arrays.asList(Extras.MINIBAR), null));
        ROOMS.add(new Room(106, RoomType.SUITE, true, true, Arrays.asList(Extras.MINIBAR, Extras.WIFI), new Pet(8)));

        // Room rates
        ROOM_RATES.put(RoomType.SINGLE, 50.0);
        ROOM_RATES.put(RoomType.DOUBLE, 80.0);
        ROOM_RATES.put(RoomType.SUITE, 150.0);

        // Extras
        EXTRAS_RATE.put(Extras.MINIBAR, 15.0);
        EXTRAS_RATE.put(Extras.WIFI, 5.0);

        // Pet fees
        PET_FEE_RATES.put("under5kg", 5.0);
        PET_FEE_RATES.put("under10kg", 10.0);
        PET_FEE_RATES.put("over10kg", 15.0);
    }
}
