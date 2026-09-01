package com.busreservation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for bus search request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchBusRequestDTO {
    private String source;
    private String destination;
    private String journeyDate; // YYYY-MM-DD format
}
