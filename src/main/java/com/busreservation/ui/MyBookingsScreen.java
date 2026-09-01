package com.busreservation.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;

public class MyBookingsScreen {
    private Stage stage;
    private NavigationContext navigationContext;

    public MyBookingsScreen(Stage stage, NavigationContext navigationContext) {
        this.stage = stage;
        this.navigationContext = navigationContext;
    }

    public Parent getView() {
        VBox root = new VBox(20);
        root.setStyle("-fx-background-color: #f5f5f5;");
        root.setPadding(new Insets(30));

        // Header with back button
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 20, 0));

        Button backButton = new Button("← Back to Dashboard");
        backButton.setStyle(
                "-fx-font-size: 11; -fx-padding: 8 16 8 16; " +
                "-fx-background-color: #95a5a6; -fx-text-fill: white; " +
                "-fx-border-radius: 4; -fx-background-radius: 4; -fx-cursor: hand;"
        );
        backButton.setOnAction(e -> navigateToDashboard());

        Label titleLabel = new Label("My Bookings");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setStyle("-fx-text-fill: #2c3e50;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(backButton, spacer, titleLabel);

        // Bookings list
        VBox bookingsListBox = new VBox(15);
        bookingsListBox.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 4; -fx-background-radius: 4;");
        bookingsListBox.setPadding(new Insets(20));

        // Add current booking if exists
        if (navigationContext.getBookingConfirmation() != null) {
            VBox currentBookingCard = createBookingCard(navigationContext.getBookingConfirmation());
            bookingsListBox.getChildren().add(currentBookingCard);
            bookingsListBox.getChildren().add(new Separator());
        }

        // Add dummy bookings
        List<NavigationContext.BookingConfirmation> dummyBookings = generateDummyBookings();
        for (NavigationContext.BookingConfirmation booking : dummyBookings) {
            VBox bookingCard = createBookingCard(booking);
            bookingsListBox.getChildren().add(bookingCard);
            bookingsListBox.getChildren().add(new Separator());
        }

        // If no bookings
        if ((navigationContext.getBookingConfirmation() == null) && dummyBookings.isEmpty()) {
            Label noBookingsLabel = new Label("You don't have any bookings yet.");
            noBookingsLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 14;");
            bookingsListBox.getChildren().add(noBookingsLabel);
        }

        ScrollPane scrollPane = new ScrollPane(bookingsListBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 0;");

        root.getChildren().addAll(header, new Separator(), scrollPane);
        return root;
    }

    private VBox createBookingCard(NavigationContext.BookingConfirmation booking) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 8;");
        card.setPadding(new Insets(20));

        // Confirmation number and status
        HBox topBox = new HBox(15);
        Label confNumLabel = new Label("Booking ID: " + booking.confirmationNumber);
        confNumLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        confNumLabel.setStyle("-fx-text-fill: #003d82;");

        Label statusLabel = new Label("✓ CONFIRMED");
        statusLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold; -fx-font-size: 11;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        topBox.getChildren().addAll(confNumLabel, spacer, statusLabel);

        // Bus details
        VBox busDetailsBox = new VBox(5);
        Label busOperatorLabel = new Label("Operator: " + booking.bus.busOperator + " (" + booking.bus.busType + ")");
        busOperatorLabel.setStyle("-fx-text-fill: #333; -fx-font-size: 12;");
        Label routeLabel = new Label("Route: " + booking.bus.departureCity + " → " + booking.bus.destinationCity);
        routeLabel.setStyle("-fx-text-fill: #333; -fx-font-size: 12;");
        Label timeLabel = new Label("Time: " + booking.bus.departureTime + " - " + booking.bus.arrivalTime + " (" + booking.bus.duration + ")");
        timeLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 11;");
        Label seatsLabel = new Label("Seats: " + seatsToString(booking.seats));
        seatsLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
        busDetailsBox.getChildren().addAll(busOperatorLabel, routeLabel, timeLabel, seatsLabel);

        // Passenger details
        VBox passengerDetailsBox = new VBox(5);
        Label passengerNameLabel = new Label("Passenger: " + booking.passengerInfo.passengerName + " (Age: " + booking.passengerInfo.age + ", " + booking.passengerInfo.gender + ")");
        passengerNameLabel.setStyle("-fx-text-fill: #333; -fx-font-size: 12;");
        Label phoneLabel = new Label("Phone: " + booking.passengerInfo.mobileNumber);
        phoneLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 11;");
        passengerDetailsBox.getChildren().addAll(passengerNameLabel, phoneLabel);

        // Price
        Label priceLabel = new Label("Total Fare: ₹" + (int)booking.totalPrice);
        priceLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        priceLabel.setStyle("-fx-text-fill: #27ae60;");

        card.getChildren().addAll(
                topBox,
                new Separator(),
                busDetailsBox,
                new Separator(),
                passengerDetailsBox,
                new Separator(),
                priceLabel
        );

        return card;
    }

    private List<NavigationContext.BookingConfirmation> generateDummyBookings() {
        List<NavigationContext.BookingConfirmation> bookings = new ArrayList<>();

        // Tamil Nadu booking 1
        NavigationContext.Bus bus1 = new NavigationContext.Bus(
                10, "TNSTC Express", "Standard", "Chennai", "Madurai", "08:30 PM", "05:00 AM", "8h 30m", 480, 50, 24
        );
        NavigationContext.PassengerInfo passenger1 = new NavigationContext.PassengerInfo("Raj Kumar", 28, "Male", "rajkumar@example.com", "9876543210");
        String[] seats1 = {"A2", "A3"};
        bookings.add(new NavigationContext.BookingConfirmation("TNBR20260831001", bus1, seats1, passenger1, 960));

        // Tamil Nadu booking 2
        NavigationContext.Bus bus2 = new NavigationContext.Bus(
                11, "KPN Travels", "AC Seater", "Coimbatore", "Chennai", "10:15 PM", "06:15 AM", "8h", 650, 45, 12
        );
        NavigationContext.PassengerInfo passenger2 = new NavigationContext.PassengerInfo("Priya Sharma", 32, "Female", "priya@example.com", "9876543211");
        String[] seats2 = {"B4"};
        bookings.add(new NavigationContext.BookingConfirmation("TNBR20260905002", bus2, seats2, passenger2, 650));

        return bookings;
    }

    private String seatsToString(String[] seats) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < seats.length; i++) {
            sb.append(seats[i]);
            if (i < seats.length - 1) sb.append(", ");
        }
        return sb.toString();
    }

    private void navigateToDashboard() {
        DashboardScreen dashboardScreen = new DashboardScreen(stage, navigationContext);
        Scene scene = new Scene(dashboardScreen.getView(), 1100, 750);
        stage.setScene(scene);
    }
}
