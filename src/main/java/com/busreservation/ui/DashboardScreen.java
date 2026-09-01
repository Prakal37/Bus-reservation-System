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

public class DashboardScreen {
    private Stage stage;
    private NavigationContext navigationContext;

    public DashboardScreen(Stage stage, NavigationContext navigationContext) {
        this.stage = stage;
        this.navigationContext = navigationContext;
    }

    public Parent getView() {
        VBox root = new VBox(0);
        root.setStyle("-fx-background-color: #f8f9fa;");

        // Header
        VBox headerBox = new VBox(10);
        headerBox.setStyle("-fx-background-color: #003d82;");
        headerBox.setPadding(new Insets(30, 40, 30, 40));

        HBox topHeaderBox = new HBox();
        topHeaderBox.setAlignment(Pos.CENTER_LEFT);

        Label welcomeLabel = new Label("Welcome, " + navigationContext.getLoggedInUser());
        welcomeLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        welcomeLabel.setStyle("-fx-text-fill: white;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button logoutButton = new Button("Logout");
        logoutButton.setStyle(
                "-fx-font-size: 11; -fx-padding: 8 16 8 16; " +
                "-fx-background-color: #e74c3c; -fx-text-fill: white; " +
                "-fx-border-radius: 4; -fx-background-radius: 4; -fx-cursor: hand;"
        );
        logoutButton.setOnMouseEntered(e -> logoutButton.setStyle(
                "-fx-font-size: 11; -fx-padding: 8 16 8 16; " +
                "-fx-background-color: #c0392b; -fx-text-fill: white; " +
                "-fx-border-radius: 4; -fx-background-radius: 4; -fx-cursor: hand;"
        ));
        logoutButton.setOnMouseExited(e -> logoutButton.setStyle(
                "-fx-font-size: 11; -fx-padding: 8 16 8 16; " +
                "-fx-background-color: #e74c3c; -fx-text-fill: white; " +
                "-fx-border-radius: 4; -fx-background-radius: 4; -fx-cursor: hand;"
        ));
        logoutButton.setOnAction(e -> {
            navigationContext.logout();
            LoginScreen loginScreen = new LoginScreen(stage, navigationContext);
            Scene scene = new Scene(loginScreen.getView(), 1100, 750);
            stage.setScene(scene);
        });

        topHeaderBox.getChildren().addAll(welcomeLabel, spacer, logoutButton);

        Label taglineLabel = new Label("Book your journey across Tamil Nadu");
        taglineLabel.setFont(Font.font("Segoe UI", 13));
        taglineLabel.setStyle("-fx-text-fill: #ccc;");

        headerBox.getChildren().addAll(topHeaderBox, taglineLabel);

        // Main content area
        VBox contentBox = new VBox(30);
        contentBox.setStyle("-fx-background-color: #f8f9fa;");
        contentBox.setPadding(new Insets(40));

        // Search card
        VBox searchCard = new VBox(15);
        searchCard.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 8; -fx-background-radius: 8;");
        searchCard.setPadding(new Insets(30));
        searchCard.setMaxWidth(800);

        Label searchTitle = new Label("Quick Search");
        searchTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        searchTitle.setStyle("-fx-text-fill: #003d82;");

        // Search form
        VBox formBox = new VBox(12);

        HBox citiesRow = new HBox(15);
        citiesRow.setAlignment(Pos.CENTER_LEFT);

        Label fromLabel = new Label("From:");
        fromLabel.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");
        ComboBox<String> fromCombo = new ComboBox<>();
        fromCombo.getItems().addAll(
                "Chennai", "Madurai", "Coimbatore", "Tiruchirappalli", "Salem",
                "Tirunelveli", "Thoothukudi", "Erode", "Vellore", "Thanjavur",
                "Kanyakumari", "Dindigul", "Hosur", "Nagercoil", "Sivakasi"
        );
        fromCombo.setValue("Chennai");
        fromCombo.setMaxWidth(200);
        fromCombo.setStyle("-fx-font-size: 12;");

        Label toLabel = new Label("To:");
        toLabel.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");
        ComboBox<String> toCombo = new ComboBox<>();
        toCombo.getItems().addAll(
                "Chennai", "Madurai", "Coimbatore", "Tiruchirappalli", "Salem",
                "Tirunelveli", "Thoothukudi", "Erode", "Vellore", "Thanjavur",
                "Kanyakumari", "Dindigul", "Hosur", "Nagercoil", "Sivakasi"
        );
        toCombo.setValue("Madurai");
        toCombo.setMaxWidth(200);
        toCombo.setStyle("-fx-font-size: 12;");

        citiesRow.getChildren().addAll(fromLabel, fromCombo, toLabel, toCombo);

        HBox datePassengersRow = new HBox(15);
        datePassengersRow.setAlignment(Pos.CENTER_LEFT);

        Label dateLabel = new Label("Date:");
        dateLabel.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");
        DatePicker datePicker = new DatePicker();
        datePicker.setValue(java.time.LocalDate.now());
        datePicker.setMaxWidth(200);
        datePicker.setStyle("-fx-font-size: 12;");

        Label passengersLabel = new Label("Passengers:");
        passengersLabel.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");
        ComboBox<Integer> passengerCombo = new ComboBox<>();
        for (int i = 1; i <= 6; i++) {
            passengerCombo.getItems().add(i);
        }
        passengerCombo.setValue(1);
        passengerCombo.setMaxWidth(100);
        passengerCombo.setStyle("-fx-font-size: 12;");

        datePassengersRow.getChildren().addAll(dateLabel, datePicker, passengersLabel, passengerCombo);

        formBox.getChildren().addAll(citiesRow, datePassengersRow);

        // Search button
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
            String from = fromCombo.getValue();
            String to = toCombo.getValue();
            String dateStr = datePicker.getValue().toString();
            int passengers = passengerCombo.getValue();

            if (from.equals(to)) {
                showAlert("Error", "Departure and destination cities must be different");
                return;
            }

            NavigationContext.SearchQuery query = new NavigationContext.SearchQuery(from, to, dateStr, passengers);
            navigationContext.setSearchQuery(query);
            navigateToSearchResults();
        });

        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().add(searchButton);

