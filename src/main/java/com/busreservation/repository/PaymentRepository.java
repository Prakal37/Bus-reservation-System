package com.busreservation.repository;

import com.busreservation.entity.Payment;
import com.busreservation.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Payment entity CRUD operations
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByBooking(Booking booking);
    
    Optional<Payment> findByTransactionId(String transactionId);
    
    Optional<Payment> findByReferenceNumber(String referenceNumber);
    
    List<Payment> findByPaymentStatus(String status);
    
    List<Payment> findByPaymentMethod(String method);
}
