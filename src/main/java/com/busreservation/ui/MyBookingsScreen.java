package com.busreservation.ui;

import com.busreservation.dto.BookingDTO;
import com.busreservation.dto.BusDTO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
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

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MyBookingsScreen {
    private final Stage stage;
    private final NavigationContext navigationContext;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");

    public MyBookingsScreen(Stage stage, NavigationContext navigationContext) {
        this.stage = stage;
        this.navigationContext = navigationContext;
    }

    public Parent getView() {
        VBox root = new VBox(0);
        root.setStyle("-fx-background-color: #f4f7fb;");

        VBox headerBox = new VBox(10);
        headerBox.getStyleClass().add("app-header");
        headerBox.setPadding(new Insets(18, 26, 18, 26));

        HBox headerRow = new HBox();
        headerRow.setAlignment(Pos.CENTER_LEFT);
        Button backButton = new Button("← Back to Dashboard");
        backButton.getStyleClass().add("ghost-button");
        backButton.setOnAction(e -> navigationContext.showScene(new DashboardScreen(stage, navigationContext).getView()));

        Label titleLabel = new Label("My Bookings");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        titleLabel.setStyle("-fx-text-fill: white;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button trackButton = new Button("Track Bus");
        trackButton.getStyleClass().add("ghost-button");
        trackButton.setOnAction(e -> navigationContext.showScene(new TrackBusScreen(stage, navigationContext).getView()));

        headerRow.getChildren().addAll(backButton, spacer, titleLabel, trackButton);
        headerBox.getChildren().add(headerRow);

        VBox contentBox = new VBox(18);
        contentBox.setPadding(new Insets(24));

        List<BookingDTO> bookings = fetchBookings();
        if (bookings.isEmpty()) {
            VBox emptyBox = new VBox(10);
            emptyBox.getStyleClass().add("card");
            emptyBox.setPadding(new Insets(24));
            emptyBox.setMaxWidth(600);
            Label noBookingsLabel = new Label("You have no bookings yet.");
            noBookingsLabel.setStyle("-fx-text-fill: #334155; -fx-font-size: 14px; -fx-font-weight: bold;");
            Button searchButton = new Button("Search Buses");
            searchButton.getStyleClass().add("secondary-button");
            searchButton.setOnAction(e -> navigationContext.showScene(new SearchBusScreen(stage, navigationContext).getView()));
            emptyBox.getChildren().addAll(noBookingsLabel, searchButton);
            contentBox.getChildren().add(emptyBox);
        } else {
            for (BookingDTO booking : bookings) {
                BusDTO busDetails = fetchBusDetails(booking.getBusId(), booking.getJourneyDate());
                contentBox.getChildren().add(createBookingCard(booking, busDetails));
            }
        }

        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        root.getChildren().addAll(headerBox, scrollPane);
        return root;
    }

    private List<BookingDTO> fetchBookings() {
        try {
            Long userId = navigationContext.getUserId();
            String token = navigationContext.getJwtToken();
            if (userId != null) {
                return navigationContext.getApiClient().getBookingsByUser(userId, token);
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch bookings: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    private BusDTO fetchBusDetails(Long busId, String journeyDate) {
        try {
            return navigationContext.getApiClient().getBusById(busId, journeyDate);
        } catch (Exception e) {
            System.err.println("Failed to fetch bus details for busId " + busId + ": " + e.getMessage());
        }
        return null;
    }

    private VBox createBookingCard(BookingDTO booking, BusDTO bus) {
        VBox card = new VBox(10);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(20));
        card.setMaxWidth(900);

        HBox topBox = new HBox(12);
        String bookingRef = booking.getBookingReference() != null ? booking.getBookingReference() : "ID-" + booking.getBookingId();
        Label refLabel = new Label("Booking Ref: " + bookingRef);
        refLabel.setStyle("-fx-text-fill: #0d47a1; -fx-font-size: 12px; -fx-font-weight: bold;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        String status = booking.getBookingStatus() != null ? booking.getBookingStatus() : "CONFIRMED";
        Label statusLabel = new Label("✓ " + status.toUpperCase());
        statusLabel.setStyle("-fx-text-fill: #16a34a; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 5 10 5 10; -fx-background-color: #e8f5e9; -fx-background-radius: 999px;");
        topBox.getChildren().addAll(refLabel, spacer, statusLabel);

        VBox details = new VBox(8);
        if (bus != null) {
            String source = bus.getRoute() != null && bus.getRoute().getSource() != null ? bus.getRoute().getSource() : "?";
            String destination = bus.getRoute() != null && bus.getRoute().getDestination() != null ? bus.getRoute().getDestination() : "?";
            Label routeLabel = new Label("Route: " + source + " → " + destination);
            routeLabel.setStyle("-fx-text-fill: #0f172a; -fx-font-size: 12px;");
            Label busLabel = new Label("Bus: " + (bus.getBusName() != null ? bus.getBusName() : "Bus") + " / " + (bus.getBusType() != null ? bus.getBusType() : "Standard"));
            busLabel.setStyle("-fx-text-fill: #334155; -fx-font-size: 12px;");
            Label timeLabel = new Label("Travel Date: " + booking.getJourneyDate() + " • " + (bus.getDepartureTime() != null ? bus.getDepartureTime() : "") + " - " + (bus.getArrivalTime() != null ? bus.getArrivalTime() : ""));
            timeLabel.setStyle("-fx-text-fill: #475569; -fx-font-size: 11px;");
            details.getChildren().addAll(routeLabel, busLabel, timeLabel);
        } else {
            Label fallback = new Label("Bus ID: " + booking.getBusId() + " • Date: " + booking.getJourneyDate());
            fallback.setStyle("-fx-text-fill: #334155; -fx-font-size: 12px;");
            details.getChildren().add(fallback);
        }

        Label seatLabel = new Label("Seat(s): " + booking.getSeatNumbers() + " • Passenger: " + booking.getPassengerName());
        seatLabel.setStyle("-fx-text-fill: #1e293b; -fx-font-size: 12px; -fx-font-weight: bold;");
        Label priceLabel = new Label("Total Fare: ₹" + (booking.getTotalPrice() != null ? booking.getTotalPrice().intValue() : 0));
        priceLabel.setStyle("-fx-text-fill: #16a34a; -fx-font-size: 16px; -fx-font-weight: bold;");

        Button detailsButton = new Button("View Details");
        detailsButton.getStyleClass().add("ghost-button");
        detailsButton.setOnAction(e -> {
            // Read-only detail summary; no backend cancellation logic is available here.
        });

        details.getChildren().addAll(seatLabel, priceLabel, detailsButton);
        card.getChildren().addAll(topBox, new Separator(), details);
        return card;
    }
}
