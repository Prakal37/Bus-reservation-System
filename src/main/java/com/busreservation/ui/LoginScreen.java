package com.busreservation.ui;

import com.busreservation.dto.ApiResponse;
import com.busreservation.dto.LoginResponseDTO;
import com.busreservation.dto.UserLoginDTO;
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

        // Username / Email
        Label mobileLabel = new Label("Username / Email:");
        mobileLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #333333; -fx-font-weight: bold;");
        TextField usernameField = new TextField();
        usernameField.setStyle("-fx-font-size: 12; -fx-padding: 10; -fx-border-radius: 4; -fx-background-radius: 4; -fx-border-color: #ddd;");
        usernameField.setPromptText("Enter your username or email");

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
            String username = usernameField.getText().trim();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                showAlert("Error", "Please enter both username/email and password");
                return;
            }

            try {
                UserLoginDTO loginDTO = new UserLoginDTO(username, password);
                ApiResponse<LoginResponseDTO> response = navigationContext.getApiClient().login(loginDTO);

                if (response != null && response.isSuccess() && response.getData() != null) {
                    LoginResponseDTO loginData = response.getData();
                    navigationContext.setJwtToken(loginData.getToken());
                    navigationContext.setCurrentUser(loginData.getUser());
                    navigationContext.setLoggedInUser(loginData.getUser() != null ? loginData.getUser().getUsername() : username);
                    navigateToDashboard();
                } else {
                    String msg = (response != null && response.getMessage() != null) ? response.getMessage() : "Invalid credentials";
                    showAlert("Login Failed", msg);
                }
            } catch (Exception ex) {
                showAlert("Connection Error", "Failed to connect to backend server. Make sure Spring Boot backend is running.\nDetails: " + ex.getMessage());
            }
        });

        // Forgot Password Link
        Button forgotPasswordButton = new Button("Forgot Password?");
        forgotPasswordButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #0066cc; -fx-cursor: hand; -fx-font-size: 11; -fx-font-weight: bold;");
        forgotPasswordButton.setOnAction(e -> showForgotPasswordDialog());

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
                usernameField,
                passwordLabel,
                passwordField,
                forgotPasswordButton,
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

    private void navigateToLogin() {
        LoginScreen loginScreen = new LoginScreen(stage, navigationContext);
        Scene scene = new Scene(loginScreen.getView(), 1100, 750);
        stage.setScene(scene);
    }

    private void showForgotPasswordDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Forgot Password");
        dialog.setHeaderText("Reset your account password");

        TextField identifierField = new TextField();
        identifierField.setPromptText("Registered email or username");

        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("New password");

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm new password");

        Label statusLabel = new Label();
        statusLabel.setWrapText(true);
        statusLabel.setVisible(false);
        statusLabel.setManaged(false);
        statusLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 12px;");

        VBox dialogContent = new VBox(10);
        dialogContent.setPadding(new Insets(20));
        dialogContent.setPrefWidth(380);
        dialogContent.getChildren().addAll(
                new Label("Registered email / username:"),
                identifierField,
                new Label("New password:"),
                newPasswordField,
                new Label("Confirm new password:"),
                confirmPasswordField,
                statusLabel
        );

        Button resetButton = new Button("Reset Password");
        resetButton.setStyle(
                "-fx-font-size: 12; -fx-font-weight: bold; -fx-padding: 10 20 10 20; " +
                "-fx-background-color: #0066cc; -fx-text-fill: white; -fx-border-radius: 4; -fx-background-radius: 4;"
        );

        Button backButton = new Button("← Back to Login");
        backButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #0066cc; -fx-font-weight: bold; -fx-cursor: hand;");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setContent(dialogContent);
        dialogPane.getButtonTypes().addAll(ButtonType.CLOSE);
        dialogPane.lookupButton(ButtonType.CLOSE).setVisible(false);

        resetButton.setOnAction(e -> {
            statusLabel.setText("");
            statusLabel.setVisible(false);
            statusLabel.setManaged(false);

            String identifier = identifierField.getText() == null ? "" : identifierField.getText().trim();
            String newPassword = newPasswordField.getText() == null ? "" : newPasswordField.getText();
            String confirmPassword = confirmPasswordField.getText() == null ? "" : confirmPasswordField.getText();

            if (identifier.isEmpty()) {
                statusLabel.setText("Please enter your registered email or username.");
                statusLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 12px;");
                statusLabel.setVisible(true);
                statusLabel.setManaged(true);
                return;
            }
            if (newPassword.isEmpty()) {
                statusLabel.setText("New password cannot be empty.");
                statusLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 12px;");
                statusLabel.setVisible(true);
                statusLabel.setManaged(true);
                return;
            }
            if (!newPassword.equals(confirmPassword)) {
                statusLabel.setText("Passwords do not match.");
                statusLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 12px;");
                statusLabel.setVisible(true);
                statusLabel.setManaged(true);
                return;
            }

            try {
                ApiResponse<Boolean> response = navigationContext.getApiClient().resetPassword(identifier, newPassword, confirmPassword);
                if (response != null && response.isSuccess()) {
                    statusLabel.setText("Password reset successfully. Please login with your new password.");
                    statusLabel.setStyle("-fx-text-fill: #15803d; -fx-font-size: 12px; -fx-font-weight: bold;");
                    statusLabel.setVisible(true);
                    statusLabel.setManaged(true);
                    dialogContent.getChildren().removeAll(identifierField, newPasswordField, confirmPasswordField, resetButton);
                    backButton.setOnAction(event -> {
                        dialog.close();
                        navigateToLogin();
                    });
                    return;
                }
                String message = response != null && response.getMessage() != null ? response.getMessage() : "Account not found.";
                statusLabel.setText(message);
                statusLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 12px;");
                statusLabel.setVisible(true);
                statusLabel.setManaged(true);
            } catch (Exception ex) {
                statusLabel.setText("Unable to reset password. Please check backend connectivity.");
                statusLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 12px;");
                statusLabel.setVisible(true);
                statusLabel.setManaged(true);
            }
        });

        dialogPane.setContent(dialogContent);
        dialogContent.getChildren().add(resetButton);
        dialogContent.getChildren().add(backButton);
        dialog.getDialogPane().setPrefWidth(420);
        dialog.showAndWait();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
