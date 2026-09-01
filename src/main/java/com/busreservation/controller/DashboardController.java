package com.busreservation.controller;

import com.busreservation.dto.ApiResponse;
import com.busreservation.dto.BookingDTO;
import com.busreservation.dto.UserResponseDTO;
import com.busreservation.service.BookingService;
import com.busreservation.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Dashboard operations
 */
@Slf4j
@RestController
@RequestMapping("/v1/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class DashboardController {

    private final UserService userService;
    private final BookingService bookingService;

    /**
     * Dashboard DTO for dashboard data
     */
    @Data
    public static class DashboardDTO {
        private UserResponseDTO userProfile;
        private List<BookingDTO> recentBookings;
        private long totalBookings;
        private long activeBookings;
        private long cancelledBookings;
    }

    /**
     * Get dashboard data for user
     * GET /api/v1/dashboard/{userId}
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<DashboardDTO>> getDashboard(@PathVariable Long userId) {
        log.info("Fetching dashboard for user: {}", userId);

        try {
            // Get user profile
            UserResponseDTO userProfile = userService.getUserById(userId);

            // Get recent bookings
            List<BookingDTO> recentBookings = bookingService.getRecentBookings(userId, 5);

            // Get booking statistics
            List<BookingDTO> allBookings = bookingService.getBookingsByUser(userId);
            long totalBookings = allBookings.size();
            long activeBookings = bookingService.getActiveBookingsByUser(userId).size();
            long cancelledBookings = bookingService.getBookingsByStatus("CANCELLED").stream()
                    .filter(b -> b.getUserId().equals(userId))
                    .count();

            // Build dashboard
            DashboardDTO dashboard = new DashboardDTO();
            dashboard.setUserProfile(userProfile);
            dashboard.setRecentBookings(recentBookings);
            dashboard.setTotalBookings(totalBookings);
            dashboard.setActiveBookings(activeBookings);
            dashboard.setCancelledBookings(cancelledBookings);

            return ResponseEntity.ok(new ApiResponse<>(true, "Dashboard data retrieved successfully", dashboard));

        } catch (IllegalArgumentException e) {
            log.warn("Dashboard retrieval failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));

        } catch (Exception e) {
            log.error("Dashboard retrieval error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to retrieve dashboard: " + e.getMessage(), null));
        }
    }

    /**
     * Get booking summary for user
     * GET /api/v1/dashboard/{userId}/booking-summary
     */
    @GetMapping("/{userId}/booking-summary")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getBookingSummary(@PathVariable Long userId) {
        log.info("Fetching booking summary for user: {}", userId);

        try {
            List<BookingDTO> allBookings = bookingService.getBookingsByUser(userId);

            // Calculate statistics
            long totalBookings = allBookings.size();
            long confirmedBookings = bookingService.getBookingsByStatus("CONFIRMED").stream()
                    .filter(b -> b.getUserId().equals(userId))
                    .count();
            long pendingBookings = bookingService.getBookingsByStatus("PENDING").stream()
                    .filter(b -> b.getUserId().equals(userId))
                    .count();
            long cancelledBookings = bookingService.getBookingsByStatus("CANCELLED").stream()
                    .filter(b -> b.getUserId().equals(userId))
                    .count();

            // Calculate total amount spent
            double totalAmountSpent = allBookings.stream()
                    .filter(b -> "CONFIRMED".equals(b.getBookingStatus()))
                    .mapToDouble(BookingDTO::getTotalPrice)
                    .sum();

            Map<String, Object> summary = new HashMap<>();
            summary.put("totalBookings", totalBookings);
            summary.put("confirmedBookings", confirmedBookings);
            summary.put("pendingBookings", pendingBookings);
            summary.put("cancelledBookings", cancelledBookings);
            summary.put("totalAmountSpent", totalAmountSpent);

            return ResponseEntity.ok(new ApiResponse<>(true, "Booking summary retrieved successfully", summary));

        } catch (Exception e) {
            log.error("Booking summary retrieval error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to retrieve booking summary: " + e.getMessage(), null));
        }
    }

    /**
     * Get user statistics
     * GET /api/v1/dashboard/{userId}/stats
     */
    @GetMapping("/{userId}/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserStats(@PathVariable Long userId) {
        log.info("Fetching user statistics for userId: {}", userId);

        try {
            UserResponseDTO user = userService.getUserById(userId);
            List<BookingDTO> bookings = bookingService.getBookingsByUser(userId);

            Map<String, Object> stats = new HashMap<>();
            stats.put("userId", userId);
            stats.put("username", user.getUsername());
            stats.put("email", user.getEmail());
            stats.put("totalBookings", bookings.size());
            stats.put("memberSince", user.getCreatedAt());

            // Bus type preferences
            Map<String, Long> busTypeCount = new HashMap<>();
            bookings.forEach(b -> {
                String busType = b.getBookingStatus(); // This would be from bus in real scenario
                busTypeCount.put(busType, busTypeCount.getOrDefault(busType, 0L) + 1);
            });
            stats.put("bookingByStatus", busTypeCount);

            return ResponseEntity.ok(new ApiResponse<>(true, "User statistics retrieved successfully", stats));

        } catch (IllegalArgumentException e) {
            log.warn("Statistics retrieval failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));

        } catch (Exception e) {
            log.error("Statistics retrieval error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to retrieve statistics: " + e.getMessage(), null));
        }
    }
}
