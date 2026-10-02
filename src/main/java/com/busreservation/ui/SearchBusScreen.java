package com.busreservation.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.time.LocalDate;

public class SearchBusScreen {
    private final Stage stage;
    private final NavigationContext navigationContext;

    public SearchBusScreen(Stage stage, NavigationContext navigationContext) {
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

        Label title = new Label("Search Buses");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: white;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        headerRow.getChildren().addAll(backButton, spacer, title);

        Label subtitle = new Label("Book bus tickets across Tamil Nadu");
        subtitle.getStyleClass().add("subtitle-text");

        headerBox.getChildren().addAll(headerRow, subtitle);

        VBox formBox = new VBox(18);
        formBox.setPadding(new Insets(28));
        formBox.setMaxWidth(740);
        formBox.getStyleClass().add("card");

        Label formTitle = new Label("Plan your journey");
        formTitle.getStyleClass().add("section-title");

        HBox fieldRow1 = new HBox(18);
        fieldRow1.setAlignment(Pos.CENTER_LEFT);
        VBox fromBox = createFieldBox("From", "Chennai");
        VBox toBox = createFieldBox("To", "Madurai");
        ComboBox<String> departureCombo = (ComboBox<String>) fromBox.getChildren().get(1);
        ComboBox<String> destinationCombo = (ComboBox<String>) toBox.getChildren().get(1);
        fieldRow1.getChildren().addAll(fromBox, toBox);

        HBox fieldRow2 = new HBox(18);
        fieldRow2.setAlignment(Pos.CENTER_LEFT);
        VBox dateBox = new VBox(6);
        Label dateLabel = new Label("Travel Date");
        dateLabel.getStyleClass().add("form-label");
        DatePicker datePicker = new DatePicker(LocalDate.now());
        datePicker.setPrefWidth(200);
        datePicker.getEditor().setStyle("-fx-padding: 10px 12px 10px 12px; -fx-border-color: #cbd5e1; -fx-border-radius: 10px; -fx-background-radius: 10px;");
        dateBox.getChildren().addAll(dateLabel, datePicker);

        VBox passengerBox = new VBox(6);
        Label passengerLabel = new Label("Passengers");
        passengerLabel.getStyleClass().add("form-label");
        ComboBox<Integer> passengerCombo = new ComboBox<>();
        for (int i = 1; i <= 6; i++) {
            passengerCombo.getItems().add(i);
        }
        passengerCombo.setValue(1);
        passengerCombo.setPrefWidth(140);
        passengerCombo.getStyleClass().add("input-field");
        passengerBox.getChildren().addAll(passengerLabel, passengerCombo);
        fieldRow2.getChildren().addAll(dateBox, passengerBox);

        HBox actionRow = new HBox(14);
        actionRow.setAlignment(Pos.CENTER_LEFT);
        Button swapButton = new Button("Swap");
        swapButton.getStyleClass().add("ghost-button");
        swapButton.setOnAction(e -> {
            String fromValue = departureCombo.getValue();
            departureCombo.setValue(destinationCombo.getValue());
            destinationCombo.setValue(fromValue);
        });

        Button clearButton = new Button("Clear");
        clearButton.getStyleClass().add("ghost-button");
        clearButton.setOnAction(e -> {
            departureCombo.setValue("Chennai");
            destinationCombo.setValue("Madurai");
            datePicker.setValue(LocalDate.now());
            passengerCombo.setValue(1);
        });

        Button searchButton = new Button("Search Buses");
        searchButton.getStyleClass().add("primary-button");
        searchButton.setOnAction(e -> {
            String departure = departureCombo.getValue();
            String destination = destinationCombo.getValue();
            LocalDate date = datePicker.getValue();
            Integer passengers = passengerCombo.getValue();

            if (departure == null || destination == null || date == null || passengers == null) {
                showAlert("Please check the form", "Please fill all fields before searching.");
                return;
            }
            if (departure.equalsIgnoreCase(destination)) {
                showAlert("Invalid route", "Departure and destination must be different.");
                return;
            }
            if (date.isBefore(LocalDate.now())) {
                showAlert("Invalid date", "Travel date cannot be in the past.");
                return;
            }

            navigationContext.setSearchQuery(new NavigationContext.SearchQuery(departure, destination, date.toString(), passengers));
            navigationContext.showScene(new BusResultsScreen(stage, navigationContext).getView());
        });

        actionRow.getChildren().addAll(swapButton, clearButton, searchButton);
        formBox.getChildren().addAll(formTitle, new Separator(), fieldRow1, fieldRow2, actionRow);

        VBox content = new VBox(20);
        content.setPadding(new Insets(30, 0, 40, 0));
        content.setAlignment(Pos.TOP_CENTER);
        content.getChildren().add(formBox);
        root.getChildren().addAll(headerBox, content);
        return root;
    }

    private VBox createFieldBox(String labelText, String defaultValue) {
        VBox box = new VBox(6);
        Label label = new Label(labelText);
        label.getStyleClass().add("form-label");
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().addAll("Chennai", "Coimbatore", "Madurai", "Tiruchirappalli", "Salem", "Tirunelveli", "Thoothukudi", "Nagercoil", "Kanyakumari", "Thanjavur", "Erode", "Vellore", "Dindigul");
        comboBox.setValue(defaultValue);
        comboBox.setPrefWidth(260);
        comboBox.getStyleClass().add("input-field");
        box.getChildren().addAll(label, comboBox);
        return box;
    }

    private void showAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
