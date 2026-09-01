package com.busreservation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO for user response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String phoneNumber;
    private String address;
    private String city;
    private String state;
    private String postalCode;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
