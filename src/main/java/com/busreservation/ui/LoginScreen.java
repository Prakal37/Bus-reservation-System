package com.busreservation.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class LoginScreen {
    private Stage stage;
    private NavigationContext navigationContext;

    public LoginScreen(Stage stage, NavigationContext navigationContext) {
        this.stage = stage;
        this.navigationContext = navigationContext;
    }

    public Parent getView() {
        VBox root = new VBox(15);
        root.setStyle("-fx-background-color: #f8f9fa;");
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));

        // Title
        Label titleLabel = new Label("Tamil Nadu Bus Reservation");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 36));
        titleLabel.setStyle("-fx-text-fill: #003d82;");

        // Subtitle
        Label subtitleLabel = new Label("Book your journey across Tamil Nadu");
        subtitleLabel.setFont(Font.font("Segoe UI", 14));
        subtitleLabel.setStyle("-fx-text-fill: #555555;");

        // Mobile Number
        Label mobileLabel = new Label("Email / Mobile Number:");
        mobileLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #333333; -fx-font-weight: bold;");
        TextField mobileField = new TextField();
        mobileField.setStyle("-fx-font-size: 12; -fx-padding: 10; -fx-border-radius: 4; -fx-background-radius: 4; -fx-border-color: #ddd;");
        mobileField.setPromptText("Enter your email or mobile number");

        // Password
        Label passwordLabel = new Label("Password:");
        passwordLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #333333; -fx-font-weight: bold;");
        PasswordField passwordField = new PasswordField();
        passwordField.setStyle("-fx-font-size: 12; -fx-padding: 10; -fx-border-radius: 4; -fx-background-radius: 4; -fx-border-color: #ddd;");
        passwordField.setPromptText("Enter your password");

        // Login Button
        Button loginButton = new Button("LOGIN");
        loginButton.setStyle(
                "-fx-font-size: 13; -fx-font-weight: bold; -fx-padding: 12 40 12 40; " +
                "-fx-background-color: #0066cc; -fx-text-fill: white; " +
                "-fx-border-radius: 4; -fx-background-radius: 4; -fx-cursor: hand;"
        );
        loginButton.setOnMouseEntered(e -> loginButton.setStyle(
                "-fx-font-size: 13; -fx-font-weight: bold; -fx-padding: 12 40 12 40; " +
                "-fx-background-color: #0052a3; -fx-text-fill: white; " +
                "-fx-border-radius: 4; -fx-background-radius: 4; -fx-cursor: hand;"
        ));
        loginButton.setOnMouseExited(e -> loginButton.setStyle(
                "-fx-font-size: 13; -fx-font-weight: bold; -fx-padding: 12 40 12 40; " +
                "-fx-background-color: #0066cc; -fx-text-fill: white; " +
                "-fx-border-radius: 4; -fx-background-radius: 4; -fx-cursor: hand;"
        ));

        loginButton.setOnAction(e -> {
            String mobile = mobileField.getText().trim();
            if (mobile.isEmpty()) {
                showAlert("Error", "Please enter email or mobile number");
                return;
            }
            navigationContext.setLoggedInUser(mobile);
            navigateToDashboard();
        });

        // Register Link
        Button registerLink = new Button("Don't have an account? Register here");
        registerLink.setStyle("-fx-background-color: transparent; -fx-text-fill: #0066cc; -fx-cursor: hand; -fx-font-size: 11;");
        registerLink.setOnAction(e -> navigateToRegister());

        VBox formBox = new VBox(10);
        formBox.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 8; -fx-background-radius: 8;");
        formBox.setPadding(new Insets(40));
        formBox.setAlignment(Pos.CENTER);
        formBox.setMaxWidth(400);

        formBox.getChildren().addAll(
                titleLabel,
                subtitleLabel,
                new Separator(),
                mobileLabel,
                mobileField,
                passwordLabel,
                passwordField,
                loginButton,
                registerLink
        );

        root.getChildren().add(formBox);
        return root;
    }

    private void navigateToDashboard() {
        DashboardScreen dashboardScreen = new DashboardScreen(stage, navigationContext);
        Scene scene = new Scene(dashboardScreen.getView(), 1100, 750);
        stage.setScene(scene);
    }

    private void navigateToRegister() {
        RegisterScreen registerScreen = new RegisterScreen(stage, navigationContext);
        Scene scene = new Scene(registerScreen.getView(), 1100, 750);
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
