package com.busreservation.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class BookingConfirmationScreen {
    private final Stage stage;
    private final NavigationContext navigationContext;

    public BookingConfirmationScreen(Stage stage, NavigationContext navigationContext) {
        this.stage = stage;
        this.navigationContext = navigationContext;
    }

    public Parent getView() {
        VBox root = new VBox(0);
        root.setStyle("-fx-background-color: #f4f7fb;");

        VBox headerBox = new VBox(10);
        headerBox.setStyle("-fx-background-color: linear-gradient(to right, #16a34a, #22c55e);");
        headerBox.setPadding(new Insets(18, 26, 18, 26));
        headerBox.setAlignment(Pos.CENTER);

        Label successLabel = new Label("✓ BOOKING CONFIRMED");
        successLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        successLabel.setStyle("-fx-text-fill: white;");
        headerBox.getChildren().add(successLabel);

        VBox contentBox = new VBox(20);
        contentBox.setPadding(new Insets(28));
        contentBox.setAlignment(Pos.TOP_CENTER);

        NavigationContext.BookingConfirmation confirmation = navigationContext.getBookingConfirmation();
        if (confirmation == null || confirmation.bus == null || confirmation.passengerInfo == null) {
            VBox fallback = new VBox(12);
            fallback.getStyleClass().add("card");
            fallback.setPadding(new Insets(26));
            fallback.setMaxWidth(500);
            Label message = new Label("Booking information is unavailable. Please return to the dashboard.");
            message.setWrapText(true);
            message.setStyle("-fx-text-fill: #334155; -fx-font-size: 13px;");
            Button dashboardButton = new Button("Back to Dashboard");
            dashboardButton.getStyleClass().add("secondary-button");
            dashboardButton.setOnAction(e -> navigationContext.showScene(new DashboardScreen(stage, navigationContext).getView()));
            fallback.getChildren().addAll(message, dashboardButton);
            contentBox.getChildren().add(fallback);
            root.getChildren().addAll(headerBox, contentBox);
            return root;
        }

        VBox ticketCard = new VBox(0);
        ticketCard.getStyleClass().add("card");
        ticketCard.setMaxWidth(760);

        VBox confNumSection = new VBox(8);
        confNumSection.setPadding(new Insets(24));
        confNumSection.setAlignment(Pos.CENTER);
        confNumSection.setStyle("-fx-border-color: #e2e8f0; -fx-border-width: 0 0 1 0;");
        Label confLabel = new Label("Confirmation ID");
        confLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px;");
        Label confValue = new Label(confirmation.confirmationNumber);
        confValue.setFont(Font.font("Courier New", FontWeight.BOLD, 22));
        confValue.setStyle("-fx-text-fill: #0d47a1;");
        confNumSection.getChildren().addAll(confLabel, confValue);

        VBox routeSection = new VBox(18);
        routeSection.setPadding(new Insets(24));
        routeSection.setStyle("-fx-border-color: #e2e8f0; -fx-border-width: 0 0 1 0;");
        HBox routeBox = new HBox(30);
        routeBox.setAlignment(Pos.CENTER_LEFT);
        VBox fromBox = new VBox(6);
        Label fromTime = new Label(confirmation.bus.departureTime);
        fromTime.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");
        Label fromCity = new Label(confirmation.bus.departureCity);
        fromCity.setStyle("-fx-font-size: 12px; -fx-text-fill: #475569;");
        fromBox.getChildren().addAll(fromTime, fromCity);

        Label arrow = new Label("→");
        arrow.setStyle("-fx-font-size: 24px; -fx-text-fill: #94a3b8;");

        VBox toBox = new VBox(6);
        Label toTime = new Label(confirmation.bus.arrivalTime);
        toTime.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");
        Label toCity = new Label(confirmation.bus.destinationCity);
        toCity.setStyle("-fx-font-size: 12px; -fx-text-fill: #475569;");
        toBox.getChildren().addAll(toTime, toCity);
        routeBox.getChildren().addAll(fromBox, arrow, toBox);

        HBox detailRow = new HBox(32);
        detailRow.setPadding(new Insets(12, 0, 0, 0));
        detailRow.getChildren().addAll(
                createMiniInfo("Bus Operator", confirmation.bus.busOperator),
                createMiniInfo("Bus Type", confirmation.bus.busType),
                createMiniInfo("Duration", confirmation.bus.duration)
        );
        routeSection.getChildren().addAll(routeBox, detailRow);

        VBox passengerSection = new VBox(18);
        passengerSection.setPadding(new Insets(24));
        passengerSection.setStyle("-fx-border-color: #e2e8f0; -fx-border-width: 0 0 1 0;");
        HBox passengerRow = new HBox(30);
        passengerRow.getChildren().addAll(
                createMiniInfo("Passenger", confirmation.passengerInfo.passengerName),
                createMiniInfo("Age", String.valueOf(confirmation.passengerInfo.age)),
                createMiniInfo("Gender", confirmation.passengerInfo.gender)
        );
        Label seatLabel = new Label("Seats: " + String.join(", ", confirmation.seats));
        seatLabel.setStyle("-fx-text-fill: #16a34a; -fx-font-weight: bold; -fx-font-size: 13px;");
        passengerSection.getChildren().addAll(passengerRow, seatLabel);

        VBox priceSection = new VBox(8);
        priceSection.setPadding(new Insets(24));
        Label totalLabel = new Label("TOTAL FARE");
        totalLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px; -fx-font-weight: bold;");
        Label totalValue = new Label("₹" + (int) confirmation.totalPrice);
        totalValue.setStyle("-fx-text-fill: #16a34a; -fx-font-size: 28px; -fx-font-weight: bold;");
        priceSection.getChildren().addAll(totalLabel, totalValue);

        ticketCard.getChildren().addAll(confNumSection, routeSection, passengerSection, priceSection);

        HBox actionRow = new HBox(14);
        actionRow.setAlignment(Pos.CENTER);
        Button myBookingsButton = new Button("View My Bookings");
        myBookingsButton.getStyleClass().add("secondary-button");
        myBookingsButton.setOnAction(e -> navigationContext.showScene(new MyBookingsScreen(stage, navigationContext).getView()));

        Button dashboardButton = new Button("Back to Dashboard");
        dashboardButton.getStyleClass().add("ghost-button");
        dashboardButton.setOnAction(e -> navigationContext.showScene(new DashboardScreen(stage, navigationContext).getView()));

        Button newBookingButton = new Button("New Booking");
        newBookingButton.getStyleClass().add("primary-button");
        newBookingButton.setOnAction(e -> navigationContext.showScene(new SearchBusScreen(stage, navigationContext).getView()));

        actionRow.getChildren().addAll(myBookingsButton, dashboardButton, newBookingButton);
        contentBox.getChildren().addAll(ticketCard, actionRow);
        root.getChildren().addAll(headerBox, contentBox);
        return root;
    }

    private VBox createMiniInfo(String title, String value) {
        VBox box = new VBox(3);
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 10px;");
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-text-fill: #0f172a; -fx-font-size: 12px; -fx-font-weight: bold;");
        box.getChildren().addAll(titleLabel, valueLabel);
        return box;
    }
}
