package com.busreservation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for bus information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusDTO {
    private Long busId;
    private String busName;
    private String busNumber;
    private Integer totalSeats;
    private String busType;
    private String amenities;
    private RouteDTO route;
    private String departureTime;
    private String arrivalTime;
    private Double pricePerSeat;
    private String operatorName;
    private Long availableSeats;
    private Boolean isActive;
}
