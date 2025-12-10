package com.classroom.client.controller;

import com.classroom.client.util.ApiClient;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Path;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.ClosePath;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class ProfileController {
    private final VBox root;
    private final MainController mainController;
    private final Long userId;
    private final boolean isOwnProfile;
    private TextField nameField;
    private TextArea bioField;
    private TextField phoneField;
    private ImageView photoView;
    private Map<String, Object> currentUser;

    public ProfileController(MainController mainController, Long userId) {
        this.mainController = mainController;
        this.userId = userId;
        Long currentUserId = ApiClient.getCurrentUserId();
        this.isOwnProfile = currentUserId != null && currentUserId.equals(userId);
        this.root = new VBox(0);
        root.setStyle("-fx-background-color: white;");

        createUI();
        loadProfile();
    }

    private void createUI() {
        // Top bar - minimalist
        HBox topBar = new HBox(15);
        topBar.setPadding(new Insets(15, 30, 15, 30));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: white; " +
                "-fx-border-color: #e0e0e0; " +
                "-fx-border-width: 0 0 1 0;");

        Button backButton = new Button("← Back");
        backButton.setStyle("-fx-background-color: transparent; " +
                "-fx-text-fill: #000000; " +
                "-fx-font-size: 14px; " +
                "-fx-cursor: hand;");
        backButton.setOnAction(e -> mainController.showGroupScreen());

        Label titleLabel = new Label("Profile");
        titleLabel.setFont(new Font("System Bold", 20));
        titleLabel.setStyle("-fx-text-fill: #000000; -fx-font-weight: bold;");

        HBox.setHgrow(titleLabel, Priority.ALWAYS);
        topBar.getChildren().addAll(backButton, titleLabel);

        // Main content - centered container with border (like login screen)
        VBox content = new VBox(24);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(40, 50, 40, 50));
        content.setMaxWidth(500);
        content.setStyle("-fx-background-color: white; " +
                "-fx-border-color: #e0e0e0; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 0;");

        // Photo section
        VBox photoSection = new VBox(15);
        photoSection.setAlignment(Pos.CENTER);

        photoView = new ImageView();
        photoView.setFitWidth(120);
        photoView.setFitHeight(120);
        photoView.setPreserveRatio(true);
        photoView.setStyle("-fx-background-color: #e0e0e0; " +
                "-fx-border-color: #e0e0e0; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 0; " +
                "-fx-background-radius: 0;");

        HBox photoButtons = new HBox(10);
        photoButtons.setAlignment(Pos.CENTER);

        if (isOwnProfile) {
            Button uploadPhotoButton = new Button("Upload photo");
            uploadPhotoButton.setStyle("-fx-background-color: #000000; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-size: 14px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-padding: 10 20; " +
                    "-fx-background-radius: 4; " +
                    "-fx-cursor: hand;");
            uploadPhotoButton.setOnMouseEntered(e -> uploadPhotoButton.setStyle(
                    "-fx-background-color: #333333; " +
                            "-fx-text-fill: white; " +
                            "-fx-font-size: 14px; " +
                            "-fx-font-weight: bold; " +
                            "-fx-padding: 10 20; " +
                            "-fx-background-radius: 4; " +
                            "-fx-cursor: hand;"));
            uploadPhotoButton.setOnMouseExited(e -> uploadPhotoButton.setStyle(
                    "-fx-background-color: #000000; " +
                            "-fx-text-fill: white; " +
                            "-fx-font-size: 14px; " +
                            "-fx-font-weight: bold; " +
                            "-fx-padding: 10 20; " +
                            "-fx-background-radius: 4; " +
                            "-fx-cursor: hand;"));
            uploadPhotoButton.setOnAction(e -> handleUploadPhoto());

            Button deletePhotoButton = new Button("Delete");
            deletePhotoButton.setStyle("-fx-background-color: #f5f5f5; " +
                    "-fx-text-fill: #000000; " +
                    "-fx-font-size: 14px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-border-color: #e0e0e0; " +
                    "-fx-border-width: 1; " +
                    "-fx-padding: 10 20; " +
                    "-fx-background-radius: 4; " +
                    "-fx-border-radius: 4; " +
                    "-fx-cursor: hand;");
            deletePhotoButton.setOnMouseEntered(e -> deletePhotoButton.setStyle(
                    "-fx-background-color: #eeeeee; " +
                            "-fx-text-fill: #000000; " +
                            "-fx-font-size: 14px; " +
                            "-fx-font-weight: bold; " +
                            "-fx-border-color: #e0e0e0; " +
                            "-fx-border-width: 1; " +
                            "-fx-padding: 10 20; " +
                            "-fx-background-radius: 4; " +
                            "-fx-border-radius: 4; " +
                            "-fx-cursor: hand;"));
            deletePhotoButton.setOnMouseExited(e -> deletePhotoButton.setStyle(
                    "-fx-background-color: #f5f5f5; " +
                            "-fx-text-fill: #000000; " +
                            "-fx-font-size: 14px; " +
                            "-fx-font-weight: bold; " +
                            "-fx-border-color: #e0e0e0; " +
                            "-fx-border-width: 1; " +
                            "-fx-padding: 10 20; " +
                            "-fx-background-radius: 4; " +
                            "-fx-border-radius: 4; " +
                            "-fx-cursor: hand;"));
            deletePhotoButton.setOnAction(e -> handleDeletePhoto());

            photoButtons.getChildren().addAll(uploadPhotoButton, deletePhotoButton);
        }

        // Photo wrapper with placeholder display capability
        StackPane photoWrapper = new StackPane();
        photoWrapper.setPrefWidth(120);
        photoWrapper.setPrefHeight(120);

        // Show placeholder by default
        StackPane defaultPlaceholder = createPlaceholderIcon();
        photoWrapper.getChildren().add(defaultPlaceholder);

        // Store reference to wrapper for later use
        photoView.setUserData(photoWrapper);

        photoSection.getChildren().addAll(photoWrapper);
        if (isOwnProfile) {
            photoSection.getChildren().add(photoButtons);
        }

        // Form fields - minimalist
        Label nameLabel = new Label("Name:");
        nameLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #000000;");
        nameField = new TextField();
        nameField.setPrefWidth(400);
        nameField.setPrefHeight(48);
        nameField.setEditable(isOwnProfile);
        if (isOwnProfile) {
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
        } else {
            nameField.setStyle("-fx-background-color: #f5f5f5; " +
                    "-fx-border-color: #e0e0e0; " +
                    "-fx-border-width: 1; " +
                    "-fx-border-radius: 4; " +
                    "-fx-background-radius: 4; " +
                    "-fx-padding: 12 16; " +
                    "-fx-font-size: 14px;");
        }

        Label bioLabel = new Label("Bio:");
        bioLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #000000;");
        bioField = new TextArea();
        bioField.setPrefWidth(400);
        bioField.setPrefRowCount(4);
        bioField.setWrapText(true);
        bioField.setEditable(isOwnProfile);
        if (isOwnProfile) {
            bioField.setStyle("-fx-background-color: white; " +
                    "-fx-border-color: #e0e0e0; " +
                    "-fx-border-width: 1; " +
                    "-fx-border-radius: 4; " +
                    "-fx-background-radius: 4; " +
                    "-fx-padding: 12 16; " +
                    "-fx-font-size: 14px;");
            bioField.focusedProperty().addListener((obs, oldVal, newVal) -> {
                bioField.setStyle(newVal ?
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
        } else {
            bioField.setStyle("-fx-background-color: #f5f5f5; " +
                    "-fx-border-color: #e0e0e0; " +
                    "-fx-border-width: 1; " +
                    "-fx-border-radius: 4; " +
                    "-fx-background-radius: 4; " +
                    "-fx-padding: 12 16; " +
                    "-fx-font-size: 14px;");
        }

        Label phoneLabel = new Label("Phone:");
        phoneLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #000000;");
        phoneField = new TextField();
        phoneField.setPrefWidth(400);
        phoneField.setPrefHeight(48);
        phoneField.setEditable(isOwnProfile);
        if (isOwnProfile) {
            phoneField.setStyle("-fx-background-color: white; " +
                    "-fx-border-color: #e0e0e0; " +
                    "-fx-border-width: 1; " +
                    "-fx-border-radius: 4; " +
                    "-fx-background-radius: 4; " +
                    "-fx-padding: 12 16; " +
                    "-fx-font-size: 14px;");
            phoneField.focusedProperty().addListener((obs, oldVal, newVal) -> {
                phoneField.setStyle(newVal ?
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
        } else {
            phoneField.setStyle("-fx-background-color: #f5f5f5; " +
                    "-fx-border-color: #e0e0e0; " +
                    "-fx-border-width: 1; " +
                    "-fx-border-radius: 4; " +
                    "-fx-background-radius: 4; " +
                    "-fx-padding: 12 16; " +
                    "-fx-font-size: 14px;");
        }

        // Save button (only for own profile)
        if (isOwnProfile) {
            Button saveButton = new Button("Save changes");
            saveButton.setPrefWidth(400);
            saveButton.setPrefHeight(48);
            saveButton.setStyle("-fx-background-color: #000000; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-size: 14px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-background-radius: 4; " +
                    "-fx-cursor: hand;");
            saveButton.setOnMouseEntered(e -> saveButton.setStyle(
                    "-fx-background-color: #333333; " +
                            "-fx-text-fill: white; " +
                            "-fx-font-size: 14px; " +
                            "-fx-font-weight: bold; " +
                            "-fx-background-radius: 4; " +
                            "-fx-cursor: hand;"));
            saveButton.setOnMouseExited(e -> saveButton.setStyle(
                    "-fx-background-color: #000000; " +
                            "-fx-text-fill: white; " +
                            "-fx-font-size: 14px; " +
                            "-fx-font-weight: bold; " +
                            "-fx-background-radius: 4; " +
                            "-fx-cursor: hand;"));
            saveButton.setOnAction(e -> handleSaveProfile());
            content.getChildren().addAll(
                    photoSection,
                    nameLabel, nameField,
                    bioLabel, bioField,
                    phoneLabel, phoneField,
                    saveButton
            );
        } else {
            // Read-only fields for viewing other users
            nameField.setEditable(false);
            bioField.setEditable(false);
            phoneField.setEditable(false);

            content.getChildren().addAll(
                    photoSection,
                    nameLabel, nameField,
                    bioLabel, bioField,
                    phoneLabel, phoneField
            );
        }

        // Center content
        VBox wrapper = new VBox();
        wrapper.setAlignment(Pos.CENTER);
        wrapper.setStyle("-fx-background-color: white;");
        wrapper.getChildren().add(content);

        root.getChildren().addAll(topBar, wrapper);
        VBox.setVgrow(wrapper, Priority.ALWAYS);
    }

    private void loadProfile() {
        try {
            var response = ApiClient.get("/users/" + userId + "/profile");
            if (response.isSuccessful()) {
                currentUser = ApiClient.parseResponse(response);

                if (currentUser != null) {
                    String name = getString(currentUser, "name");
                    String bio = getString(currentUser, "bio");
                    String phone = getString(currentUser, "phone");

                    nameField.setText(name);
                    bioField.setText(bio);
                    phoneField.setText(phone);

                    String photoUrl = getString(currentUser, "profilePhotoUrl");
                    if (photoUrl != null && !photoUrl.isEmpty()) {
                        loadPhoto(photoUrl);
                    } else {
                        showPlaceholder();
                    }
                }
            } else {
                showAlert("Error", "Failed to load profile", Alert.AlertType.ERROR);
            }
        } catch (IOException e) {
            showAlert("Error", "Connection error: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadPhoto(String photoUrl) {
        try {
            if (photoUrl == null || photoUrl.isEmpty()) {
                showPlaceholder();
                return;
            }

            String fullUrl;
            if (photoUrl.startsWith("http://") || photoUrl.startsWith("https://")) {
                fullUrl = photoUrl;
            } else {
                // Add base URL if it's a relative path
                fullUrl = "http://localhost:8080" + (photoUrl.startsWith("/") ? photoUrl : "/" + photoUrl);
            }

            System.out.println("Loading photo from: " + fullUrl);

            Image image = new Image(fullUrl, true);

            // Error handling for loading
            image.errorProperty().addListener((obs, wasError, isError) -> {
                if (isError) {
                    System.err.println("Error loading image: " + fullUrl);
                    showPlaceholder();
                } else if (!wasError && !isError) {
                    // Image successfully loaded
                    showPhoto(image);
                }
            });

            // Check loading state after a short delay
            javafx.application.Platform.runLater(() -> {
                if (image.isError()) {
                    showPlaceholder();
                } else if (image.getProgress() == 1.0 || image.getWidth() > 0) {
                    showPhoto(image);
                }
            });
        } catch (Exception e) {
            System.err.println("Exception loading photo: " + e.getMessage());
            e.printStackTrace();
            showPlaceholder();
        }
    }

    private void showPhoto(Image image) {
        photoView.setImage(image);
        StackPane photoWrapper = (StackPane) photoView.getUserData();
        if (photoWrapper != null) {
            photoWrapper.getChildren().clear();
            photoWrapper.getChildren().add(photoView);
        }
    }

    private StackPane createPlaceholderIcon() {
        // Create placeholder icon (white circle with dark gray silhouette)
        StackPane placeholderPane = new StackPane();
        placeholderPane.setPrefWidth(120);
        placeholderPane.setPrefHeight(120);

        // White circle with border
        Circle backgroundCircle = new Circle(60);
        backgroundCircle.setFill(Color.WHITE);
        backgroundCircle.setStroke(Color.web("#757575"));
        backgroundCircle.setStrokeWidth(1);

        // Head (small circle on top)
        Circle head = new Circle(18);
        head.setFill(Color.web("#424242"));
        head.setTranslateY(-20);

        // Shoulders (large inverted U-shape)
        Path shoulders = new Path();
        shoulders.getElements().add(new MoveTo(-35, 10));
        shoulders.getElements().add(new ArcTo(35, 25, 0, 35, 10, false, true));
        shoulders.getElements().add(new LineTo(35, 25));
        shoulders.getElements().add(new LineTo(-35, 25));
        shoulders.getElements().add(new ClosePath());
        shoulders.setFill(Color.web("#424242"));
        shoulders.setTranslateY(15);

        placeholderPane.getChildren().addAll(backgroundCircle, shoulders, head);
        return placeholderPane;
    }

    private void showPlaceholder() {
        // Clear ImageView and show placeholder
        photoView.setImage(null);

        // Find wrapper and replace content
        StackPane photoWrapper = (StackPane) photoView.getUserData();
        if (photoWrapper != null) {
            photoWrapper.getChildren().clear();
            photoWrapper.getChildren().add(createPlaceholderIcon());
        }
    }

    private void handleSaveProfile() {
        try {
            Map<String, Object> request = Map.of(
                    "name", nameField.getText(),
                    "bio", bioField.getText() != null ? bioField.getText() : "",
                    "phone", phoneField.getText() != null ? phoneField.getText() : ""
            );

            var response = ApiClient.put("/users/" + userId + "/profile", request);
            Map<String, Object> result = ApiClient.parseResponse(response);

            if (Boolean.TRUE.equals(result.get("success"))) {
                showAlert("Success", "Profile updated", Alert.AlertType.INFORMATION);
                loadProfile(); // Reload profile
            } else {
                showAlert("Error", (String) result.getOrDefault("message", "Update failed"), Alert.AlertType.ERROR);
            }
        } catch (IOException e) {
            showAlert("Error", "Connection error", Alert.AlertType.ERROR);
        }
    }

    private void handleUploadPhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select profile photo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        Stage stage = mainController.getStage();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try {
                var response = ApiClient.uploadFile("/users/" + userId + "/profile/photo", file);
                Map<String, Object> result = ApiClient.parseResponse(response);

                if (Boolean.TRUE.equals(result.get("success"))) {
                    showAlert("Success", "Photo uploaded", Alert.AlertType.INFORMATION);
                    loadProfile(); // Reload profile
                } else {
                    showAlert("Error", (String) result.getOrDefault("message", "Upload failed"), Alert.AlertType.ERROR);
                }
            } catch (IOException e) {
                showAlert("Error", "File upload error: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private void handleDeletePhoto() {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation");
        confirmAlert.setHeaderText("Delete profile photo?");
        confirmAlert.setContentText("This action cannot be undone.");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    var response2 = ApiClient.delete("/users/" + userId + "/profile/photo");
                    Map<String, Object> result = ApiClient.parseResponse(response2);

                    if (Boolean.TRUE.equals(result.get("success"))) {
                        showAlert("Success", "Photo deleted", Alert.AlertType.INFORMATION);
                        loadProfile(); // Reload profile
                    } else {
                        showAlert("Error", (String) result.getOrDefault("message", "Delete failed"), Alert.AlertType.ERROR);
                    }
                } catch (IOException e) {
                    showAlert("Error", "Connection error", Alert.AlertType.ERROR);
                }
            }
        });
    }

    private String getString(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : "";
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

