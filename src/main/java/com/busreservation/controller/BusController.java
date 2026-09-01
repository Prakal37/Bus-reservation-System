package com.busreservation.controller;

import com.busreservation.dto.ApiResponse;
import com.busreservation.dto.BusDTO;
import com.busreservation.dto.SearchBusRequestDTO;
import com.busreservation.service.BusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Bus operations
 */
@Slf4j
@RestController
@RequestMapping("/v1/buses")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class BusController {

    private final BusService busService;

    /**
     * Search buses by source, destination and date
     * POST /api/v1/buses/search
     */
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<List<BusDTO>>> searchBuses(@RequestBody SearchBusRequestDTO searchRequest) {
        log.info("Bus search request: from {} to {} on {}", 
                searchRequest.getSource(), searchRequest.getDestination(), searchRequest.getJourneyDate());

        try {
            // Validate input
            if (searchRequest.getSource() == null || searchRequest.getSource().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Source is required", null));
            }

            if (searchRequest.getDestination() == null || searchRequest.getDestination().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Destination is required", null));
            }

            if (searchRequest.getJourneyDate() == null || searchRequest.getJourneyDate().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Journey date is required", null));
            }

            List<BusDTO> buses = busService.searchBusesBySourceAndDestination(
                    searchRequest.getSource(),
                    searchRequest.getDestination(),
                    searchRequest.getJourneyDate()
            );

            if (buses.isEmpty()) {
                return ResponseEntity.ok(new ApiResponse<>(true, "No buses found for the given route and date", buses));
            }

            return ResponseEntity.ok(new ApiResponse<>(true, "Buses found successfully", buses));

        } catch (Exception e) {
            log.error("Bus search error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Bus search failed: " + e.getMessage(), null));
        }
    }

    /**
     * Get bus by ID with available seats
     * GET /api/v1/buses/{busId}
     */
    @GetMapping("/{busId}")
    public ResponseEntity<ApiResponse<BusDTO>> getBusById(
            @PathVariable Long busId,
            @RequestParam(required = false) String journeyDate) {
        log.info("Getting bus details for busId: {} on date: {}", busId, journeyDate);

        try {
            BusDTO bus = busService.getBusById(busId, journeyDate);
            return ResponseEntity.ok(new ApiResponse<>(true, "Bus retrieved successfully", bus));

        } catch (IllegalArgumentException e) {
            log.warn("Bus not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));

        } catch (Exception e) {
            log.error("Bus retrieval error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Bus retrieval failed: " + e.getMessage(), null));
        }
    }

    /**
     * Get all active buses (with pagination consideration)
     * GET /api/v1/buses
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<BusDTO>>> getAllBuses(
            @RequestParam(required = false) String journeyDate) {
        log.info("Fetching all active buses for date: {}", journeyDate);

        try {
            List<BusDTO> buses = busService.getAllActiveBuses(journeyDate);
            return ResponseEntity.ok(new ApiResponse<>(true, "Buses retrieved successfully", buses));

        } catch (Exception e) {
            log.error("Get all buses error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to retrieve buses: " + e.getMessage(), null));
        }
    }

    /**
     * Get buses by bus type
     * GET /api/v1/buses/type/{busType}
     */
    @GetMapping("/type/{busType}")
    public ResponseEntity<ApiResponse<List<BusDTO>>> getBusesByType(
            @PathVariable String busType,
            @RequestParam(required = false) String journeyDate) {
        log.info("Getting buses by type: {}", busType);

        try {
            List<BusDTO> buses = busService.getBusesByType(busType, journeyDate);

            if (buses.isEmpty()) {
                return ResponseEntity.ok(new ApiResponse<>(true, "No buses found for type: " + busType, buses));
            }

            return ResponseEntity.ok(new ApiResponse<>(true, "Buses retrieved successfully", buses));

        } catch (Exception e) {
            log.error("Get buses by type error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to retrieve buses: " + e.getMessage(), null));
        }
    }

    /**
     * Get buses by operator name
     * GET /api/v1/buses/operator/{operatorName}
     */
    @GetMapping("/operator/{operatorName}")
    public ResponseEntity<ApiResponse<List<BusDTO>>> getBusesByOperator(
            @PathVariable String operatorName,
            @RequestParam(required = false) String journeyDate) {
        log.info("Getting buses by operator: {}", operatorName);

        try {
            List<BusDTO> buses = busService.getBusesByOperator(operatorName, journeyDate);

            if (buses.isEmpty()) {
                return ResponseEntity.ok(new ApiResponse<>(true, "No buses found for operator: " + operatorName, buses));
            }

            return ResponseEntity.ok(new ApiResponse<>(true, "Buses retrieved successfully", buses));

        } catch (Exception e) {
            log.error("Get buses by operator error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to retrieve buses: " + e.getMessage(), null));
        }
    }

    /**
     * Add a new bus (Admin only)
     * POST /api/v1/buses
     */
    @PostMapping
    public ResponseEntity<ApiResponse<BusDTO>> addBus(@RequestBody BusDTO busDTO) {
        log.info("Adding new bus with number: {}", busDTO.getBusNumber());

        try {
            // Validate input
            if (busDTO.getBusName() == null || busDTO.getBusName().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Bus name is required", null));
            }

            if (busDTO.getBusNumber() == null || busDTO.getBusNumber().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Bus number is required", null));
            }

            if (busDTO.getTotalSeats() == null || busDTO.getTotalSeats() <= 0) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Valid total seats is required", null));
            }

            BusDTO savedBus = busService.addBus(busDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, "Bus added successfully", savedBus));

        } catch (IllegalArgumentException e) {
            log.warn("Add bus failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));

        } catch (Exception e) {
            log.error("Add bus error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to add bus: " + e.getMessage(), null));
        }
    }

    /**
     * Update bus (Admin only)
     * PUT /api/v1/buses/{busId}
     */
    @PutMapping("/{busId}")
    public ResponseEntity<ApiResponse<BusDTO>> updateBus(
            @PathVariable Long busId,
            @RequestBody BusDTO busDTO) {
        log.info("Updating bus with busId: {}", busId);

        try {
            BusDTO updatedBus = busService.updateBus(busId, busDTO);
            return ResponseEntity.ok(new ApiResponse<>(true, "Bus updated successfully", updatedBus));

        } catch (IllegalArgumentException e) {
            log.warn("Update bus failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));

        } catch (Exception e) {
            log.error("Update bus error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to update bus: " + e.getMessage(), null));
        }
    }

    /**
     * Delete bus (Admin only)
     * DELETE /api/v1/buses/{busId}
     */
    @DeleteMapping("/{busId}")
    public ResponseEntity<ApiResponse<String>> deleteBus(@PathVariable Long busId) {
        log.info("Deleting bus with busId: {}", busId);

        try {
            busService.deleteBus(busId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Bus deleted successfully", null));

        } catch (IllegalArgumentException e) {
            log.warn("Delete bus failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));

        } catch (Exception e) {
            log.error("Delete bus error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to delete bus: " + e.getMessage(), null));
        }
    }
}
