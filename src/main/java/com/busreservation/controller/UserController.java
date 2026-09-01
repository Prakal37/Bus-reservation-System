package com.busreservation.controller;

import com.busreservation.dto.ApiResponse;
import com.busreservation.dto.UserRegisterDTO;
import com.busreservation.dto.UserResponseDTO;
import com.busreservation.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for User profile operations
 */
@Slf4j
@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    private final UserService userService;

    /**
     * Get user by ID
     * GET /api/v1/users/{userId}
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUserById(@PathVariable Long userId) {
        log.info("Fetching user with userId: {}", userId);

        try {
            UserResponseDTO user = userService.getUserById(userId);
            return ResponseEntity.ok(new ApiResponse<>(true, "User retrieved successfully", user));

        } catch (IllegalArgumentException e) {
            log.warn("User not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));

        } catch (Exception e) {
            log.error("User retrieval error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to retrieve user: " + e.getMessage(), null));
        }
    }

    /**
     * Get user by email
     * GET /api/v1/users/email/{email}
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUserByEmail(@PathVariable String email) {
        log.info("Fetching user by email: {}", email);

        try {
            UserResponseDTO user = userService.getUserByEmail(email);
            return ResponseEntity.ok(new ApiResponse<>(true, "User retrieved successfully", user));

        } catch (IllegalArgumentException e) {
            log.warn("User not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));

        } catch (Exception e) {
            log.error("User retrieval error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to retrieve user: " + e.getMessage(), null));
        }
    }

    /**
     * Get user by username
     * GET /api/v1/users/username/{username}
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUserByUsername(@PathVariable String username) {
        log.info("Fetching user by username: {}", username);

        try {
            UserResponseDTO user = userService.getUserByUsername(username);
            return ResponseEntity.ok(new ApiResponse<>(true, "User retrieved successfully", user));

        } catch (IllegalArgumentException e) {
            log.warn("User not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));

        } catch (Exception e) {
            log.error("User retrieval error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to retrieve user: " + e.getMessage(), null));
        }
    }

    /**
     * Update user profile
     * PUT /api/v1/users/{userId}
     */
    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateUserProfile(
            @PathVariable Long userId,
            @RequestBody UserRegisterDTO updateDTO) {
        log.info("Updating user profile for userId: {}", userId);

        try {
            // Validate input
            if (updateDTO.getFirstName() == null || updateDTO.getFirstName().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "First name is required", null));
            }

            if (updateDTO.getLastName() == null || updateDTO.getLastName().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Last name is required", null));
            }

            UserResponseDTO user = userService.updateUserProfile(userId, updateDTO);
            return ResponseEntity.ok(new ApiResponse<>(true, "User profile updated successfully", user));

        } catch (IllegalArgumentException e) {
            log.warn("User update failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));

        } catch (Exception e) {
            log.error("User update error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to update user: " + e.getMessage(), null));
        }
    }

    /**
     * Get user profile (for dashboard)
     * GET /api/v1/users/{userId}/profile
     */
    @GetMapping("/{userId}/profile")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUserProfile(@PathVariable Long userId) {
        log.info("Fetching user profile for userId: {}", userId);

        try {
            UserResponseDTO user = userService.getUserById(userId);
            return ResponseEntity.ok(new ApiResponse<>(true, "User profile retrieved successfully", user));

        } catch (IllegalArgumentException e) {
            log.warn("User profile not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));

        } catch (Exception e) {
            log.error("User profile retrieval error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to retrieve user profile: " + e.getMessage(), null));
        }
    }
}
