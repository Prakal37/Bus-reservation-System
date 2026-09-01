package com.busreservation.service;

import com.busreservation.entity.Booking;
import com.busreservation.entity.Payment;
import com.busreservation.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service for Payment related operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingService bookingService;

    /**
     * Create a new payment for a booking
     */
    @Transactional
    public Payment createPayment(Long bookingId, String paymentMethod, String paymentGateway) {
        log.info("Creating payment for booking: {}", bookingId);

        Booking booking = bookingService.getBookingEntityById(bookingId);

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(booking.getTotalPrice());
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentGateway(paymentGateway);
        payment.setPaymentStatus("PENDING");
        payment.setTransactionId(generateTransactionId());
        payment.setReferenceNumber(generateReferenceNumber());

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment created successfully with paymentId: {}", savedPayment.getPaymentId());

        return savedPayment;
    }

    /**
     * Get payment by ID
     */
    public Payment getPaymentById(Long paymentId) {
        log.info("Fetching payment by paymentId: {}", paymentId);
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found with paymentId: " + paymentId));
    }

    /**
     * Get payment by booking
     */
    public Payment getPaymentByBooking(Long bookingId) {
        log.info("Fetching payment by bookingId: {}", bookingId);
        Booking booking = bookingService.getBookingEntityById(bookingId);
        return paymentRepository.findByBooking(booking)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found for booking: " + bookingId));
    }

    /**
     * Get payment by transaction ID
     */
    public Payment getPaymentByTransactionId(String transactionId) {
        log.info("Fetching payment by transactionId: {}", transactionId);
        return paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found with transactionId: " + transactionId));
    }

    /**
     * Process payment (success)
     */
    @Transactional
    public Payment processPayment(Long paymentId) {
        log.info("Processing payment with paymentId: {}", paymentId);

        Payment payment = getPaymentById(paymentId);
        payment.setPaymentStatus("SUCCESS");
        payment.setProcessedAt(LocalDateTime.now());

        Payment savedPayment = paymentRepository.save(payment);

        // Update booking status
        Booking booking = payment.getBooking();
        booking.setPaymentStatus("PAID");
        booking.setBookingStatus("CONFIRMED");
        bookingService.confirmBooking(booking.getBookingId());

        log.info("Payment processed successfully with paymentId: {}", paymentId);

        return savedPayment;
    }

    /**
     * Process payment failure
     */
    @Transactional
    public Payment failPayment(Long paymentId) {
        log.info("Payment failed for paymentId: {}", paymentId);

        Payment payment = getPaymentById(paymentId);
        payment.setPaymentStatus("FAILED");
        payment.setProcessedAt(LocalDateTime.now());

        Payment savedPayment = paymentRepository.save(payment);

        // Update booking status
        Booking booking = payment.getBooking();
        booking.setPaymentStatus("FAILED");
        booking.setBookingStatus("CANCELLED");

        log.info("Payment marked as failed with paymentId: {}", paymentId);

        return savedPayment;
    }

    /**
     * Get all payments by status
     */
    public List<Payment> getPaymentsByStatus(String status) {
        log.info("Fetching payments by status: {}", status);
        return paymentRepository.findByPaymentStatus(status);
    }

    /**
     * Get all payments by method
     */
    public List<Payment> getPaymentsByMethod(String method) {
        log.info("Fetching payments by method: {}", method);
        return paymentRepository.findByPaymentMethod(method);
    }

    /**
     * Generate unique transaction ID
     */
    private String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 16).toUpperCase();
    }

    /**
     * Generate unique reference number
     */
    private String generateReferenceNumber() {
        return "REF-" + System.currentTimeMillis();
    }
}
