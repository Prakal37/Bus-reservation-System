package com.busreservation.service;

import com.busreservation.dto.BusDTO;
import com.busreservation.dto.RouteDTO;
import com.busreservation.entity.Bus;
import com.busreservation.entity.Route;
import com.busreservation.repository.BusRepository;
import com.busreservation.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for Bus related operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BusService {

    private final BusRepository busRepository;
    private final SeatRepository seatRepository;
    private final RouteService routeService;
    private final ModelMapper modelMapper;

    /**
     * Add a new bus
     */
    @Transactional
    public BusDTO addBus(BusDTO busDTO) {
        log.info("Adding new bus with number: {}", busDTO.getBusNumber());

        // Check if bus number already exists
        if (busRepository.findByBusNumber(busDTO.getBusNumber()).isPresent()) {
            throw new IllegalArgumentException("Bus with this number already exists");
        }

        // Get route
        Route route = routeService.getRouteEntityById(busDTO.getRoute().getRouteId());

        Bus bus = new Bus();
        bus.setBusName(busDTO.getBusName());
        bus.setBusNumber(busDTO.getBusNumber());
        bus.setTotalSeats(busDTO.getTotalSeats());
        bus.setBusType(busDTO.getBusType());
        bus.setAmenities(busDTO.getAmenities());
        bus.setRoute(route);
        bus.setDepartureTime(busDTO.getDepartureTime());
        bus.setArrivalTime(busDTO.getArrivalTime());
        bus.setPricePerSeat(busDTO.getPricePerSeat());
        bus.setOperatorName(busDTO.getOperatorName());
        bus.setIsActive(true);

        Bus savedBus = busRepository.save(bus);
        log.info("Bus added successfully with busId: {}", savedBus.getBusId());

        return convertToBusDTO(savedBus, null);
    }

    /**
     * Get all active buses
     */
    public List<BusDTO> getAllActiveBuses(String journeyDate) {
        log.info("Fetching all active buses");
        List<Bus> buses = busRepository.findByIsActiveTrue();
        return buses.stream()
                .map(bus -> convertToBusDTO(bus, journeyDate))
                .collect(Collectors.toList());
    }

    /**
     * Get bus by ID
     */
    public BusDTO getBusById(Long busId, String journeyDate) {
        log.info("Fetching bus by busId: {}", busId);
        Bus bus = busRepository.findById(busId)
                .orElseThrow(() -> new IllegalArgumentException("Bus not found with busId: " + busId));
        return convertToBusDTO(bus, journeyDate);
    }

    /**
     * Search buses by source and destination
     */
    public List<BusDTO> searchBusesBySourceAndDestination(String source, String destination, String journeyDate) {
        log.info("Searching buses from {} to {} for date: {}", source, destination, journeyDate);
        List<Bus> buses = busRepository.findBySourceAndDestination(source, destination);
        return buses.stream()
                .map(bus -> convertToBusDTO(bus, journeyDate))
                .collect(Collectors.toList());
    }

    /**
     * Get buses by route ID
     */
    public List<BusDTO> getBusesByRouteId(Long routeId, String journeyDate) {
        log.info("Fetching buses by routeId: {}", routeId);
        List<Bus> buses = busRepository.findActiveByRouteId(routeId);
        return buses.stream()
                .map(bus -> convertToBusDTO(bus, journeyDate))
                .collect(Collectors.toList());
    }

    /**
     * Get buses by bus type
     */
    public List<BusDTO> getBusesByType(String busType, String journeyDate) {
        log.info("Fetching buses by type: {}", busType);
        List<Bus> buses = busRepository.findByBusType(busType);
        return buses.stream()
                .map(bus -> convertToBusDTO(bus, journeyDate))
                .collect(Collectors.toList());
    }

    /**
     * Get buses by operator name
     */
    public List<BusDTO> getBusesByOperator(String operatorName, String journeyDate) {
        log.info("Fetching buses by operator: {}", operatorName);
        List<Bus> buses = busRepository.findByOperatorName(operatorName);
        return buses.stream()
                .map(bus -> convertToBusDTO(bus, journeyDate))
                .collect(Collectors.toList());
    }

    /**
     * Update bus
     */
    @Transactional
    public BusDTO updateBus(Long busId, BusDTO busDTO) {
        log.info("Updating bus with busId: {}", busId);

        Bus bus = busRepository.findById(busId)
                .orElseThrow(() -> new IllegalArgumentException("Bus not found with busId: " + busId));

        bus.setBusName(busDTO.getBusName());
        bus.setTotalSeats(busDTO.getTotalSeats());
        bus.setBusType(busDTO.getBusType());
        bus.setAmenities(busDTO.getAmenities());
        bus.setDepartureTime(busDTO.getDepartureTime());
        bus.setArrivalTime(busDTO.getArrivalTime());
        bus.setPricePerSeat(busDTO.getPricePerSeat());
        bus.setOperatorName(busDTO.getOperatorName());

        Bus updatedBus = busRepository.save(bus);
        log.info("Bus updated successfully with busId: {}", busId);

        return convertToBusDTO(updatedBus, null);
    }

    /**
     * Delete bus (soft delete)
     */
    @Transactional
    public void deleteBus(Long busId) {
        log.info("Deleting bus with busId: {}", busId);

        Bus bus = busRepository.findById(busId)
                .orElseThrow(() -> new IllegalArgumentException("Bus not found with busId: " + busId));

        bus.setIsActive(false);
        busRepository.save(bus);
        log.info("Bus deleted successfully with busId: {}", busId);
    }

    /**
     * Get Bus entity by ID (for internal use)
     */
    public Bus getBusEntityById(Long busId) {
        return busRepository.findById(busId)
                .orElseThrow(() -> new IllegalArgumentException("Bus not found with busId: " + busId));
    }

    /**
     * Convert Bus entity to BusDTO with available seats count
     */
    private BusDTO convertToBusDTO(Bus bus, String journeyDate) {
        BusDTO busDTO = new BusDTO();
        busDTO.setBusId(bus.getBusId());
        busDTO.setBusName(bus.getBusName());
        busDTO.setBusNumber(bus.getBusNumber());
        busDTO.setTotalSeats(bus.getTotalSeats());
        busDTO.setBusType(bus.getBusType());
        busDTO.setAmenities(bus.getAmenities());
        busDTO.setRoute(modelMapper.map(bus.getRoute(), RouteDTO.class));
        busDTO.setDepartureTime(bus.getDepartureTime());
        busDTO.setArrivalTime(bus.getArrivalTime());
        busDTO.setPricePerSeat(bus.getPricePerSeat());
        busDTO.setOperatorName(bus.getOperatorName());
        busDTO.setIsActive(bus.getIsActive());

        // Calculate available seats if journey date is provided
        if (journeyDate != null && !journeyDate.isEmpty()) {
            long availableSeats = seatRepository.countAvailableSeats(bus.getBusId(), journeyDate);
            busDTO.setAvailableSeats(availableSeats);
        } else {
            busDTO.setAvailableSeats((long) bus.getTotalSeats());
        }

        return busDTO;
    }
}
