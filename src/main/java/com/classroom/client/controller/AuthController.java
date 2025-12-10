package com.classroom.client.controller;

import com.classroom.client.util.ApiClient;
import com.classroom.client.util.Styles;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.concurrent.Worker;

import java.io.IOException;
import java.util.Map;

public class AuthController {
    private final VBox root;
    private final MainController mainController;

    public AuthController(MainController mainController) {
        this.mainController = mainController;
        this.root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(60));
        root.setStyle(Styles.background());

        createUI();
    }

    private boolean isSignInMode = false;

    private void createUI() {
        // White background
        root.setStyle("-fx-background-color: white;");

        // Centered container with border
        VBox container = new VBox(24);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(40, 50, 40, 50));
        container.setMaxWidth(420);
        container.setStyle("-fx-background-color: white; " +
                "-fx-border-color: #e0e0e0; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 0;");

        // App Name - large, bold, black
        Label appNameLabel = new Label("Classroom App");
        appNameLabel.setFont(new Font("System Bold", 32));
        appNameLabel.setStyle("-fx-text-fill: #000000; " +
                "-fx-font-weight: bold;");

        // Title (changes depending on mode)
        Label titleLabel = new Label();
        titleLabel.setFont(new Font("System Bold", 20));
        titleLabel.setStyle("-fx-text-fill: #000000; " +
                "-fx-font-weight: bold;");

        // Subtitle
        Label subtitleLabel = new Label();
        subtitleLabel.setFont(new Font("System", 14));
        subtitleLabel.setStyle("-fx-text-fill: #000000;");

        // Name field (only for sign up)
        TextField nameField = new TextField();
        nameField.setPromptText("Your name");
        nameField.setPrefWidth(320);
        nameField.setPrefHeight(48);
        nameField.setStyle("-fx-background-color: white; " +
                "-fx-border-color: #e0e0e0; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 4; " +
                "-fx-background-radius: 4; " +
                "-fx-padding: 12 16; " +
                "-fx-font-size: 14px;");
        nameField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            nameField.setStyle(newVal ?
                    "-fx-background-color: white; " +
                            "-fx-border-color: #000000; " +
                            "-fx-border-width: 1; " +
                            "-fx-border-radius: 4; " +
                            "-fx-background-radius: 4; " +
                            "-fx-padding: 12 16; " +
                            "-fx-font-size: 14px;" :
                    "-fx-background-color: white; " +
                            "-fx-border-color: #e0e0e0; " +
                            "-fx-border-width: 1; " +
                            "-fx-border-radius: 4; " +
                            "-fx-background-radius: 4; " +
                            "-fx-padding: 12 16; " +
                            "-fx-font-size: 14px;");
        });
        nameField.setVisible(false);
        nameField.setManaged(false);

        // Email field
        TextField emailField = new TextField();
        emailField.setPromptText("email@domain.com");
        emailField.setPrefWidth(320);
        emailField.setPrefHeight(48);
        emailField.setStyle("-fx-background-color: white; " +
                "-fx-border-color: #e0e0e0; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 4; " +
                "-fx-background-radius: 4; " +
                "-fx-padding: 12 16; " +
                "-fx-font-size: 14px;");
        emailField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            emailField.setStyle(newVal ?
                    "-fx-background-color: white; " +
                            "-fx-border-color: #000000; " +
                            "-fx-border-width: 1; " +
                            "-fx-border-radius: 4; " +
                            "-fx-background-radius: 4; " +
                            "-fx-padding: 12 16; " +
                            "-fx-font-size: 14px;" :
                    "-fx-background-color: white; " +
                            "-fx-border-color: #e0e0e0; " +
                            "-fx-border-width: 1; " +
                            "-fx-border-radius: 4; " +
                            "-fx-background-radius: 4; " +
                            "-fx-padding: 12 16; " +
                            "-fx-font-size: 14px;");
        });

        // Password field
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setPrefWidth(320);
        passwordField.setPrefHeight(48);
        passwordField.setStyle("-fx-background-color: white; " +
                "-fx-border-color: #e0e0e0; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 4; " +
                "-fx-background-radius: 4; " +
                "-fx-padding: 12 16; " +
                "-fx-font-size: 14px;");
        passwordField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            passwordField.setStyle(newVal ?
                    "-fx-background-color: white; " +
                            "-fx-border-color: #000000; " +
                            "-fx-border-width: 1; " +
                            "-fx-border-radius: 4; " +
                            "-fx-background-radius: 4; " +
                            "-fx-padding: 12 16; " +
                            "-fx-font-size: 14px;" :
                    "-fx-background-color: white; " +
                            "-fx-border-color: #e0e0e0; " +
                            "-fx-border-width: 1; " +
                            "-fx-border-radius: 4; " +
                            "-fx-background-radius: 4; " +
                            "-fx-padding: 12 16; " +
                            "-fx-font-size: 14px;");
        });

        // Black button
        Button actionButton = new Button();
        actionButton.setPrefWidth(320);
        actionButton.setPrefHeight(48);
        actionButton.setStyle("-fx-background-color: #000000; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 4; " +
                "-fx-cursor: hand;");
        actionButton.setOnMouseEntered(e -> actionButton.setStyle(
                "-fx-background-color: #333333; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 4; " +
                        "-fx-cursor: hand;"));
        actionButton.setOnMouseExited(e -> actionButton.setStyle(
                "-fx-background-color: #000000; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 4; " +
                        "-fx-cursor: hand;"));

        // Divider "or continue with"
        HBox dividerBox = new HBox(12);
        dividerBox.setAlignment(Pos.CENTER);
        dividerBox.setPrefWidth(320);

        javafx.scene.shape.Line line1 = new javafx.scene.shape.Line(0, 0, 100, 0);
        line1.setStroke(javafx.scene.paint.Color.valueOf("#e0e0e0"));
        line1.setStrokeWidth(1);

        Label orLabel = new Label("or continue with");
        orLabel.setFont(new Font("System", 12));
        orLabel.setStyle("-fx-text-fill: #757575;");

        javafx.scene.shape.Line line2 = new javafx.scene.shape.Line(0, 0, 100, 0);
        line2.setStroke(javafx.scene.paint.Color.valueOf("#e0e0e0"));
        line2.setStrokeWidth(1);

        dividerBox.getChildren().addAll(line1, orLabel, line2);

        // Gray Google button
        Button googleButton = new Button("Google");
        googleButton.setPrefWidth(320);
        googleButton.setPrefHeight(48);
        googleButton.setStyle("-fx-background-color: #f5f5f5; " +
                "-fx-text-fill: #000000; " +
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold; " +
                "-fx-border-color: #e0e0e0; " +
                "-fx-border-width: 1; " +
                "-fx-background-radius: 4; " +
                "-fx-border-radius: 4; " +
                "-fx-cursor: hand;");
        googleButton.setOnMouseEntered(e -> googleButton.setStyle(
                "-fx-background-color: #eeeeee; " +
                        "-fx-text-fill: #000000; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-border-color: #e0e0e0; " +
                        "-fx-border-width: 1; " +
                        "-fx-background-radius: 4; " +
                        "-fx-border-radius: 4; " +
                        "-fx-cursor: hand;"));
        googleButton.setOnMouseExited(e -> googleButton.setStyle(
                "-fx-background-color: #f5f5f5; " +
                        "-fx-text-fill: #000000; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-border-color: #e0e0e0; " +
                        "-fx-border-width: 1; " +
                        "-fx-background-radius: 4; " +
                        "-fx-border-radius: 4; " +
                        "-fx-cursor: hand;"));
        googleButton.setOnAction(e -> handleGoogleLogin());

        // Legal text
        Label legalLabel = new Label("By clicking continue, you agree to our Terms of Service and Privacy Policy");
        legalLabel.setFont(new Font("System", 11));
        legalLabel.setStyle("-fx-text-fill: #757575;");
        legalLabel.setWrapText(true);
        legalLabel.setPrefWidth(320);
        legalLabel.setAlignment(Pos.CENTER);

        // Link for switching between sign up and sign in
        HBox switchBox = new HBox(5);
        switchBox.setAlignment(Pos.CENTER);
        Label switchLabel = new Label();
        switchLabel.setFont(new Font("System", 12));
        switchLabel.setStyle("-fx-text-fill: #757575;");

        Label switchLink = new Label();
        switchLink.setFont(new Font("System Bold", 12));
        switchLink.setStyle("-fx-text-fill: #000000; " +
                "-fx-font-weight: bold; " +
                "-fx-underline: true; " +
                "-fx-cursor: hand;");
        switchLink.setOnMouseEntered(e -> switchLink.setStyle(
                "-fx-text-fill: #333333; " +
                        "-fx-font-weight: bold; " +
                        "-fx-underline: true; " +
                        "-fx-cursor: hand;"));
        switchLink.setOnMouseExited(e -> switchLink.setStyle(
                "-fx-text-fill: #000000; " +
                        "-fx-font-weight: bold; " +
                        "-fx-underline: true; " +
                        "-fx-cursor: hand;"));

        // Function to update UI depending on mode
        Runnable updateUI = () -> {
            if (isSignInMode) {
                titleLabel.setText("Sign in");
                subtitleLabel.setText("Enter your email and password to sign in");
                actionButton.setText("Sign in");
                actionButton.setOnAction(e -> {
                    if (emailField.getText().isEmpty() || passwordField.getText().isEmpty()) {
                        showAlert("Error", "Please fill in all fields", Alert.AlertType.ERROR);
                        return;
                    }
                    handleLogin(emailField.getText(), passwordField.getText());
                });
                switchLabel.setText("Don't have an account? ");
                switchLink.setText("Sign up");
                nameField.setVisible(false);
                nameField.setManaged(false);
                passwordField.setVisible(true);
                passwordField.setManaged(true);
            } else {
                titleLabel.setText("Create an account");
                subtitleLabel.setText("Enter your information to sign up for this app");
                actionButton.setText("Sign up with email");
                actionButton.setOnAction(e -> {
                    if (nameField.getText().trim().isEmpty() ||
                            emailField.getText().trim().isEmpty() ||
                            passwordField.getText().isEmpty()) {
                        showAlert("Error", "Please fill in all fields", Alert.AlertType.ERROR);
                        return;
                    }
                    handleRegister(nameField.getText().trim(),
                            emailField.getText().trim(),
                            passwordField.getText());
                });
                switchLabel.setText("Already have an account? ");
                switchLink.setText("Sign in");
                nameField.setVisible(true);
                nameField.setManaged(true);
                passwordField.setVisible(true);
                passwordField.setManaged(true);
            }
        };

        switchLink.setOnMouseClicked(e -> {
            isSignInMode = !isSignInMode;
            nameField.clear();
            emailField.clear();
            passwordField.clear();
            updateUI.run();
        });

        switchBox.getChildren().addAll(switchLabel, switchLink);

        // Initialize UI
        updateUI.run();

        // Add elements
        container.getChildren().addAll(
                appNameLabel,
                titleLabel,
                subtitleLabel,
                nameField,
                emailField,
                passwordField,
                actionButton,
                dividerBox,
                googleButton,
                legalLabel,
                switchBox
        );

        // Center container
        VBox wrapper = new VBox();
        wrapper.setAlignment(Pos.CENTER);
        wrapper.setStyle("-fx-background-color: white;");
        wrapper.getChildren().add(container);

        root.getChildren().add(wrapper);
    }

    private void handleLogin(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please fill in all fields", Alert.AlertType.ERROR);
            return;
        }

        try {
            Map<String, Object> request = Map.of(
                    "email", email,
                    "password", password
            );

            var response = ApiClient.post("/auth/login", request);
            Map<String, Object> result = ApiClient.parseResponse(response);

            if (Boolean.TRUE.equals(result.get("success"))) {
                String token = (String) result.get("token");
                ApiClient.setAuthToken(token);
                @SuppressWarnings("unchecked")
                Map<String, Object> user = (Map<String, Object>) result.get("user");
                if (user != null && user.get("userId") != null) {
                    Long userId = ((Number) user.get("userId")).longValue();
                    ApiClient.setCurrentUserId(userId);
                }
                mainController.showGroupScreen();
            } else {
                showAlert("Error", (String) result.getOrDefault("message", "Invalid credentials"), Alert.AlertType.ERROR);
            }
        } catch (IOException e) {
            showAlert("Error", "Server connection error", Alert.AlertType.ERROR);
        }
    }

    private void handleRegister(String name, String email, String password) {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please fill in all fields", Alert.AlertType.ERROR);
            return;
        }

        try {
            Map<String, Object> request = Map.of(
                    "name", name,
                    "email", email,
                    "password", password
            );

            var response = ApiClient.post("/auth/register", request);
            Map<String, Object> result = ApiClient.parseResponse(response);

            if (Boolean.TRUE.equals(result.get("success"))) {
                String token = (String) result.get("token");
                ApiClient.setAuthToken(token);
                @SuppressWarnings("unchecked")
                Map<String, Object> user = (Map<String, Object>) result.get("user");
                if (user != null && user.get("userId") != null) {
                    Long userId = ((Number) user.get("userId")).longValue();
                    ApiClient.setCurrentUserId(userId);
                }
                mainController.showGroupScreen();
            } else {
                showAlert("Error", (String) result.getOrDefault("message", "Registration error"), Alert.AlertType.ERROR);
            }
        } catch (IOException e) {
            showAlert("Error", "Server connection error", Alert.AlertType.ERROR);
        }
    }

    private void handleGoogleLogin() {
        // Open embedded window with WebView to automatically intercept token
        Stage oauthStage = new Stage();
        oauthStage.initModality(Modality.APPLICATION_MODAL);
        oauthStage.setTitle("Sign in with Google");
        oauthStage.setWidth(800);
        oauthStage.setHeight(700);

        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();

        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                String location = webEngine.getLocation();
                // When we reach the success page, extract token from textarea#token
                if (location.contains("/api/auth/oauth2/success")) {
                    try {
                        Object tokenObj = webEngine.executeScript(
                                "document.getElementById('token') ? document.getElementById('token').value : null;"
                        );
                        if (tokenObj != null) {
                            String token = tokenObj.toString().trim();
                            if (!token.isEmpty()) {
                                oauthStage.close();
                                completeOAuthLogin(token);
                            }
                        }
                    } catch (Exception e) {
                        // Ignore and let user copy if something went wrong
                    }
                } else if (location.contains("/api/auth/oauth2/failure")) {
                    oauthStage.close();
                    showAlert("Error", "Failed to sign in with Google", Alert.AlertType.ERROR);
                }
            }
        });

        webEngine.load("http://localhost:8080/oauth2/authorization/google");

        Scene scene = new Scene(webView);
        oauthStage.setScene(scene);
        oauthStage.show();
    }

    private void completeOAuthLogin(String token) {
        ApiClient.setAuthToken(token);
        try {
            var response = ApiClient.get("/oauth2/user");
            if (response.isSuccessful()) {
                Map<String, Object> result = ApiClient.parseResponse(response);
                if (Boolean.TRUE.equals(result.get("success"))) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> user = (Map<String, Object>) result.get("user");
                    if (user != null && user.get("userId") != null) {
                        Long userId = ((Number) user.get("userId")).longValue();
                        ApiClient.setCurrentUserId(userId);
                    }
                    mainController.showGroupScreen();
                    return;
                }
            }
            showAlert("Error", "Failed to get user data after login", Alert.AlertType.ERROR);
        } catch (IOException e) {
            showAlert("Error", "Connection error: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public Parent getRoot() {
        return root;
    }
}

