package com.busreservation.repository;

import com.busreservation.entity.Bus;
import com.busreservation.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Bus entity CRUD operations
 */
@Repository
public interface BusRepository extends JpaRepository<Bus, Long> {
    Optional<Bus> findByBusNumber(String busNumber);
    
    List<Bus> findByRoute(Route route);
    
    List<Bus> findByRouteAndIsActiveTrue(Route route);
    
    List<Bus> findByBusType(String busType);
    
    List<Bus> findByOperatorName(String operatorName);
    
    List<Bus> findByIsActiveTrue();
    
    @Query("SELECT b FROM Bus b WHERE b.route.routeId = :routeId AND b.isActive = true")
    List<Bus> findActiveByRouteId(@Param("routeId") Long routeId);
    
    @Query("SELECT b FROM Bus b WHERE b.route.source = :source AND b.route.destination = :destination AND b.isActive = true")
    List<Bus> findBySourceAndDestination(@Param("source") String source, @Param("destination") String destination);
}
