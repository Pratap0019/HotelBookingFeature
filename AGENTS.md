# AGENTS.md - Hotel Booking Feature Guide

## Project Overview
A Spring Boot web application for hotel room booking and billing. The app allows users to select rooms, add extras (mini-fridge, WiFi, mattress), include pets, and generates itemized bills with automatic surcharges and service charges.

## Architecture & Data Flow

### Core Components
- **HotelBookingController** (`controller/`): Three main endpoints handle the booking workflow
  - `GET /` → Homepage (room availability display)
  - `GET /booking` → Booking form page
  - `POST /calculatePrice` → Computes bill based on form inputs, returns booking page with breakdown
  - `POST /confirmBooking` → Marks room as booked
  
- **PratapHotel** (business logic): Single static method `calculateBill(roomNumber, daysStayed, selectedExtras, petWeight)` that returns a `Bill` object with total amount and itemized breakdown map.

- **HotelData** (static data store): Contains four static collections initialized in static block:
  - `ROOMS`: List of 12 Room objects (hotel inventory)
  - `ROOM_RATES`: Map of RoomType → daily base rate (SINGLE: 800₹, DOUBLE: 1200₹, SUITE: 2500₹)
  - `EXTRAS_RATE`: Map of Extras enum → daily rate (MINIFRIDGE: 200₹, WIFI: 100₹, MATTRESS: 200₹)
  - `PET_FEE_RATES`: Map of weight ranges → per-day fee (under8kg: 100₹, under15kg: 150₹, over15kg: 200₹)

### Billing Algorithm (in PratapHotel.calculateBill)
1. Get room details from HotelData.ROOMS
2. Calculate daily rate: baseRate + seaView surcharge (20%) + balcony surcharge (10%)
3. Build breakdown map with entries: Room Charge, each Extra (if selected), Pet Fee (if present), Service Charge
4. All extras and pet fees are multiplied by daysStayed
5. Service charge = 10% of (room + extras + pet fees)
6. Return Bill with total and LinkedHashMap breakdown (preserves insertion order for display)

## Critical Developer Workflows

### Build & Run
```bash
# Build project
mvn clean install

# Run tests (filtered by **/*Test.java pattern via maven-surefire-plugin)
mvn test

# Run application locally (default port 8080)
mvn spring-boot:run
```

### Testing Approach
Use JUnit 5 (jupiter) with Assertions. Test file: `src/test/java/com/pratap/hotel/HotelTest.java`
- Tests verify bill calculations with varied scenarios: room types, day counts, extra combinations, pet weights
- Pet weight boundaries: ≤8kg, ≤15kg, >15kg trigger different fee tiers
- Always test edge case: invalid room number (999) throws RuntimeException

### Configuration
- Spring Boot version: 3.5.5
- Java version: 17
- View template engine: Thymeleaf (not JSP, though application.properties incorrectly lists JSP config)
- Bootstrap 5.1.3 for UI styling

## Project Patterns & Conventions

### Static Data Model (No Database)
All hotel data lives in `HotelData` static collections. This is the source-of-truth:
- When modifying rates, room inventory, or pet fees: edit HotelData static block
- Room objects are mutable (`.setBooked(boolean)` called on confirmBooking)
- Changes persist only in memory; app restart clears all bookings

### Bill Breakdown LinkedHashMap
The breakdown Map preserves insertion order (LinkedHashMap) and is rendered directly in `booking.html`:
```
Room Charge → price for all days
MINIFRIDGE → if selected, days × rate
WIFI → if selected, days × rate
MATTRESS → if selected, days × rate
Pet Fee → if weight provided, daily fee for stay duration
Service Charge → 10% of subtotal
```
Add new line items by inserting into the breakdown Map in PratapHotel.calculateBill.

### Surcharge Logic
Sea view (room.hasSeaView()) and balcony (room.hasBalcony()) apply percentage surcharges **once** to base rate before multiplying by days. Room 203 (SUITE with both) gets: 2500 + 250 (balcony 10%) + 500 (sea view 20%) = 3250 per day.

### Room & Extras Lookup
- Room lookup: Stream filter over HotelData.ROOMS by roomNumber → orElseThrow RuntimeException
- Extras: Use enum Extras for type-safe selection; controller receives List<Extras> from form checkboxes
- Pet fees use String keys ("under8kg", "under15kg", "over15kg") not enums; determine tier via weight conditionals

### Thymeleaf Template Variables
Both templates (homepage.html, booking.html) expect these Model attributes from controller:
- `rooms` → List<Room>
- `roomRates` → Map<RoomType, Double>
- `extrasRate` → Map<Extras, Double>
- `petFeeRates` → Map<String, Double>
- `bill` (optional, set after POST /calculatePrice) → Bill object

Template loops use `th:each` with thymeleaf syntax; method calls like `room.hasSeaView()` work directly in expressions.

## Key Files & Functions at a Glance

| File | Purpose | Key Method/Focus |
|------|---------|------------------|
| `HotelBookingApplication.java` | Spring Boot entry point | @SpringBootApplication main() |
| `HotelBookingController.java` | HTTP routing & data binding | showIndex, bookingPage, calculatePrice, confirmBooking |
| `PratapHotel.java` | Billing engine | calculateBill(int, int, List<Extras>, Double) → Bill |
| `HotelData.java` | Static data initialization | 4 static collections + static block setup |
| `model/Room.java` | Room entity | Fields: number, roomType, booked, seaView, balcony |
| `model/Bill.java` | Bill DTO | Fields: totalAmount, breakdown (Map) |
| `model/Extras.java` | Enum | MINIFRIDGE, WIFI, MATTRESS |
| `model/RoomType.java` | Enum | SINGLE, DOUBLE, SUITE |
| `templates/booking.html` | Booking & bill display | Form: room, dates, extras, pet weight; displays bill breakdown |

## Common Extension Points

1. **Add new extra type**: Add to Extras enum, add entry in HotelData.EXTRAS_RATE static block, add checkbox in booking.html form.

2. **Change surcharge rates**: Edit PratapHotel.calculateBill multipliers (currently 0.2 for sea view, 0.1 for balcony).

3. **Modify pet fee tiers**: Update conditional logic in PratapHotel.calculateBill (≤8, ≤15, >15 boundaries) and corresponding tiers in HotelData.PET_FEE_RATES.

4. **Add service charge percent**: Edit line `double serviceCharge = subtotal * 0.1;` in PratapHotel.calculateBill.

5. **Persist bookings**: Replace /confirmBooking endpoint logic to save Room state to database instead of only modifying in-memory object.

## Testing Expectations

- Test file covers: single room, multiple room types, surcharges, extras combinations, 3 pet weight tiers, multi-day stays, complex scenarios, invalid room exception
- Tests assume bill calculation works correctly; no integration tests for controller endpoints
- Modify bill calculation → run `mvn test` to verify old tests still pass or update expected values

