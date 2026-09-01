package com.busreservation.controller;

import com.busreservation.dto.ApiResponse;
import com.busreservation.dto.SeatDTO;
import com.busreservation.service.BusService;
import com.busreservation.service.SeatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Seat operations
 */
@Slf4j
@RestController
@RequestMapping("/v1/seats")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class SeatController {

    private final SeatService seatService;
    private final BusService busService;

    /**
     * Get available seats for a bus on a specific date
     * GET /api/v1/seats/bus/{busId}/available
     */
    @GetMapping("/bus/{busId}/available")
    public ResponseEntity<ApiResponse<List<SeatDTO>>> getAvailableSeats(
            @PathVariable Long busId,
            @RequestParam String journeyDate) {
        log.info("Fetching available seats for bus: {} on date: {}", busId, journeyDate);

        try {
            // Validate input
            if (journeyDate == null || journeyDate.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Journey date is required", null));
            }

            // Ensure bus exists
            busService.getBusById(busId, journeyDate);

            List<SeatDTO> seats = seatService.getAvailableSeatsForBusAndDate(busId, journeyDate);
            return ResponseEntity.ok(new ApiResponse<>(true, "Available seats retrieved successfully", seats));

        } catch (IllegalArgumentException e) {
            log.warn("Seats retrieval failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));

        } catch (Exception e) {
            log.error("Seats retrieval error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to retrieve seats: " + e.getMessage(), null));
        }
    }

    /**
     * Get all seats (both available and booked) for a bus on a specific date
     * GET /api/v1/seats/bus/{busId}
     */
    @GetMapping("/bus/{busId}")
    public ResponseEntity<ApiResponse<List<SeatDTO>>> getAllSeatsForBus(
            @PathVariable Long busId,
            @RequestParam String journeyDate) {
        log.info("Fetching all seats for bus: {} on date: {}", busId, journeyDate);

        try {
            // Validate input
            if (journeyDate == null || journeyDate.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Journey date is required", null));
            }

            // Get bus
            var bus = busService.getBusEntityById(busId);

            List<SeatDTO> seats = seatService.getSeatsForBusAndDate(bus, journeyDate);
            return ResponseEntity.ok(new ApiResponse<>(true, "Seats retrieved successfully", seats));

        } catch (IllegalArgumentException e) {
            log.warn("Seats retrieval failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));

        } catch (Exception e) {
            log.error("Seats retrieval error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to retrieve seats: " + e.getMessage(), null));
        }
    }

    /**
     * Get seat by ID
     * GET /api/v1/seats/{seatId}
     */
    @GetMapping("/{seatId}")
    public ResponseEntity<ApiResponse<SeatDTO>> getSeatById(@PathVariable Long seatId) {
        log.info("Fetching seat with seatId: {}", seatId);

        try {
            SeatDTO seat = seatService.getSeatById(seatId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Seat retrieved successfully", seat));

        } catch (IllegalArgumentException e) {
            log.warn("Seat not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));

        } catch (Exception e) {
            log.error("Seat retrieval error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to retrieve seat: " + e.getMessage(), null));
        }
    }

    /**
     * Count available seats for a bus on a specific date
     * GET /api/v1/seats/bus/{busId}/count
     */
    @GetMapping("/bus/{busId}/count")
    public ResponseEntity<ApiResponse<Long>> countAvailableSeats(
            @PathVariable Long busId,
            @RequestParam String journeyDate) {
        log.info("Counting available seats for bus: {} on date: {}", busId, journeyDate);

        try {
            // Validate input
            if (journeyDate == null || journeyDate.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Journey date is required", null));
            }

            // Ensure bus exists
            busService.getBusById(busId, journeyDate);

            long count = seatService.countAvailableSeats(busId, journeyDate);
            return ResponseEntity.ok(new ApiResponse<>(true, "Available seat count retrieved successfully", count));

        } catch (IllegalArgumentException e) {
            log.warn("Seat count retrieval failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));

        } catch (Exception e) {
            log.error("Seat count retrieval error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to retrieve seat count: " + e.getMessage(), null));
        }
    }
}
