package com.classroom.client.controller;

import com.classroom.client.util.ApiClient;
import com.classroom.client.util.WebSocketClient;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.Map;

public class MainController {
    private final StackPane root;
    private final Stage stage;
    private AuthController authController;
    private GroupController groupController;

    public MainController(Stage stage) {
        this.stage = stage;
        this.root = new StackPane();

        // Check if there is a saved token
        if (ApiClient.isAuthenticated()) {
            // Token is already loaded and verified in ApiClient
            connectWebSocket();
            showGroupScreen();
        } else {
            showAuthScreen();
        }
    }

    private void connectWebSocket() {
        WebSocketClient.connect(this::handleNotification);
    }

    private void handleNotification(Map<String, Object> notification) {
        // Handle notifications
        System.out.println("📨 Received notification: " + notification);

        String type = (String) notification.get("type");
        String message = (String) notification.get("message");

        if (type == null) type = "UNKNOWN";
        if (message == null) message = "No message";

        if (groupController != null) {
            groupController.showNotification(type, message);
        } else {
            System.out.println("⚠️ groupController is null, notification will be lost");
        }
    }

    public Parent getRoot() {
        return root;
    }

    public void showAuthScreen() {
        authController = new AuthController(this);
        root.getChildren().clear();
        root.getChildren().add(authController.getRoot());
    }

    public void showGroupScreen() {
        // Connect WebSocket before showing screen
        if (!WebSocketClient.isConnected()) {
            connectWebSocket();
        }
        groupController = new GroupController(this);
        root.getChildren().clear();
        root.getChildren().add(groupController.getRoot());
    }

    public void showProfileScreen(Long userId) {
        ProfileController profileController = new ProfileController(this, userId);
        root.getChildren().clear();
        root.getChildren().add(profileController.getRoot());
    }

    public Stage getStage() {
        return stage;
    }
}

