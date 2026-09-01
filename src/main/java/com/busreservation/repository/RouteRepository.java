package com.busreservation.repository;

import com.busreservation.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Route entity CRUD operations
 */
@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    Optional<Route> findBySourceAndDestination(String source, String destination);
    List<Route> findBySourceAndDestinationAndIsActiveTrue(String source, String destination);
    List<Route> findBySourceContainingIgnoreCase(String source);
    List<Route> findByDestinationContainingIgnoreCase(String destination);
    List<Route> findByIsActiveTrue();
}
