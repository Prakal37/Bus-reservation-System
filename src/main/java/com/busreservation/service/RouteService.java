package com.busreservation.service;

import com.busreservation.dto.RouteDTO;
import com.busreservation.entity.Route;
import com.busreservation.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for Route related operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RouteService {

    private final RouteRepository routeRepository;
    private final ModelMapper modelMapper;

    /**
     * Add a new route
     */
    @Transactional
    public RouteDTO addRoute(RouteDTO routeDTO) {
        log.info("Adding new route from {} to {}", routeDTO.getSource(), routeDTO.getDestination());

        Route route = modelMapper.map(routeDTO, Route.class);
        route.setIsActive(true);

        Route savedRoute = routeRepository.save(route);
        log.info("Route added successfully with routeId: {}", savedRoute.getRouteId());

        return modelMapper.map(savedRoute, RouteDTO.class);
    }

    /**
     * Get all active routes
     */
    public List<RouteDTO> getAllActiveRoutes() {
        log.info("Fetching all active routes");
        List<Route> routes = routeRepository.findByIsActiveTrue();
        return routes.stream()
                .map(route -> modelMapper.map(route, RouteDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Get route by ID
     */
    public RouteDTO getRouteById(Long routeId) {
        log.info("Fetching route by routeId: {}", routeId);
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new IllegalArgumentException("Route not found with routeId: " + routeId));
        return modelMapper.map(route, RouteDTO.class);
    }

    /**
     * Get route by source and destination
     */
    public RouteDTO getRouteBySourceAndDestination(String source, String destination) {
        log.info("Fetching route from {} to {}", source, destination);
        Route route = routeRepository.findBySourceAndDestination(source, destination)
                .orElseThrow(() -> new IllegalArgumentException("Route not found from " + source + " to " + destination));
        return modelMapper.map(route, RouteDTO.class);
    }

    /**
     * Get routes by source and destination (returns list)
     */
    public List<RouteDTO> getRoutesBySourceAndDestination(String source, String destination) {
        log.info("Fetching routes from {} to {}", source, destination);
        List<Route> routes = routeRepository.findBySourceAndDestinationAndIsActiveTrue(source, destination);
        return routes.stream()
                .map(route -> modelMapper.map(route, RouteDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Get routes by source
     */
    public List<RouteDTO> getRoutesBySource(String source) {
        log.info("Fetching routes by source: {}", source);
        List<Route> routes = routeRepository.findBySourceContainingIgnoreCase(source);
        return routes.stream()
                .map(route -> modelMapper.map(route, RouteDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Get routes by destination
     */
    public List<RouteDTO> getRoutesByDestination(String destination) {
        log.info("Fetching routes by destination: {}", destination);
        List<Route> routes = routeRepository.findByDestinationContainingIgnoreCase(destination);
        return routes.stream()
                .map(route -> modelMapper.map(route, RouteDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Update route
     */
    @Transactional
    public RouteDTO updateRoute(Long routeId, RouteDTO routeDTO) {
        log.info("Updating route with routeId: {}", routeId);

        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new IllegalArgumentException("Route not found with routeId: " + routeId));

        route.setSource(routeDTO.getSource());
        route.setDestination(routeDTO.getDestination());
        route.setDistanceKm(routeDTO.getDistanceKm());
        route.setApproximateDurationHours(routeDTO.getApproximateDurationHours());

        Route updatedRoute = routeRepository.save(route);
        log.info("Route updated successfully with routeId: {}", routeId);

        return modelMapper.map(updatedRoute, RouteDTO.class);
    }

    /**
     * Delete route (soft delete)
     */
    @Transactional
    public void deleteRoute(Long routeId) {
        log.info("Deleting route with routeId: {}", routeId);

        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new IllegalArgumentException("Route not found with routeId: " + routeId));

        route.setIsActive(false);
        routeRepository.save(route);
        log.info("Route deleted successfully with routeId: {}", routeId);
    }

    /**
     * Get Route entity by ID (for internal use)
     */
    public Route getRouteEntityById(Long routeId) {
        return routeRepository.findById(routeId)
                .orElseThrow(() -> new IllegalArgumentException("Route not found with routeId: " + routeId));
    }
}
