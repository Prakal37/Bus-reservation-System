package com.busreservation.controller;

import com.busreservation.dto.ApiResponse;
import com.busreservation.dto.RouteDTO;
import com.busreservation.service.RouteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Route operations
 */
@Slf4j
@RestController
@RequestMapping("/v1/routes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class RouteController {

    private final RouteService routeService;

    /**
     * Get all active routes
     * GET /api/v1/routes
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<RouteDTO>>> getAllRoutes() {
        log.info("Fetching all active routes");

        try {
            List<RouteDTO> routes = routeService.getAllActiveRoutes();
            return ResponseEntity.ok(new ApiResponse<>(true, "Routes retrieved successfully", routes));

        } catch (Exception e) {
            log.error("Routes retrieval error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to retrieve routes: " + e.getMessage(), null));
        }
    }

    /**
     * Get route by ID
     * GET /api/v1/routes/{routeId}
     */
    @GetMapping("/{routeId}")
    public ResponseEntity<ApiResponse<RouteDTO>> getRouteById(@PathVariable Long routeId) {
        log.info("Fetching route with routeId: {}", routeId);

        try {
            RouteDTO route = routeService.getRouteById(routeId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Route retrieved successfully", route));

        } catch (IllegalArgumentException e) {
            log.warn("Route not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));

        } catch (Exception e) {
            log.error("Route retrieval error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to retrieve route: " + e.getMessage(), null));
        }
    }

    /**
     * Get routes by source and destination
     * GET /api/v1/routes/search
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<RouteDTO>>> getRoutesBySourceAndDestination(
            @RequestParam String source,
            @RequestParam String destination) {
        log.info("Searching routes from {} to {}", source, destination);

        try {
            List<RouteDTO> routes = routeService.getRoutesBySourceAndDestination(source, destination);

            if (routes.isEmpty()) {
                return ResponseEntity.ok(new ApiResponse<>(true, "No routes found", routes));
            }

            return ResponseEntity.ok(new ApiResponse<>(true, "Routes retrieved successfully", routes));

        } catch (Exception e) {
            log.error("Routes search error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to search routes: " + e.getMessage(), null));
        }
    }

    /**
     * Get routes by source
     * GET /api/v1/routes/source/{source}
     */
    @GetMapping("/source/{source}")
    public ResponseEntity<ApiResponse<List<RouteDTO>>> getRoutesBySource(@PathVariable String source) {
        log.info("Fetching routes by source: {}", source);

        try {
            List<RouteDTO> routes = routeService.getRoutesBySource(source);
            return ResponseEntity.ok(new ApiResponse<>(true, "Routes retrieved successfully", routes));

        } catch (Exception e) {
            log.error("Routes retrieval error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to retrieve routes: " + e.getMessage(), null));
        }
    }

    /**
     * Get routes by destination
     * GET /api/v1/routes/destination/{destination}
     */
    @GetMapping("/destination/{destination}")
    public ResponseEntity<ApiResponse<List<RouteDTO>>> getRoutesByDestination(@PathVariable String destination) {
        log.info("Fetching routes by destination: {}", destination);

        try {
            List<RouteDTO> routes = routeService.getRoutesByDestination(destination);
            return ResponseEntity.ok(new ApiResponse<>(true, "Routes retrieved successfully", routes));

        } catch (Exception e) {
            log.error("Routes retrieval error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to retrieve routes: " + e.getMessage(), null));
        }
    }

    /**
     * Add a new route (Admin only)
     * POST /api/v1/routes
     */
    @PostMapping
    public ResponseEntity<ApiResponse<RouteDTO>> addRoute(@RequestBody RouteDTO routeDTO) {
        log.info("Adding new route from {} to {}", routeDTO.getSource(), routeDTO.getDestination());

        try {
            // Validate input
            if (routeDTO.getSource() == null || routeDTO.getSource().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Source is required", null));
            }

            if (routeDTO.getDestination() == null || routeDTO.getDestination().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Destination is required", null));
            }

            RouteDTO savedRoute = routeService.addRoute(routeDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, "Route added successfully", savedRoute));

        } catch (Exception e) {
            log.error("Add route error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to add route: " + e.getMessage(), null));
        }
    }

    /**
     * Update route (Admin only)
     * PUT /api/v1/routes/{routeId}
     */
    @PutMapping("/{routeId}")
    public ResponseEntity<ApiResponse<RouteDTO>> updateRoute(
            @PathVariable Long routeId,
            @RequestBody RouteDTO routeDTO) {
        log.info("Updating route with routeId: {}", routeId);

        try {
            RouteDTO updatedRoute = routeService.updateRoute(routeId, routeDTO);
            return ResponseEntity.ok(new ApiResponse<>(true, "Route updated successfully", updatedRoute));

        } catch (IllegalArgumentException e) {
            log.warn("Update route failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));

        } catch (Exception e) {
            log.error("Update route error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to update route: " + e.getMessage(), null));
        }
    }

    /**
     * Delete route (Admin only)
     * DELETE /api/v1/routes/{routeId}
     */
    @DeleteMapping("/{routeId}")
    public ResponseEntity<ApiResponse<String>> deleteRoute(@PathVariable Long routeId) {
        log.info("Deleting route with routeId: {}", routeId);

        try {
            routeService.deleteRoute(routeId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Route deleted successfully", null));

        } catch (IllegalArgumentException e) {
            log.warn("Delete route failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));

        } catch (Exception e) {
            log.error("Delete route error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to delete route: " + e.getMessage(), null));
        }
    }
}
