package com.busreservation.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;

public class BusResultsScreen {
    private Stage stage;
    private NavigationContext navigationContext;

    public BusResultsScreen(Stage stage, NavigationContext navigationContext) {
        this.stage = stage;
        this.navigationContext = navigationContext;
    }

    public Parent getView() {
        VBox root = new VBox(0);
        root.setStyle("-fx-background-color: #f8f9fa;");

        // Header
        VBox headerBox = new VBox(5);
        headerBox.setStyle("-fx-background-color: #003d82;");
        headerBox.setPadding(new Insets(20));
        headerBox.setAlignment(Pos.CENTER_LEFT);

        NavigationContext.SearchQuery query = navigationContext.getSearchQuery();
        Label headerTitle = new Label(query.departureCity + " → " + query.destinationCity);
        headerTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        headerTitle.setStyle("-fx-text-fill: white;");

        Label headerDate = new Label("Date: " + query.date);
        headerDate.setStyle("-fx-text-fill: #ccc; -fx-font-size: 12;");

        headerBox.getChildren().addAll(headerTitle, headerDate);

        // Bus list
        VBox busListBox = new VBox(15);
        busListBox.setPadding(new Insets(20));
        busListBox.setStyle("-fx-background-color: #f8f9fa;");

        List<NavigationContext.Bus> buses = generateDummyBuses(query);

        if (buses.isEmpty()) {
            Label noResultsLabel = new Label("No buses found for your search");
            noResultsLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 14;");
            busListBox.getChildren().add(noResultsLabel);
        } else {
            for (NavigationContext.Bus bus : buses) {
                VBox busCard = createBusCard(bus);
                busListBox.getChildren().add(busCard);
            }
        }

        ScrollPane scrollPane = new ScrollPane(busListBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #f8f9fa;");

        root.getChildren().addAll(headerBox, scrollPane);
        return root;
    }

    private VBox createBusCard(NavigationContext.Bus bus) {
        VBox card = new VBox(8);
        card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e0e0e0; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 15;");
        card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #ddd; -fx-border-width: 1; -fx-border-radius: 8; -fx-background-radius: 8;");

        // Operator and Type
        HBox operatorBox = new HBox(15);
        Label operatorLabel = new Label(bus.busOperator);
        operatorLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        operatorLabel.setStyle("-fx-text-fill: #003d82;");
        
        Label typeLabel = new Label(bus.busType);
        typeLabel.setFont(Font.font("Segoe UI", 11));
        typeLabel.setStyle("-fx-text-fill: #ff9800; -fx-font-weight: bold;");
        operatorBox.getChildren().addAll(operatorLabel, typeLabel);

        // Departure and Arrival times with duration
        HBox timesBox = new HBox(20);
        timesBox.setAlignment(Pos.CENTER_LEFT);
        
        VBox departBox = new VBox(2);
        Label deptTimeLabel = new Label(bus.departureTime);
        deptTimeLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        deptTimeLabel.setStyle("-fx-text-fill: #333;");
        Label deptCityLabel = new Label(bus.departureCity);
        deptCityLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 11;");
        departBox.getChildren().addAll(deptTimeLabel, deptCityLabel);
        
        Label arrowLabel = new Label("→");
        arrowLabel.setFont(Font.font("Segoe UI", 16));
        arrowLabel.setStyle("-fx-text-fill: #ccc;");
        
        VBox arrivalBox = new VBox(2);
        Label arrTimeLabel = new Label(bus.arrivalTime);
        arrTimeLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        arrTimeLabel.setStyle("-fx-text-fill: #333;");
        Label arrCityLabel = new Label(bus.destinationCity);
        arrCityLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 11;");
        arrivalBox.getChildren().addAll(arrTimeLabel, arrCityLabel);
        
        Label durationLabel = new Label(bus.duration);
        durationLabel.setStyle("-fx-text-fill: #999; -fx-font-size: 11;");
        
        timesBox.getChildren().addAll(departBox, arrowLabel, arrivalBox);

        // Fare and Availability
        HBox priceBox = new HBox(40);
        priceBox.setAlignment(Pos.CENTER_LEFT);
        
        VBox fareBox = new VBox(2);
        Label fareLabel = new Label("₹" + (int)bus.price);
        fareLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        fareLabel.setStyle("-fx-text-fill: #27ae60;");
        Label fareTextLabel = new Label("per seat");
        fareTextLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 10;");
        fareBox.getChildren().addAll(fareLabel, fareTextLabel);
        
        VBox seatsBox = new VBox(2);
        Label seatsLabel = new Label(bus.availableSeats + " Seats");
        seatsLabel.setFont(Font.font("Segoe UI", 11));
        seatsLabel.setStyle("-fx-text-fill: #333;");
        Label availLabel = new Label("Available");
        availLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 10;");
        seatsBox.getChildren().addAll(seatsLabel, availLabel);

        priceBox.getChildren().addAll(fareBox, seatsBox);

        // Select button
        Button selectButton = new Button("SELECT SEAT");
        selectButton.setStyle(
                "-fx-font-size: 12; -fx-font-weight: bold; -fx-padding: 10 30 10 30; " +
                "-fx-background-color: #0066cc; -fx-text-fill: white; " +
                "-fx-border-radius: 4; -fx-background-radius: 4; -fx-cursor: hand;"
        );
        selectButton.setOnMouseEntered(e -> selectButton.setStyle(
                "-fx-font-size: 12; -fx-font-weight: bold; -fx-padding: 10 30 10 30; " +
                "-fx-background-color: #0052a3; -fx-text-fill: white; " +
                "-fx-border-radius: 4; -fx-background-radius: 4; -fx-cursor: hand;"
        ));
        selectButton.setOnMouseExited(e -> selectButton.setStyle(
                "-fx-font-size: 12; -fx-font-weight: bold; -fx-padding: 10 30 10 30; " +
                "-fx-background-color: #0066cc; -fx-text-fill: white; " +
                "-fx-border-radius: 4; -fx-background-radius: 4; -fx-cursor: hand;"
        ));
        selectButton.setOnAction(e -> {
            navigationContext.setSelectedBus(bus);
            navigateToSeatSelection();
        });

        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.getChildren().add(selectButton);

        card.getChildren().addAll(operatorBox, timesBox, priceBox, buttonBox);
        return card;
    }

    private List<NavigationContext.Bus> generateDummyBuses(NavigationContext.SearchQuery query) {
        List<NavigationContext.Bus> buses = new ArrayList<>();
        
        // Tamil Nadu buses
        buses.add(new NavigationContext.Bus(1, "SETC", "Ultra Deluxe", query.departureCity, query.destinationCity, 
                "09:00 PM", "05:30 AM", "8h 30m", 520, 40, 18));
        buses.add(new NavigationContext.Bus(2, "KPN Travels", "AC Seater", query.departureCity, query.destinationCity, 
                "10:15 PM", "06:15 AM", "8h", 650, 45, 12));
        buses.add(new NavigationContext.Bus(3, "TNSTC Express", "Standard", query.departureCity, query.destinationCity, 
                "08:30 PM", "05:00 AM", "8h 30m", 480, 50, 24));
        buses.add(new NavigationContext.Bus(4, "Parveen Travels", "Premium", query.departureCity, query.destinationCity, 
                "09:45 PM", "06:00 AM", "8h 15m", 700, 40, 15));
        
        return buses;
    }

    private void navigateToSearch() {
        SearchBusScreen searchScreen = new SearchBusScreen(stage, navigationContext);
        Scene scene = new Scene(searchScreen.getView(), 1100, 750);
        stage.setScene(scene);
    }

    private void navigateToSeatSelection() {
        SeatSelectionScreen seatSelectionScreen = new SeatSelectionScreen(stage, navigationContext);
        Scene scene = new Scene(seatSelectionScreen.getView(), 1100, 750);
        stage.setScene(scene);
    }
}
