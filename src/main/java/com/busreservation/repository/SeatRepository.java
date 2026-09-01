package com.busreservation.repository;

import com.busreservation.entity.Seat;
import com.busreservation.entity.Bus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Seat entity CRUD operations
 */
@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByBus(Bus bus);
    
    List<Seat> findByBusAndIsAvailableTrue(Bus bus);
    
    List<Seat> findByBusAndBookingDate(Bus bus, String bookingDate);
    
    Optional<Seat> findByBusAndSeatNumber(Bus bus, Integer seatNumber);
    
    @Query("SELECT COUNT(s) FROM Seat s WHERE s.bus.busId = :busId AND s.isAvailable = true AND s.bookingDate = :bookingDate")
    long countAvailableSeats(@Param("busId") Long busId, @Param("bookingDate") String bookingDate);
    
    @Query("SELECT s FROM Seat s WHERE s.bus.busId = :busId AND s.isAvailable = true AND s.bookingDate = :bookingDate")
    List<Seat> findAvailableSeatsByBusAndDate(@Param("busId") Long busId, @Param("bookingDate") String bookingDate);
    
    List<Seat> findByBusAndBookingDateAndIsAvailableTrue(Bus bus, String bookingDate);
}
