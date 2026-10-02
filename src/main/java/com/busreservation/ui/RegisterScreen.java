package com.busreservation.ui;

import com.busreservation.dto.ApiResponse;
import com.busreservation.dto.UserRegisterDTO;
import com.busreservation.dto.UserResponseDTO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class RegisterScreen {
    private Stage stage;
    private NavigationContext navigationContext;

    public RegisterScreen(Stage stage, NavigationContext navigationContext) {
        this.stage = stage;
        this.navigationContext = navigationContext;
    }

    public Parent getView() {
        VBox root = new VBox(0);
        root.setStyle("-fx-background-color: #f8f9fa;");

        // Header
        VBox headerBox = new VBox(5);
        headerBox.setStyle("-fx-background-color: #003d82;");
        headerBox.setPadding(new Insets(30, 40, 30, 40));

        Label titleLabel = new Label("Tamil Nadu Bus Reservation");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        titleLabel.setStyle("-fx-text-fill: white;");

        Label subtitleLabel = new Label("Create your account to book buses");
        subtitleLabel.setFont(Font.font("Segoe UI", 13));
        subtitleLabel.setStyle("-fx-text-fill: #ccc;");

        headerBox.getChildren().addAll(titleLabel, subtitleLabel);

        // Form container
        VBox containerBox = new VBox();
        containerBox.setAlignment(Pos.CENTER);
        containerBox.setPadding(new Insets(30, 0, 30, 0));

        VBox formBox = new VBox(12);
        formBox.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 8; -fx-background-radius: 8;");
        formBox.setPadding(new Insets(30));
        formBox.setMaxWidth(500);

        Label registerTitle = new Label("Register Account");
        registerTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        registerTitle.setStyle("-fx-text-fill: #003d82;");
        formBox.getChildren().add(registerTitle);
        formBox.getChildren().add(new Separator());

        // First Name
        Label firstNameLabel = new Label("First Name:");
        firstNameLabel.setStyle("-fx-font-size: 12; -fx-font-weight: bold; -fx-text-fill: #333;");
        TextField firstNameField = new TextField();
        firstNameField.setStyle("-fx-font-size: 12; -fx-padding: 8; -fx-border-radius: 4;");
        firstNameField.setPromptText("Enter your first name");

        // Last Name
        Label lastNameLabel = new Label("Last Name:");
        lastNameLabel.setStyle("-fx-font-size: 12; -fx-font-weight: bold; -fx-text-fill: #333;");
        TextField lastNameField = new TextField();
        lastNameField.setStyle("-fx-font-size: 12; -fx-padding: 8; -fx-border-radius: 4;");
        lastNameField.setPromptText("Enter your last name");

        // Username
        Label usernameLabel = new Label("Username:");
        usernameLabel.setStyle("-fx-font-size: 12; -fx-font-weight: bold; -fx-text-fill: #333;");
        TextField usernameField = new TextField();
        usernameField.setStyle("-fx-font-size: 12; -fx-padding: 8; -fx-border-radius: 4;");
        usernameField.setPromptText("Choose a unique username");

        // Email
        Label emailLabel = new Label("Email Address:");
        emailLabel.setStyle("-fx-font-size: 12; -fx-font-weight: bold; -fx-text-fill: #333;");
        TextField emailField = new TextField();
        emailField.setStyle("-fx-font-size: 12; -fx-padding: 8; -fx-border-radius: 4;");
        emailField.setPromptText("Enter your email");

        // Phone
        Label phoneLabel = new Label("Phone Number:");
        phoneLabel.setStyle("-fx-font-size: 12; -fx-font-weight: bold; -fx-text-fill: #333;");
        TextField phoneField = new TextField();
        phoneField.setStyle("-fx-font-size: 12; -fx-padding: 8; -fx-border-radius: 4;");
        phoneField.setPromptText("Enter your mobile number");

        // Password
        Label passwordLabel = new Label("Password:");
        passwordLabel.setStyle("-fx-font-size: 12; -fx-font-weight: bold; -fx-text-fill: #333;");
        PasswordField passwordField = new PasswordField();
        passwordField.setStyle("-fx-font-size: 12; -fx-padding: 8; -fx-border-radius: 4;");
        passwordField.setPromptText("Create a password (min 6 chars)");

        // Confirm Password
        Label confirmPasswordLabel = new Label("Confirm Password:");
        confirmPasswordLabel.setStyle("-fx-font-size: 12; -fx-font-weight: bold; -fx-text-fill: #333;");
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setStyle("-fx-font-size: 12; -fx-padding: 8; -fx-border-radius: 4;");
        confirmPasswordField.setPromptText("Re-enter your password");

        formBox.getChildren().addAll(
                firstNameLabel, firstNameField,
                lastNameLabel, lastNameField,
                usernameLabel, usernameField,
                emailLabel, emailField,
                phoneLabel, phoneField,
                passwordLabel, passwordField,
                confirmPasswordLabel, confirmPasswordField
        );

        formBox.getChildren().add(new Separator());

        // Register Button
        Button registerButton = new Button("CREATE ACCOUNT");
        registerButton.setStyle(
                "-fx-font-size: 13; -fx-font-weight: bold; -fx-padding: 12 40 12 40; " +
                "-fx-background-color: #ff9800; -fx-text-fill: white; " +
                "-fx-border-radius: 4; -fx-background-radius: 4; -fx-cursor: hand;"
        );
        registerButton.setOnMouseEntered(e -> registerButton.setStyle(
                "-fx-font-size: 13; -fx-font-weight: bold; -fx-padding: 12 40 12 40; " +
                "-fx-background-color: #f57c00; -fx-text-fill: white; " +
                "-fx-border-radius: 4; -fx-background-radius: 4; -fx-cursor: hand;"
        ));
        registerButton.setOnMouseExited(e -> registerButton.setStyle(
                "-fx-font-size: 13; -fx-font-weight: bold; -fx-padding: 12 40 12 40; " +
                "-fx-background-color: #ff9800; -fx-text-fill: white; " +
                "-fx-border-radius: 4; -fx-background-radius: 4; -fx-cursor: hand;"
        ));

        registerButton.setOnAction(e -> {
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            if (firstName.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "First name, username, email, and password are required");
                return;
            }

            if (!password.equals(confirmPassword)) {
                showAlert(Alert.AlertType.ERROR, "Error", "Passwords do not match");
                return;
            }

            try {
                UserRegisterDTO dto = new UserRegisterDTO(
                        firstName,
                        lastName.isEmpty() ? firstName : lastName,
                        email,
                        username,
                        password,
                        confirmPassword,
                        phone,
                        "Tamil Nadu",
                        "Chennai",
                        "Tamil Nadu",
                        "600001"
                );

                ApiResponse<UserResponseDTO> response = navigationContext.getApiClient().register(dto);
                if (response != null && response.isSuccess()) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Registration successful! Account saved to database. Please login with your credentials.");
                    navigateToLogin();
                } else {
                    String msg = (response != null && response.getMessage() != null) ? response.getMessage() : "Registration failed";
                    showAlert(Alert.AlertType.ERROR, "Registration Failed", msg);
                }
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to register account: " + ex.getMessage());
            }
        });

        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().add(registerButton);

        formBox.getChildren().add(buttonBox);

        // Login link
        HBox loginLinkBox = new HBox();
        loginLinkBox.setAlignment(Pos.CENTER);
        loginLinkBox.setPadding(new Insets(15, 0, 0, 0));

        Label alreadyLabel = new Label("Already have an account? ");
        alreadyLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #666;");

        Button loginLink = new Button("Login here");
        loginLink.setStyle("-fx-background-color: transparent; -fx-text-fill: #0066cc; -fx-cursor: hand; -fx-font-size: 12; -fx-font-weight: bold; -fx-padding: 0;");
        loginLink.setOnAction(e -> navigateToLogin());

        loginLinkBox.getChildren().addAll(alreadyLabel, loginLink);

        containerBox.getChildren().addAll(formBox, loginLinkBox);

        ScrollPane scrollPane = new ScrollPane(containerBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #f8f9fa;");

        root.getChildren().addAll(headerBox, scrollPane);
        return root;
    }

    private void navigateToLogin() {
        LoginScreen loginScreen = new LoginScreen(stage, navigationContext);
        Scene scene = new Scene(loginScreen.getView(), 1100, 750);
        stage.setScene(scene);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
