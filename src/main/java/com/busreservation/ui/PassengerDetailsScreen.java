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

public class PassengerDetailsScreen {
    private Stage stage;
    private NavigationContext navigationContext;

    public PassengerDetailsScreen(Stage stage, NavigationContext navigationContext) {
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

        Button backButton = new Button("← Back");
        backButton.setStyle(
                "-fx-font-size: 11; -fx-padding: 8 16 8 16; " +
                "-fx-background-color: #95a5a6; -fx-text-fill: white; " +
                "-fx-border-radius: 4; -fx-background-radius: 4; -fx-cursor: hand;"
        );
        backButton.setOnAction(e -> navigateToSeatSelection());

        Label titleLabel = new Label("Passenger Details");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setStyle("-fx-text-fill: #2c3e50;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(backButton, spacer, titleLabel);

        // Form
        VBox formBox = new VBox(15);
        formBox.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 4; -fx-background-radius: 4;");
        formBox.setPadding(new Insets(30));
        formBox.setMaxWidth(500);
        formBox.setAlignment(Pos.TOP_CENTER);

        // Selected bus info
        NavigationContext.Bus bus = navigationContext.getSelectedBus();
        String[] seats = navigationContext.getSelectedSeats();

        Label busInfoLabel = new Label("Bus: " + bus.busOperator + " | Seats: " + seatsToString(seats) + " | Price per seat: ₹" + (int)bus.price);
        busInfoLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #7f8c8d;");

        // Passenger Name
        Label nameLabel = new Label("Full Name:");
        nameLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #2c3e50; -fx-font-weight: bold;");
        TextField nameField = new TextField();
        nameField.setPromptText("Enter your full name");
        nameField.setStyle("-fx-font-size: 12; -fx-padding: 8;");

        // Age
        Label ageLabel = new Label("Age:");
        ageLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #2c3e50; -fx-font-weight: bold;");
        TextField ageField = new TextField();
        ageField.setPromptText("Enter your age");
        ageField.setStyle("-fx-font-size: 12; -fx-padding: 8;");

        // Gender
        Label genderLabel = new Label("Gender:");
        genderLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #2c3e50; -fx-font-weight: bold;");
        ComboBox<String> genderBox = new ComboBox<>();
        genderBox.getItems().addAll("Male", "Female", "Other");
        genderBox.setStyle("-fx-font-size: 12; -fx-padding: 8;");

        // Email
        Label emailLabel = new Label("Email:");
        emailLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #2c3e50; -fx-font-weight: bold;");
        TextField emailField = new TextField();
        emailField.setPromptText("Enter your email address");
        emailField.setStyle("-fx-font-size: 12; -fx-padding: 8;");

        // Phone
        Label phoneLabel = new Label("Phone Number:");
        phoneLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #2c3e50; -fx-font-weight: bold;");
        TextField phoneField = new TextField();
        phoneField.setPromptText("Enter your phone number");
        phoneField.setStyle("-fx-font-size: 12; -fx-padding: 8;");

        // Proceed button
        Button proceedButton = new Button("Proceed to Confirmation");
        proceedButton.setStyle(
                "-fx-font-size: 12; -fx-padding: 10 40 10 40; " +
                "-fx-background-color: #27ae60; -fx-text-fill: white; " +
                "-fx-border-radius: 4; -fx-background-radius: 4; -fx-cursor: hand;"
        );
        proceedButton.setOnMouseEntered(e -> proceedButton.setStyle(
                "-fx-font-size: 12; -fx-padding: 10 40 10 40; " +
                "-fx-background-color: #229954; -fx-text-fill: white; " +
                "-fx-border-radius: 4; -fx-background-radius: 4; -fx-cursor: hand;"
        ));
        proceedButton.setOnMouseExited(e -> proceedButton.setStyle(
                "-fx-font-size: 12; -fx-padding: 10 40 10 40; " +
                "-fx-background-color: #27ae60; -fx-text-fill: white; " +
                "-fx-border-radius: 4; -fx-background-radius: 4; -fx-cursor: hand;"
        ));

        proceedButton.setOnAction(e -> {
            String name = nameField.getText().trim();
            String ageStr = ageField.getText().trim();
            String gender = genderBox.getValue();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();

            if (name.isEmpty() || ageStr.isEmpty() || gender == null || email.isEmpty() || phone.isEmpty()) {
                showAlert("Error", "Please fill all fields");
                return;
            }

            int age;
            try {
                age = Integer.parseInt(ageStr);
            } catch (NumberFormatException ex) {
                showAlert("Error", "Please enter a valid age");
                return;
            }

            NavigationContext.PassengerInfo passengerInfo = new NavigationContext.PassengerInfo(name, age, gender, email, phone);
            navigationContext.setPassengerInfo(passengerInfo);

            // Generate booking confirmation number with TNBR format
            long timestamp = System.currentTimeMillis();
            String confirmationNumber = "TNBR" + timestamp;
            double totalPrice = bus.price * seats.length;
            NavigationContext.BookingConfirmation confirmation = 
                    new NavigationContext.BookingConfirmation(confirmationNumber, bus, seats, passengerInfo, totalPrice);
            navigationContext.setBookingConfirmation(confirmation);

            navigateToConfirmation();
        });

        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().add(proceedButton);

        formBox.getChildren().addAll(
                busInfoLabel,
                new Separator(),
                nameLabel,
                nameField,
                ageLabel,
                ageField,
                genderLabel,
                genderBox,
                emailLabel,
                emailField,
                phoneLabel,
                phoneField,
                new Separator(),
                buttonBox
        );

        root.getChildren().addAll(header, new Separator(), formBox);
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

    private void navigateToSeatSelection() {
        SeatSelectionScreen seatSelectionScreen = new SeatSelectionScreen(stage, navigationContext);
        Scene scene = new Scene(seatSelectionScreen.getView(), 1100, 750);
        stage.setScene(scene);
    }

    private void navigateToConfirmation() {
        BookingConfirmationScreen confirmationScreen = new BookingConfirmationScreen(stage, navigationContext);
        Scene scene = new Scene(confirmationScreen.getView(), 1100, 750);
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
