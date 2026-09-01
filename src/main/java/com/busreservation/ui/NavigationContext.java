package com.busreservation.ui;

import javafx.stage.Stage;

/**
 * Manages navigation and session state across all screens
 */
public class NavigationContext {
    private Stage stage;
    private String loggedInUser;
    private SearchQuery searchQuery;
    private Bus selectedBus;
    private String[] selectedSeats;
    private PassengerInfo passengerInfo;
    private BookingConfirmation bookingConfirmation;

    public NavigationContext(Stage stage) {
        this.stage = stage;
    }

    public Stage getStage() {
        return stage;
    }

    public String getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(String username) {
        this.loggedInUser = username;
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

    public void logout() {
        this.loggedInUser = null;
        this.searchQuery = null;
        this.selectedBus = null;
        this.selectedSeats = null;
        this.passengerInfo = null;
        this.bookingConfirmation = null;
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
