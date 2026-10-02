package com.busreservation.config;

import com.busreservation.entity.Bus;
import com.busreservation.entity.Route;
import com.busreservation.repository.BusRepository;
import com.busreservation.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Seeds initial Tamil Nadu bus and route data into H2 database if empty
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RouteRepository routeRepository;
    private final BusRepository busRepository;

    @Override
    public void run(String... args) throws Exception {
        if (routeRepository.count() > 0 && busRepository.count() > 0) {
            log.info("Database already contains route/bus data. Skipping initialization.");
            return;
        }

        log.info("Initializing demo Tamil Nadu routes and buses in H2 database...");

        // 1. Chennai -> Madurai
        Route r1 = createRoute("Chennai", "Madurai", 460.0, 8);
        createBus("SETC Ultra Deluxe", "TN-01-N-1234", 40, "AC Sleeper", "WiFi, Charging Port, Blanket", r1, "21:00", "05:30", 520.0, "SETC");
        createBus("KPN Travels", "TN-38-AB-5678", 40, "AC Seater", "WiFi, Reclining Seats", r1, "22:15", "06:15", 650.0, "KPN Travels");
        createBus("TNSTC Express", "TN-58-N-9012", 40, "Non-AC Seater", "Reading Light", r1, "20:30", "05:00", 480.0, "TNSTC");

        // 2. Chennai -> Coimbatore
        Route r2 = createRoute("Chennai", "Coimbatore", 500.0, 9);
        createBus("Parveen Travels", "TN-02-C-4321", 40, "AC Sleeper", "WiFi, TV, Charging Port", r2, "21:30", "06:30", 750.0, "Parveen Travels");
        createBus("SETC Deluxe", "TN-37-N-8765", 40, "AC Semi-Sleeper", "Water Bottle, Charging Port", r2, "22:00", "07:00", 580.0, "SETC");

        // 3. Coimbatore -> Madurai
        Route r3 = createRoute("Coimbatore", "Madurai", 210.0, 4);
        createBus("TNSTC Super Fast", "TN-38-N-3456", 40, "Non-AC Seater", "Express", r3, "06:00", "10:00", 250.0, "TNSTC");
        createBus("KPN Express", "TN-38-XY-7890", 40, "AC Seater", "Reclining Seats", r3, "14:30", "18:30", 350.0, "KPN Travels");

        // 4. Chennai -> Tirunelveli
        Route r4 = createRoute("Chennai", "Tirunelveli", 620.0, 10);
        createBus("SETC Ultra Deluxe", "TN-72-N-1122", 40, "AC Sleeper", "WiFi, Charging Port, Pillow", r4, "19:30", "05:30", 720.0, "SETC");

        // 5. Madurai -> Nagercoil
        Route r5 = createRoute("Madurai", "Nagercoil", 240.0, 4);
        createBus("TNSTC Deluxe", "TN-74-N-3344", 40, "Non-AC Seater", "Standard", r5, "08:00", "12:00", 220.0, "TNSTC");

        // 6. Tiruchirappalli -> Chennai
        Route r6 = createRoute("Tiruchirappalli", "Chennai", 330.0, 6);
        createBus("Rathi Meena Travels", "TN-45-Z-5566", 40, "AC Semi-Sleeper", "Charging Port, Water Bottle", r6, "15:00", "21:00", 450.0, "Rathi Meena");

        // 7. Salem -> Chennai
        Route r7 = createRoute("Salem", "Chennai", 340.0, 6);
        createBus("TNSTC Express", "TN-27-N-7788", 40, "Non-AC Seater", "Express", r7, "07:00", "13:00", 380.0, "TNSTC");

        log.info("Demo Tamil Nadu bus and route data successfully loaded into H2 database.");
    }

    private Route createRoute(String source, String destination, Double distance, Integer durationHours) {
        Route route = new Route();
        route.setSource(source);
        route.setDestination(destination);
        route.setDistanceKm(distance);
        route.setApproximateDurationHours(durationHours);
        route.setIsActive(true);
        return routeRepository.save(route);
    }

    private void createBus(String name, String number, int totalSeats, String busType, String amenities, Route route, String depTime, String arrTime, double price, String operator) {
        Bus bus = new Bus();
        bus.setBusName(name);
        bus.setBusNumber(number);
        bus.setTotalSeats(totalSeats);
        bus.setBusType(busType);
        bus.setAmenities(amenities);
        bus.setRoute(route);
        bus.setDepartureTime(depTime);
        bus.setArrivalTime(arrTime);
        bus.setPricePerSeat(price);
        bus.setOperatorName(operator);
        bus.setIsActive(true);
        busRepository.save(bus);
    }
}
