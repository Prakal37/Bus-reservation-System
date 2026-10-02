package com.busreservation.service;

import com.busreservation.dto.UserRegisterDTO;
import com.busreservation.dto.UserResponseDTO;
import com.busreservation.entity.User;
import com.busreservation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service for User related operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    /**
     * Register a new user
     */
    @Transactional
    public UserResponseDTO registerUser(UserRegisterDTO registerDTO) {
        log.info("Registering new user with email: {}", registerDTO.getEmail());

        String email = registerDTO.getEmail() != null ? registerDTO.getEmail().trim().toLowerCase() : null;
        String username = registerDTO.getUsername() != null ? registerDTO.getUsername().trim() : null;

        // Check if email already exists
        if (email == null || email.isEmpty() || userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(email == null || email.isEmpty() ? "Email is required" : "Email already exists");
        }

        // Check if username already exists
        if (username == null || username.isEmpty() || userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException(username == null || username.isEmpty() ? "Username is required" : "Username already exists");
        }

        // Check password confirmation
        if (registerDTO.getConfirmPassword() != null
                && !registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        // Create new user
        User user = new User();
        user.setFirstName(registerDTO.getFirstName().trim());
        user.setLastName(registerDTO.getLastName() == null || registerDTO.getLastName().trim().isEmpty()
                ? registerDTO.getFirstName().trim()
                : registerDTO.getLastName().trim());
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setPhoneNumber(registerDTO.getPhoneNumber());
        user.setAddress(registerDTO.getAddress());
        user.setCity(registerDTO.getCity());
        user.setState(registerDTO.getState());
        user.setPostalCode(registerDTO.getPostalCode());
        user.setIsActive(true);

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with userId: {}", savedUser.getUserId());

        return modelMapper.map(savedUser, UserResponseDTO.class);
    }

    /**
     * Get user by email
     */
    public UserResponseDTO getUserByEmail(String email) {
        log.info("Fetching user by email: {}", email);
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found with email: " + email);
        }
        return modelMapper.map(user.get(), UserResponseDTO.class);
    }

    /**
     * Get user by username
     */
    public UserResponseDTO getUserByUsername(String username) {
        log.info("Fetching user by username: {}", username);
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found with username: " + username);
        }
        return modelMapper.map(user.get(), UserResponseDTO.class);
    }

    /**
     * Get user by ID
     */
    public UserResponseDTO getUserById(Long userId) {
        log.info("Fetching user by userId: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with userId: " + userId));
        return modelMapper.map(user, UserResponseDTO.class);
    }

    public User getUserEntityById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with userId: " + userId));
    }

    public User authenticateUser(String usernameOrEmail, String password) {
        log.info("Authenticating user with username/email: {}", usernameOrEmail);
        String identifier = usernameOrEmail == null ? "" : usernameOrEmail.trim();
        Optional<User> user = userRepository.findByEmailOrUsername(identifier, identifier);
        if (user.isEmpty() && identifier.contains("@")) {
            user = userRepository.findByEmailOrUsername(identifier.toLowerCase(), identifier.toLowerCase());
        }
        
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found with username/email: " + usernameOrEmail);
        }

        User foundUser = user.get();
        if (!passwordEncoder.matches(password, foundUser.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        return foundUser;
    }

    /**
     * Update user profile
     */
    @Transactional
    public UserResponseDTO updateUserProfile(Long userId, UserRegisterDTO updateDTO) {
        log.info("Updating user profile for userId: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with userId: " + userId));

        user.setFirstName(updateDTO.getFirstName());
        user.setLastName(updateDTO.getLastName());
        user.setPhoneNumber(updateDTO.getPhoneNumber());
        user.setAddress(updateDTO.getAddress());
        user.setCity(updateDTO.getCity());
        user.setState(updateDTO.getState());
        user.setPostalCode(updateDTO.getPostalCode());

        User updatedUser = userRepository.save(user);
        log.info("User profile updated successfully for userId: {}", userId);

        return modelMapper.map(updatedUser, UserResponseDTO.class);
    }

    /**
     * Reset a user's password using either email or username.
     */
    @Transactional
    public UserResponseDTO resetPassword(String identifier, String newPassword, String confirmPassword) {
        log.info("Reset password request received for identifier: {}", identifier);

        String lookup = identifier == null ? "" : identifier.trim();
        if (lookup.isEmpty()) {
            throw new IllegalArgumentException("Email or username is required");
        }

        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("New password is required");
        }

        if (confirmPassword == null || !newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        Optional<User> userOptional = userRepository.findByEmailOrUsername(lookup, lookup);
        if (userOptional.isEmpty() && lookup.contains("@")) {
            userOptional = userRepository.findByEmailOrUsername(lookup.toLowerCase(), lookup.toLowerCase());
        }

        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("No account found for the provided email or username");
        }

        User user = userOptional.get();
        user.setPassword(passwordEncoder.encode(newPassword.trim()));
        User updatedUser = userRepository.save(user);
        log.info("Password reset successful for userId: {}", updatedUser.getUserId());

        return modelMapper.map(updatedUser, UserResponseDTO.class);
    }

    /**
     * Check if email exists
     */
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Check if username exists
     */
    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }
}
