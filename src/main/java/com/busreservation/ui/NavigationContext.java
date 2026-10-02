package com.busreservation.ui;

import com.busreservation.dto.BookingDTO;
import com.busreservation.dto.BusDTO;
import com.busreservation.dto.UserResponseDTO;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * Manages navigation and session state across all screens
 */
public class NavigationContext {
    private Stage stage;
    private Long userId;
    private String loggedInUser;
    private String jwtToken;
    private UserResponseDTO currentUser;

    private SearchQuery searchQuery;
    private Bus selectedBus;
    private BusDTO selectedBusDTO;
    private String[] selectedSeats;
    private PassengerInfo passengerInfo;
    private BookingConfirmation bookingConfirmation;
    private BookingDTO createdBookingDTO;

    private final ApiClient apiClient;

    public NavigationContext(Stage stage) {
        this.stage = stage;
        this.apiClient = new ApiClient();
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public Stage getStage() {
        return stage;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(String username) {
        this.loggedInUser = username;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public UserResponseDTO getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(UserResponseDTO currentUser) {
        this.currentUser = currentUser;
        if (currentUser != null) {
            this.userId = currentUser.getUserId();
            this.loggedInUser = currentUser.getUsername();
        }
    }

    public SearchQuery getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(SearchQuery query) {
        this.searchQuery = query;
    }

    public Bus getSelectedBus() {
        return selectedBus;
    }

    public void setSelectedBus(Bus bus) {
        this.selectedBus = bus;
    }

    public BusDTO getSelectedBusDTO() {
        return selectedBusDTO;
    }

    public void setSelectedBusDTO(BusDTO selectedBusDTO) {
        this.selectedBusDTO = selectedBusDTO;
        if (selectedBusDTO != null) {
            // Resolve source/destination from nested RouteDTO
            String src = "";
            String dest = "";
            String duration = "8h";
            if (selectedBusDTO.getRoute() != null) {
                src = selectedBusDTO.getRoute().getSource() != null ? selectedBusDTO.getRoute().getSource() : "";
                dest = selectedBusDTO.getRoute().getDestination() != null ? selectedBusDTO.getRoute().getDestination() : "";
                Integer hrs = selectedBusDTO.getRoute().getApproximateDurationHours();
                duration = hrs != null ? hrs + "h" : "8h";
            }
            int availSeats = selectedBusDTO.getAvailableSeats() != null ? (int) (long) selectedBusDTO.getAvailableSeats() : 40;
            // Map BusDTO to legacy UI Bus object for backwards compatibility
            this.selectedBus = new Bus(
                    selectedBusDTO.getBusId().intValue(),
                    selectedBusDTO.getOperatorName() != null ? selectedBusDTO.getOperatorName() : selectedBusDTO.getBusName(),
                    selectedBusDTO.getBusType() != null ? selectedBusDTO.getBusType() : "Standard",
                    src,
                    dest,
                    selectedBusDTO.getDepartureTime() != null ? selectedBusDTO.getDepartureTime() : "",
                    selectedBusDTO.getArrivalTime() != null ? selectedBusDTO.getArrivalTime() : "",
                    duration,
                    selectedBusDTO.getPricePerSeat() != null ? selectedBusDTO.getPricePerSeat() : 0.0,
                    selectedBusDTO.getTotalSeats() != null ? selectedBusDTO.getTotalSeats() : 40,
                    availSeats
            );
        }
    }

    public String[] getSelectedSeats() {
        return selectedSeats;
    }

    public void setSelectedSeats(String[] seats) {
        this.selectedSeats = seats;
    }

    public PassengerInfo getPassengerInfo() {
        return passengerInfo;
    }

    public void setPassengerInfo(PassengerInfo info) {
        this.passengerInfo = info;
    }

    public BookingConfirmation getBookingConfirmation() {
        return bookingConfirmation;
    }

    public void setBookingConfirmation(BookingConfirmation confirmation) {
        this.bookingConfirmation = confirmation;
    }

    public BookingDTO getCreatedBookingDTO() {
        return createdBookingDTO;
    }

    public void setCreatedBookingDTO(BookingDTO createdBookingDTO) {
        this.createdBookingDTO = createdBookingDTO;
    }

    public Scene buildScene(Parent root) {
        Scene scene = new Scene(root, 1100, 750);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());
        return scene;
    }

    public void showScene(Parent root) {
        stage.setScene(buildScene(root));
    }

    public void clearBookingFlow() {
        this.selectedBus = null;
        this.selectedBusDTO = null;
        this.selectedSeats = null;
        this.passengerInfo = null;
        this.bookingConfirmation = null;
        this.createdBookingDTO = null;
    }

    public void logout() {
        this.userId = null;
        this.loggedInUser = null;
        this.jwtToken = null;
        this.currentUser = null;
        this.searchQuery = null;
        this.selectedBus = null;
        this.selectedBusDTO = null;
        this.selectedSeats = null;
        this.passengerInfo = null;
        this.bookingConfirmation = null;
        this.createdBookingDTO = null;
    }

    // Data Models for Frontend Only
    public static class SearchQuery {
        public String departureCity;
        public String destinationCity;
        public String date;
        public int passengers;

        public SearchQuery(String departureCity, String destinationCity, String date, int passengers) {
            this.departureCity = departureCity;
            this.destinationCity = destinationCity;
            this.date = date;
            this.passengers = passengers;
        }
    }

    public static class Bus {
        public int busId;
        public String busOperator;
        public String busType;
        public String departureCity;
        public String destinationCity;
        public String departureTime;
        public String arrivalTime;
        public String duration;
        public double price;
        public int totalSeats;
        public int availableSeats;

        public Bus(int busId, String busOperator, String busType, String departureCity, String destinationCity,
                   String departureTime, String arrivalTime, String duration, double price, int totalSeats, int availableSeats) {
            this.busId = busId;
            this.busOperator = busOperator;
            this.busType = busType;
            this.departureCity = departureCity;
            this.destinationCity = destinationCity;
            this.departureTime = departureTime;
            this.arrivalTime = arrivalTime;
            this.duration = duration;
            this.price = price;
            this.totalSeats = totalSeats;
            this.availableSeats = availableSeats;
        }
    }

    public static class PassengerInfo {
        public String passengerName;
        public int age;
        public String gender;
        public String mobileNumber;
        public String email;

        public PassengerInfo(String name, int age, String gender, String email, String mobileNumber) {
            this.passengerName = name;
            this.age = age;
            this.gender = gender;
            this.email = email;
            this.mobileNumber = mobileNumber;
        }
    }

    public static class BookingConfirmation {
        public String confirmationNumber;
        public Bus bus;
        public String[] seats;
        public PassengerInfo passengerInfo;
        public double totalPrice;

        public BookingConfirmation(String confirmationNumber, Bus bus, String[] seats, PassengerInfo passengerInfo, double totalPrice) {
            this.confirmationNumber = confirmationNumber;
            this.bus = bus;
            this.seats = seats;
            this.passengerInfo = passengerInfo;
            this.totalPrice = totalPrice;
        }
    }
}
