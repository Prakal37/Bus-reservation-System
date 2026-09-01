package com.busreservation.repository;

import com.busreservation.entity.Booking;
import com.busreservation.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Booking entity CRUD operations
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUser(User user);
    
    List<Booking> findByUserOrderByBookedAtDesc(User user);
    
    Optional<Booking> findByBookingReference(String bookingReference);
    
    List<Booking> findByBookingStatus(String status);
    
    List<Booking> findByUserAndBookingStatusOrderByBookedAtDesc(User user, String status);
    
    @Query("SELECT b FROM Booking b WHERE b.bus.busId = :busId AND b.journeyDate = :journeyDate")
    List<Booking> findByBusAndJourneyDate(@Param("busId") Long busId, @Param("journeyDate") String journeyDate);
    
    @Query("SELECT b FROM Booking b WHERE b.user.userId = :userId AND b.bookingStatus != 'CANCELLED' ORDER BY b.bookedAt DESC")
    List<Booking> findActiveBookingsByUser(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.bus.busId = :busId AND b.journeyDate = :journeyDate AND b.bookingStatus = 'CONFIRMED'")
    long countConfirmedBookingsByBusAndDate(@Param("busId") Long busId, @Param("journeyDate") String journeyDate);
}
