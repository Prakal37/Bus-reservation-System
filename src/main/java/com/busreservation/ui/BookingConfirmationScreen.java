package com.busreservation.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class BookingConfirmationScreen {
    private Stage stage;
    private NavigationContext navigationContext;

    public BookingConfirmationScreen(Stage stage, NavigationContext navigationContext) {
        this.stage = stage;
        this.navigationContext = navigationContext;
    }

    public Parent getView() {
        VBox root = new VBox(0);
        root.setStyle("-fx-background-color: #f8f9fa;");

        // Header
        VBox headerBox = new VBox(5);
        headerBox.setStyle("-fx-background-color: #27ae60;");
        headerBox.setPadding(new Insets(20));
        headerBox.setAlignment(Pos.CENTER);

        Label successLabel = new Label("✓ BOOKING CONFIRMED");
        successLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        successLabel.setStyle("-fx-text-fill: white;");

        headerBox.getChildren().add(successLabel);

        // Main content area
        VBox contentBox = new VBox(30);
        contentBox.setPadding(new Insets(40, 80, 40, 80));
        contentBox.setStyle("-fx-background-color: #f8f9fa;");
        contentBox.setAlignment(Pos.TOP_CENTER);

        // Get booking data
        NavigationContext.BookingConfirmation confirmation = navigationContext.getBookingConfirmation();
        NavigationContext.Bus bus = confirmation.bus;
        NavigationContext.PassengerInfo passenger = confirmation.passengerInfo;

        // Ticket card
        VBox ticketCard = new VBox(0);
        ticketCard.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 1; -fx-border-radius: 8;");
        ticketCard.setMaxWidth(700);

        // Confirmation number section
        VBox confNumSection = new VBox(8);
        confNumSection.setPadding(new Insets(30));
        confNumSection.setStyle("-fx-border-color: #ddd; -fx-border-width: 0 0 2 0;");
        confNumSection.setAlignment(Pos.CENTER);

        Label confNumLabel = new Label("Confirmation ID:");
        confNumLabel.setFont(Font.font("Segoe UI", 11));
        confNumLabel.setStyle("-fx-text-fill: #666;");

        Label confNumValue = new Label(confirmation.confirmationNumber);
        confNumValue.setFont(Font.font("Courier New", FontWeight.BOLD, 20));
        confNumValue.setStyle("-fx-text-fill: #003d82;");

        confNumSection.getChildren().addAll(confNumLabel, confNumValue);

        // Journey details section
        VBox journeySection = new VBox(20);
        journeySection.setPadding(new Insets(30));
        journeySection.setStyle("-fx-border-color: #ddd; -fx-border-width: 0 0 2 0;");

        HBox routeBox = new HBox(40);
        routeBox.setAlignment(Pos.CENTER);

        VBox fromBox = new VBox(5);
        Label fromTimeLabel = new Label(bus.departureTime);
        fromTimeLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        fromTimeLabel.setStyle("-fx-text-fill: #333;");
        Label fromCityLabel = new Label(bus.departureCity);
        fromCityLabel.setFont(Font.font("Segoe UI", 14));
        fromCityLabel.setStyle("-fx-text-fill: #666;");
        fromBox.getChildren().addAll(fromTimeLabel, fromCityLabel);

        Label arrowLabel = new Label("→");
        arrowLabel.setFont(Font.font("Segoe UI", 24));
        arrowLabel.setStyle("-fx-text-fill: #ccc;");

        VBox toBox = new VBox(5);
        Label toTimeLabel = new Label(bus.arrivalTime);
        toTimeLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        toTimeLabel.setStyle("-fx-text-fill: #333;");
        Label toCityLabel = new Label(bus.destinationCity);
        toCityLabel.setFont(Font.font("Segoe UI", 14));
        toCityLabel.setStyle("-fx-text-fill: #666;");
        toBox.getChildren().addAll(toTimeLabel, toCityLabel);

        routeBox.getChildren().addAll(fromBox, arrowLabel, toBox);

        // Bus details in a grid
        HBox busDetailsBox = new HBox(60);
        busDetailsBox.setAlignment(Pos.CENTER);
        busDetailsBox.setPadding(new Insets(20, 0, 0, 0));

        VBox operatorBox = new VBox(3);
        Label operatorTitleLabel = new Label("Bus Operator");
        operatorTitleLabel.setStyle("-fx-text-fill: #999; -fx-font-size: 10;");
        Label operatorValueLabel = new Label(bus.busOperator);
        operatorValueLabel.setFont(Font.font("Segoe UI", 12));
        operatorValueLabel.setStyle("-fx-text-fill: #333;");
        operatorBox.getChildren().addAll(operatorTitleLabel, operatorValueLabel);

        VBox typeBox = new VBox(3);
        Label typeTitleLabel = new Label("Bus Type");
        typeTitleLabel.setStyle("-fx-text-fill: #999; -fx-font-size: 10;");
        Label typeValueLabel = new Label(bus.busType);
        typeValueLabel.setFont(Font.font("Segoe UI", 12));
        typeValueLabel.setStyle("-fx-text-fill: #333;");
        typeBox.getChildren().addAll(typeTitleLabel, typeValueLabel);

        VBox durationBox = new VBox(3);
        Label durationTitleLabel = new Label("Duration");
        durationTitleLabel.setStyle("-fx-text-fill: #999; -fx-font-size: 10;");
        Label durationValueLabel = new Label(bus.duration);
        durationValueLabel.setFont(Font.font("Segoe UI", 12));
        durationValueLabel.setStyle("-fx-text-fill: #333;");
        durationBox.getChildren().addAll(durationTitleLabel, durationValueLabel);

        busDetailsBox.getChildren().addAll(operatorBox, typeBox, durationBox);

        journeySection.getChildren().addAll(routeBox, busDetailsBox);

        // Passenger and seats section
        VBox passengerSection = new VBox(15);
        passengerSection.setPadding(new Insets(30));
        passengerSection.setStyle("-fx-border-color: #ddd; -fx-border-width: 0 0 2 0;");

        HBox passengerDetailsBox = new HBox(80);
        passengerDetailsBox.setAlignment(Pos.CENTER_LEFT);

        VBox passengerNameBox = new VBox(3);
        Label passengerNameTitle = new Label("Passenger Name");
        passengerNameTitle.setStyle("-fx-text-fill: #999; -fx-font-size: 10;");
        Label passengerNameValue = new Label(passenger.passengerName);
        passengerNameValue.setFont(Font.font("Segoe UI", 13));
        passengerNameValue.setStyle("-fx-text-fill: #333;");
        passengerNameBox.getChildren().addAll(passengerNameTitle, passengerNameValue);

        VBox passengerAgeBox = new VBox(3);
        Label passengerAgeTitle = new Label("Age");
        passengerAgeTitle.setStyle("-fx-text-fill: #999; -fx-font-size: 10;");
        Label passengerAgeValue = new Label(String.valueOf(passenger.age));
        passengerAgeValue.setFont(Font.font("Segoe UI", 13));
        passengerAgeValue.setStyle("-fx-text-fill: #333;");
        passengerAgeBox.getChildren().addAll(passengerAgeTitle, passengerAgeValue);

        VBox passengerGenderBox = new VBox(3);
        Label passengerGenderTitle = new Label("Gender");
        passengerGenderTitle.setStyle("-fx-text-fill: #999; -fx-font-size: 10;");
        Label passengerGenderValue = new Label(passenger.gender);
        passengerGenderValue.setFont(Font.font("Segoe UI", 13));
        passengerGenderValue.setStyle("-fx-text-fill: #333;");
        passengerGenderBox.getChildren().addAll(passengerGenderTitle, passengerGenderValue);

        passengerDetailsBox.getChildren().addAll(passengerNameBox, passengerAgeBox, passengerGenderBox);

        VBox seatsBox = new VBox(10);
        Label seatsTitle = new Label("SEATS RESERVED");
        seatsTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        seatsTitle.setStyle("-fx-text-fill: #003d82;");

        Label seatsValue = new Label(seatsToString(confirmation.seats));
        seatsValue.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        seatsValue.setStyle("-fx-text-fill: #27ae60;");

        seatsBox.getChildren().addAll(seatsTitle, seatsValue);

        passengerSection.getChildren().addAll(passengerDetailsBox, seatsBox);

        // Price section
        VBox priceSection = new VBox(10);
        priceSection.setPadding(new Insets(30));
        priceSection.setAlignment(Pos.CENTER_RIGHT);

        Label priceTitle = new Label("TOTAL FARE");
        priceTitle.setStyle("-fx-text-fill: #999; -fx-font-size: 11; -fx-font-weight: bold;");

        Label priceValue = new Label("₹" + (int)confirmation.totalPrice);
        priceValue.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));
        priceValue.setStyle("-fx-text-fill: #27ae60;");

        priceSection.getChildren().addAll(priceTitle, priceValue);

        // Add all sections to ticket card
        ticketCard.getChildren().addAll(confNumSection, journeySection, passengerSection, priceSection);

        // Action buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(30, 0, 0, 0));

        Button downloadButton = new Button("DOWNLOAD TICKET");
        downloadButton.setStyle(
                "-fx-font-size: 13; -fx-font-weight: bold; -fx-padding: 12 35 12 35; " +
                "-fx-background-color: #0066cc; -fx-text-fill: white; " +
                "-fx-border-radius: 4; -fx-background-radius: 4; -fx-cursor: hand;"
        );
        downloadButton.setOnMouseEntered(e -> downloadButton.setStyle(
                "-fx-font-size: 13; -fx-font-weight: bold; -fx-padding: 12 35 12 35; " +
                "-fx-background-color: #0052a3; -fx-text-fill: white; " +
                "-fx-border-radius: 4; -fx-background-radius: 4; -fx-cursor: hand;"
        ));
        downloadButton.setOnMouseExited(e -> downloadButton.setStyle(
                "-fx-font-size: 13; -fx-font-weight: bold; -fx-padding: 12 35 12 35; " +
                "-fx-background-color: #0066cc; -fx-text-fill: white; " +
                "-fx-border-radius: 4; -fx-background-radius: 4; -fx-cursor: hand;"
        ));

        Button myBookingsButton = new Button("MY BOOKINGS");
        myBookingsButton.setStyle(
                "-fx-font-size: 13; -fx-font-weight: bold; -fx-padding: 12 35 12 35; " +
                "-fx-background-color: #ff9800; -fx-text-fill: white; " +
                "-fx-border-radius: 4; -fx-background-radius: 4; -fx-cursor: hand;"
        );
        myBookingsButton.setOnMouseEntered(e -> myBookingsButton.setStyle(
                "-fx-font-size: 13; -fx-font-weight: bold; -fx-padding: 12 35 12 35; " +
                "-fx-background-color: #f57c00; -fx-text-fill: white; " +
                "-fx-border-radius: 4; -fx-background-radius: 4; -fx-cursor: hand;"
        ));
        myBookingsButton.setOnMouseExited(e -> myBookingsButton.setStyle(
                "-fx-font-size: 13; -fx-font-weight: bold; -fx-padding: 12 35 12 35; " +
                "-fx-background-color: #ff9800; -fx-text-fill: white; " +
                "-fx-border-radius: 4; -fx-background-radius: 4; -fx-cursor: hand;"
        ));
        myBookingsButton.setOnAction(e -> navigateToMyBookings());

        Button dashboardButton = new Button("BACK TO DASHBOARD");
        dashboardButton.setStyle(
                "-fx-font-size: 13; -fx-font-weight: bold; -fx-padding: 12 35 12 35; " +
                "-fx-background-color: #95a5a6; -fx-text-fill: white; " +
                "-fx-border-radius: 4; -fx-background-radius: 4; -fx-cursor: hand;"
        );
        dashboardButton.setOnMouseEntered(e -> dashboardButton.setStyle(
                "-fx-font-size: 13; -fx-font-weight: bold; -fx-padding: 12 35 12 35; " +
                "-fx-background-color: #7f8c8d; -fx-text-fill: white; " +
                "-fx-border-radius: 4; -fx-background-radius: 4; -fx-cursor: hand;"
        ));
        dashboardButton.setOnMouseExited(e -> dashboardButton.setStyle(
                "-fx-font-size: 13; -fx-font-weight: bold; -fx-padding: 12 35 12 35; " +
                "-fx-background-color: #95a5a6; -fx-text-fill: white; " +
                "-fx-border-radius: 4; -fx-background-radius: 4; -fx-cursor: hand;"
        ));
        dashboardButton.setOnAction(e -> navigateToDashboard());

        buttonBox.getChildren().addAll(downloadButton, myBookingsButton, dashboardButton);

        contentBox.getChildren().addAll(ticketCard, buttonBox);

        root.getChildren().addAll(headerBox, contentBox);
        return root;
    }

    private String seatsToString(String[] seats) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < seats.length; i++) {
            sb.append(seats[i]);
            if (i < seats.length - 1) sb.append(", ");
        }
        return sb.toString();
    }

    private void navigateToMyBookings() {
        MyBookingsScreen myBookingsScreen = new MyBookingsScreen(stage, navigationContext);
        Scene scene = new Scene(myBookingsScreen.getView(), 1100, 750);
        stage.setScene(scene);
    }

    private void navigateToDashboard() {
        DashboardScreen dashboardScreen = new DashboardScreen(stage, navigationContext);
        Scene scene = new Scene(dashboardScreen.getView(), 1100, 750);
        stage.setScene(scene);
    }
}
