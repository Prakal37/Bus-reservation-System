package com.busreservation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.modelmapper.ModelMapper;

/**
 * Main Spring Boot Application class for Bus Reservation System
 */
@SpringBootApplication
public class BusReservationApplication {

    public static void main(String[] args) {
        SpringApplication.run(BusReservationApplication.class, args);
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
