package com.busreservation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for seat information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatDTO {
    private Long seatId;
    private Integer seatNumber;
    private String seatType;
    private Boolean isAvailable;
    private String bookingDate;
}
