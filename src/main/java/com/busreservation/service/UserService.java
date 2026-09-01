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

        // Check if email already exists
        if (userRepository.existsByEmail(registerDTO.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Check if username already exists
        if (userRepository.existsByUsername(registerDTO.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        // Check password confirmation
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        // Create new user
        User user = new User();
        user.setFirstName(registerDTO.getFirstName());
        user.setLastName(registerDTO.getLastName());
        user.setEmail(registerDTO.getEmail());
        user.setUsername(registerDTO.getUsername());
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

    /**
     * Authenticate user (for login)
     */
    public User authenticateUser(String username, String password) {
        log.info("Authenticating user with username: {}", username);
        Optional<User> user = userRepository.findByUsername(username);
        
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found with username: " + username);
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
