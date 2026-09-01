package com.busreservation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO for booking information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDTO {
    private Long bookingId;
    private Long userId;
    private Long busId;
    private String seatNumbers;
    private Integer numberOfSeats;
    private String journeyDate;
    private Double totalPrice;
    private String bookingStatus;
    private String paymentStatus;
    private String passengerName;
    private String passengerEmail;
    private String passengerPhone;
    private String bookingReference;
    private LocalDateTime bookedAt;
}
