package com.busreservation.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class PassengerDetailsScreen {
    private final Stage stage;
    private final NavigationContext navigationContext;

    public PassengerDetailsScreen(Stage stage, NavigationContext navigationContext) {
        this.stage = stage;
        this.navigationContext = navigationContext;
    }

    public Parent getView() {
        VBox root = new VBox(18);
        root.setPadding(new Insets(28));
        root.setStyle("-fx-background-color: #f4f7fb;");

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 8, 0));
        Button backButton = new Button("← Back to Seat Selection");
        backButton.getStyleClass().add("ghost-button");
        backButton.setOnAction(e -> navigationContext.showScene(new SeatSelectionScreen(stage, navigationContext).getView()));

        Label titleLabel = new Label("Passenger Details");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        titleLabel.setStyle("-fx-text-fill: #0f172a;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button homeButton = new Button("Home");
        homeButton.getStyleClass().add("ghost-button");
        homeButton.setOnAction(e -> navigationContext.showScene(new DashboardScreen(stage, navigationContext).getView()));

        header.getChildren().addAll(backButton, spacer, titleLabel, homeButton);

        HBox content = new HBox(20);
        content.setAlignment(Pos.TOP_LEFT);

        VBox formBox = new VBox(12);
        formBox.getStyleClass().add("card");
        formBox.setPadding(new Insets(22));
        formBox.setPrefWidth(520);

        Label nameLabel = new Label("Full Name");
        nameLabel.getStyleClass().add("form-label");
        TextField nameField = new TextField();
        nameField.getStyleClass().add("input-field");
        if (navigationContext.getCurrentUser() != null) {
            nameField.setText(navigationContext.getCurrentUser().getFirstName() + " " + navigationContext.getCurrentUser().getLastName());
        }

        Label ageLabel = new Label("Age");
        ageLabel.getStyleClass().add("form-label");
        TextField ageField = new TextField("25");
        ageField.getStyleClass().add("input-field");

        Label genderLabel = new Label("Gender");
        genderLabel.getStyleClass().add("form-label");
        ComboBox<String> genderBox = new ComboBox<>();
        genderBox.getItems().addAll("Male", "Female", "Other");
        genderBox.setValue("Male");
        genderBox.setPrefWidth(200);
        genderBox.getStyleClass().add("input-field");

        Label emailLabel = new Label("Email");
        emailLabel.getStyleClass().add("form-label");
        TextField emailField = new TextField();
        emailField.getStyleClass().add("input-field");
        if (navigationContext.getCurrentUser() != null) {
            emailField.setText(navigationContext.getCurrentUser().getEmail());
        }

        Label phoneLabel = new Label("Phone Number");
        phoneLabel.getStyleClass().add("form-label");
        TextField phoneField = new TextField();
        phoneField.getStyleClass().add("input-field");
        if (navigationContext.getCurrentUser() != null && navigationContext.getCurrentUser().getPhoneNumber() != null) {
            phoneField.setText(navigationContext.getCurrentUser().getPhoneNumber());
        }

        Button continueButton = new Button("Continue to Payment");
        continueButton.getStyleClass().add("primary-button");
        continueButton.setOnAction(e -> {
            String name = nameField.getText() == null ? "" : nameField.getText().trim();
            String ageText = ageField.getText() == null ? "" : ageField.getText().trim();
            String gender = genderBox.getValue();
            String email = emailField.getText() == null ? "" : emailField.getText().trim();
            String phone = phoneField.getText() == null ? "" : phoneField.getText().trim();

            if (name.isEmpty() || ageText.isEmpty() || gender == null || email.isEmpty() || phone.isEmpty()) {
                showAlert("Missing details", "Please fill in all passenger details before continuing.");
                return;
            }
            int age;
            try {
                age = Integer.parseInt(ageText);
            } catch (NumberFormatException ex) {
                showAlert("Invalid age", "Please enter a valid age.");
                return;
            }
            navigationContext.setPassengerInfo(new NavigationContext.PassengerInfo(name, age, gender, email, phone));
            navigationContext.showScene(new PaymentScreen(stage, navigationContext).getView());
        });

        formBox.getChildren().addAll(nameLabel, nameField, ageLabel, ageField, genderLabel, genderBox, emailLabel, emailField, phoneLabel, phoneField, continueButton);

        VBox summaryBox = new VBox(14);
        summaryBox.getStyleClass().add("card");
        summaryBox.setPadding(new Insets(20));
        summaryBox.setPrefWidth(300);

        Label summaryTitle = new Label("Booking Summary");
        summaryTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        summaryTitle.setStyle("-fx-text-fill: #0f172a;");

        NavigationContext.Bus bus = navigationContext.getSelectedBus();
        NavigationContext.SearchQuery query = navigationContext.getSearchQuery();
        String[] seats = navigationContext.getSelectedSeats();
        String route = query != null ? query.departureCity + " → " + query.destinationCity : "Chennai → Madurai";
        String travelDate = query != null ? query.date : "2026-10-01";
        double pricePerSeat = bus != null ? bus.price : 500.0;
        int totalFare = (seats == null || seats.length == 0 ? 1 : seats.length) * (int) pricePerSeat;

        Label busLine = new Label((bus != null ? bus.busOperator : "Bus") + " • " + route);
        busLine.setWrapText(true);
        busLine.setStyle("-fx-text-fill: #334155; -fx-font-size: 12px;");
        Label dateLine = new Label("Travel date: " + travelDate);
        dateLine.setStyle("-fx-text-fill: #334155; -fx-font-size: 12px;");
        Label seatLine = new Label("Seat(s): " + (seats == null || seats.length == 0 ? "A1" : String.join(", ", seats)));
        seatLine.setStyle("-fx-text-fill: #16a34a; -fx-font-size: 12px; -fx-font-weight: bold;");
        Label fareLine = new Label("Fare: ₹" + totalFare);
        fareLine.setStyle("-fx-text-fill: #0d47a1; -fx-font-weight: bold; -fx-font-size: 18px;");

        summaryBox.getChildren().addAll(summaryTitle, new Separator(), busLine, dateLine, seatLine, fareLine);
        content.getChildren().addAll(formBox, summaryBox);
        root.getChildren().addAll(header, content);
        return root;
    }

    private void showAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
