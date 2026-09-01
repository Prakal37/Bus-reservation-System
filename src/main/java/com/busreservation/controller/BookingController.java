package com.busreservation.controller;

import com.busreservation.dto.ApiResponse;
import com.busreservation.dto.BookingDTO;
import com.busreservation.dto.BookingRequestDTO;
import com.busreservation.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Booking operations
 */
@Slf4j
@RestController
@RequestMapping("/v1/bookings")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class BookingController {

    private final BookingService bookingService;

    /**
     * Create a new booking
     * POST /api/v1/bookings
     */
    @PostMapping
    public ResponseEntity<ApiResponse<BookingDTO>> createBooking(
            @RequestHeader(value = "userId", required = false) Long userId,
            @RequestBody BookingRequestDTO bookingRequest) {
        log.info("Creating booking for user: {} on bus: {}", userId, bookingRequest.getBusId());

        try {
            // Validate input
            if (userId == null || userId <= 0) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "User ID is required (userId header)", null));
            }

            if (bookingRequest.getBusId() == null || bookingRequest.getBusId() <= 0) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Bus ID is required", null));
            }

            if (bookingRequest.getSeatNumbers() == null || bookingRequest.getSeatNumbers().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "At least one seat must be selected", null));
            }

            if (bookingRequest.getJourneyDate() == null || bookingRequest.getJourneyDate().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Journey date is required", null));
            }

            if (bookingRequest.getPassengerName() == null || bookingRequest.getPassengerName().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Passenger name is required", null));
            }

            BookingDTO booking = bookingService.createBooking(userId, bookingRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, "Booking created successfully", booking));

        } catch (IllegalArgumentException e) {
            log.warn("Booking creation failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));

        } catch (Exception e) {
            log.error("Booking creation error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Booking creation failed: " + e.getMessage(), null));
        }
    }

    /**
     * Get booking by ID
     * GET /api/v1/bookings/{bookingId}
     */
    @GetMapping("/{bookingId}")
    public ResponseEntity<ApiResponse<BookingDTO>> getBookingById(@PathVariable Long bookingId) {
        log.info("Fetching booking with bookingId: {}", bookingId);

        try {
            BookingDTO booking = bookingService.getBookingById(bookingId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Booking retrieved successfully", booking));

        } catch (IllegalArgumentException e) {
            log.warn("Booking not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));

        } catch (Exception e) {
            log.error("Booking retrieval error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to retrieve booking: " + e.getMessage(), null));
        }
    }

    /**
     * Get booking by reference code
     * GET /api/v1/bookings/reference/{bookingReference}
     */
    @GetMapping("/reference/{bookingReference}")
    public ResponseEntity<ApiResponse<BookingDTO>> getBookingByReference(@PathVariable String bookingReference) {
        log.info("Fetching booking with reference: {}", bookingReference);

        try {
            BookingDTO booking = bookingService.getBookingByReference(bookingReference);
            return ResponseEntity.ok(new ApiResponse<>(true, "Booking retrieved successfully", booking));

        } catch (IllegalArgumentException e) {
            log.warn("Booking not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));

        } catch (Exception e) {
            log.error("Booking retrieval error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to retrieve booking: " + e.getMessage(), null));
        }
    }

    /**
     * Get all bookings for a user
     * GET /api/v1/bookings/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<BookingDTO>>> getBookingsByUser(@PathVariable Long userId) {
        log.info("Fetching bookings for user: {}", userId);

        try {
            List<BookingDTO> bookings = bookingService.getBookingsByUser(userId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Bookings retrieved successfully", bookings));

        } catch (Exception e) {
            log.error("Bookings retrieval error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to retrieve bookings: " + e.getMessage(), null));
        }
    }

    /**
     * Get active bookings for a user (for dashboard)
     * GET /api/v1/bookings/user/{userId}/active
     */
    @GetMapping("/user/{userId}/active")
    public ResponseEntity<ApiResponse<List<BookingDTO>>> getActiveBookingsByUser(@PathVariable Long userId) {
        log.info("Fetching active bookings for user: {}", userId);

        try {
            List<BookingDTO> bookings = bookingService.getActiveBookingsByUser(userId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Active bookings retrieved successfully", bookings));

        } catch (Exception e) {
            log.error("Active bookings retrieval error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to retrieve bookings: " + e.getMessage(), null));
        }
    }

    /**
     * Get recent bookings for user (for dashboard)
     * GET /api/v1/bookings/user/{userId}/recent
     */
    @GetMapping("/user/{userId}/recent")
    public ResponseEntity<ApiResponse<List<BookingDTO>>> getRecentBookings(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "5") int limit) {
        log.info("Fetching recent bookings for user: {} with limit: {}", userId, limit);

        try {
            List<BookingDTO> bookings = bookingService.getRecentBookings(userId, limit);
            return ResponseEntity.ok(new ApiResponse<>(true, "Recent bookings retrieved successfully", bookings));

        } catch (Exception e) {
            log.error("Recent bookings retrieval error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to retrieve bookings: " + e.getMessage(), null));
        }
    }

    /**
     * Confirm booking (after payment)
     * PUT /api/v1/bookings/{bookingId}/confirm
     */
    @PutMapping("/{bookingId}/confirm")
    public ResponseEntity<ApiResponse<BookingDTO>> confirmBooking(@PathVariable Long bookingId) {
        log.info("Confirming booking with bookingId: {}", bookingId);

        try {
            BookingDTO booking = bookingService.confirmBooking(bookingId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Booking confirmed successfully", booking));

        } catch (IllegalArgumentException e) {
            log.warn("Booking confirmation failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));

        } catch (Exception e) {
            log.error("Booking confirmation error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Booking confirmation failed: " + e.getMessage(), null));
        }
    }

    /**
     * Cancel booking
     * PUT /api/v1/bookings/{bookingId}/cancel
     */
    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<ApiResponse<BookingDTO>> cancelBooking(@PathVariable Long bookingId) {
        log.info("Cancelling booking with bookingId: {}", bookingId);

        try {
            BookingDTO booking = bookingService.cancelBooking(bookingId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Booking cancelled successfully", booking));

        } catch (IllegalArgumentException e) {
            log.warn("Booking cancellation failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));

        } catch (Exception e) {
            log.error("Booking cancellation error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Booking cancellation failed: " + e.getMessage(), null));
        }
    }

    /**
     * Get bookings by status
     * GET /api/v1/bookings/status/{status}
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<BookingDTO>>> getBookingsByStatus(@PathVariable String status) {
        log.info("Fetching bookings by status: {}", status);

        try {
            List<BookingDTO> bookings = bookingService.getBookingsByStatus(status);
            return ResponseEntity.ok(new ApiResponse<>(true, "Bookings retrieved successfully", bookings));

        } catch (Exception e) {
            log.error("Bookings retrieval error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to retrieve bookings: " + e.getMessage(), null));
        }
    }
}
