package com.busreservation.ui;

import com.busreservation.dto.BusDTO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BusResultsScreen {
    private final Stage stage;
    private final NavigationContext navigationContext;

    public BusResultsScreen(Stage stage, NavigationContext navigationContext) {
        this.stage = stage;
        this.navigationContext = navigationContext;
    }

    public Parent getView() {
        VBox root = new VBox(0);
        root.setStyle("-fx-background-color: #f4f7fb;");

        VBox headerBox = new VBox(5);
        headerBox.getStyleClass().add("app-header");
        headerBox.setPadding(new Insets(18, 26, 18, 26));

        HBox headerRow = new HBox(12);
        headerRow.setAlignment(Pos.CENTER_LEFT);
        Button backButton = new Button("← Back to Search");
        backButton.getStyleClass().add("ghost-button");
        backButton.setOnAction(e -> navigationContext.showScene(new SearchBusScreen(stage, navigationContext).getView()));

        Button homeButton = new Button("Home");
        homeButton.getStyleClass().add("ghost-button");
        homeButton.setOnAction(e -> navigationContext.showScene(new DashboardScreen(stage, navigationContext).getView()));

        NavigationContext.SearchQuery query = navigationContext.getSearchQuery();
        String from = query != null ? query.departureCity : "Chennai";
        String to = query != null ? query.destinationCity : "Madurai";
        String date = query != null ? query.date : "2026-10-01";

        Label title = new Label(from + " → " + to);
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: white;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        headerRow.getChildren().addAll(backButton, homeButton, spacer, title);

        Label subTitle = new Label("Travel Date: " + date + "  •  Real bus data from H2 database");
        subTitle.setStyle("-fx-text-fill: rgba(255,255,255,0.82); -fx-font-size: 12px;");
        headerBox.getChildren().addAll(headerRow, subTitle);

        VBox busListBox = new VBox(16);
        busListBox.setPadding(new Insets(24));

        List<BusDTO> buses = fetchBusesFromBackend(from, to, date);
        if (buses.isEmpty()) {
            VBox emptyBox = new VBox(14);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(40));
            emptyBox.getStyleClass().add("card");
            Label noResultsLabel = new Label("No buses found for the selected route and date.");
            noResultsLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 15px; -fx-font-weight: bold;");
            Label suggestLabel = new Label("Try a different date or search a popular route such as Chennai → Madurai.");
            suggestLabel.setStyle("-fx-text-fill: #475569; -fx-font-size: 12px;");
            Button retryButton = new Button("Back to Search");
            retryButton.getStyleClass().add("secondary-button");
            retryButton.setOnAction(e -> navigationContext.showScene(new SearchBusScreen(stage, navigationContext).getView()));
            emptyBox.getChildren().addAll(noResultsLabel, suggestLabel, retryButton);
            busListBox.getChildren().add(emptyBox);
        } else {
            ComboBox<String> sortCombo = new ComboBox<>();
            sortCombo.getItems().addAll("Lowest Fare", "Earliest Departure", "Available Seats", "Bus Type");
            sortCombo.setValue("Lowest Fare");
            sortCombo.setPrefWidth(180);
            sortCombo.setOnAction(e -> {
                busListBox.getChildren().clear();
                busListBox.getChildren().addAll(buildSortedBusCards(sortBuses(buses, sortCombo.getValue())));
            });
            busListBox.getChildren().add(sortCombo);
            busListBox.getChildren().addAll(buildSortedBusCards(sortBuses(buses, sortCombo.getValue())));
        }

        ScrollPane scrollPane = new ScrollPane(busListBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
        root.getChildren().addAll(headerBox, scrollPane);
        return root;
    }

    private List<BusDTO> fetchBusesFromBackend(String source, String destination, String journeyDate) {
        try {
            List<BusDTO> result = navigationContext.getApiClient().searchBuses(source, destination, journeyDate);
            if (result != null && !result.isEmpty()) {
                return result;
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch buses from REST backend: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    private List<BusDTO> sortBuses(List<BusDTO> buses, String selectedOption) {
        List<BusDTO> sorted = new ArrayList<>(buses);
        Comparator<BusDTO> comparator = Comparator.comparing(bus -> bus.getPricePerSeat() == null ? 0.0 : bus.getPricePerSeat());
        if ("Earliest Departure".equals(selectedOption)) {
            comparator = Comparator.comparing(bus -> bus.getDepartureTime() == null ? "" : bus.getDepartureTime());
        } else if ("Available Seats".equals(selectedOption)) {
            comparator = Comparator.comparing(bus -> bus.getAvailableSeats() == null ? 0L : bus.getAvailableSeats(), Comparator.reverseOrder());
        } else if ("Bus Type".equals(selectedOption)) {
            comparator = Comparator.comparing(bus -> bus.getBusType() == null ? "" : bus.getBusType());
        }
        sorted.sort(comparator);
        return sorted;
    }

    private List<Parent> buildSortedBusCards(List<BusDTO> buses) {
        List<Parent> cards = new ArrayList<>();
        for (BusDTO bus : buses) {
            cards.add(createBusCard(bus));
        }
        return cards;
    }

    private VBox createBusCard(BusDTO bus) {
        VBox card = new VBox(10);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(18));
        card.setMaxWidth(900);

        HBox topRow = new HBox(12);
        topRow.setAlignment(Pos.CENTER_LEFT);
        Label operatorLabel = new Label(bus.getOperatorName() != null ? bus.getOperatorName() : bus.getBusName());
        operatorLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        operatorLabel.setStyle("-fx-text-fill: #0f172a;");
        Label typeLabel = new Label(bus.getBusType() != null ? bus.getBusType() : "Standard");
        typeLabel.setStyle("-fx-text-fill: #ff9800; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 5 8 5 8; -fx-background-color: #fff3e0; -fx-background-radius: 999px;");
        Label busNumberLabel = new Label(bus.getBusNumber() != null ? bus.getBusNumber() : "");
        busNumberLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px;");
        Region filler = new Region();
        HBox.setHgrow(filler, Priority.ALWAYS);
        Label priceLabel = new Label("₹" + (bus.getPricePerSeat() != null ? (int) Math.round(bus.getPricePerSeat().doubleValue()) : 0));
        priceLabel.setStyle("-fx-text-fill: #16a34a; -fx-font-weight: bold; -fx-font-size: 22px;");
        topRow.getChildren().addAll(operatorLabel, typeLabel, busNumberLabel, filler, priceLabel);

        HBox routeRow = new HBox(18);
        routeRow.setAlignment(Pos.CENTER_LEFT);
        VBox deptBox = new VBox(2);
        Label depTime = new Label(bus.getDepartureTime() != null ? bus.getDepartureTime() : "09:00 PM");
        depTime.setStyle("-fx-font-weight: bold; -fx-font-size: 19px; -fx-text-fill: #0f172a;");
        String from = bus.getRoute() != null && bus.getRoute().getSource() != null ? bus.getRoute().getSource() : "Chennai";
        Label depCity = new Label(from);
        depCity.setStyle("-fx-text-fill: #475569; -fx-font-size: 11px;");
        deptBox.getChildren().addAll(depTime, depCity);

        Label arrow = new Label("→");
        arrow.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 26px;");

        VBox arrivalBox = new VBox(2);
        Label arrTime = new Label(bus.getArrivalTime() != null ? bus.getArrivalTime() : "05:30 AM");
        arrTime.setStyle("-fx-font-weight: bold; -fx-font-size: 19px; -fx-text-fill: #0f172a;");
        String to = bus.getRoute() != null && bus.getRoute().getDestination() != null ? bus.getRoute().getDestination() : "Madurai";
        Label arrCity = new Label(to);
        arrCity.setStyle("-fx-text-fill: #475569; -fx-font-size: 11px;");
        arrivalBox.getChildren().addAll(arrTime, arrCity);

        String duration = bus.getRoute() != null && bus.getRoute().getApproximateDurationHours() != null ? bus.getRoute().getApproximateDurationHours() + "h" : "8h";
        Label durationLabel = new Label(duration);
        durationLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px; -fx-font-weight: bold;");

        routeRow.getChildren().addAll(deptBox, arrow, arrivalBox, durationLabel);

        HBox bottomRow = new HBox(18);
        bottomRow.setAlignment(Pos.CENTER_LEFT);
        VBox seatsBox = new VBox(2);
        int availableSeats = bus.getAvailableSeats() != null ? (int) (long) bus.getAvailableSeats() : 40;
        Label seatsLabel = new Label(availableSeats + " seats available");
        seatsLabel.setStyle("-fx-text-fill: #1e293b; -fx-font-size: 12px; -fx-font-weight: bold;");
        Label seatsCaption = new Label("Available in H2 database");
        seatsCaption.setStyle("-fx-text-fill: #64748b; -fx-font-size: 10px;");
        seatsBox.getChildren().addAll(seatsLabel, seatsCaption);
        Region bottomSpacer = new Region();
        HBox.setHgrow(bottomSpacer, Priority.ALWAYS);
        Button selectButton = new Button("View Seats");
        selectButton.getStyleClass().add("secondary-button");
        selectButton.setOnAction(e -> {
            navigationContext.setSelectedBusDTO(bus);
            navigationContext.showScene(new SeatSelectionScreen(stage, navigationContext).getView());
        });
        bottomRow.getChildren().addAll(seatsBox, bottomSpacer, selectButton);

        card.getChildren().addAll(topRow, routeRow, bottomRow);
        return card;
    }
}
