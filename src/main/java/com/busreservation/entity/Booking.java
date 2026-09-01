package com.busreservation.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Booking Entity representing a bus ticket booking
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long bookingId;

    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull(message = "Bus is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bus_id", nullable = false)
    private Bus bus;

    @Column(name = "seat_numbers", nullable = false)
    private String seatNumbers; // Comma-separated seat numbers

    @Column(name = "number_of_seats", nullable = false)
    private Integer numberOfSeats;

    @Column(name = "journey_date", nullable = false)
    private String journeyDate; // YYYY-MM-DD format

    @Column(name = "total_price", nullable = false)
    private Double totalPrice;

    @Column(name = "booking_status", nullable = false)
    private String bookingStatus; // CONFIRMED, PENDING, CANCELLED

    @Column(name = "payment_status", nullable = false)
    private String paymentStatus; // PAID, PENDING, FAILED

    @Column(name = "passenger_name", nullable = false)
    private String passengerName;

    @Column(name = "passenger_email", nullable = false)
    private String passengerEmail;

    @Column(name = "passenger_phone", nullable = false)
    private String passengerPhone;

    @Column(name = "booking_reference")
    private String bookingReference; // Unique booking reference code

    @CreationTimestamp
    @Column(name = "booked_at", nullable = false, updatable = false)
    private LocalDateTime bookedAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;
}
