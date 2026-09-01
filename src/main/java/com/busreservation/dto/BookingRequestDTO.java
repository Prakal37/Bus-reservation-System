package com.busreservation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * DTO for booking request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestDTO {
    private Long busId;
    private List<Integer> seatNumbers;
    private String journeyDate;
    private String passengerName;
    private String passengerEmail;
    private String passengerPhone;
}
