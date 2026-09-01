package com.busreservation.controller;

import com.busreservation.dto.*;
import com.busreservation.entity.User;
import com.busreservation.security.JwtTokenProvider;
import com.busreservation.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Authentication operations (Login and Registration)
 */
@Slf4j
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Register a new user
     * POST /api/v1/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponseDTO>> register(@RequestBody UserRegisterDTO registerDTO) {
        log.info("User registration request received for email: {}", registerDTO.getEmail());

        try {
            // Validate input
            if (registerDTO.getFirstName() == null || registerDTO.getFirstName().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "First name is required", null));
            }

            if (registerDTO.getEmail() == null || registerDTO.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Email is required", null));
            }

            if (registerDTO.getUsername() == null || registerDTO.getUsername().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Username is required", null));
            }

            if (registerDTO.getPassword() == null || registerDTO.getPassword().length() < 6) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Password must be at least 6 characters", null));
            }

            UserResponseDTO user = userService.registerUser(registerDTO);
            log.info("User registered successfully: {}", user.getUserId());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, "User registered successfully", user));

        } catch (IllegalArgumentException e) {
            log.warn("Registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));

        } catch (Exception e) {
            log.error("Registration error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Registration failed: " + e.getMessage(), null));
        }
    }

    /**
     * Login user
     * POST /api/v1/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(@RequestBody UserLoginDTO loginDTO) {
        log.info("User login request received for username: {}", loginDTO.getUsername());

        try {
            // Validate input
            if (loginDTO.getUsername() == null || loginDTO.getUsername().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Username is required", null));
            }

            if (loginDTO.getPassword() == null || loginDTO.getPassword().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Password is required", null));
            }

            // Authenticate user
            User user = userService.authenticateUser(loginDTO.getUsername(), loginDTO.getPassword());

            // Generate JWT token
            String token = jwtTokenProvider.generateToken(user.getUsername());
            long expiresIn = jwtTokenProvider.getExpirationTime();

            UserResponseDTO userResponse = new UserResponseDTO(
                    user.getUserId(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    user.getUsername(),
                    user.getPhoneNumber(),
                    user.getAddress(),
                    user.getCity(),
                    user.getState(),
                    user.getPostalCode(),
                    user.getIsActive(),
                    user.getCreatedAt()
            );

            LoginResponseDTO loginResponse = new LoginResponseDTO(token, "Bearer", expiresIn, userResponse);

            log.info("User logged in successfully: {}", user.getUserId());
            return ResponseEntity.ok(new ApiResponse<>(true, "Login successful", loginResponse));

        } catch (IllegalArgumentException e) {
            log.warn("Login failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Invalid username or password", null));

        } catch (Exception e) {
            log.error("Login error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Login failed: " + e.getMessage(), null));
        }
    }

    /**
     * Check if email exists
     * GET /api/v1/auth/check-email
     */
    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse<Boolean>> checkEmailExists(@RequestParam String email) {
        log.info("Checking if email exists: {}", email);

        try {
            boolean exists = userService.emailExists(email);
            return ResponseEntity.ok(new ApiResponse<>(true, "Email check completed", exists));

        } catch (Exception e) {
            log.error("Email check error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Email check failed: " + e.getMessage(), false));
        }
    }

    /**
     * Check if username exists
     * GET /api/v1/auth/check-username
     */
    @GetMapping("/check-username")
    public ResponseEntity<ApiResponse<Boolean>> checkUsernameExists(@RequestParam String username) {
        log.info("Checking if username exists: {}", username);

        try {
            boolean exists = userService.usernameExists(username);
            return ResponseEntity.ok(new ApiResponse<>(true, "Username check completed", exists));

        } catch (Exception e) {
            log.error("Username check error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Username check failed: " + e.getMessage(), false));
        }
    }

    /**
     * Validate token
     * GET /api/v1/auth/validate-token
     */
    @GetMapping("/validate-token")
    public ResponseEntity<ApiResponse<Boolean>> validateToken(@RequestHeader("Authorization") String authHeader) {
        log.info("Token validation request received");

        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Invalid token format", false));
            }

            String token = authHeader.substring(7);
            boolean isValid = jwtTokenProvider.validateToken(token);

            return ResponseEntity.ok(new ApiResponse<>(true, "Token validation completed", isValid));

        } catch (Exception e) {
            log.error("Token validation error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Token validation failed: " + e.getMessage(), false));
        }
    }
}
