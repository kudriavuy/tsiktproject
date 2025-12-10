package com.classroom.client.controller;

import com.classroom.client.util.ApiClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GroupController {
    private final BorderPane root;
    private final MainController mainController;
    private final Gson gson = new Gson();
    private FlowPane currentGroupsContainer;
    private boolean isMyGroupsSelected = true;
    private VBox notificationsContainer;

    public GroupController(MainController mainController) {
        this.mainController = mainController;
        this.root = new BorderPane();
        createUI();
        loadMyGroups();
    }

    private BorderPane contentArea;

    private void createUI() {
        // White background
        root.setStyle("-fx-background-color: white;");

        // Top bar - minimalist
        HBox topBar = new HBox(15);
        topBar.setPadding(new Insets(15, 30, 15, 30));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: white; " +
                "-fx-border-color: #e0e0e0; " +
                "-fx-border-width: 0 0 1 0;");

        Label titleLabel = new Label("Classroom App");
        titleLabel.setFont(new Font("System Bold", 20));
        titleLabel.setStyle("-fx-text-fill: #000000; -fx-font-weight: bold;");

        HBox.setHgrow(titleLabel, Priority.ALWAYS);

        Button profileButton = new Button("Profile");
        profileButton.setStyle("-fx-background-color: transparent; " +
                "-fx-text-fill: #000000; " +
                "-fx-font-size: 14px; " +
                "-fx-cursor: hand;");
        profileButton.setOnMouseEntered(e -> profileButton.setStyle(
                "-fx-background-color: #f5f5f5; " +
                        "-fx-text-fill: #000000; " +
                        "-fx-font-size: 14px; " +
                        "-fx-cursor: hand;"));
        profileButton.setOnMouseExited(e -> profileButton.setStyle(
                "-fx-background-color: transparent; " +
                        "-fx-text-fill: #000000; " +
                        "-fx-font-size: 14px; " +
                        "-fx-cursor: hand;"));
        profileButton.setOnAction(e -> {
            Long userId = ApiClient.getCurrentUserId();
            if (userId != null) {
                mainController.showProfileScreen(userId);
            }
        });

        topBar.getChildren().addAll(titleLabel, profileButton);
        root.setTop(topBar);

        // Main layout with sidebar and content
        HBox mainLayout = new HBox();
        mainLayout.setStyle("-fx-background-color: white;");

        // Left sidebar - white
        VBox sidebar = createSidebar();
        sidebar.setPrefWidth(220);

        // Right content area
        contentArea = new BorderPane();
        contentArea.setStyle("-fx-background-color: white;");

        mainLayout.getChildren().addAll(sidebar, contentArea);
        HBox.setHgrow(contentArea, Priority.ALWAYS);

        // Notifications panel
        notificationsContainer = new VBox(10);
        notificationsContainer.setPrefWidth(300);
        notificationsContainer.setPadding(new Insets(20));
        notificationsContainer.setStyle("-fx-background-color: #f5f5f5;");

        HBox notificationsHeader = new HBox(10);
        notificationsHeader.setAlignment(Pos.CENTER_LEFT);

        Label notificationsTitle = new Label("🔔 Notifications");
        notificationsTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #000000;");

        HBox.setHgrow(notificationsTitle, Priority.ALWAYS);
        notificationsHeader.getChildren().add(notificationsTitle);
        notificationsContainer.getChildren().add(notificationsHeader);

        ScrollPane notificationsScroll = new ScrollPane();
        VBox notificationsList = new VBox(10);
        notificationsList.setPadding(new Insets(10, 0, 0, 0));
        notificationsScroll.setContent(notificationsList);
        notificationsScroll.setFitToWidth(true);
        notificationsScroll.setStyle("-fx-background-color: transparent;");
        notificationsContainer.getChildren().add(notificationsScroll);

        root.setCenter(mainLayout);
        root.setRight(notificationsContainer);

        // Load default content
        loadTasksView();
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(0);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: white; " +
                "-fx-border-color: #e0e0e0; " +
                "-fx-border-width: 0 1 0 0;");

        // App title
        Label appTitle = new Label("Classroom App");
        appTitle.setFont(new Font("System Bold", 18));
        appTitle.setStyle("-fx-text-fill: #000000; -fx-font-weight: bold;");
        appTitle.setPadding(new Insets(0, 0, 30, 0));

        // Menu items
        VBox menuItems = new VBox(5);

        Button groupsButton = createSidebarButton("Groups", false);
        Button tasksButton = createSidebarButton("Tasks", true);

        groupsButton.setOnAction(e -> {
            updateSidebarButton(groupsButton, true);
            updateSidebarButton(tasksButton, false);
            loadGroupsView();
        });

        tasksButton.setOnAction(e -> {
            updateSidebarButton(tasksButton, true);
            updateSidebarButton(groupsButton, false);
            loadTasksView();
        });

        menuItems.getChildren().addAll(groupsButton, tasksButton);

        // Sign out at bottom
        VBox.setVgrow(menuItems, Priority.ALWAYS);

        Button signOutButton = new Button("Sign out");
        signOutButton.setPrefWidth(180);
        signOutButton.setPrefHeight(40);
        signOutButton.setAlignment(Pos.CENTER_LEFT);
        signOutButton.setPadding(new Insets(0, 0, 0, 15));
        signOutButton.setStyle("-fx-background-color: transparent; " +
                "-fx-text-fill: #757575; " +
                "-fx-font-size: 14px; " +
                "-fx-cursor: hand;");
        signOutButton.setOnMouseEntered(e -> signOutButton.setStyle(
                "-fx-background-color: #fafafa; " +
                        "-fx-text-fill: #000000; " +
                        "-fx-font-size: 14px; " +
                        "-fx-cursor: hand;"));
        signOutButton.setOnMouseExited(e -> signOutButton.setStyle(
                "-fx-background-color: transparent; " +
                        "-fx-text-fill: #757575; " +
                        "-fx-font-size: 14px; " +
                        "-fx-cursor: hand;"));
        signOutButton.setOnAction(e -> {
            ApiClient.clearAuth();
            mainController.showAuthScreen();
        });

        sidebar.getChildren().addAll(appTitle, menuItems, signOutButton);
        return sidebar;
    }

    private void updateSidebarButton(Button button, boolean isActive) {
        if (isActive) {
            button.setStyle("-fx-background-color: #f5f5f5; " +
                    "-fx-text-fill: #000000; " +
                    "-fx-font-size: 14px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-cursor: hand;");
        } else {
            button.setStyle("-fx-background-color: transparent; " +
                    "-fx-text-fill: #757575; " +
                    "-fx-font-size: 14px; " +
                    "-fx-cursor: hand;");
        }
    }

    private Button createSidebarButton(String text, boolean isActive) {
        Button button = new Button(text);
        button.setPrefWidth(180);
        button.setPrefHeight(40);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setPadding(new Insets(0, 0, 0, 15));

        if (isActive) {
            button.setStyle("-fx-background-color: #f5f5f5; " +
                    "-fx-text-fill: #000000; " +
                    "-fx-font-size: 14px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-cursor: hand;");
        } else {
            button.setStyle("-fx-background-color: transparent; " +
                    "-fx-text-fill: #757575; " +
                    "-fx-font-size: 14px; " +
                    "-fx-cursor: hand;");
        }

        button.setOnMouseEntered(e -> {
            if (!isActive) {
                button.setStyle("-fx-background-color: #fafafa; " +
                        "-fx-text-fill: #000000; " +
                        "-fx-font-size: 14px; " +
                        "-fx-cursor: hand;");
            }
        });
        button.setOnMouseExited(e -> {
            if (!isActive) {
                button.setStyle("-fx-background-color: transparent; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-cursor: hand;");
            }
        });

        return button;
    }

    private void loadGroupsView() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: white;");

        // Header with create button
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label("Groups");
        titleLabel.setFont(new Font("System Bold", 24));
        titleLabel.setStyle("-fx-text-fill: #000000; -fx-font-weight: bold;");

        Button createGroupButton = new Button("Create group");
        createGroupButton.setStyle("-fx-background-color: #000000; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 10 20; " +
                "-fx-background-radius: 4; " +
                "-fx-cursor: hand;");
        createGroupButton.setOnMouseEntered(e -> createGroupButton.setStyle(
                "-fx-background-color: #333333; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 10 20; " +
                        "-fx-background-radius: 4; " +
                        "-fx-cursor: hand;"));
        createGroupButton.setOnMouseExited(e -> createGroupButton.setStyle(
                "-fx-background-color: #000000; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 10 20; " +
                        "-fx-background-radius: 4; " +
                        "-fx-cursor: hand;"));
        createGroupButton.setOnAction(e -> showCreateGroupDialog());

        HBox.setHgrow(titleLabel, Priority.ALWAYS);
        header.getChildren().addAll(titleLabel, createGroupButton);
        content.getChildren().add(header);

        // Buttons for switching between "My Groups" and "All Groups"
        HBox buttonsBox = new HBox(10);
        buttonsBox.setPadding(new Insets(20, 0, 0, 0));

        Button myGroupsButton = new Button("My Groups");
        Button allGroupsButton = new Button("All Groups");

        // Button styling
        updateGroupButton(myGroupsButton, true);
        updateGroupButton(allGroupsButton, false);

        myGroupsButton.setOnAction(e -> {
            updateGroupButton(myGroupsButton, true);
            updateGroupButton(allGroupsButton, false);
            isMyGroupsSelected = true;
            loadMyGroups();
        });

        allGroupsButton.setOnAction(e -> {
            updateGroupButton(allGroupsButton, true);
            updateGroupButton(myGroupsButton, false);
            isMyGroupsSelected = false;
            loadAllGroups();
        });

        buttonsBox.getChildren().addAll(myGroupsButton, allGroupsButton);
        content.getChildren().add(buttonsBox);

        // Groups container
        FlowPane groupsContainer = new FlowPane();
        groupsContainer.setHgap(20);
        groupsContainer.setVgap(20);
        groupsContainer.setPadding(new Insets(20, 0, 0, 0));
        groupsContainer.setStyle("-fx-background-color: white;");
        this.currentGroupsContainer = groupsContainer;

        ScrollPane scrollPane = new ScrollPane(groupsContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: white;");

        VBox mainContent = new VBox(10);
        mainContent.getChildren().addAll(header, buttonsBox, scrollPane);
        mainContent.setStyle("-fx-background-color: white;");

        ScrollPane outerScroll = new ScrollPane(mainContent);
        outerScroll.setFitToWidth(true);
        outerScroll.setStyle("-fx-background-color: white;");

        contentArea.setCenter(outerScroll);

        // Load "My Groups" by default
        isMyGroupsSelected = true;
        loadMyGroups();
    }

    private void loadTasksView() {
        try {
            Long userId = ApiClient.getCurrentUserId();
            if (userId == null) return;

            VBox content = new VBox(30);
            content.setPadding(new Insets(30));
            content.setStyle("-fx-background-color: white;");

            // Tasks section
            VBox tasksSection = createTasksSection(userId);
            content.getChildren().add(tasksSection);

            ScrollPane scrollPane = new ScrollPane(content);
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle("-fx-background-color: white;");

            contentArea.setCenter(scrollPane);
        } catch (Exception e) {
            showAlert("Error", "Failed to load tasks view", Alert.AlertType.ERROR);
        }
    }

    private VBox createTasksSection(Long userId) {
        VBox section = new VBox(20);
        section.setStyle("-fx-background-color: white; " +
                "-fx-border-color: #e8e8e8; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8; " +
                "-fx-padding: 25; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 8, 0, 0, 2);");

        HBox titleBox = new HBox(10);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        Label titleLabel = new Label("📋 Recent Tasks");
        titleLabel.setFont(new Font("System Bold", 20));
        titleLabel.setStyle("-fx-text-fill: #1a1a1a; -fx-font-weight: bold;");
        titleBox.getChildren().add(titleLabel);

        section.getChildren().add(titleBox);

        try {
            // Load user groups
            var groupsResponse = ApiClient.get("/groups/user/" + userId);
            if (!groupsResponse.isSuccessful()) {
                return section;
            }

            String groupsBody = groupsResponse.body() != null ? groupsResponse.body().string() : "[]";
            Type listType = new TypeToken<List<Map<String, Object>>>(){}.getType();
            List<Map<String, Object>> groups = gson.fromJson(groupsBody, listType);

            // Load tasks from all groups
            List<Map<String, Object>> allTasks = new ArrayList<>();
            for (Map<String, Object> group : groups) {
                Long groupId = ((Number) group.get("groupId")).longValue();
                var tasksResponse = ApiClient.get("/tasks/group/" + groupId);
                if (tasksResponse.isSuccessful()) {
                    String tasksBody = tasksResponse.body() != null ? tasksResponse.body().string() : "[]";
                    List<Map<String, Object>> tasks = gson.fromJson(tasksBody, listType);
                    for (Map<String, Object> task : tasks) {
                        task.put("groupName", group.get("name"));
                        task.put("groupId", groupId);
                    }
                    allTasks.addAll(tasks);
                }
            }

            // Limit to 3 tasks
            List<Map<String, Object>> recentTasks = allTasks.stream()
                    .limit(3)
                    .toList();

            // Create task cards
            HBox tasksContainer = new HBox(20);
            tasksContainer.setAlignment(Pos.TOP_LEFT);

            for (Map<String, Object> task : recentTasks) {
                tasksContainer.getChildren().add(createTaskCard(task));
            }

            if (recentTasks.isEmpty()) {
                Label emptyLabel = new Label("No tasks available");
                emptyLabel.setStyle("-fx-text-fill: #757575; -fx-font-size: 14px;");
                tasksContainer.getChildren().add(emptyLabel);
            }

            section.getChildren().add(tasksContainer);
        } catch (IOException e) {
            // Ignore
        }

        return section;
    }

    private VBox createTaskCard(Map<String, Object> task) {
        VBox card = new VBox(12);
        card.setPrefWidth(300);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; " +
                "-fx-border-color: #e8e8e8; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 8, 0, 0, 2);");

        card.setOnMouseEntered(e -> card.setStyle(
                "-fx-background-color: #fafafa; " +
                        "-fx-border-color: #d0d0d0; " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 8; " +
                        "-fx-background-radius: 8; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 12, 0, 0, 3);"));
        card.setOnMouseExited(e -> card.setStyle(
                "-fx-background-color: white; " +
                        "-fx-border-color: #e8e8e8; " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 8; " +
                        "-fx-background-radius: 8; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 8, 0, 0, 2);"));

        // Title
        Label titleLabel = new Label((String) task.get("title"));
        titleLabel.setFont(new Font("System Bold", 16));
        titleLabel.setStyle("-fx-text-fill: #1a1a1a; -fx-font-weight: bold;");
        titleLabel.setWrapText(true);

        // Info, status and deadline
        VBox infoBox = new VBox(5);

        String status = (String) task.getOrDefault("status", "OPEN");
        String statusText = getStatusText(status);
        String statusColor = switch (status) {
            case "COMPLETED" -> "#4caf50";
            case "IN_PROGRESS" -> "#2196f3";
            default -> "#757575";
        };
        Label statusLabel = new Label("● " + statusText);
        statusLabel.setStyle("-fx-text-fill: " + statusColor + "; -fx-font-size: 13px; -fx-font-weight: bold;");

        String deadline = (String) task.getOrDefault("deadline", "");
        if (!deadline.isEmpty()) {
            try {
                String dateStr = deadline.substring(0, 10);
                Label deadlineLabel = new Label("Deadline: " + dateStr);
                deadlineLabel.setStyle("-fx-text-fill: #757575; -fx-font-size: 12px;");
                infoBox.getChildren().add(deadlineLabel);
            } catch (Exception e) {
                // Ignore
            }
        }

        String groupName = (String) task.getOrDefault("groupName", "");
        if (!groupName.isEmpty()) {
            Label groupLabel = new Label("Group: " + groupName);
            groupLabel.setStyle("-fx-text-fill: #757575; -fx-font-size: 12px;");
            infoBox.getChildren().add(groupLabel);
        }

        infoBox.getChildren().add(0, statusLabel);

        card.getChildren().addAll(titleLabel, infoBox);

        // Click handler
        card.setOnMouseClicked(e -> {
            try {
                Long groupId = ((Number) task.get("groupId")).longValue();
                var groupsResponse = ApiClient.get("/groups/" + groupId);
                if (groupsResponse.isSuccessful()) {
                    String body = groupsResponse.body() != null ? groupsResponse.body().string() : "{}";
                    Map<String, Object> group = gson.fromJson(body, new TypeToken<Map<String, Object>>(){}.getType());
                    showGroupDetails(group);
                }
            } catch (IOException ex) {
                // Ignore
            }
        });

        return card;
    }

    private String getStatusText(String status) {
        return switch (status) {
            case "OPEN" -> "Open";
            case "IN_PROGRESS" -> "In Progress";
            case "COMPLETED" -> "Completed";
            default -> "Open";
        };
    }

    private VBox createStatisticsSection(Long userId) {
        VBox section = new VBox(20);
        section.setStyle("-fx-background-color: white; " +
                "-fx-border-color: #e8e8e8; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8; " +
                "-fx-padding: 25; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 8, 0, 0, 2);");

        HBox titleBox = new HBox(10);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        Label titleLabel = new Label("📊 Statistics");
        titleLabel.setFont(new Font("System Bold", 20));
        titleLabel.setStyle("-fx-text-fill: #1a1a1a; -fx-font-weight: bold;");
        titleBox.getChildren().add(titleLabel);

        section.getChildren().add(titleBox);

        try {
            // Load personal statistics
            var statsResponse = ApiClient.get("/statistics/user/" + userId + "/activity");
            if (statsResponse.isSuccessful()) {
                String body = statsResponse.body() != null ? statsResponse.body().string() : "{}";
                Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
                Map<String, Object> stats = gson.fromJson(body, mapType);

                // Display statistics
                VBox statsContainer = new VBox(10);

                @SuppressWarnings("unchecked")
                Map<String, Object> taskStats = (Map<String, Object>) stats.get("taskStatistics");
                if (taskStats != null) {
                    long totalTasks = ((Number) taskStats.getOrDefault("totalTasks", 0)).longValue();
                    long completedTasks = ((Number) taskStats.getOrDefault("completedTasks", 0)).longValue();
                    long openTasks = ((Number) taskStats.getOrDefault("openTasks", 0)).longValue();

                    HBox statsRow1 = new HBox(20);
                    statsRow1.getChildren().addAll(
                            createStatItem("Total Tasks", String.valueOf(totalTasks)),
                            createStatItem("Completed", String.valueOf(completedTasks)),
                            createStatItem("Open", String.valueOf(openTasks))
                    );
                    statsContainer.getChildren().add(statsRow1);
                }

                @SuppressWarnings("unchecked")
                List<Map<String, Object>> recentActivity = (List<Map<String, Object>>) stats.get("recentActivity");
                if (recentActivity != null && !recentActivity.isEmpty()) {
                    Label activityLabel = new Label("Recent Activity:");
                    activityLabel.setStyle("-fx-text-fill: #000000; -fx-font-size: 14px; -fx-font-weight: bold;");
                    activityLabel.setPadding(new Insets(10, 0, 5, 0));
                    statsContainer.getChildren().add(activityLabel);

                    VBox activityList = new VBox(5);
                    for (Map<String, Object> activity : recentActivity.stream().limit(5).toList()) {
                        String action = (String) activity.getOrDefault("action", "");
                        String timestamp = (String) activity.getOrDefault("timestamp", "");
                        Label activityItem = new Label(action + " - " + (timestamp.length() > 10 ? timestamp.substring(0, 10) : timestamp));
                        activityItem.setStyle("-fx-text-fill: #757575; -fx-font-size: 12px;");
                        activityList.getChildren().add(activityItem);
                    }
                    statsContainer.getChildren().add(activityList);
                }

                section.getChildren().add(statsContainer);
            }
        } catch (IOException e) {
            // Ignore
        }

        return section;
    }

    private VBox createStatItem(String label, String value) {
        VBox item = new VBox(8);
        item.setPadding(new Insets(15));
        item.setStyle("-fx-background-color: #f8f9fa; " +
                "-fx-border-color: #e8e8e8; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 6; " +
                "-fx-background-radius: 6; " +
                "-fx-pref-width: 150;");

        Label valueLabel = new Label(value);
        valueLabel.setFont(new Font("System Bold", 28));
        valueLabel.setStyle("-fx-text-fill: #1a1a1a; -fx-font-weight: bold;");

        Label labelLabel = new Label(label);
        labelLabel.setStyle("-fx-text-fill: #757575; -fx-font-size: 13px;");

        item.getChildren().addAll(valueLabel, labelLabel);
        return item;
    }

    private void updateGroupButton(Button button, boolean isActive) {
        if (isActive) {
            button.setStyle("-fx-background-color: #000000; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-size: 14px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-padding: 10 20; " +
                    "-fx-background-radius: 4; " +
                    "-fx-cursor: hand;");
        } else {
            button.setStyle("-fx-background-color: #f5f5f5; " +
                    "-fx-text-fill: #000000; " +
                    "-fx-font-size: 14px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-border-color: #e0e0e0; " +
                    "-fx-border-width: 1; " +
                    "-fx-padding: 10 20; " +
                    "-fx-background-radius: 4; " +
                    "-fx-border-radius: 4; " +
                    "-fx-cursor: hand;");
        }
    }

    private void loadMyGroups() {
        try {
            Long userId = ApiClient.getCurrentUserId();
            if (userId == null) {
                showAlert("Error", "User is not authorized", Alert.AlertType.ERROR);
                return;
            }
            var response = ApiClient.get("/groups/user/" + userId);
            if (response.isSuccessful()) {
                String body = response.body() != null ? response.body().string() : "[]";
                Type listType = new TypeToken<List<Map<String, Object>>>(){}.getType();
                List<Map<String, Object>> groups = gson.fromJson(body, listType);

                if (currentGroupsContainer != null) {
                    currentGroupsContainer.getChildren().clear();

                    for (Map<String, Object> group : groups) {
                        currentGroupsContainer.getChildren().add(createGroupCard(group, true));
                    }
                }
            }
        } catch (IOException e) {
            showAlert("Error", "Failed to load my groups", Alert.AlertType.ERROR);
        }
    }

    private void loadAllGroups() {
        try {
            Long userId = ApiClient.getCurrentUserId();
            if (userId == null) {
                return;
            }

            // Load all groups
            var allGroupsResponse = ApiClient.get("/groups");
            if (!allGroupsResponse.isSuccessful()) {
                return;
            }

            String allGroupsBody = allGroupsResponse.body() != null ? allGroupsResponse.body().string() : "[]";
            Type listType = new TypeToken<List<Map<String, Object>>>(){}.getType();
            List<Map<String, Object>> allGroups = gson.fromJson(allGroupsBody, listType);

            // Load user groups to check membership
            var myGroupsResponse = ApiClient.get("/groups/user/" + userId);
            List<Map<String, Object>> myGroups = List.of();
            if (myGroupsResponse.isSuccessful()) {
                String myGroupsBody = myGroupsResponse.body() != null ? myGroupsResponse.body().string() : "[]";
                myGroups = gson.fromJson(myGroupsBody, listType);
            }

            // Create list of user group IDs
            List<Long> myGroupIds = myGroups.stream()
                    .map(g -> ((Number) g.get("groupId")).longValue())
                    .toList();

            if (currentGroupsContainer != null) {
                currentGroupsContainer.getChildren().clear();

                for (Map<String, Object> group : allGroups) {
                    Long groupId = ((Number) group.get("groupId")).longValue();
                    boolean isMember = myGroupIds.contains(groupId);
                    currentGroupsContainer.getChildren().add(createGroupCard(group, isMember));
                }
            }
        } catch (IOException e) {
            showAlert("Error", "Failed to load all groups", Alert.AlertType.ERROR);
        }
    }

    private VBox createGroupCard(Map<String, Object> group, boolean isMember) {
        VBox card = new VBox(15);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(20));
        card.setPrefWidth(250);
        card.setPrefHeight(280);
        card.setStyle("-fx-background-color: white; " +
                "-fx-border-color: #e8e8e8; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 8, 0, 0, 2);");

        card.setOnMouseEntered(e -> card.setStyle(
                "-fx-background-color: #fafafa; " +
                        "-fx-border-color: #d0d0d0; " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 8; " +
                        "-fx-background-radius: 8; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 12, 0, 0, 3);"));
        card.setOnMouseExited(e -> card.setStyle(
                "-fx-background-color: white; " +
                        "-fx-border-color: #e8e8e8; " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 8; " +
                        "-fx-background-radius: 8; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 8, 0, 0, 2);"));

        // Group name
        Label nameLabel = new Label((String) group.get("name"));
        nameLabel.setFont(new Font("System Bold", 18));
        nameLabel.setStyle("-fx-text-fill: #000000; -fx-font-weight: bold;");
        nameLabel.setAlignment(Pos.CENTER);
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(210);
        nameLabel.setMaxHeight(50);

        // Description (truncated)
        String description = (String) group.getOrDefault("description", "");
        Label descLabel = new Label(description.length() > 80 ? description.substring(0, 80) + "..." : description);
        descLabel.setWrapText(true);
        descLabel.setStyle("-fx-text-fill: #757575; -fx-font-size: 13px;");
        descLabel.setMaxWidth(210);
        descLabel.setMaxHeight(60);

        // Member badge
        VBox badgeContainer = new VBox(5);
        badgeContainer.setAlignment(Pos.CENTER);
        if (isMember) {
            Label memberLabel = new Label("✓ Member");
            memberLabel.setStyle("-fx-text-fill: #4caf50; " +
                    "-fx-font-size: 12px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-padding: 5 12; " +
                    "-fx-background-color: #e8f5e9; " +
                    "-fx-background-radius: 12;");
            badgeContainer.getChildren().add(memberLabel);
        }

        // View button
        Button viewButton = new Button("View →");
        viewButton.setPrefWidth(210);
        viewButton.setStyle("-fx-background-color: #000000; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 10 20; " +
                "-fx-background-radius: 6; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 4, 0, 0, 2);");
        viewButton.setOnMouseEntered(e -> viewButton.setStyle(
                "-fx-background-color: #1a1a1a; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 10 20; " +
                        "-fx-background-radius: 6; " +
                        "-fx-cursor: hand; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 6, 0, 0, 3);"));
        viewButton.setOnMouseExited(e -> viewButton.setStyle(
                "-fx-background-color: #000000; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 10 20; " +
                        "-fx-background-radius: 6; " +
                        "-fx-cursor: hand; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 4, 0, 0, 2);"));
        viewButton.setOnAction(e -> showGroupDetails(group));

        card.getChildren().addAll(nameLabel, descLabel, badgeContainer, viewButton);
        VBox.setVgrow(badgeContainer, Priority.ALWAYS);
        return card;
    }

    private void joinGroup(Map<String, Object> group) {
        try {
            Long userId = ApiClient.getCurrentUserId();
            Long groupId = ((Number) group.get("groupId")).longValue();

            if (userId == null) {
                showAlert("Error", "User is not authorized", Alert.AlertType.ERROR);
                return;
            }

            Map<String, Object> request = Map.of(
                    "userId", userId,
                    "role", "STUDENT"
            );

            var response = ApiClient.post("/groups/" + groupId + "/members", request);
            if (response.isSuccessful()) {
                showAlert("Success", "You have successfully joined the group", Alert.AlertType.INFORMATION);
                // Update currently selected list
                if (isMyGroupsSelected) {
                    // Update currently selected list
                    if (isMyGroupsSelected) {
                        loadMyGroups();
                    } else {
                        loadAllGroups();
                    }
                } else {
                    loadAllGroups();
                }
            } else {
                showAlert("Error", "Failed to join group. Code: " + response.code(), Alert.AlertType.ERROR);
            }
        } catch (IOException e) {
            showAlert("Error", "Connection error: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showCreateGroupDialog() {
        Dialog<Map<String, Object>> dialog = new Dialog<>();
        dialog.setTitle("Create group");
        dialog.setHeaderText("Enter group information");

        Label nameLabel = new Label("Name:");
        nameLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #000000;");
        TextField nameField = new TextField();
        nameField.setPromptText("Group name");
        nameField.setPrefWidth(400);
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

        Label descLabel = new Label("Description:");
        descLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #000000;");
        TextArea descField = new TextArea();
        descField.setPromptText("Group description");
        descField.setPrefRowCount(4);
        descField.setPrefWidth(400);
        descField.setWrapText(true);
        descField.setStyle("-fx-background-color: white; " +
                "-fx-border-color: #e0e0e0; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 4; " +
                "-fx-background-radius: 4; " +
                "-fx-padding: 12 16; " +
                "-fx-font-size: 14px;");
        descField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            descField.setStyle(newVal ?
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

        VBox content = new VBox(16);
        content.setPadding(new Insets(30));
        content.getChildren().addAll(
                nameLabel, nameField,
                descLabel, descField
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                if (nameField.getText().trim().isEmpty()) {
                    showAlert("Error", "Group name cannot be empty", Alert.AlertType.ERROR);
                    return null;
                }
                Long userId = ApiClient.getCurrentUserId();
                if (userId == null) {
                    showAlert("Error", "User not authorized", Alert.AlertType.ERROR);
                    return null;
                }
                return Map.of(
                        "name", nameField.getText().trim(),
                        "description", descField.getText() != null ? descField.getText().trim() : "",
                        "createdBy", userId
                );
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            if (result == null) return;
            try {
                var response = ApiClient.post("/groups", result);
                if (response.isSuccessful()) {
                    showAlert("Success", "Group created successfully", Alert.AlertType.INFORMATION);
                    // Update currently selected list
                    if (isMyGroupsSelected) {
                        loadMyGroups();
                    } else {
                        loadAllGroups();
                    }
                } else {
                    showAlert("Error", "Failed to create group. Code: " + response.code(), Alert.AlertType.ERROR);
                }
            } catch (IOException e) {
                showAlert("Error", "Connection error: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    private boolean checkIfTeacher(Long groupId) {
        try {
            Long userId = ApiClient.getCurrentUserId();
            if (userId == null) return false;

            var response = ApiClient.get("/groups/" + groupId + "/members");
            if (response.isSuccessful()) {
                String body = response.body() != null ? response.body().string() : "[]";
                Type listType = new TypeToken<List<Map<String, Object>>>(){}.getType();
                List<Map<String, Object>> members = gson.fromJson(body, listType);

                return members.stream()
                        .anyMatch(m -> {
                            Long memberUserId = ((Number) m.get("userId")).longValue();
                            String role = (String) m.get("role");
                            return memberUserId.equals(userId) && "TEACHER".equals(role);
                        });
            }
        } catch (IOException e) {
            // Ignore
        }
        return false;
    }

    private void showEditGroupDialog(Map<String, Object> group) {
        Dialog<Map<String, Object>> dialog = new Dialog<>();
        dialog.setTitle("Edit group");
        dialog.setHeaderText("Update group information");

        Label nameLabel = new Label("Name:");
        nameLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #000000;");
        TextField nameField = new TextField();
        nameField.setText((String) group.get("name"));
        nameField.setPromptText("Group name");
        nameField.setPrefWidth(400);
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

        Label descLabel = new Label("Description:");
        descLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #000000;");
        TextArea descField = new TextArea();
        descField.setText((String) group.getOrDefault("description", ""));
        descField.setPromptText("Group description");
        descField.setPrefRowCount(4);
        descField.setPrefWidth(400);
        descField.setWrapText(true);
        descField.setStyle("-fx-background-color: white; " +
                "-fx-border-color: #e0e0e0; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 4; " +
                "-fx-background-radius: 4; " +
                "-fx-padding: 12 16; " +
                "-fx-font-size: 14px;");
        descField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            descField.setStyle(newVal ?
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

        VBox content = new VBox(16);
        content.setPadding(new Insets(30));
        content.getChildren().addAll(
                nameLabel, nameField,
                descLabel, descField
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                if (nameField.getText().trim().isEmpty()) {
                    showAlert("Error", "Group name cannot be empty", Alert.AlertType.ERROR);
                    return null;
                }
                Long userId = ApiClient.getCurrentUserId();
                if (userId == null) {
                    showAlert("Error", "User not authorized", Alert.AlertType.ERROR);
                    return null;
                }
                Map<String, Object> request = new java.util.HashMap<>();
                request.put("userId", userId);
                request.put("name", nameField.getText().trim());
                request.put("description", descField.getText() != null ? descField.getText().trim() : "");
                return request;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            if (result == null) return;
            try {
                Long groupId = ((Number) group.get("groupId")).longValue();
                var response = ApiClient.put("/groups/" + groupId, result);
                if (response.isSuccessful()) {
                    showAlert("Success", "Group updated successfully", Alert.AlertType.INFORMATION);
                    // Update currently selected list
                    if (isMyGroupsSelected) {
                        loadMyGroups();
                    } else {
                        loadAllGroups();
                    }
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "";
                    showAlert("Error", "Failed to update group. Code: " + response.code() +
                            (errorBody.isEmpty() ? "" : "\n" + errorBody), Alert.AlertType.ERROR);
                }
            } catch (IOException e) {
                showAlert("Error", "Connection error: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    private void showGroupDetails(Map<String, Object> group) {
        GroupDetailsController detailsController = new GroupDetailsController(mainController, group);
        StackPane mainRoot = (StackPane) mainController.getRoot();
        mainRoot.getChildren().clear();
        mainRoot.getChildren().add(detailsController.getRoot());
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void showNotification(String type, String message) {
        Platform.runLater(() -> {
            if (notificationsContainer == null) return;

            ScrollPane scrollPane = (ScrollPane) notificationsContainer.getChildren().get(1);
            VBox notificationsList = (VBox) scrollPane.getContent();

            VBox notificationCard = new VBox(5);
            notificationCard.setPadding(new Insets(12));
            notificationCard.setStyle("-fx-background-color: white; " +
                    "-fx-border-color: #e0e0e0; " +
                    "-fx-border-width: 1; " +
                    "-fx-border-radius: 4; " +
                    "-fx-background-radius: 4;");

            String emoji = switch (type) {
                case "TASK_CREATED" -> "📝";
                case "COMMENT_ADDED" -> "💬";
                case "MEMBER_ADDED" -> "👤";
                default -> "🔔";
            };

            Label typeLabel = new Label(emoji + " " + type);
            typeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #757575;");

            Label messageLabel = new Label(message);
            messageLabel.setWrapText(true);
            messageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #000000;");

            notificationCard.getChildren().addAll(typeLabel, messageLabel);
            notificationsList.getChildren().add(0, notificationCard);

            // Limit number of notifications
            if (notificationsList.getChildren().size() > 10) {
                notificationsList.getChildren().remove(notificationsList.getChildren().size() - 1);
            }
        });
    }

    public Parent getRoot() {
        return root;
    }
}

