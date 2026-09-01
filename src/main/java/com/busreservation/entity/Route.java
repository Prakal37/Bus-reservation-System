package com.busreservation.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Route Entity representing a bus route between two locations
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "routes")
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "route_id")
    private Long routeId;

    @NotBlank(message = "Source location is required")
    @Column(name = "source", nullable = false)
    private String source;

    @NotBlank(message = "Destination is required")
    @Column(name = "destination", nullable = false)
    private String destination;

    @Column(name = "distance_km")
    private Double distanceKm;

    @Column(name = "approximate_duration_hours")
    private Integer approximateDurationHours;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Override
    public String toString() {
        return source + " to " + destination;
    }
}