        searchCard.getChildren().addAll(searchTitle, new Separator(), formBox, new Separator(), buttonBox);

        // Popular routes section
        VBox popularRoutesBox = createPopularRoutesSection();

        // My bookings card
        VBox myBookingsCard = new VBox(15);
        myBookingsCard.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 8;");
        myBookingsCard.setPadding(new Insets(25));

        Label bookingsTitle = new Label("Your Recent Bookings");
        bookingsTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        bookingsTitle.setStyle("-fx-text-fill: #003d82;");

        Button viewBookingsBtn = new Button("VIEW MY BOOKINGS");
        viewBookingsBtn.setStyle(
                "-fx-font-size: 12; -fx-font-weight: bold; -fx-padding: 10 30 10 30; " +
                "-fx-background-color: #0066cc; -fx-text-fill: white; " +
                "-fx-border-radius: 4; -fx-background-radius: 4; -fx-cursor: hand;"
        );
        viewBookingsBtn.setOnMouseEntered(e -> viewBookingsBtn.setStyle(
                "-fx-font-size: 12; -fx-font-weight: bold; -fx-padding: 10 30 10 30; " +
                "-fx-background-color: #0052a3; -fx-text-fill: white; " +
                "-fx-border-radius: 4; -fx-background-radius: 4; -fx-cursor: hand;"
        ));
        viewBookingsBtn.setOnMouseExited(e -> viewBookingsBtn.setStyle(
                "-fx-font-size: 12; -fx-font-weight: bold; -fx-padding: 10 30 10 30; " +
                "-fx-background-color: #0066cc; -fx-text-fill: white; " +
                "-fx-border-radius: 4; -fx-background-radius: 4; -fx-cursor: hand;"
        ));
        viewBookingsBtn.setOnAction(e -> navigateToMyBookings());

        myBookingsCard.getChildren().addAll(bookingsTitle, viewBookingsBtn);

        contentBox.getChildren().addAll(searchCard, popularRoutesBox, myBookingsCard);

        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #f8f9fa;");

        root.getChildren().addAll(headerBox, scrollPane);
        return root;
    }

    private VBox createPopularRoutesSection() {
        VBox box = new VBox(15);
        box.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 8;");
        box.setPadding(new Insets(25));

        Label title = new Label("Popular Routes");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        title.setStyle("-fx-text-fill: #003d82;");

        String[][] routes = {
                {"Chennai", "Madurai"},
                {"Chennai", "Coimbatore"},
                {"Coimbatore", "Madurai"},
                {"Chennai", "Tirunelveli"},
                {"Madurai", "Nagercoil"}
        };

        VBox routesContainer = new VBox(8);
        for (String[] route : routes) {
            HBox routeItem = new HBox(15);
            routeItem.setAlignment(Pos.CENTER_LEFT);
            routeItem.setStyle("-fx-padding: 8; -fx-background-color: #f8f9fa; -fx-border-radius: 4;");

            Label routeLabel = new Label(route[0] + " → " + route[1]);
            routeLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #333;");

            Region filler = new Region();
            HBox.setHgrow(filler, Priority.ALWAYS);

            Button bookBtn = new Button("Book");
            bookBtn.setStyle("-fx-font-size: 10; -fx-padding: 5 15 5 15; -fx-background-color: #ff9800; -fx-text-fill: white; -fx-border-radius: 3;");
            final String from = route[0];
            final String to = route[1];
            bookBtn.setOnAction(e -> {
                NavigationContext.SearchQuery query = new NavigationContext.SearchQuery(from, to, java.time.LocalDate.now().toString(), 1);
                navigationContext.setSearchQuery(query);
                navigateToSearchResults();
            });

            routeItem.getChildren().addAll(routeLabel, filler, bookBtn);
            routesContainer.getChildren().add(routeItem);
        }

        box.getChildren().addAll(title, routesContainer);
        return box;
    }

    private void navigateToSearchResults() {
        BusResultsScreen busResultsScreen = new BusResultsScreen(stage, navigationContext);
        Scene scene = new Scene(busResultsScreen.getView(), 1100, 750);
        stage.setScene(scene);
    }

    private void navigateToMyBookings() {
        MyBookingsScreen myBookingsScreen = new MyBookingsScreen(stage, navigationContext);
        Scene scene = new Scene(myBookingsScreen.getView(), 1100, 750);
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
