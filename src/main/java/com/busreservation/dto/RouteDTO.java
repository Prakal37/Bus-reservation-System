package com.busreservation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for route information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteDTO {
    private Long routeId;
    private String source;
    private String destination;
    private Double distanceKm;
    private Integer approximateDurationHours;
    private Boolean isActive;
}
