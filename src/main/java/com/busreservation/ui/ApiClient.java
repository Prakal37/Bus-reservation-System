package com.busreservation.ui;

import com.busreservation.dto.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Collections;
import java.util.List;

/**
 * HTTP REST API Client for JavaFX Frontend to communicate with Spring Boot Backend
 */
public class ApiClient {

    private static final String BASE_URL = "http://localhost:8080/api";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ApiClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        this.objectMapper = new ObjectMapper();
        // The backend serialises java.time values (ApiResponse.timestamp,
        // UserResponseDTO.createdAt, BookingDTO.bookedAt) as ISO-8601 strings.
        // Without the JSR-310 module Jackson rejects every response with
        // "Java 8 date/time type java.time.LocalDateTime not supported by default".
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public ApiResponse<UserResponseDTO> register(UserRegisterDTO dto) throws Exception {
        String json = objectMapper.writeValueAsString(dto);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/v1/auth/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readValue(response.body(), new TypeReference<ApiResponse<UserResponseDTO>>() {});
    }

    public ApiResponse<LoginResponseDTO> login(UserLoginDTO dto) throws Exception {
        String json = objectMapper.writeValueAsString(dto);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/v1/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readValue(response.body(), new TypeReference<ApiResponse<LoginResponseDTO>>() {});
    }

    public ApiResponse<Boolean> resetPassword(String identifier, String newPassword, String confirmPassword) throws Exception {
        PasswordResetRequestDTO dto = new PasswordResetRequestDTO(identifier, newPassword, confirmPassword);
        String json = objectMapper.writeValueAsString(dto);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/v1/auth/reset-password"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readValue(response.body(), new TypeReference<ApiResponse<Boolean>>() {});
    }

    public List<BusDTO> searchBuses(String source, String destination, String journeyDate) throws Exception {
        SearchBusRequestDTO searchDTO = new SearchBusRequestDTO(source, destination, journeyDate);
        String json = objectMapper.writeValueAsString(searchDTO);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/v1/buses/search"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        ApiResponse<List<BusDTO>> apiResponse = objectMapper.readValue(response.body(), new TypeReference<ApiResponse<List<BusDTO>>>() {});
        return apiResponse.getData() != null ? apiResponse.getData() : Collections.emptyList();
    }

    public List<SeatDTO> getAvailableSeats(Long busId, String journeyDate) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/v1/seats/bus/" + busId + "/available?journeyDate=" + journeyDate))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        ApiResponse<List<SeatDTO>> apiResponse = objectMapper.readValue(response.body(), new TypeReference<ApiResponse<List<SeatDTO>>>() {});
        return apiResponse.getData() != null ? apiResponse.getData() : Collections.emptyList();
    }

    public BusDTO getBusById(Long busId, String journeyDate) throws Exception {
        String url = BASE_URL + "/v1/buses/" + busId;
        if (journeyDate != null && !journeyDate.isEmpty()) {
            url += "?journeyDate=" + journeyDate;
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        ApiResponse<BusDTO> apiResponse = objectMapper.readValue(response.body(), new TypeReference<ApiResponse<BusDTO>>() {});
        return apiResponse.getData();
    }

    public ApiResponse<BookingDTO> createBooking(Long userId, BookingRequestDTO bookingRequest, String token) throws Exception {
        String json = objectMapper.writeValueAsString(bookingRequest);
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/v1/bookings"))
                .header("Content-Type", "application/json")
                .header("userId", String.valueOf(userId))
                .POST(HttpRequest.BodyPublishers.ofString(json));

        if (token != null && !token.isEmpty()) {
            builder.header("Authorization", "Bearer " + token);
        }

        HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        return objectMapper.readValue(response.body(), new TypeReference<ApiResponse<BookingDTO>>() {});
    }

    public List<BookingDTO> getBookingsByUser(Long userId, String token) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/v1/bookings/user/" + userId))
                .header("Accept", "application/json")
                .GET();

        if (token != null && !token.isEmpty()) {
            builder.header("Authorization", "Bearer " + token);
        }

        HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        ApiResponse<List<BookingDTO>> apiResponse = objectMapper.readValue(response.body(), new TypeReference<ApiResponse<List<BookingDTO>>>() {});
        return apiResponse.getData() != null ? apiResponse.getData() : Collections.emptyList();
    }
}
