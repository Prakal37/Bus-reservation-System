package com.busreservation.ui;

import com.busreservation.dto.SeatDTO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SeatSelectionScreen {
    private final Stage stage;
    private final NavigationContext navigationContext;
    private final Set<String> selectedSeats = new HashSet<>();
    private final Set<Integer> occupiedSeatNumbers = new HashSet<>();

    public SeatSelectionScreen(Stage stage, NavigationContext navigationContext) {
        this.stage = stage;
        this.navigationContext = navigationContext;
        if (navigationContext.getSelectedSeats() != null) {
            selectedSeats.addAll(Arrays.asList(navigationContext.getSelectedSeats()));
        }
    }

    public Parent getView() {
        VBox root = new VBox(0);
        root.setStyle("-fx-background-color: #f4f7fb;");

        VBox headerBox = new VBox(5);
        headerBox.getStyleClass().add("app-header");
        headerBox.setPadding(new Insets(18, 26, 18, 26));

        NavigationContext.Bus bus = navigationContext.getSelectedBus();
        NavigationContext.SearchQuery query = navigationContext.getSearchQuery();
        String journeyDate = query != null ? query.date : "2026-10-01";

        Label title = new Label("Select Your Seats");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: white;");

        String departureCity = bus != null ? bus.departureCity : "Origin";
        String destinationCity = bus != null ? bus.destinationCity : "Destination";
        String busOperator = bus != null ? bus.busOperator : "Bus";
        Label subtitle = new Label(busOperator + " • " + departureCity + " → " + destinationCity + " • " + journeyDate);
        subtitle.setStyle("-fx-text-fill: rgba(255,255,255,0.82); -fx-font-size: 12px;");
        headerBox.getChildren().addAll(title, subtitle);

        occupiedSeatNumbers.clear();
        fetchOccupiedSeats(bus, journeyDate);

        VBox contentBox = new VBox(18);
        contentBox.setPadding(new Insets(30));
        contentBox.setAlignment(Pos.TOP_CENTER);

        int totalSeats = bus != null ? bus.totalSeats : 40;
        int availableCount = totalSeats - occupiedSeatNumbers.size();
        Label instructionLabel = new Label("Select your seats — " + availableCount + " of " + totalSeats + " seats available");
        instructionLabel.setStyle("-fx-text-fill: #475569; -fx-font-size: 12px;");

        GridPane seatGrid = new GridPane();
        seatGrid.setHgap(10);
        seatGrid.setVgap(10);
        seatGrid.setAlignment(Pos.CENTER);
        seatGrid.setPadding(new Insets(18));

        char[] rows = {'A', 'B', 'C', 'D'};
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 10; col++) {
                String seatName = String.valueOf(rows[row]) + (col + 1);
                int seatNumber = row * 10 + (col + 1);
                Button seatButton = new Button(seatName);
                seatButton.setPrefSize(52, 40);
                if (occupiedSeatNumbers.contains(seatNumber)) {
                    seatButton.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10px; -fx-border-radius: 10px;");
                    seatButton.setDisable(true);
                } else {
                    seatButton.setStyle("-fx-background-color: #cbd5e1; -fx-text-fill: #0f172a; -fx-font-weight: bold; -fx-background-radius: 10px; -fx-border-radius: 10px;");
                    if (selectedSeats.contains(seatName)) {
                        seatButton.setStyle("-fx-background-color: #22c55e; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10px; -fx-border-radius: 10px;");
                    }
                }
                seatGrid.add(seatButton, col, row);
            }
        }

        HBox legendBox = new HBox(18);
        legendBox.setAlignment(Pos.CENTER);
        legendBox.setPadding(new Insets(8, 0, 8, 0));
        legendBox.getChildren().addAll(
                makeLegendItem("Available", "#cbd5e1"),
                makeLegendItem("Selected", "#22c55e"),
                makeLegendItem("Occupied", "#ef4444")
        );

        Label selectedSummaryLabel = new Label(selectedSeats.isEmpty() ? "No seats selected" : "Selected: " + String.join(", ", selectedSeats.stream().sorted().toList()));
        selectedSummaryLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #0d47a1;");

        Button backButton = new Button("← Back to Bus Results");
        backButton.getStyleClass().add("ghost-button");
        backButton.setOnAction(e -> navigationContext.showScene(new BusResultsScreen(stage, navigationContext).getView()));

        Button continueButton = new Button("Continue to Passenger Details");
        continueButton.getStyleClass().add("primary-button");
        continueButton.setOnAction(e -> {
            if (selectedSeats.isEmpty()) {
                showAlert("Seat selection", "Please select at least one seat before continuing.");
                return;
            }
            navigationContext.setSelectedSeats(selectedSeats.toArray(new String[0]));
            navigationContext.showScene(new PassengerDetailsScreen(stage, navigationContext).getView());
        });

        HBox actionBar = new HBox(14);
        actionBar.setAlignment(Pos.CENTER);
        actionBar.getChildren().addAll(backButton, continueButton);

        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 10; col++) {
                final int currentRow = row;
                final int currentCol = col;
                Button seatButton = (Button) seatGrid.getChildren().stream()
                        .filter(node -> GridPane.getRowIndex(node) == currentRow && GridPane.getColumnIndex(node) == currentCol)
                        .findFirst()
                        .orElse(null);
                if (seatButton == null || seatButton.isDisabled()) {
                    continue;
                }
                final String seatName = seatButton.getText();
                final Button selectedSeatButton = seatButton;
                final Label selectedSummary = selectedSummaryLabel;
                selectedSeatButton.setOnAction(e -> toggleSeat(seatName, selectedSeatButton, selectedSummary));
            }
        }

        contentBox.getChildren().addAll(backButton, instructionLabel, new Separator(), seatGrid, legendBox, selectedSummaryLabel, actionBar);
        root.getChildren().addAll(headerBox, contentBox);
        return root;
    }

    private HBox makeLegendItem(String labelText, String color) {
        HBox box = new HBox(8);
        box.setAlignment(Pos.CENTER_LEFT);
        Button swatch = new Button();
        swatch.setPrefSize(20, 18);
        swatch.setDisable(true);
        swatch.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 5px; -fx-border-radius: 5px;");
        Label label = new Label(labelText);
        label.setStyle("-fx-text-fill: #334155; -fx-font-size: 11px;");
        box.getChildren().addAll(swatch, label);
        return box;
    }

    private void toggleSeat(String seatName, Button seatButton, Label summaryLabel) {
        if (selectedSeats.contains(seatName)) {
            selectedSeats.remove(seatName);
            seatButton.setStyle("-fx-background-color: #cbd5e1; -fx-text-fill: #0f172a; -fx-font-weight: bold; -fx-background-radius: 10px; -fx-border-radius: 10px;");
        } else {
            selectedSeats.add(seatName);
            seatButton.setStyle("-fx-background-color: #22c55e; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10px; -fx-border-radius: 10px;");
        }
        summaryLabel.setText(selectedSeats.isEmpty() ? "No seats selected" : "Selected: " + String.join(", ", selectedSeats.stream().sorted().toList()));
    }

    private void fetchOccupiedSeats(NavigationContext.Bus bus, String journeyDate) {
        if (bus == null) {
            return;
        }
        try {
            Long busId = (long) bus.busId;
            List<SeatDTO> availableSeats = navigationContext.getApiClient().getAvailableSeats(busId, journeyDate);
            Set<Integer> availableSeatNumbers = new HashSet<>();
            for (SeatDTO seat : availableSeats) {
                if (seat.getSeatNumber() != null) {
                    availableSeatNumbers.add(seat.getSeatNumber());
                }
            }
            int total = bus.totalSeats > 0 ? bus.totalSeats : 40;
            for (int i = 1; i <= total; i++) {
                if (!availableSeatNumbers.contains(i)) {
                    occupiedSeatNumbers.add(i);
                }
            }
        } catch (Exception e) {
            System.err.println("Could not fetch seat availability from backend: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
