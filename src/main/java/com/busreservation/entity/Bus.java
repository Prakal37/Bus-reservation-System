package com.busreservation.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Bus Entity representing a bus with its details and capacity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "buses")
public class Bus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bus_id")
    private Long busId;

    @NotBlank(message = "Bus name is required")
    @Column(name = "bus_name", nullable = false)
    private String busName;

    @NotBlank(message = "Bus number/registration is required")
    @Column(name = "bus_number", nullable = false, unique = true)
    private String busNumber;

    @NotNull(message = "Total seats is required")
    @Positive(message = "Total seats must be greater than 0")
    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats;

    @Column(name = "bus_type")
    private String busType; // AC, Non-AC, Sleeper, Semi-Sleeper

    @Column(name = "amenities")
    private String amenities; // WiFi, Charging Port, Blanket, Pillow, etc.

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @Column(name = "departure_time")
    private String departureTime; // HH:MM format

    @Column(name = "arrival_time")
    private String arrivalTime; // HH:MM format

    @Positive(message = "Price must be greater than 0")
    @Column(name = "price_per_seat", nullable = false)
    private Double pricePerSeat;

    @Column(name = "operator_name")
    private String operatorName;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
