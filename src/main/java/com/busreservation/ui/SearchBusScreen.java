package com.busreservation.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.time.LocalDate;

public class SearchBusScreen {
    private Stage stage;
    private NavigationContext navigationContext;

    public SearchBusScreen(Stage stage, NavigationContext navigationContext) {
        this.stage = stage;
        this.navigationContext = navigationContext;
    }

    public Parent getView() {
        VBox root = new VBox(20);
        root.setStyle("-fx-background-color: #f8f9fa;");
        root.setPadding(new Insets(20));

        // Header
        VBox headerBox = new VBox(10);
        headerBox.setStyle("-fx-background-color: #003d82;");
        headerBox.setPadding(new Insets(20));
        headerBox.setAlignment(Pos.CENTER);

        Label headerTitle = new Label("Find Buses");
        headerTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        headerTitle.setStyle("-fx-text-fill: white;");

        Label headerSubtitle = new Label("Search buses across Tamil Nadu");
        headerSubtitle.setFont(Font.font("Segoe UI", 14));
        headerSubtitle.setStyle("-fx-text-fill: #ccc;");

        headerBox.getChildren().addAll(headerTitle, headerSubtitle);

        // Search form
        VBox formBox = new VBox(15);
        formBox.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 8; -fx-background-radius: 8;");
        formBox.setPadding(new Insets(30));

        // Departure City
        Label departureLabel = new Label("From:");
        ComboBox<String> departureCombo = new ComboBox<>();
        departureCombo.getItems().addAll("Chennai", "Coimbatore", "Madurai", "Tiruchirappalli", "Salem", 
                "Tirunelveli", "Thoothukudi", "Nagercoil", "Kanyakumari", "Thanjavur", "Erode", "Vellore", "Dindigul");
        departureCombo.setValue("Chennai");
        departureCombo.setStyle("-fx-font-size: 12; -fx-padding: 10; -fx-border-radius: 4; -fx-background-radius: 4;");

        // Destination City
        Label destinationLabel = new Label("To:");
        destinationLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #333333; -fx-font-weight: bold;");
        ComboBox<String> destinationCombo = new ComboBox<>();
        destinationCombo.getItems().addAll("Chennai", "Coimbatore", "Madurai", "Tiruchirappalli", "Salem", 
                "Tirunelveli", "Thoothukudi", "Nagercoil", "Kanyakumari", "Thanjavur", "Erode", "Vellore", "Dindigul");
        destinationCombo.setValue("Madurai");
        destinationCombo.setStyle("-fx-font-size: 12; -fx-padding: 10; -fx-border-radius: 4; -fx-background-radius: 4;");

        // Date
        Label dateLabel = new Label("Travel Date:");
        dateLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #333333; -fx-font-weight: bold;");
        DatePicker datePicker = new DatePicker();
        datePicker.setStyle("-fx-font-size: 12; -fx-padding: 10;");
        datePicker.setValue(LocalDate.now());

        // Passengers
        Label passengerLabel = new Label("Passengers:");
        passengerLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #333333; -fx-font-weight: bold;");
        ComboBox<Integer> passengerCombo = new ComboBox<>();
        for (int i = 1; i <= 6; i++) {
            passengerCombo.getItems().add(i);
        }
        passengerCombo.setValue(1);
        passengerCombo.setStyle("-fx-font-size: 12; -fx-padding: 10;");

        // Search Button
        Button searchButton = new Button("SEARCH BUSES");
        searchButton.setStyle(
                "-fx-font-size: 13; -fx-font-weight: bold; -fx-padding: 12 40 12 40; " +
                "-fx-background-color: #ff9800; -fx-text-fill: white; " +
                "-fx-border-radius: 4; -fx-background-radius: 4; -fx-cursor: hand;"
        );
        searchButton.setOnMouseEntered(e -> searchButton.setStyle(
                "-fx-font-size: 13; -fx-font-weight: bold; -fx-padding: 12 40 12 40; " +
                "-fx-background-color: #f57c00; -fx-text-fill: white; " +
                "-fx-border-radius: 4; -fx-background-radius: 4; -fx-cursor: hand;"
        ));
        searchButton.setOnMouseExited(e -> searchButton.setStyle(
                "-fx-font-size: 13; -fx-font-weight: bold; -fx-padding: 12 40 12 40; " +
                "-fx-background-color: #ff9800; -fx-text-fill: white; " +
                "-fx-border-radius: 4; -fx-background-radius: 4; -fx-cursor: hand;"
        ));

        searchButton.setOnAction(e -> {
            String departure = departureCombo.getValue();
            String destination = destinationCombo.getValue();
            LocalDate date = datePicker.getValue();
            Integer passengers = passengerCombo.getValue();

            if (departure == null || destination == null || date == null || passengers == null) {
                showAlert("Error", "Please fill all fields");
                return;
            }

            if (departure.equals(destination)) {
                showAlert("Error", "Departure and destination must be different");
                return;
            }

            String dateStr = date.toString();
            navigationContext.setSearchQuery(new NavigationContext.SearchQuery(departure, destination, dateStr, passengers));
            navigateToBusResults();
        });

        HBox fieldRow1 = new HBox(20);
        fieldRow1.setStyle("-fx-padding: 0;");
        VBox fromBox = new VBox(5);
        fromBox.getChildren().addAll(departureLabel, departureCombo);
        VBox toBox = new VBox(5);
        toBox.getChildren().addAll(destinationLabel, destinationCombo);
        fieldRow1.getChildren().addAll(fromBox, toBox);

        HBox fieldRow2 = new HBox(20);
        fieldRow2.setStyle("-fx-padding: 0;");
        VBox dateBox = new VBox(5);
        dateBox.getChildren().addAll(dateLabel, datePicker);
        VBox passengerBox = new VBox(5);
        passengerBox.getChildren().addAll(passengerLabel, passengerCombo);
        fieldRow2.getChildren().addAll(dateBox, passengerBox);

        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));
        buttonBox.getChildren().add(searchButton);

        formBox.getChildren().addAll(fieldRow1, fieldRow2, buttonBox);

        root.getChildren().addAll(headerBox, formBox);
        return root;
    }

    private void navigateToDashboard() {
        DashboardScreen dashboardScreen = new DashboardScreen(stage, navigationContext);
        Scene scene = new Scene(dashboardScreen.getView(), 1100, 750);
        stage.setScene(scene);
    }

    private void navigateToBusResults() {
        BusResultsScreen busResultsScreen = new BusResultsScreen(stage, navigationContext);
        Scene scene = new Scene(busResultsScreen.getView(), 1100, 750);
        stage.setScene(scene);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
