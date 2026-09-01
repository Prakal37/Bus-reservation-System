package com.busreservation.service;

import com.busreservation.dto.SeatDTO;
import com.busreservation.entity.Bus;
import com.busreservation.entity.Seat;
import com.busreservation.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for Seat related operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;
    private final ModelMapper modelMapper;

    /**
     * Initialize seats for a bus on a specific date
     */
    @Transactional
    public void initializeSeatsForBusAndDate(Bus bus, String journeyDate) {
        log.info("Initializing seats for bus {} on date {}", bus.getBusId(), journeyDate);

        // Check if seats already exist for this date
        List<Seat> existingSeats = seatRepository.findByBusAndBookingDate(bus, journeyDate);
        if (!existingSeats.isEmpty()) {
            log.info("Seats already initialized for bus {} on date {}", bus.getBusId(), journeyDate);
            return;
        }

        // Create seats for each seat number
        List<Seat> seatsToCreate = new ArrayList<>();
        for (int i = 1; i <= bus.getTotalSeats(); i++) {
            Seat seat = new Seat();
            seat.setBus(bus);
            seat.setSeatNumber(i);
            seat.setSeatType(getSeatType(i, bus.getTotalSeats()));
            seat.setBookingDate(journeyDate);
            seat.setIsAvailable(true);
            seatsToCreate.add(seat);
        }

        seatRepository.saveAll(seatsToCreate);
        log.info("Seats initialized successfully for bus {} on date {}", bus.getBusId(), journeyDate);
    }

    /**
     * Get available seats for a bus on a specific date
     */
    public List<SeatDTO> getAvailableSeatsForBusAndDate(Long busId, String journeyDate) {
        log.info("Fetching available seats for bus {} on date {}", busId, journeyDate);
        List<Seat> availableSeats = seatRepository.findAvailableSeatsByBusAndDate(busId, journeyDate);
        return availableSeats.stream()
                .map(seat -> modelMapper.map(seat, SeatDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Get all seats for a bus on a specific date
     */
    public List<SeatDTO> getSeatsForBusAndDate(Bus bus, String journeyDate) {
        log.info("Fetching all seats for bus {} on date {}", bus.getBusId(), journeyDate);
        List<Seat> seats = seatRepository.findByBusAndBookingDate(bus, journeyDate);
        if (seats.isEmpty()) {
            // Initialize seats if they don't exist
            initializeSeatsForBusAndDate(bus, journeyDate);
            seats = seatRepository.findByBusAndBookingDate(bus, journeyDate);
        }
        return seats.stream()
                .map(seat -> modelMapper.map(seat, SeatDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Get seat by ID
     */
    public SeatDTO getSeatById(Long seatId) {
        log.info("Fetching seat by seatId: {}", seatId);
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new IllegalArgumentException("Seat not found with seatId: " + seatId));
        return modelMapper.map(seat, SeatDTO.class);
    }

    /**
     * Check if seats are available
     */
    public boolean areSeatsAvailable(Bus bus, List<Integer> seatNumbers, String journeyDate) {
        log.info("Checking availability of seats for bus {} on date {}", bus.getBusId(), journeyDate);
        
        for (Integer seatNumber : seatNumbers) {
            Seat seat = seatRepository.findByBusAndSeatNumber(bus, seatNumber)
                    .orElse(null);
            
            if (seat == null || !seat.getIsAvailable() || !journeyDate.equals(seat.getBookingDate())) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Mark seats as booked
     */
    @Transactional
    public void markSeatsAsBooked(Bus bus, List<Integer> seatNumbers, String journeyDate) {
        log.info("Marking seats as booked for bus {} on date {}", bus.getBusId(), journeyDate);

        for (Integer seatNumber : seatNumbers) {
            Seat seat = seatRepository.findByBusAndSeatNumber(bus, seatNumber)
                    .orElseThrow(() -> new IllegalArgumentException("Seat not found: " + seatNumber));

            seat.setIsAvailable(false);
            seatRepository.save(seat);
        }

        log.info("Seats marked as booked successfully");
    }

    /**
     * Mark seats as available (for cancellation)
     */
    @Transactional
    public void markSeatsAsAvailable(Bus bus, List<Integer> seatNumbers, String journeyDate) {
        log.info("Marking seats as available for bus {} on date {}", bus.getBusId(), journeyDate);

        for (Integer seatNumber : seatNumbers) {
            Seat seat = seatRepository.findByBusAndSeatNumber(bus, seatNumber)
                    .orElseThrow(() -> new IllegalArgumentException("Seat not found: " + seatNumber));

            seat.setIsAvailable(true);
            seatRepository.save(seat);
        }

        log.info("Seats marked as available successfully");
    }

    /**
     * Count available seats for a bus on a specific date
     */
    public long countAvailableSeats(Long busId, String journeyDate) {
        return seatRepository.countAvailableSeats(busId, journeyDate);
    }

    /**
     * Determine seat type based on seat number
     */
    private String getSeatType(int seatNumber, int totalSeats) {
        int seatsPerRow = 4; // Assuming 4 seats per row (Window, Middle, Middle, Aisle)
        int positionInRow = ((seatNumber - 1) % seatsPerRow) + 1;

        return switch (positionInRow) {
            case 1 -> "WINDOW";
            case 4 -> "AISLE";
            default -> "MIDDLE";
        };
    }

    /**
     * Get Seat entity by ID (for internal use)
     */
    public Seat getSeatEntityById(Long seatId) {
        return seatRepository.findById(seatId)
                .orElseThrow(() -> new IllegalArgumentException("Seat not found with seatId: " + seatId));
    }
}
