package com.busreservation.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.util.HashSet;
import java.util.Set;

public class SeatSelectionScreen {
    private Stage stage;
    private NavigationContext navigationContext;
    private Set<String> selectedSeats = new HashSet<>();
    private Button[][] seatButtons;

    public SeatSelectionScreen(Stage stage, NavigationContext navigationContext) {
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

        NavigationContext.Bus bus = navigationContext.getSelectedBus();
        Label headerTitle = new Label("Select Your Seats");
        headerTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        headerTitle.setStyle("-fx-text-fill: white;");

        Label headerSubtitle = new Label(bus.busOperator + " - " + bus.departureCity + " → " + bus.destinationCity);
        headerSubtitle.setStyle("-fx-text-fill: #ccc; -fx-font-size: 12;");

        headerBox.getChildren().addAll(headerTitle, headerSubtitle);

        // Main content
        VBox contentBox = new VBox(20);
        contentBox.setStyle("-fx-background-color: #f8f9fa;");
        contentBox.setPadding(new Insets(30));
        contentBox.setAlignment(Pos.TOP_CENTER);

        // Seat selection instructions
        Label instructionLabel = new Label("Click on seats to select them (Max: " + bus.availableSeats + " seats)");
        instructionLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #7f8c8d;");

        // Seat grid
        GridPane seatGrid = new GridPane();
        seatGrid.setHgap(10);
        seatGrid.setVgap(10);
        seatGrid.setAlignment(Pos.CENTER);
        seatGrid.setStyle("-fx-padding: 20;");

        seatButtons = new Button[4][10];
        char[] rows = {'A', 'B', 'C', 'D'};

        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 10; col++) {
                String seatName = String.valueOf(rows[row]) + (col + 1);
                Button seatButton = new Button(seatName);
                seatButton.setPrefSize(45, 45);
                seatButton.setStyle("-fx-font-size: 10; -fx-font-weight: bold; -fx-background-color: #95a5a6; -fx-text-fill: white; -fx-border-radius: 4; -fx-background-radius: 4;");

                seatButtons[row][col] = seatButton;
                final String seat = seatName;

                seatButton.setOnAction(e -> toggleSeat(seat, seatButton));

                seatGrid.add(seatButton, col, row);
            }
        }

        // Legend
        HBox legendBox = new HBox(20);
        legendBox.setAlignment(Pos.CENTER);
        legendBox.setPadding(new Insets(15, 0, 15, 0));

        Button availableDemo = new Button();
        availableDemo.setPrefSize(30, 30);
        availableDemo.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-disabled: true;");
        availableDemo.setDisable(true);
        Label availableLabel = new Label("Available");

        Button selectedDemo = new Button();
        selectedDemo.setPrefSize(30, 30);
        selectedDemo.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-disabled: true;");
        selectedDemo.setDisable(true);
        Label selectedLabel = new Label("Selected");

        legendBox.getChildren().addAll(
                availableDemo, availableLabel,
                new Separator(),
                selectedDemo, selectedLabel
        );

        // Continue button
        Button continueButton = new Button("Continue");
        continueButton.setStyle(
                "-fx-font-size: 12; -fx-padding: 10 40 10 40; " +
                "-fx-background-color: #27ae60; -fx-text-fill: white; " +
                "-fx-border-radius: 4; -fx-background-radius: 4; -fx-cursor: hand;"
        );
        continueButton.setOnMouseEntered(e -> continueButton.setStyle(
                "-fx-font-size: 12; -fx-padding: 10 40 10 40; " +
                "-fx-background-color: #229954; -fx-text-fill: white; " +
                "-fx-border-radius: 4; -fx-background-radius: 4; -fx-cursor: hand;"
        ));
        continueButton.setOnMouseExited(e -> continueButton.setStyle(
                "-fx-font-size: 12; -fx-padding: 10 40 10 40; " +
                "-fx-background-color: #27ae60; -fx-text-fill: white; " +
                "-fx-border-radius: 4; -fx-background-radius: 4; -fx-cursor: hand;"
        ));
        continueButton.setOnAction(e -> {
            if (selectedSeats.isEmpty()) {
                showAlert("Error", "Please select at least one seat");
                return;
            }
            String[] seatsArray = selectedSeats.toArray(new String[0]);
            navigationContext.setSelectedSeats(seatsArray);
            navigateToPassengerDetails();
        });

        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().add(continueButton);

        contentBox.getChildren().addAll(
                instructionLabel,
                new Separator(),
                seatGrid,
                legendBox,
                new Separator(),
                buttonBox
        );

        root.getChildren().addAll(headerBox, contentBox);
        return root;
    }

    private void toggleSeat(String seatName, Button seatButton) {
        NavigationContext.Bus bus = navigationContext.getSelectedBus();
        if (selectedSeats.contains(seatName)) {
            selectedSeats.remove(seatName);
            seatButton.setStyle("-fx-font-size: 10; -fx-font-weight: bold; -fx-background-color: #95a5a6; -fx-text-fill: white; -fx-border-radius: 4; -fx-background-radius: 4;");
        } else {
            if (selectedSeats.size() < bus.availableSeats) {
                selectedSeats.add(seatName);
                seatButton.setStyle("-fx-font-size: 10; -fx-font-weight: bold; -fx-background-color: #27ae60; -fx-text-fill: white; -fx-border-radius: 4; -fx-background-radius: 4;");
            } else {
                showAlert("Error", "You can select maximum " + bus.availableSeats + " seats");
            }
        }
    }

    private void navigateToBusResults() {
        BusResultsScreen busResultsScreen = new BusResultsScreen(stage, navigationContext);
        Scene scene = new Scene(busResultsScreen.getView(), 1100, 750);
        stage.setScene(scene);
    }

    private void navigateToPassengerDetails() {
        PassengerDetailsScreen passengerDetailsScreen = new PassengerDetailsScreen(stage, navigationContext);
        Scene scene = new Scene(passengerDetailsScreen.getView(), 1100, 750);
        stage.setScene(scene);
    }

    private void showAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
