package com.busreservation.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Payment Entity representing payment details for bookings
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    @NotNull(message = "Booking is required")
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod; // CREDIT_CARD, DEBIT_CARD, UPI, NET_BANKING

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "payment_status", nullable = false)
    private String paymentStatus; // SUCCESS, FAILED, PENDING

    @Column(name = "payment_gateway")
    private String paymentGateway; // Stripe, PayPal, RazorPay, etc.

    @Column(name = "reference_number")
    private String referenceNumber;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;
}
