package com.busreservation.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for booking request
 */
@Data
@NoArgsConstructor
public class BookingRequestDTO {
    private Long busId;
    private List<Integer> seatNumbers;
    private String journeyDate;
    private String passengerName;
    private String passengerEmail;
    private String passengerPhone;
    /**
     * Mock payment method chosen by the user on the payment screen
     * (UPI, CREDIT_CARD, DEBIT_CARD, NET_BANKING). Optional - defaults to UPI.
     */
    private String paymentMethod;

    /**
     * Backwards-compatible constructor without the payment method, so existing
     * callers that only supply the six passenger/seat fields keep working.
     */
    public BookingRequestDTO(Long busId, List<Integer> seatNumbers, String journeyDate,
                             String passengerName, String passengerEmail, String passengerPhone) {
        this(busId, seatNumbers, journeyDate, passengerName, passengerEmail, passengerPhone, null);
    }

    public BookingRequestDTO(Long busId, List<Integer> seatNumbers, String journeyDate,
                             String passengerName, String passengerEmail, String passengerPhone,
                             String paymentMethod) {
        this.busId = busId;
        this.seatNumbers = seatNumbers;
        this.journeyDate = journeyDate;
        this.passengerName = passengerName;
        this.passengerEmail = passengerEmail;
        this.passengerPhone = passengerPhone;
        this.paymentMethod = paymentMethod;
    }
}
