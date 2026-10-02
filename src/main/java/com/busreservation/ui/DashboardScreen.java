package com.busreservation.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class DashboardScreen {
    private final Stage stage;
    private final NavigationContext navigationContext;

    public DashboardScreen(Stage stage, NavigationContext navigationContext) {
        this.stage = stage;
        this.navigationContext = navigationContext;
    }

    public Parent getView() {
        VBox root = new VBox(0);
        root.setStyle("-fx-background-color: #f4f7fb;");

        VBox headerBox = new VBox(10);
        headerBox.getStyleClass().add("app-header");
        headerBox.setPadding(new Insets(18, 28, 18, 28));

        HBox topHeaderBox = new HBox(18);
        topHeaderBox.setAlignment(Pos.CENTER_LEFT);

        Label brandLabel = new Label("BusGo");
        brandLabel.getStyleClass().add("brand-text");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox navBox = new HBox(10);
        navBox.setAlignment(Pos.CENTER_RIGHT);
        Button homeButton = new Button("Home");
        Button searchNavButton = new Button("Search Buses");
        Button bookingsNavButton = new Button("My Bookings");
        Button trackNavButton = new Button("Track Bus");
        for (Button button : new Button[]{homeButton, searchNavButton, bookingsNavButton, trackNavButton}) {
            button.getStyleClass().add("ghost-button");
        }
        homeButton.setOnAction(e -> navigationContext.showScene(getView()));
        searchNavButton.setOnAction(e -> navigationContext.showScene(new SearchBusScreen(stage, navigationContext).getView()));
        bookingsNavButton.setOnAction(e -> navigationContext.showScene(new MyBookingsScreen(stage, navigationContext).getView()));
        trackNavButton.setOnAction(e -> navigationContext.showScene(new TrackBusScreen(stage, navigationContext).getView()));
        navBox.getChildren().addAll(homeButton, searchNavButton, bookingsNavButton, trackNavButton);

        Button logoutButton = new Button("Logout");
        logoutButton.getStyleClass().add("danger-button");
        logoutButton.setOnAction(e -> {
            navigationContext.logout();
            navigationContext.showScene(new LoginScreen(stage, navigationContext).getView());
        });

        Label welcomeLabel = new Label("Welcome, " + (navigationContext.getLoggedInUser() == null ? "traveller" : navigationContext.getLoggedInUser()));
        welcomeLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        welcomeLabel.setStyle("-fx-text-fill: white;");

        topHeaderBox.getChildren().addAll(brandLabel, spacer, navBox, welcomeLabel, logoutButton);

        Label taglineLabel = new Label("Travel anywhere in Tamil Nadu");
        taglineLabel.getStyleClass().add("subtitle-text");

        headerBox.getChildren().addAll(topHeaderBox, taglineLabel);

        VBox contentBox = new VBox(26);
        contentBox.setPadding(new Insets(28, 28, 36, 28));

        VBox heroCard = new VBox(18);
        heroCard.getStyleClass().add("card");
        heroCard.setPadding(new Insets(24));
        heroCard.setMaxWidth(980);

        Label heroTitle = new Label("Book buses quickly, securely and comfortably.");
        heroTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 30));
        heroTitle.setStyle("-fx-text-fill: #0f172a;");

        HBox searchRow = new HBox(16);
        searchRow.setAlignment(Pos.CENTER_LEFT);
        searchRow.setPadding(new Insets(8, 0, 0, 0));

        VBox fromBox = new VBox(6);
        Label fromLabel = new Label("From");
        fromLabel.getStyleClass().add("form-label");
        ComboBox<String> fromCombo = new ComboBox<>();
        fromCombo.getItems().addAll("Chennai", "Madurai", "Coimbatore", "Tiruchirappalli", "Salem", "Tirunelveli", "Thoothukudi", "Nagercoil", "Kanyakumari", "Erode", "Vellore");
        fromCombo.setValue("Chennai");
        fromCombo.getStyleClass().add("input-field");
        fromCombo.setPrefWidth(180);
        fromBox.getChildren().addAll(fromLabel, fromCombo);

        VBox toBox = new VBox(6);
        Label toLabel = new Label("To");
        toLabel.getStyleClass().add("form-label");
        ComboBox<String> toCombo = new ComboBox<>();
        toCombo.getItems().addAll("Chennai", "Madurai", "Coimbatore", "Tiruchirappalli", "Salem", "Tirunelveli", "Thoothukudi", "Nagercoil", "Kanyakumari", "Erode", "Vellore");
        toCombo.setValue("Madurai");
        toCombo.getStyleClass().add("input-field");
        toCombo.setPrefWidth(180);
        toBox.getChildren().addAll(toLabel, toCombo);

        VBox dateBox = new VBox(6);
        Label dateLabel = new Label("Travel Date");
        dateLabel.getStyleClass().add("form-label");
        DatePicker datePicker = new DatePicker(java.time.LocalDate.now());
        datePicker.setPrefWidth(180);
        datePicker.setStyle("-fx-background-color: white; -fx-padding: 0; -fx-border-radius: 10; -fx-background-radius: 10;");
        dateBox.getChildren().addAll(dateLabel, datePicker);

        Button searchButton = new Button("Search Buses");
        searchButton.getStyleClass().add("primary-button");
        searchButton.setOnAction(e -> {
            String from = fromCombo.getValue();
            String to = toCombo.getValue();
            if (from == null || to == null || from.isBlank() || to.isBlank()) {
                showAlert("Search error", "Please choose both source and destination cities.");
                return;
            }
            if (from.equalsIgnoreCase(to)) {
                showAlert("Search error", "Source and destination cities must be different.");
                return;
            }
            if (datePicker.getValue() == null) {
                showAlert("Search error", "Please select a travel date.");
                return;
            }
            if (datePicker.getValue().isBefore(java.time.LocalDate.now())) {
                showAlert("Search error", "Travel date cannot be in the past.");
                return;
            }
            navigationContext.setSearchQuery(new NavigationContext.SearchQuery(from, to, datePicker.getValue().toString(), 1));
            navigationContext.showScene(new BusResultsScreen(stage, navigationContext).getView());
        });

        searchRow.getChildren().addAll(fromBox, toBox, dateBox, searchButton);
        heroCard.getChildren().addAll(heroTitle, searchRow);

        HBox quickGrid = new HBox(18);
        quickGrid.setAlignment(Pos.CENTER_LEFT);

        VBox quickSearch = createQuickCard("Search Buses", "Find the best fares for your route", "primary");
        VBox quickBookings = createQuickCard("My Bookings", "Review your confirmed bookings", "secondary");
        VBox quickTrack = createQuickCard("Track Bus", "Check your bus journey status", "accent");
        quickSearch.setOnMouseClicked(e -> navigationContext.showScene(new SearchBusScreen(stage, navigationContext).getView()));
        quickBookings.setOnMouseClicked(e -> navigationContext.showScene(new MyBookingsScreen(stage, navigationContext).getView()));
        quickTrack.setOnMouseClicked(e -> navigationContext.showScene(new TrackBusScreen(stage, navigationContext).getView()));
        quickGrid.getChildren().addAll(quickSearch, quickBookings, quickTrack);

        VBox routesCard = new VBox(16);
        routesCard.getStyleClass().add("card");
        routesCard.setPadding(new Insets(22));
        routesCard.setMaxWidth(980);

        Label routesTitle = new Label("Popular Routes");
        routesTitle.getStyleClass().add("section-title");

        VBox routeList = new VBox(10);
        String[][] routes = {
                {"Chennai", "Madurai"},
                {"Chennai", "Coimbatore"},
                {"Chennai", "Tirunelveli"},
                {"Chennai", "Trichy"},
                {"Madurai", "Chennai"},
                {"Coimbatore", "Chennai"}
        };
        for (String[] route : routes) {
            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(8, 10, 8, 10));
            row.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 10px;");
            Label routeLabel = new Label(route[0] + " → " + route[1]);
            routeLabel.setStyle("-fx-text-fill: #1e293b; -fx-font-size: 12px; -fx-font-weight: bold;");
            Region filler = new Region();
            HBox.setHgrow(filler, Priority.ALWAYS);
            Button routeButton = new Button("Book Now");
            routeButton.getStyleClass().add("secondary-button");
            final String from = route[0];
            final String to = route[1];
            routeButton.setOnAction(e -> {
                navigationContext.setSearchQuery(new NavigationContext.SearchQuery(from, to, java.time.LocalDate.now().toString(), 1));
                navigationContext.showScene(new BusResultsScreen(stage, navigationContext).getView());
            });
            row.getChildren().addAll(routeLabel, filler, routeButton);
            routeList.getChildren().add(row);
        }
        routesCard.getChildren().addAll(routesTitle, routeList);

        VBox featuresCard = new VBox(16);
        featuresCard.getStyleClass().add("card");
        featuresCard.setPadding(new Insets(22));
        featuresCard.setMaxWidth(980);
        Label featuresTitle = new Label("Why choose BusGo?");
        featuresTitle.getStyleClass().add("section-title");
        HBox featuresRow = new HBox(18);
        featuresRow.getChildren().addAll(
                createFeatureTile("✓ Easy Booking"),
                createFeatureTile("✓ Live Seat Availability"),
                createFeatureTile("✓ Secure Login"),
                createFeatureTile("✓ Digital Booking Confirmation"),
                createFeatureTile("✓ Booking History")
        );
        featuresCard.getChildren().addAll(featuresTitle, featuresRow);

        contentBox.getChildren().addAll(heroCard, quickGrid, routesCard, featuresCard);

        ScrollPane scroll = new ScrollPane(contentBox);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: #f4f7fb;");
        root.getChildren().addAll(headerBox, scroll);
        return root;
    }

    private VBox createQuickCard(String title, String subText, String color) {
        VBox card = new VBox(8);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(18));
        card.setPrefWidth(240);
        card.setAlignment(Pos.CENTER_LEFT);
        String borderColor = switch (color) {
            case "secondary" -> "#dbeafe";
            case "accent" -> "#fef3c7";
            default -> "#ffedd5";
        };
        card.setStyle("-fx-background-color: white; -fx-border-color: " + borderColor + "; -fx-border-width: 2; -fx-border-radius: 14px; -fx-background-radius: 14px; -fx-cursor: hand;");
        Label label = new Label(title);
        label.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        label.setStyle("-fx-text-fill: #0f172a;");
        Label text = new Label(subText);
        text.setWrapText(true);
        text.setStyle("-fx-text-fill: #475569; -fx-font-size: 11px;");
        card.getChildren().addAll(label, text);
        return card;
    }

    private VBox createFeatureTile(String text) {
        VBox tile = new VBox();
        tile.setPadding(new Insets(12, 14, 12, 14));
        tile.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 12px;");
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: #1e293b; -fx-font-size: 12px; -fx-font-weight: bold;");
        tile.getChildren().add(label);
        return tile;
    }

    private void showAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
