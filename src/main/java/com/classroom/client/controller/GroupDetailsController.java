package com.classroom.client.controller;

import com.classroom.client.util.ApiClient;
import com.classroom.client.util.Styles;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.Circle;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupDetailsController {
    private final BorderPane root;
    private final MainController mainController;
    private final Map<String, Object> group;
    private final Gson gson = new Gson();

    public GroupDetailsController(MainController mainController, Map<String, Object> group) {
        this.mainController = mainController;
        this.group = group;
        this.root = new BorderPane();
        createUI();
        loadData();
    }

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

        Button backButton = new Button("← Back");
        backButton.setStyle("-fx-background-color: transparent; " +
                "-fx-text-fill: #000000; " +
                "-fx-font-size: 14px; " +
                "-fx-cursor: hand;");
        backButton.setOnAction(e -> {
            StackPane mainRoot = (StackPane) mainController.getRoot();
            GroupController groupController = new GroupController(mainController);
            mainRoot.getChildren().clear();
            mainRoot.getChildren().add(groupController.getRoot());
        });

        Label titleLabel = new Label((String) group.get("name"));
        titleLabel.setFont(new Font("System Bold", 20));
        titleLabel.setStyle("-fx-text-fill: #000000; -fx-font-weight: bold;");

        HBox.setHgrow(titleLabel, Priority.ALWAYS);
        topBar.getChildren().addAll(backButton, titleLabel);

        // Check if user is a member
        Long groupId = ((Number) group.get("groupId")).longValue();
        boolean isMember = checkIfMember(groupId);

        if (!isMember) {
            Button joinButton = new Button("Join");
            joinButton.setStyle("-fx-background-color: #000000; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-size: 14px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-padding: 10 20; " +
                    "-fx-background-radius: 4; " +
                    "-fx-cursor: hand;");
            joinButton.setOnMouseEntered(e -> joinButton.setStyle(
                    "-fx-background-color: #333333; " +
                            "-fx-text-fill: white; " +
                            "-fx-font-size: 14px; " +
                            "-fx-font-weight: bold; " +
                            "-fx-padding: 10 20; " +
                            "-fx-background-radius: 4; " +
                            "-fx-cursor: hand;"));
            joinButton.setOnMouseExited(e -> joinButton.setStyle(
                    "-fx-background-color: #000000; " +
                            "-fx-text-fill: white; " +
                            "-fx-font-size: 14px; " +
                            "-fx-font-weight: bold; " +
                            "-fx-padding: 10 20; " +
                            "-fx-background-radius: 4; " +
                            "-fx-cursor: hand;"));
            joinButton.setOnAction(e -> joinGroup());
            topBar.getChildren().add(joinButton);
        }

        root.setTop(topBar);

        // Main layout with sidebar and content
        HBox mainLayout = new HBox();
        mainLayout.setStyle("-fx-background-color: white;");

        // Left sidebar
        VBox sidebar = createSidebar();
        sidebar.setPrefWidth(220);

        // Right content area
        BorderPane contentArea = new BorderPane();
        contentArea.setStyle("-fx-background-color: white;");

        mainLayout.getChildren().addAll(sidebar, contentArea);
        HBox.setHgrow(contentArea, Priority.ALWAYS);

        root.setCenter(mainLayout);

        // Store reference to content area for updates
        this.contentArea = contentArea;
    }

    private BorderPane contentArea;

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

        Button tasksButton = createSidebarButton("Tasks", true);
        tasksButton.setOnAction(e -> showTasksView());

        Button membersButton = createSidebarButton("Members", false);
        membersButton.setOnAction(e -> showMembersView());

        Button statisticsButton = createSidebarButton("Statistics", false);
        statisticsButton.setOnAction(e -> showStatisticsView());

        menuItems.getChildren().addAll(tasksButton, membersButton, statisticsButton);

        sidebar.getChildren().addAll(appTitle, menuItems);
        return sidebar;
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
                        "-fx-text-fill: #757575; " +
                        "-fx-font-size: 14px; " +
                        "-fx-cursor: hand;");
            }
        });

        return button;
    }

    private void showTasksView() {
        loadTasks();
    }

    private void showMembersView() {
        loadMembers();
    }

    private void showStatisticsView() {
        loadStatistics();
    }


    private void loadData() {
        loadTasks();
        loadMembers();
        loadStatistics();
    }

    private void loadStatistics() {
        try {
            Long groupId = ((Number) group.get("groupId")).longValue();
            var response = ApiClient.get("/statistics/group/" + groupId + "/overall");
            if (response.isSuccessful()) {
                String body = response.body() != null ? response.body().string() : "{}";
                Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
                Map<String, Object> stats = gson.fromJson(body, mapType);

                VBox content = new VBox(20);
                content.setPadding(new Insets(30));
                content.setStyle("-fx-background-color: white;");

                // Header
                Label titleLabel = new Label("Statistics");
                titleLabel.setFont(new Font("System Bold", 20));
                titleLabel.setStyle("-fx-text-fill: #000000; -fx-font-weight: bold;");
                content.getChildren().add(titleLabel);

                // Task statistics
                @SuppressWarnings("unchecked")
                Map<String, Object> taskStats = (Map<String, Object>) stats.get("taskStatistics");
                if (taskStats != null) {
                    content.getChildren().add(createTaskStatisticsSection(taskStats));
                }

                // Engagement statistics
                @SuppressWarnings("unchecked")
                Map<String, Object> engagementStats = (Map<String, Object>) stats.get("engagementStatistics");
                if (engagementStats != null) {
                    content.getChildren().add(createEngagementSection(engagementStats));
                }

                ScrollPane scrollPane = new ScrollPane(content);
                scrollPane.setFitToWidth(true);
                scrollPane.setStyle("-fx-background-color: white;");

                contentArea.setCenter(scrollPane);
            }
        } catch (IOException e) {
            // Ignore errors
        }
    }

    private VBox createTaskStatisticsSection(Map<String, Object> taskStats) {
        VBox section = new VBox(15);
        section.setPadding(new Insets(20));
        section.setStyle(Styles.card());

        Label title = new Label("📝 Task Statistics");
        title.setFont(new Font(18));
        title.setStyle(Styles.headingLabel());

        // Number cards
        HBox statsCards = new HBox(15);
        statsCards.setAlignment(Pos.CENTER);

        long totalTasks = ((Number) taskStats.getOrDefault("totalTasks", 0)).longValue();
        long openTasks = ((Number) taskStats.getOrDefault("openTasks", 0)).longValue();
        long inProgressTasks = ((Number) taskStats.getOrDefault("inProgressTasks", 0)).longValue();
        long completedTasks = ((Number) taskStats.getOrDefault("completedTasks", 0)).longValue();

        statsCards.getChildren().addAll(
                createStatCard("Total", String.valueOf(totalTasks), Styles.PRIMARY_COLOR),
                createStatCard("Open", String.valueOf(openTasks), "#ff9800"),
                createStatCard("In Progress", String.valueOf(inProgressTasks), "#2196f3"),
                createStatCard("Completed", String.valueOf(completedTasks), Styles.SECONDARY_COLOR)
        );

        // Task status chart
        PieChart statusChart = createTaskStatusChart(openTasks, inProgressTasks, completedTasks);

        // Task creation chart by dates
        @SuppressWarnings("unchecked")
        Map<String, Object> tasksByDateObj = (Map<String, Object>) taskStats.get("tasksByDate");
        Map<String, Long> tasksByDate = new HashMap<>();
        if (tasksByDateObj != null) {
            tasksByDateObj.forEach((k, v) -> tasksByDate.put(k, ((Number) v).longValue()));
        }
        LineChart<String, Number> tasksChart = createTasksByDateChart(tasksByDate);

        // Task completion chart
        @SuppressWarnings("unchecked")
        Map<String, Object> completedByDateObj = (Map<String, Object>) taskStats.get("completedByDate");
        Map<String, Long> completedByDate = new HashMap<>();
        if (completedByDateObj != null) {
            completedByDateObj.forEach((k, v) -> completedByDate.put(k, ((Number) v).longValue()));
        }
        LineChart<String, Number> completedChart = createCompletedByDateChart(completedByDate);

        section.getChildren().addAll(title, statsCards, statusChart, tasksChart, completedChart);
        return section;
    }

    private VBox createEngagementSection(Map<String, Object> engagementStats) {
        VBox section = new VBox(15);
        section.setPadding(new Insets(20));
        section.setStyle(Styles.card());

        Label title = new Label("👥 Participant Engagement");
        title.setFont(new Font(18));
        title.setStyle(Styles.headingLabel());

        // Number cards
        HBox statsCards = new HBox(15);
        statsCards.setAlignment(Pos.CENTER);

        long totalMembers = ((Number) engagementStats.getOrDefault("totalMembers", 0)).longValue();
        long teachers = ((Number) engagementStats.getOrDefault("teachers", 0)).longValue();
        long students = ((Number) engagementStats.getOrDefault("students", 0)).longValue();
        long activeMembers = ((Number) engagementStats.getOrDefault("activeMembers", 0)).longValue();
        double engagementRate = ((Number) engagementStats.getOrDefault("engagementRate", 0)).doubleValue();

        statsCards.getChildren().addAll(
                createStatCard("Total Members", String.valueOf(totalMembers), Styles.PRIMARY_COLOR),
                createStatCard("Teachers", String.valueOf(teachers), "#9c27b0"),
                createStatCard("Students", String.valueOf(students), "#00bcd4"),
                createStatCard("Active", String.valueOf(activeMembers), Styles.SECONDARY_COLOR),
                createStatCard("Engagement", String.format("%.1f%%", engagementRate), "#ff9800")
        );

        // Engagement chart
        PieChart engagementChart = createEngagementChart(teachers, students);

        // Task completion chart by participants
        @SuppressWarnings("unchecked")
        Map<String, Object> memberTaskCountObj = (Map<String, Object>) engagementStats.get("memberTaskCount");
        Map<String, Long> memberTaskCount = new HashMap<>();
        if (memberTaskCountObj != null) {
            memberTaskCountObj.forEach((k, v) -> memberTaskCount.put(k, ((Number) v).longValue()));
        }
        BarChart<String, Number> memberChart = createMemberTaskChart(memberTaskCount);

        section.getChildren().addAll(title, statsCards, engagementChart, memberChart);
        return section;
    }

    private VBox createStatCard(String label, String value, String color) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(15));
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: white; " +
                "-fx-border-color: " + color + "; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        card.setPrefWidth(150);

        Label valueLabel = new Label(value);
        valueLabel.setFont(new Font(24));
        valueLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");

        Label nameLabel = new Label(label);
        nameLabel.setFont(new Font(12));
        nameLabel.setStyle("-fx-text-fill: " + Styles.TEXT_SECONDARY + ";");

        card.getChildren().addAll(valueLabel, nameLabel);
        return card;
    }

    private PieChart createTaskStatusChart(long open, long inProgress, long completed) {
        PieChart chart = new PieChart();
        chart.setTitle("Task Statuses");
        chart.setPrefHeight(300);

        if (open > 0) {
            chart.getData().add(new PieChart.Data("Open (" + open + ")", open));
        }
        if (inProgress > 0) {
            chart.getData().add(new PieChart.Data("In Progress (" + inProgress + ")", inProgress));
        }
        if (completed > 0) {
            chart.getData().add(new PieChart.Data("Completed (" + completed + ")", completed));
        }

        return chart;
    }

    private LineChart<String, Number> createTasksByDateChart(Map<String, Long> tasksByDate) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Date");
        yAxis.setLabel("Number of Tasks");

        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Task Creation (Last 30 Days)");
        chart.setPrefHeight(300);
        chart.setLegendVisible(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        if (tasksByDate != null) {
            tasksByDate.forEach((date, count) -> {
                String shortDate = date.length() > 5 ? date.substring(5) : date; // YYYY-MM-DD -> MM-DD
                series.getData().add(new XYChart.Data<>(shortDate, count));
            });
        }
        chart.getData().add(series);

        return chart;
    }

    private LineChart<String, Number> createCompletedByDateChart(Map<String, Long> completedByDate) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Date");
        yAxis.setLabel("Number Completed");

        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Task Completion (Last 30 Days)");
        chart.setPrefHeight(300);
        chart.setLegendVisible(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        if (completedByDate != null) {
            completedByDate.forEach((date, count) -> {
                String shortDate = date.length() > 5 ? date.substring(5) : date; // YYYY-MM-DD -> MM-DD
                series.getData().add(new XYChart.Data<>(shortDate, count));
            });
        }
        chart.getData().add(series);

        return chart;
    }

    private PieChart createEngagementChart(long teachers, long students) {
        PieChart chart = new PieChart();
        chart.setTitle("Participant Distribution");
        chart.setPrefHeight(300);

        if (teachers > 0) {
            chart.getData().add(new PieChart.Data("Teachers (" + teachers + ")", teachers));
        }
        if (students > 0) {
            chart.getData().add(new PieChart.Data("Students (" + students + ")", students));
        }

        return chart;
    }

    private BarChart<String, Number> createMemberTaskChart(Map<String, Long> memberTaskCount) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Participant");
        yAxis.setLabel("Number of Completed Tasks");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Task Completion by Participants");
        chart.setPrefHeight(400);
        chart.setLegendVisible(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        if (memberTaskCount != null && !memberTaskCount.isEmpty()) {
            memberTaskCount.forEach((name, count) -> {
                String shortName = name.length() > 15 ? name.substring(0, 15) + "..." : name;
                series.getData().add(new XYChart.Data<>(shortName, count));
            });
        }
        chart.getData().add(series);

        return chart;
    }

    private void loadTasks() {
        try {
            Long groupId = ((Number) group.get("groupId")).longValue();
            var response = ApiClient.get("/tasks/group/" + groupId);
            if (response.isSuccessful()) {
                String body = response.body() != null ? response.body().string() : "[]";
                Type listType = new TypeToken<List<Map<String, Object>>>(){}.getType();
                List<Map<String, Object>> tasks = gson.fromJson(body, listType);

                // Header with search and buttons
                VBox header = new VBox(15);
                header.setPadding(new Insets(20, 30, 20, 30));
                header.setStyle("-fx-background-color: white;");

                HBox headerTop = new HBox(15);
                headerTop.setAlignment(Pos.CENTER_LEFT);

                Label titleLabel = new Label("Tasks");
                titleLabel.setFont(new Font("System Bold", 20));
                titleLabel.setStyle("-fx-text-fill: #000000; -fx-font-weight: bold;");

                HBox searchBox = new HBox(10);
                searchBox.setAlignment(Pos.CENTER_LEFT);

                TextField searchField = new TextField();
                searchField.setPromptText("Search tasks...");
                searchField.setPrefWidth(300);
                searchField.setPrefHeight(40);
                searchField.setStyle("-fx-background-color: #f5f5f5; " +
                        "-fx-border-color: #e0e0e0; " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 4; " +
                        "-fx-background-radius: 4; " +
                        "-fx-padding: 10 15; " +
                        "-fx-font-size: 14px;");

                Button filterButton = new Button("Filter");
                filterButton.setStyle("-fx-background-color: #f5f5f5; " +
                        "-fx-text-fill: #000000; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-border-color: #e0e0e0; " +
                        "-fx-border-width: 1; " +
                        "-fx-padding: 10 20; " +
                        "-fx-background-radius: 4; " +
                        "-fx-border-radius: 4; " +
                        "-fx-cursor: hand;");

                HBox.setHgrow(titleLabel, Priority.ALWAYS);
                headerTop.getChildren().addAll(titleLabel, searchField, filterButton);

                // Check if user is a teacher of the group
                boolean isTeacher = checkIfTeacher(groupId);
                HBox actionBox = new HBox(10);
                actionBox.setAlignment(Pos.CENTER_LEFT);

                if (isTeacher) {
                    Button addTaskButton = new Button("Create task");
                    addTaskButton.setStyle("-fx-background-color: #000000; " +
                            "-fx-text-fill: white; " +
                            "-fx-font-size: 14px; " +
                            "-fx-font-weight: bold; " +
                            "-fx-padding: 10 20; " +
                            "-fx-background-radius: 4; " +
                            "-fx-cursor: hand;");
                    addTaskButton.setOnMouseEntered(e -> addTaskButton.setStyle(
                            "-fx-background-color: #333333; " +
                                    "-fx-text-fill: white; " +
                                    "-fx-font-size: 14px; " +
                                    "-fx-font-weight: bold; " +
                                    "-fx-padding: 10 20; " +
                                    "-fx-background-radius: 4; " +
                                    "-fx-cursor: hand;"));
                    addTaskButton.setOnMouseExited(e -> addTaskButton.setStyle(
                            "-fx-background-color: #000000; " +
                                    "-fx-text-fill: white; " +
                                    "-fx-font-size: 14px; " +
                                    "-fx-font-weight: bold; " +
                                    "-fx-padding: 10 20; " +
                                    "-fx-background-radius: 4; " +
                                    "-fx-cursor: hand;"));
                    addTaskButton.setOnAction(e -> showCreateTaskDialog());
                    actionBox.getChildren().add(addTaskButton);
                }

                header.getChildren().addAll(headerTop, actionBox);

                // Tasks table
                TableView<Map<String, Object>> tableView = createTasksTable(tasks);

                VBox content = new VBox();
                content.getChildren().addAll(header, tableView);
                VBox.setVgrow(tableView, Priority.ALWAYS);

                ScrollPane scrollPane = new ScrollPane(content);
                scrollPane.setFitToWidth(true);
                scrollPane.setStyle("-fx-background-color: white;");

                contentArea.setCenter(scrollPane);
            }
        } catch (IOException e) {
            showAlert("Error", "Failed to load tasks", Alert.AlertType.ERROR);
        }
    }

    private TableView<Map<String, Object>> createTasksTable(List<Map<String, Object>> tasks) {
        TableView<Map<String, Object>> table = new TableView<>();
        table.setStyle("-fx-background-color: white;");

        // Task ID column
        TableColumn<Map<String, Object>, String> taskIdCol = new TableColumn<>("Task");
        taskIdCol.setPrefWidth(100);
        taskIdCol.setCellValueFactory(data -> {
            Long taskId = ((Number) data.getValue().get("taskId")).longValue();
            return new javafx.beans.property.SimpleStringProperty("TASK-" + taskId);
        });
        taskIdCol.setCellFactory(col -> new TableCell<Map<String, Object>, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: #000000; -fx-font-size: 14px;");
                }
            }
        });

        // Title column
        TableColumn<Map<String, Object>, String> titleCol = new TableColumn<>("Title");
        titleCol.setPrefWidth(300);
        titleCol.setCellValueFactory(data -> {
            String title = (String) data.getValue().get("title");
            return new javafx.beans.property.SimpleStringProperty(title != null ? title : "");
        });
        titleCol.setCellFactory(col -> new TableCell<Map<String, Object>, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: #000000; -fx-font-size: 14px;");
                }
            }
        });

        // Status column
        TableColumn<Map<String, Object>, String> statusCol = new TableColumn<>("Status");
        statusCol.setPrefWidth(120);
        statusCol.setCellValueFactory(data -> {
            String status = (String) data.getValue().getOrDefault("status", "OPEN");
            return new javafx.beans.property.SimpleStringProperty(getStatusText(status));
        });
        statusCol.setCellFactory(col -> new TableCell<Map<String, Object>, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: #757575; " +
                            "-fx-font-size: 12px; " +
                            "-fx-padding: 4 8; " +
                            "-fx-background-color: #f5f5f5; " +
                            "-fx-background-radius: 4;");
                }
            }
        });

        // Created date column
        TableColumn<Map<String, Object>, String> createdCol = new TableColumn<>("Created");
        createdCol.setPrefWidth(120);
        createdCol.setCellValueFactory(data -> {
            String createdAt = (String) data.getValue().get("createdAt");
            if (createdAt != null && !createdAt.isEmpty()) {
                try {
                    return new javafx.beans.property.SimpleStringProperty(createdAt.substring(0, 10));
                } catch (Exception e) {
                    return new javafx.beans.property.SimpleStringProperty(createdAt);
                }
            }
            return new javafx.beans.property.SimpleStringProperty("-");
        });
        createdCol.setCellFactory(col -> new TableCell<Map<String, Object>, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: #757575; -fx-font-size: 14px;");
                }
            }
        });

        // Deadline column
        TableColumn<Map<String, Object>, String> deadlineCol = new TableColumn<>("Deadline");
        deadlineCol.setPrefWidth(120);
        deadlineCol.setCellValueFactory(data -> {
            String deadline = (String) data.getValue().get("deadline");
            if (deadline != null && !deadline.isEmpty()) {
                try {
                    return new javafx.beans.property.SimpleStringProperty(deadline.substring(0, 10));
                } catch (Exception e) {
                    return new javafx.beans.property.SimpleStringProperty(deadline);
                }
            }
            return new javafx.beans.property.SimpleStringProperty("-");
        });
        deadlineCol.setCellFactory(col -> new TableCell<Map<String, Object>, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: #757575; -fx-font-size: 14px;");
                }
            }
        });

        // Actions column
        TableColumn<Map<String, Object>, String> actionsCol = new TableColumn<>("");
        actionsCol.setPrefWidth(80);
        actionsCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(""));
        actionsCol.setCellFactory(col -> new TableCell<Map<String, Object>, String>() {
            private final Button menuButton = new Button("⋯");

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    menuButton.setStyle("-fx-background-color: transparent; " +
                            "-fx-text-fill: #757575; " +
                            "-fx-font-size: 18px; " +
                            "-fx-cursor: hand;");
                    menuButton.setOnAction(e -> {
                        Map<String, Object> task = getTableView().getItems().get(getIndex());
                        showTaskMenu(task);
                    });
                    setGraphic(menuButton);
                }
            }
        });

        @SuppressWarnings("unchecked")
        TableColumn<Map<String, Object>, ?>[] columns = new TableColumn[]{
                taskIdCol, titleCol, statusCol, createdCol, deadlineCol, actionsCol
        };
        table.getColumns().addAll(columns);

        // Add event handler for row click
        table.setRowFactory(tv -> {
            TableRow<Map<String, Object>> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Map<String, Object> task = row.getItem();
                    showTaskDetailsDialog(task);
                }
            });
            return row;
        });

        // Add data
        for (Map<String, Object> task : tasks) {
            table.getItems().add(task);
        }

        return table;
    }

    private void showTaskMenu(Map<String, Object> task) {
        ContextMenu menu = new ContextMenu();

        MenuItem viewItem = new MenuItem("View");
        viewItem.setOnAction(e -> showTaskDetailsDialog(task));

        Long groupId = ((Number) group.get("groupId")).longValue();
        boolean isTeacher = checkIfTeacher(groupId);
        boolean isMember = checkIfMember(groupId);

        if (isTeacher) {
            MenuItem editItem = new MenuItem("Edit");
            editItem.setOnAction(e -> showEditTaskDialog(task));
            menu.getItems().add(editItem);

            MenuItem addResourceItem = new MenuItem("Add resource");
            addResourceItem.setOnAction(e -> showAddResourceDialog(task));
            menu.getItems().add(addResourceItem);
        }

        // Add option for adding/editing solution for students
        if (isMember && !isTeacher) {
            Long solutionUploadedBy = task.get("solutionUploadedBy") != null ?
                    ((Number) task.get("solutionUploadedBy")).longValue() : null;
            Long userId = ApiClient.getCurrentUserId();
            boolean isMySolution = solutionUploadedBy != null && userId != null && solutionUploadedBy.equals(userId);

            MenuItem solutionItem = new MenuItem(isMySolution ? "Edit solution" : "Submit solution");
            solutionItem.setOnAction(e -> showAddSolutionDialog(task));
            menu.getItems().add(solutionItem);
        }

        menu.getItems().add(viewItem);
        menu.show(javafx.stage.Stage.getWindows().get(0));
    }

    private void showTaskDetailsDialog(Map<String, Object> task) {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: white;");

        // Top bar with back button
        HBox topBar = new HBox(15);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(0, 0, 20, 0));

        Button backButton = new Button("← Back");
        backButton.setStyle("-fx-background-color: transparent; " +
                "-fx-text-fill: #000000; " +
                "-fx-font-size: 14px; " +
                "-fx-cursor: hand; " +
                "-fx-border-color: transparent;");
        backButton.setOnMouseEntered(e -> backButton.setStyle(
                "-fx-background-color: #f5f5f5; " +
                        "-fx-text-fill: #000000; " +
                        "-fx-font-size: 14px; " +
                        "-fx-cursor: hand; " +
                        "-fx-border-color: transparent;"));
        backButton.setOnMouseExited(e -> backButton.setStyle(
                "-fx-background-color: transparent; " +
                        "-fx-text-fill: #000000; " +
                        "-fx-font-size: 14px; " +
                        "-fx-cursor: hand; " +
                        "-fx-border-color: transparent;"));
        backButton.setOnAction(e -> showTasksView());

        topBar.getChildren().add(backButton);

        // Title
        String title = (String) task.getOrDefault("title", "No title");
        Label titleLabel = new Label(title);
        titleLabel.setFont(new Font("System Bold", 24));
        titleLabel.setStyle("-fx-text-fill: #000000; -fx-font-weight: bold;");
        titleLabel.setWrapText(true);

        // Status and deadline
        HBox infoBox = new HBox(20);
        infoBox.setAlignment(Pos.CENTER_LEFT);

        String status = (String) task.getOrDefault("status", "OPEN");
        String statusText = switch (status) {
            case "OPEN" -> "Open";
            case "IN_PROGRESS" -> "In Progress";
            case "COMPLETED" -> "Completed";
            default -> "Open";
        };
        String statusColor = switch (status) {
            case "COMPLETED" -> "#4caf50";
            case "IN_PROGRESS" -> "#2196f3";
            default -> "#757575";
        };
        Label statusLabel = new Label("Status: ● " + statusText);
        statusLabel.setStyle("-fx-text-fill: " + statusColor + "; -fx-font-size: 14px; -fx-font-weight: bold;");

        String deadline = (String) task.getOrDefault("deadline", "");
        Label deadlineLabel = new Label("Deadline: " + (deadline.isEmpty() ? "No deadline" : deadline.substring(0, Math.min(10, deadline.length()))));
        deadlineLabel.setStyle("-fx-text-fill: #757575; -fx-font-size: 14px;");

        infoBox.getChildren().addAll(statusLabel, deadlineLabel);

        // Description
        VBox descriptionBox = new VBox(10);
        Label descTitle = new Label("Description:");
        descTitle.setStyle("-fx-text-fill: #000000; -fx-font-size: 18px; -fx-font-weight: bold;");

        String description = (String) task.getOrDefault("description", "");
        TextArea descArea = new TextArea(description.isEmpty() ? "No description" : description);
        descArea.setEditable(false);
        descArea.setWrapText(true);
        descArea.setPrefRowCount(6);
        descArea.setStyle("-fx-background-color: #f9f9f9; " +
                "-fx-border-color: #e0e0e0; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 4; " +
                "-fx-background-radius: 4; " +
                "-fx-padding: 15; " +
                "-fx-font-size: 14px;");
        descriptionBox.getChildren().addAll(descTitle, descArea);

        // Resources
        VBox resourcesBox = loadTaskResources(task);

        // Solution section
        Long groupId = ((Number) group.get("groupId")).longValue();
        boolean isMember = checkIfMember(groupId);
        VBox solutionBox = createSolutionSection(task, groupId, isMember);

        // Comments section
        VBox commentsBox = createCommentsSection(task, groupId, isMember);

        content.getChildren().addAll(topBar, titleLabel, infoBox, descriptionBox, resourcesBox);
        if (solutionBox != null) {
            content.getChildren().add(solutionBox);
        }
        content.getChildren().add(commentsBox);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: white;");

        contentArea.setCenter(scrollPane);
    }

    private boolean checkIfMember(Long groupId) {
        try {
            Long userId = ApiClient.getCurrentUserId();
            if (userId == null) return false;

            var response = ApiClient.get("/groups/user/" + userId);
            if (response.isSuccessful()) {
                String body = response.body() != null ? response.body().string() : "[]";
                Type listType = new TypeToken<List<Map<String, Object>>>(){}.getType();
                List<Map<String, Object>> myGroups = gson.fromJson(body, listType);

                return myGroups.stream()
                        .anyMatch(g -> ((Number) g.get("groupId")).longValue() == groupId);
            }
        } catch (IOException e) {
            // Ignore
        }
        return false;
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

    private VBox createTaskCard(Map<String, Object> task) {
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setStyle(Styles.card());
        card.setPrefWidth(1100);

        card.setOnMouseEntered(e -> card.setStyle(Styles.cardHover()));
        card.setOnMouseExited(e -> card.setStyle(Styles.card()));

        HBox headerBox = new HBox(15);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label((String) task.get("title"));
        titleLabel.setFont(new Font(18));
        titleLabel.setStyle(Styles.headingLabel());

        String status = (String) task.getOrDefault("status", "OPEN");
        Label statusLabel = new Label(getStatusEmoji(status) + " " + getStatusText(status));
        statusLabel.setStyle(getStatusStyle(status));

        HBox.setHgrow(titleLabel, Priority.ALWAYS);
        headerBox.getChildren().addAll(titleLabel, statusLabel);

        Label descLabel = new Label((String) task.getOrDefault("description", ""));
        descLabel.setWrapText(true);
        descLabel.setStyle(Styles.subtitleLabel());
        descLabel.setPadding(new Insets(5, 0, 0, 0));

        String deadline = (String) task.get("deadline");
        Label deadlineLabel = null;
        if (deadline != null && !deadline.isEmpty()) {
            deadlineLabel = new Label("📅 Deadline: " + deadline);
            deadlineLabel.setStyle(Styles.subtitleLabel());
            deadlineLabel.setPadding(new Insets(5, 0, 0, 0));
        }

        // Load resources for the task
        VBox resourcesContainer = loadTaskResources(task);

        HBox buttonBox = new HBox(10);
        if (deadlineLabel != null) {
            buttonBox.getChildren().add(deadlineLabel);
        }

        // Add buttons for adding resources and editing (only for members)
        Long groupId = ((Number) group.get("groupId")).longValue();
        boolean isMember = checkIfMember(groupId);
        boolean isTeacher = checkIfTeacher(groupId);

        if (isMember) {
            Button addResourceButton = new Button("📎 Add Resource");
            addResourceButton.setStyle(Styles.secondaryButton());
            addResourceButton.setOnMouseEntered(e -> addResourceButton.setStyle(
                    "-fx-background-color: #43a047; " +
                            "-fx-text-fill: white; " +
                            "-fx-font-size: 14px; " +
                            "-fx-font-weight: bold; " +
                            "-fx-padding: 10 20; " +
                            "-fx-background-radius: 8; " +
                            "-fx-cursor: hand; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 3);"));
            addResourceButton.setOnMouseExited(e -> addResourceButton.setStyle(Styles.secondaryButton()));
            addResourceButton.setOnAction(e -> showAddResourceDialog(task));
            buttonBox.getChildren().add(addResourceButton);

            // Edit task button (only for teachers)
            if (isTeacher) {
                Button editTaskButton = new Button("✏️ Edit");
                editTaskButton.setStyle(Styles.secondaryButton());
                editTaskButton.setOnMouseEntered(e -> editTaskButton.setStyle(
                        "-fx-background-color: #ff9800; " +
                                "-fx-text-fill: white; " +
                                "-fx-font-size: 14px; " +
                                "-fx-font-weight: bold; " +
                                "-fx-padding: 10 20; " +
                                "-fx-background-radius: 8; " +
                                "-fx-cursor: hand; " +
                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 3);"));
                editTaskButton.setOnMouseExited(e -> editTaskButton.setStyle(Styles.secondaryButton()));
                editTaskButton.setOnAction(e -> showEditTaskDialog(task));
                buttonBox.getChildren().add(editTaskButton);
            }
        }

        // Add solution display and button for adding/editing solution
        VBox solutionContainer = createSolutionSection(task, groupId, isMember);
        if (solutionContainer != null) {
            card.getChildren().add(solutionContainer);
        }

        card.getChildren().addAll(headerBox, descLabel, buttonBox);
        if (resourcesContainer.getChildren().size() > 0) {
            card.getChildren().add(resourcesContainer);
        }
        return card;
    }

    private VBox loadTaskResources(Map<String, Object> task) {
        VBox resourcesContainer = new VBox(5);
        resourcesContainer.setPadding(new Insets(10, 0, 0, 0));

        try {
            Long taskId = ((Number) task.get("taskId")).longValue();
            var response = ApiClient.get("/resources/task/" + taskId);
            if (response.isSuccessful()) {
                String body = response.body() != null ? response.body().string() : "[]";
                Type listType = new TypeToken<List<Map<String, Object>>>(){}.getType();
                List<Map<String, Object>> resources = gson.fromJson(body, listType);

                if (!resources.isEmpty()) {
                    Label resourcesLabel = new Label("📎 Resources:");
                    resourcesLabel.setStyle(Styles.headingLabel());
                    resourcesLabel.setPadding(new Insets(10, 0, 5, 0));
                    resourcesContainer.getChildren().add(resourcesLabel);

                    for (Map<String, Object> resource : resources) {
                        resourcesContainer.getChildren().add(createResourceItem(resource));
                    }
                }
            }
        } catch (IOException e) {
            // Ignore
        }

        return resourcesContainer;
    }

    private HBox createResourceItem(Map<String, Object> resource) {
        HBox resourceBox = new HBox(10);
        resourceBox.setAlignment(Pos.CENTER_LEFT);
        resourceBox.setPadding(new Insets(8, 0, 8, 20));
        resourceBox.setStyle("-fx-background-color: #f9f9f9; -fx-background-radius: 6; -fx-padding: 8;");

        String type = (String) resource.get("type");
        String title = (String) resource.get("title");
        String pathOrUrl = (String) resource.get("pathOrUrl");

        String emoji = type.equals("LINK") ? "🔗" : "📄";
        Label resourceLabel = new Label(emoji + " " + title + " (" + (type.equals("LINK") ? "Link" : "File") + ")");
        resourceLabel.setStyle("-fx-text-fill: " + Styles.PRIMARY_COLOR + "; -fx-font-size: 14px;");

        if (type.equals("LINK")) {
            resourceLabel.setOnMouseClicked(e -> {
                try {
                    java.awt.Desktop.getDesktop().browse(new java.net.URI(pathOrUrl));
                } catch (Exception ex) {
                    showAlert("Error", "Failed to open link", Alert.AlertType.ERROR);
                }
            });
            resourceLabel.setStyle("-fx-text-fill: " + Styles.PRIMARY_COLOR + "; " +
                    "-fx-underline: true; " +
                    "-fx-font-size: 14px; " +
                    "-fx-cursor: hand;");
            resourceLabel.setOnMouseEntered(e -> resourceLabel.setStyle(
                    "-fx-text-fill: " + Styles.PRIMARY_DARK + "; " +
                            "-fx-underline: true; " +
                            "-fx-font-size: 14px; " +
                            "-fx-cursor: hand;"));
            resourceLabel.setOnMouseExited(e -> resourceLabel.setStyle(
                    "-fx-text-fill: " + Styles.PRIMARY_COLOR + "; " +
                            "-fx-underline: true; " +
                            "-fx-font-size: 14px; " +
                            "-fx-cursor: hand;"));
        }

        resourceBox.getChildren().add(resourceLabel);
        return resourceBox;
    }

    private void showAddResourceDialog(Map<String, Object> task) {
        Dialog<Map<String, Object>> dialog = new Dialog<>();
        dialog.setTitle("Add resource");
        dialog.setHeaderText("Enter resource information");

        Label titleLabel = new Label("Title:");
        titleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #000000;");
        TextField titleField = new TextField();
        titleField.setPromptText("Resource title");
        titleField.setPrefWidth(500);
        titleField.setPrefHeight(48);
        titleField.setStyle("-fx-background-color: white; " +
                "-fx-border-color: #e0e0e0; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 4; " +
                "-fx-background-radius: 4; " +
                "-fx-padding: 12 16; " +
                "-fx-font-size: 14px;");
        titleField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            titleField.setStyle(newVal ?
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

        Label typeLabel = new Label("Type:");
        typeLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #000000;");
        ComboBox<String> typeComboBox = new ComboBox<>();
        typeComboBox.getItems().addAll("LINK", "FILE");
        typeComboBox.getSelectionModel().selectFirst();
        typeComboBox.setPrefWidth(500);
        typeComboBox.setPrefHeight(48);
        typeComboBox.setStyle("-fx-background-color: white; " +
                "-fx-border-color: #e0e0e0; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 4; " +
                "-fx-background-radius: 4; " +
                "-fx-padding: 12 16; " +
                "-fx-font-size: 14px;");

        Label pathLabel = new Label("URL/Path:");
        pathLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #000000;");
        TextField pathOrUrlField = new TextField();
        pathOrUrlField.setPromptText("URL or file path");
        pathOrUrlField.setPrefWidth(500);
        pathOrUrlField.setPrefHeight(48);
        pathOrUrlField.setStyle("-fx-background-color: white; " +
                "-fx-border-color: #e0e0e0; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 4; " +
                "-fx-background-radius: 4; " +
                "-fx-padding: 12 16; " +
                "-fx-font-size: 14px;");
        pathOrUrlField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            pathOrUrlField.setStyle(newVal ?
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
                titleLabel, titleField,
                typeLabel, typeComboBox,
                pathLabel, pathOrUrlField
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                if (titleField.getText().trim().isEmpty() || pathOrUrlField.getText().trim().isEmpty()) {
                    showAlert("Error", "Fill all fields", Alert.AlertType.ERROR);
                    return null;
                }
                Long userId = ApiClient.getCurrentUserId();
                Long groupId = ((Number) group.get("groupId")).longValue();
                Long taskId = ((Number) task.get("taskId")).longValue();

                if (userId == null) {
                    showAlert("Error", "User not authorized", Alert.AlertType.ERROR);
                    return null;
                }

                return Map.of(
                        "groupId", groupId,
                        "taskId", taskId,
                        "uploadedBy", userId,
                        "title", titleField.getText().trim(),
                        "type", typeComboBox.getSelectionModel().getSelectedItem(),
                        "pathOrUrl", pathOrUrlField.getText().trim()
                );
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            if (result == null) return;
            try {
                var response = ApiClient.post("/resources", result);
                if (response.isSuccessful()) {
                    showAlert("Success", "Resource added successfully", Alert.AlertType.INFORMATION);
                    loadTasks(); // Update task list
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "";
                    showAlert("Error", "Failed to add resource. Code: " + response.code() +
                            (errorBody.isEmpty() ? "" : "\n" + errorBody), Alert.AlertType.ERROR);
                }
            } catch (IOException e) {
                showAlert("Error", "Connection error: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    private String getStatusEmoji(String status) {
        return switch (status) {
            case "OPEN" -> "📋";
            case "IN_PROGRESS" -> "🔄";
            case "COMPLETED" -> "✅";
            default -> "📋";
        };
    }

    private String getStatusText(String status) {
        return switch (status) {
            case "OPEN" -> "Open";
            case "IN_PROGRESS" -> "In Progress";
            case "COMPLETED" -> "Completed";
            default -> "Open";
        };
    }

    private String getStatusStyle(String status) {
        String baseStyle = "-fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 6 14; -fx-background-radius: 15;";
        return switch (status) {
            case "OPEN" -> baseStyle + " -fx-text-fill: " + Styles.PRIMARY_COLOR + "; -fx-background-color: #e3f2fd;";
            case "IN_PROGRESS" -> baseStyle + " -fx-text-fill: #f57c00; -fx-background-color: #fff3e0;";
            case "COMPLETED" -> baseStyle + " -fx-text-fill: " + Styles.SECONDARY_COLOR + "; -fx-background-color: #e8f5e9;";
            default -> baseStyle + " -fx-text-fill: " + Styles.TEXT_SECONDARY + "; -fx-background-color: #f5f5f5;";
        };
    }

    private void showCreateTaskDialog() {
        Dialog<Map<String, Object>> dialog = new Dialog<>();
        dialog.setTitle("Create Task");
        dialog.setHeaderText("Enter task details");

        Label titleLabel = new Label("Title:");
        titleLabel.setStyle(Styles.headingLabel());
        TextField titleField = new TextField();
        titleField.setPromptText("Task title");
        titleField.setPrefWidth(450);
        titleField.setPrefHeight(45);
        titleField.setStyle(Styles.inputField());
        titleField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            titleField.setStyle(newVal ? Styles.inputFieldFocused() : Styles.inputField());
        });

        Label descLabel = new Label("Description:");
        descLabel.setStyle(Styles.headingLabel());
        TextArea descField = new TextArea();
        descField.setPromptText("Description");
        descField.setPrefRowCount(4);
        descField.setPrefWidth(450);
        descField.setWrapText(true);
        descField.setStyle(Styles.inputField());
        descField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            descField.setStyle(newVal ? Styles.inputFieldFocused() : Styles.inputField());
        });

        Label deadlineLabel = new Label("Deadline:");
        deadlineLabel.setStyle(Styles.headingLabel());
        DatePicker deadlinePicker = new DatePicker();
        deadlinePicker.setPrefWidth(450);
        deadlinePicker.setPrefHeight(45);
        deadlinePicker.setStyle(Styles.inputField());

        VBox content = new VBox(15);
        content.setPadding(new Insets(30));
        content.getChildren().addAll(
                titleLabel, titleField,
                descLabel, descField,
                deadlineLabel, deadlinePicker
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                if (titleField.getText().trim().isEmpty()) {
                    showAlert("Error", "Task title cannot be empty", Alert.AlertType.ERROR);
                    return null;
                }
                Long userId = ApiClient.getCurrentUserId();
                Long groupId = ((Number) group.get("groupId")).longValue();
                if (userId == null) {
                    showAlert("Error", "User is not authorized", Alert.AlertType.ERROR);
                    return null;
                }

                java.util.Map<String, Object> request = new java.util.HashMap<>();
                request.put("groupId", groupId);
                request.put("createdBy", userId);
                request.put("title", titleField.getText().trim());
                request.put("description", descField.getText() != null ? descField.getText().trim() : "");

                if (deadlinePicker.getValue() != null) {
                    LocalDate date = deadlinePicker.getValue();
                    String deadline = date.atTime(23, 59, 59).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
                    request.put("deadline", deadline);
                } else {
                    request.put("deadline", "");
                }

                return request;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            if (result == null) return;
            try {
                var response = ApiClient.post("/tasks", result);
                if (response.isSuccessful()) {
                    showAlert("Success", "Task created successfully", Alert.AlertType.INFORMATION);
                    loadTasks();
                } else {
                    showAlert("Error", "Failed to create task. Code: " + response.code(), Alert.AlertType.ERROR);
                }
            } catch (IOException e) {
                showAlert("Error", "Connection error: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    private void showEditTaskDialog(Map<String, Object> task) {
        Dialog<Map<String, Object>> dialog = new Dialog<>();
        dialog.setTitle("Edit Task");
        dialog.setHeaderText("Enter new task details");

        Label titleLabel = new Label("Title:");
        titleLabel.setStyle(Styles.headingLabel());
        TextField titleField = new TextField();
        titleField.setText((String) task.get("title"));
        titleField.setPromptText("Task title");
        titleField.setPrefWidth(450);
        titleField.setPrefHeight(45);
        titleField.setStyle(Styles.inputField());
        titleField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            titleField.setStyle(newVal ? Styles.inputFieldFocused() : Styles.inputField());
        });

        Label descLabel = new Label("Description:");
        descLabel.setStyle(Styles.headingLabel());
        TextArea descField = new TextArea();
        descField.setText((String) task.getOrDefault("description", ""));
        descField.setPromptText("Description");
        descField.setPrefRowCount(4);
        descField.setPrefWidth(450);
        descField.setWrapText(true);
        descField.setStyle(Styles.inputField());
        descField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            descField.setStyle(newVal ? Styles.inputFieldFocused() : Styles.inputField());
        });

        Label deadlineLabel = new Label("Deadline:");
        deadlineLabel.setStyle(Styles.headingLabel());
        DatePicker deadlinePicker = new DatePicker();
        deadlinePicker.setPrefWidth(450);
        deadlinePicker.setPrefHeight(45);
        deadlinePicker.setStyle(Styles.inputField());

        // Set current deadline date if it exists
        String deadlineStr = (String) task.get("deadline");
        if (deadlineStr != null && !deadlineStr.isEmpty()) {
            try {
                LocalDate deadline = LocalDate.parse(deadlineStr.substring(0, 10));
                deadlinePicker.setValue(deadline);
            } catch (Exception e) {
                // Ignore parsing errors
            }
        }

        VBox content = new VBox(15);
        content.setPadding(new Insets(30));
        content.getChildren().addAll(
                titleLabel, titleField,
                descLabel, descField,
                deadlineLabel, deadlinePicker
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                if (titleField.getText().trim().isEmpty()) {
                    showAlert("Error", "Task title cannot be empty", Alert.AlertType.ERROR);
                    return null;
                }
                Long userId = ApiClient.getCurrentUserId();
                if (userId == null) {
                    showAlert("Error", "User is not authorized", Alert.AlertType.ERROR);
                    return null;
                }

                java.util.Map<String, Object> request = new java.util.HashMap<>();
                request.put("userId", userId);
                request.put("title", titleField.getText().trim());
                request.put("description", descField.getText() != null ? descField.getText().trim() : "");

                if (deadlinePicker.getValue() != null) {
                    LocalDate date = deadlinePicker.getValue();
                    String deadline = date.atTime(23, 59, 59).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
                    request.put("deadline", deadline);
                } else {
                    request.put("deadline", "");
                }

                return request;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            if (result == null) return;
            try {
                Long taskId = ((Number) task.get("taskId")).longValue();
                var response = ApiClient.put("/tasks/" + taskId, result);
                if (response.isSuccessful()) {
                    showAlert("Success", "Task updated successfully", Alert.AlertType.INFORMATION);
                    loadTasks();
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "";
                    showAlert("Error", "Failed to update task. Code: " + response.code() +
                            (errorBody.isEmpty() ? "" : "\n" + errorBody), Alert.AlertType.ERROR);
                }
            } catch (IOException e) {
                showAlert("Error", "Connection error: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    private void loadMembers() {
        try {
            Long groupId = ((Number) group.get("groupId")).longValue();
            var response = ApiClient.get("/groups/" + groupId + "/members");
            if (response.isSuccessful()) {
                String body = response.body() != null ? response.body().string() : "[]";
                Type listType = new TypeToken<List<Map<String, Object>>>(){}.getType();
                List<Map<String, Object>> memberships = gson.fromJson(body, listType);

                VBox content = new VBox(15);
                content.setPadding(new Insets(30));
                content.setStyle("-fx-background-color: white;");

                // Header
                HBox header = new HBox(15);
                header.setAlignment(Pos.CENTER_LEFT);

                Label titleLabel = new Label("Members");
                titleLabel.setFont(new Font("System Bold", 20));
                titleLabel.setStyle("-fx-text-fill: #000000; -fx-font-weight: bold;");

                HBox.setHgrow(titleLabel, Priority.ALWAYS);
                header.getChildren().add(titleLabel);

                // Check if user is administrator (TEACHER) - only they can add users
                boolean isTeacher = checkIfTeacher(groupId);

                if (isTeacher) {
                    Button addMemberButton = new Button("Add member");
                    addMemberButton.setStyle("-fx-background-color: #000000; " +
                            "-fx-text-fill: white; " +
                            "-fx-font-size: 14px; " +
                            "-fx-font-weight: bold; " +
                            "-fx-padding: 10 20; " +
                            "-fx-background-radius: 4; " +
                            "-fx-cursor: hand;");
                    addMemberButton.setOnMouseEntered(e -> addMemberButton.setStyle(
                            "-fx-background-color: #333333; " +
                                    "-fx-text-fill: white; " +
                                    "-fx-font-size: 14px; " +
                                    "-fx-font-weight: bold; " +
                                    "-fx-padding: 10 20; " +
                                    "-fx-background-radius: 4; " +
                                    "-fx-cursor: hand;"));
                    addMemberButton.setOnMouseExited(e -> addMemberButton.setStyle(
                            "-fx-background-color: #000000; " +
                                    "-fx-text-fill: white; " +
                                    "-fx-font-size: 14px; " +
                                    "-fx-font-weight: bold; " +
                                    "-fx-padding: 10 20; " +
                                    "-fx-background-radius: 4; " +
                                    "-fx-cursor: hand;"));
                    addMemberButton.setOnAction(e -> showAddMemberDialog());
                    header.getChildren().add(addMemberButton);
                }

                content.getChildren().add(header);

                FlowPane membersContainer = new FlowPane();
                membersContainer.setHgap(20);
                membersContainer.setVgap(20);
                membersContainer.setPadding(new Insets(20, 0, 0, 0));

                // Load user information
                var usersResponse = ApiClient.get("/users");
                if (usersResponse.isSuccessful()) {
                    String usersBody = usersResponse.body() != null ? usersResponse.body().string() : "[]";
                    Type usersListType = new TypeToken<List<Map<String, Object>>>(){}.getType();
                    List<Map<String, Object>> allUsers = gson.fromJson(usersBody, usersListType);

                    for (Map<String, Object> membership : memberships) {
                        Long userId = ((Number) membership.get("userId")).longValue();
                        String role = (String) membership.get("role");

                        allUsers.stream()
                                .filter(u -> ((Number) u.get("userId")).longValue() == userId)
                                .findFirst()
                                .ifPresent(user -> {
                                    membersContainer.getChildren().add(createMemberCard(user, role));
                                });
                    }
                }

                content.getChildren().add(membersContainer);

                ScrollPane scrollPane = new ScrollPane(content);
                scrollPane.setFitToWidth(true);
                scrollPane.setStyle("-fx-background-color: white;");

                contentArea.setCenter(scrollPane);
            }
        } catch (IOException e) {
            showAlert("Error", "Failed to load members", Alert.AlertType.ERROR);
        }
    }

    private VBox createMemberCard(Map<String, Object> user, String role) {
        VBox card = new VBox(15);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setPrefWidth(180);
        card.setPrefHeight(220);
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

        // Add click handler for viewing profile
        card.setOnMouseClicked(e -> {
            Long userId = ((Number) user.get("userId")).longValue();
            StackPane mainRoot = (StackPane) mainController.getRoot();
            ProfileController profileController = new ProfileController(mainController, userId);
            mainRoot.getChildren().clear();
            mainRoot.getChildren().add(profileController.getRoot());
        });

        // User photo
        StackPane photoContainer = new StackPane();
        photoContainer.setPrefWidth(120);
        photoContainer.setPrefHeight(120);

        String photoUrl = (String) user.get("profilePhotoUrl");
        ImageView photoView = new ImageView();
        photoView.setFitWidth(120);
        photoView.setFitHeight(120);
        photoView.setPreserveRatio(true);
        photoView.setSmooth(true);

        if (photoUrl != null && !photoUrl.isEmpty()) {
            try {
                String fullUrl;
                if (photoUrl.startsWith("http")) {
                    fullUrl = photoUrl;
                } else {
                    fullUrl = "http://localhost:8080/api/users/uploads/profiles/" + photoUrl;
                }
                Image image = new Image(fullUrl, true);
                image.errorProperty().addListener((obs, wasError, isError) -> {
                    if (isError) {
                        showPlaceholderPhoto(photoView);
                    } else {
                        photoView.setImage(image);
                        photoView.setClip(new Circle(60, 60, 60));
                    }
                });
                if (!image.isError()) {
                    photoView.setImage(image);
                    photoView.setClip(new Circle(60, 60, 60));
                } else {
                    showPlaceholderPhoto(photoView);
                }
            } catch (Exception e) {
                // If failed to load photo, show placeholder
                showPlaceholderPhoto(photoView);
            }
        } else {
            showPlaceholderPhoto(photoView);
        }

        photoContainer.getChildren().add(photoView);

        // User name
        Label nameLabel = new Label((String) user.get("name"));
        nameLabel.setFont(new Font("System Bold", 16));
        nameLabel.setStyle("-fx-text-fill: #000000; -fx-font-weight: bold;");
        nameLabel.setAlignment(Pos.CENTER);
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(160);

        // Role
        Label roleLabel = new Label(role.equals("TEACHER") ? "Teacher" : "Student");
        roleLabel.setStyle("-fx-text-fill: #757575; " +
                "-fx-font-size: 12px; " +
                "-fx-padding: 4 12; " +
                "-fx-background-color: #f5f5f5; " +
                "-fx-background-radius: 12;");

        card.getChildren().addAll(photoContainer, nameLabel, roleLabel);
        return card;
    }

    private void showPlaceholderPhoto(ImageView photoView) {
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

        // Convert StackPane to Image via snapshot
        javafx.scene.image.WritableImage snapshot = new javafx.scene.image.WritableImage(120, 120);
        placeholderPane.snapshot(null, snapshot);
        photoView.setImage(snapshot);
        photoView.setClip(new Circle(60, 60, 60));
    }

    private void showAddMemberDialog() {
        Dialog<Map<String, Object>> dialog = new Dialog<>();
        dialog.setTitle("Add Member");
        dialog.setHeaderText("Select user and role");

        try {
            var usersResponse = ApiClient.get("/users");
            if (!usersResponse.isSuccessful()) {
                showAlert("Error", "Failed to load users", Alert.AlertType.ERROR);
                return;
            }

            String usersBody = usersResponse.body() != null ? usersResponse.body().string() : "[]";
            Type usersListType = new TypeToken<List<Map<String, Object>>>(){}.getType();
            List<Map<String, Object>> allUsers = gson.fromJson(usersBody, usersListType);

            Long groupId = ((Number) group.get("groupId")).longValue();
            var membersResponse = ApiClient.get("/groups/" + groupId + "/members");
            String membersBody = membersResponse.body() != null ? membersResponse.body().string() : "[]";
            Type membersListType = new TypeToken<List<Map<String, Object>>>(){}.getType();
            List<Map<String, Object>> existingMembers = gson.fromJson(membersBody, membersListType);

            // Filter already added users
            List<Long> existingUserIds = existingMembers.stream()
                    .map(m -> ((Number) m.get("userId")).longValue())
                    .toList();

            List<Map<String, Object>> availableUsers = allUsers.stream()
                    .filter(u -> !existingUserIds.contains(((Number) u.get("userId")).longValue()))
                    .toList();

            if (availableUsers.isEmpty()) {
                showAlert("Information", "All users are already added to the group", Alert.AlertType.INFORMATION);
                return;
            }

            ComboBox<String> userComboBox = new ComboBox<>();
            for (Map<String, Object> user : availableUsers) {
                String display = (String) user.get("name") + " (" + user.get("email") + ")";
                userComboBox.getItems().add(display);
            }
            userComboBox.getSelectionModel().selectFirst();

            ComboBox<String> roleComboBox = new ComboBox<>();
            roleComboBox.getItems().addAll("STUDENT", "TEACHER");
            roleComboBox.getSelectionModel().selectFirst();

            VBox content = new VBox(10);
            content.setPadding(new Insets(20));
            content.getChildren().addAll(
                    new Label("User:"), userComboBox,
                    new Label("Role:"), roleComboBox
            );

            dialog.getDialogPane().setContent(content);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            dialog.setResultConverter(buttonType -> {
                if (buttonType == ButtonType.OK) {
                    int selectedIndex = userComboBox.getSelectionModel().getSelectedIndex();
                    if (selectedIndex < 0 || selectedIndex >= availableUsers.size()) {
                        return null;
                    }
                    Map<String, Object> selectedUser = availableUsers.get(selectedIndex);
                    Long userId = ((Number) selectedUser.get("userId")).longValue();
                    String role = roleComboBox.getSelectionModel().getSelectedItem();

                    Long addedByUserId = ApiClient.getCurrentUserId();
                    return Map.of(
                            "addedByUserId", addedByUserId,
                            "userId", userId,
                            "role", role
                    );
                }
                return null;
            });

            dialog.showAndWait().ifPresent(result -> {
                if (result == null) return;
                try {
                    Long groupId2 = ((Number) group.get("groupId")).longValue();
                    var response = ApiClient.post("/groups/" + groupId2 + "/members", result);
                    if (response.isSuccessful()) {
                        showAlert("Success", "Member added successfully", Alert.AlertType.INFORMATION);
                        loadMembers();
                    } else {
                        showAlert("Error", "Failed to add member. Code: " + response.code(), Alert.AlertType.ERROR);
                    }
                } catch (IOException e) {
                    showAlert("Error", "Connection error: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            });
        } catch (IOException e) {
            showAlert("Error", "Connection error: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void joinGroup() {
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
                // Update UI - recreate screen
                StackPane mainRoot = (StackPane) mainController.getRoot();
                GroupDetailsController newController = new GroupDetailsController(mainController, group);
                mainRoot.getChildren().clear();
                mainRoot.getChildren().add(newController.getRoot());
            } else {
                showAlert("Error", "Failed to join group. Code: " + response.code(), Alert.AlertType.ERROR);
            }
        } catch (IOException e) {
            showAlert("Error", "Connection error: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private VBox createSolutionSection(Map<String, Object> task, Long groupId, boolean isMember) {
        VBox solutionContainer = new VBox(10);
        solutionContainer.setPadding(new Insets(15, 0, 0, 0));
        solutionContainer.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 8; -fx-padding: 15;");

        String solutionText = (String) task.get("solutionText");
        String solutionUrl = (String) task.get("solutionUrl");
        Long solutionUploadedBy = task.get("solutionUploadedBy") != null ?
                ((Number) task.get("solutionUploadedBy")).longValue() : null;
        String solutionUploadedAt = (String) task.get("solutionUploadedAt");

        Label solutionLabel = new Label("💡 Solution:");
        solutionLabel.setStyle(Styles.headingLabel());
        solutionLabel.setPadding(new Insets(0, 0, 10, 0));
        solutionContainer.getChildren().add(solutionLabel);

        if (solutionText != null && !solutionText.isEmpty()) {
            Label textLabel = new Label(solutionText);
            textLabel.setWrapText(true);
            textLabel.setStyle(Styles.subtitleLabel());
            textLabel.setPadding(new Insets(5, 0, 5, 0));
            solutionContainer.getChildren().add(textLabel);
        }

        if (solutionUrl != null && !solutionUrl.isEmpty()) {
            HBox urlBox = new HBox(10);
            urlBox.setAlignment(Pos.CENTER_LEFT);

            Label urlLabel = new Label("🔗 " + solutionUrl);
            urlLabel.setStyle("-fx-text-fill: " + Styles.PRIMARY_COLOR + "; " +
                    "-fx-underline: true; " +
                    "-fx-font-size: 14px; " +
                    "-fx-cursor: hand;");
            urlLabel.setOnMouseClicked(e -> {
                try {
                    java.awt.Desktop.getDesktop().browse(new java.net.URI(solutionUrl));
                } catch (Exception ex) {
                    showAlert("Error", "Failed to open link", Alert.AlertType.ERROR);
                }
            });
            urlLabel.setOnMouseEntered(e -> urlLabel.setStyle(
                    "-fx-text-fill: " + Styles.PRIMARY_DARK + "; " +
                            "-fx-underline: true; " +
                            "-fx-font-size: 14px; " +
                            "-fx-cursor: hand;"));
            urlLabel.setOnMouseExited(e -> urlLabel.setStyle(
                    "-fx-text-fill: " + Styles.PRIMARY_COLOR + "; " +
                            "-fx-underline: true; " +
                            "-fx-font-size: 14px; " +
                            "-fx-cursor: hand;"));

            urlBox.getChildren().add(urlLabel);
            solutionContainer.getChildren().add(urlBox);
        }

        // Display solution author
        if (solutionUploadedBy != null) {
            try {
                var userResponse = ApiClient.get("/users/" + solutionUploadedBy);
                if (userResponse.isSuccessful()) {
                    String userBody = userResponse.body() != null ? userResponse.body().string() : "{}";
                    Map<String, Object> user = gson.fromJson(userBody, new TypeToken<Map<String, Object>>(){}.getType());
                    String userName = (String) user.getOrDefault("name", "Unknown");
                    Label authorLabel = new Label("👤 From: " + userName);
                    authorLabel.setStyle(Styles.subtitleLabel());
                    authorLabel.setPadding(new Insets(5, 0, 0, 0));
                    solutionContainer.getChildren().add(authorLabel);
                }
            } catch (IOException e) {
                // Ignore
            }
        }

        if (solutionUploadedAt != null && !solutionUploadedAt.isEmpty()) {
            Label dateLabel = new Label("📅 Added: " + solutionUploadedAt);
            dateLabel.setStyle(Styles.subtitleLabel());
            dateLabel.setPadding(new Insets(5, 0, 0, 0));
            solutionContainer.getChildren().add(dateLabel);
        }

        // Add button for adding/editing solution (only for students who are members)
        if (isMember && !checkIfTeacher(groupId)) {
            Long userId = ApiClient.getCurrentUserId();
            boolean isMySolution = solutionUploadedBy != null && userId != null && solutionUploadedBy.equals(userId);
            String buttonText = isMySolution ? "✏️ Edit Solution" : "➕ Add Solution";

            Button solutionButton = new Button(buttonText);
            solutionButton.setStyle(Styles.secondaryButton());
            solutionButton.setOnMouseEntered(e -> solutionButton.setStyle(
                    "-fx-background-color: #43a047; " +
                            "-fx-text-fill: white; " +
                            "-fx-font-size: 14px; " +
                            "-fx-font-weight: bold; " +
                            "-fx-padding: 10 20; " +
                            "-fx-background-radius: 8; " +
                            "-fx-cursor: hand; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 3);"));
            solutionButton.setOnMouseExited(e -> solutionButton.setStyle(Styles.secondaryButton()));
            solutionButton.setOnAction(e -> showAddSolutionDialog(task));
            solutionContainer.getChildren().add(solutionButton);
        }

        // Add solution approval button for administrators
        Object statusObj = task.get("status");
        String status = statusObj instanceof String ? (String) statusObj :
                statusObj != null ? statusObj.toString() : "OPEN";
        boolean hasSolution = (solutionText != null && !solutionText.isEmpty()) ||
                (solutionUrl != null && !solutionUrl.isEmpty());
        boolean isCompleted = "COMPLETED".equals(status);

        if (isMember && checkIfTeacher(groupId) && hasSolution && !isCompleted) {
            Button approveButton = new Button("✓ Approve solution");
            approveButton.setStyle("-fx-background-color: #4caf50; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-size: 14px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-padding: 10 20; " +
                    "-fx-background-radius: 8; " +
                    "-fx-cursor: hand; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 4, 0, 0, 2);");
            approveButton.setOnMouseEntered(e -> approveButton.setStyle(
                    "-fx-background-color: #43a047; " +
                            "-fx-text-fill: white; " +
                            "-fx-font-size: 14px; " +
                            "-fx-font-weight: bold; " +
                            "-fx-padding: 10 20; " +
                            "-fx-background-radius: 8; " +
                            "-fx-cursor: hand; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 6, 0, 0, 3);"));
            approveButton.setOnMouseExited(e -> approveButton.setStyle(
                    "-fx-background-color: #4caf50; " +
                            "-fx-text-fill: white; " +
                            "-fx-font-size: 14px; " +
                            "-fx-font-weight: bold; " +
                            "-fx-padding: 10 20; " +
                            "-fx-background-radius: 8; " +
                            "-fx-cursor: hand; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 4, 0, 0, 2);"));
            approveButton.setOnAction(e -> approveSolution(task));
            solutionContainer.getChildren().add(approveButton);
        }

        return solutionContainer;
    }

    private void showAddSolutionDialog(Map<String, Object> task) {
        Dialog<Map<String, Object>> dialog = new Dialog<>();
        Long solutionUploadedBy = task.get("solutionUploadedBy") != null ?
                ((Number) task.get("solutionUploadedBy")).longValue() : null;
        Long userId = ApiClient.getCurrentUserId();
        boolean isUpdate = solutionUploadedBy != null && userId != null && solutionUploadedBy.equals(userId);

        dialog.setTitle(isUpdate ? "Edit solution" : "Submit solution");
        dialog.setHeaderText("Enter your solution");

        Label textLabel = new Label("Solution text:");
        textLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #000000;");
        TextArea textArea = new TextArea();
        textArea.setPromptText("Describe your solution...");
        textArea.setPrefRowCount(6);
        textArea.setPrefWidth(500);
        textArea.setWrapText(true);
        textArea.setStyle("-fx-background-color: white; " +
                "-fx-border-color: #e0e0e0; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 4; " +
                "-fx-background-radius: 4; " +
                "-fx-padding: 12 16; " +
                "-fx-font-size: 14px;");

        // Fill if solution already exists
        String existingText = (String) task.get("solutionText");
        if (existingText != null && !existingText.isEmpty()) {
            textArea.setText(existingText);
        }

        textArea.focusedProperty().addListener((obs, oldVal, newVal) -> {
            textArea.setStyle(newVal ?
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

        Label urlLabel = new Label("Link (optional):");
        urlLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #000000;");
        TextField urlField = new TextField();
        urlField.setPromptText("https://...");
        urlField.setPrefWidth(500);
        urlField.setPrefHeight(48);
        urlField.setStyle("-fx-background-color: white; " +
                "-fx-border-color: #e0e0e0; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 4; " +
                "-fx-background-radius: 4; " +
                "-fx-padding: 12 16; " +
                "-fx-font-size: 14px;");

        // Fill if link already exists
        String existingUrl = (String) task.get("solutionUrl");
        if (existingUrl != null && !existingUrl.isEmpty()) {
            urlField.setText(existingUrl);
        }

        urlField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            urlField.setStyle(newVal ?
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
                textLabel, textArea,
                urlLabel, urlField
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                Long currentUserId = ApiClient.getCurrentUserId();
                if (currentUserId == null) {
                    showAlert("Error", "User not authorized", Alert.AlertType.ERROR);
                    return null;
                }

                String solutionText = textArea.getText() != null ? textArea.getText().trim() : "";
                String solutionUrl = urlField.getText() != null ? urlField.getText().trim() : "";

                if (solutionText.isEmpty() && solutionUrl.isEmpty()) {
                    showAlert("Error", "Enter solution text or link", Alert.AlertType.ERROR);
                    return null;
                }

                Map<String, Object> request = new java.util.HashMap<>();
                request.put("userId", currentUserId);
                request.put("solutionText", solutionText);
                request.put("solutionUrl", solutionUrl);

                return request;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            if (result == null) return;
            try {
                Long taskId = ((Number) task.get("taskId")).longValue();
                String existingSolution = (String) task.get("solutionText");
                boolean solutionExists = existingSolution != null && !existingSolution.isEmpty();

                var response = solutionExists ?
                        ApiClient.put("/tasks/" + taskId + "/solution", result) :
                        ApiClient.post("/tasks/" + taskId + "/solution", result);

                if (response.isSuccessful()) {
                    showAlert("Success", "Solution " + (solutionExists ? "updated" : "submitted") + " successfully", Alert.AlertType.INFORMATION);
                    loadTasks();
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "";
                    showAlert("Error", "Failed to submit solution. Code: " + response.code() +
                            (errorBody.isEmpty() ? "" : "\n" + errorBody), Alert.AlertType.ERROR);
                }
            } catch (IOException e) {
                showAlert("Error", "Connection error: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    private void approveSolution(Map<String, Object> task) {
        try {
            Long taskId = ((Number) task.get("taskId")).longValue();
            Long userId = ApiClient.getCurrentUserId();
            if (userId == null) {
                showAlert("Error", "User not authorized", Alert.AlertType.ERROR);
                return;
            }

            Map<String, Object> request = new java.util.HashMap<>();
            request.put("teacherId", userId);
            var response = ApiClient.post("/tasks/" + taskId + "/solution/approve", request);

            if (response.isSuccessful()) {
                showAlert("Success", "Solution approved. Task marked as completed.", Alert.AlertType.INFORMATION);
                loadTasks(); // Update task list
                // Update task details if they are open
                try {
                    var taskResponse = ApiClient.get("/tasks/" + taskId);
                    if (taskResponse.isSuccessful()) {
                        String taskBody = taskResponse.body() != null ? taskResponse.body().string() : "{}";
                        Map<String, Object> updatedTask = gson.fromJson(taskBody, new TypeToken<Map<String, Object>>(){}.getType());
                        // Check if task details are open
                        if (contentArea.getCenter() instanceof ScrollPane) {
                            ScrollPane scrollPane = (ScrollPane) contentArea.getCenter();
                            if (scrollPane.getContent() instanceof VBox) {
                                VBox content = (VBox) scrollPane.getContent();
                                // Check if this is task details (has "← Back" button)
                                if (content.getChildren().size() > 0 &&
                                        content.getChildren().get(0) instanceof HBox) {
                                    HBox topBar = (HBox) content.getChildren().get(0);
                                    if (topBar.getChildren().size() > 0 &&
                                            topBar.getChildren().get(0) instanceof Button) {
                                        Button backBtn = (Button) topBar.getChildren().get(0);
                                        if (backBtn.getText().equals("← Back")) {
                                            showTaskDetailsDialog(updatedTask);
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (IOException ex) {
                    // Ignore
                }
            } else {
                String errorBody = response.body() != null ? response.body().string() : "";
                showAlert("Error", "Failed to approve solution. Code: " + response.code() +
                        (errorBody.isEmpty() ? "" : "\n" + errorBody), Alert.AlertType.ERROR);
            }
        } catch (IOException e) {
            showAlert("Error", "Connection error: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private VBox createCommentsSection(Map<String, Object> task, Long groupId, boolean isMember) {
        VBox commentsBox = new VBox(10);
        commentsBox.setPadding(new Insets(20, 0, 0, 0));

        Label commentsTitle = new Label("💬 Comments:");
        commentsTitle.setStyle("-fx-text-fill: #000000; -fx-font-size: 18px; -fx-font-weight: bold;");
        commentsBox.getChildren().add(commentsTitle);

        // Load comments
        Long taskId = ((Number) task.get("taskId")).longValue();
        VBox commentsList = new VBox(10);
        commentsList.setPadding(new Insets(10, 0, 0, 0));

        try {
            var response = ApiClient.get("/comments/task/" + taskId);
            if (response.isSuccessful()) {
                String body = response.body() != null ? response.body().string() : "[]";
                Type listType = new TypeToken<List<Map<String, Object>>>(){}.getType();
                List<Map<String, Object>> comments = gson.fromJson(body, listType);

                if (comments.isEmpty()) {
                    Label noCommentsLabel = new Label("No comments yet");
                    noCommentsLabel.setStyle("-fx-text-fill: #757575; -fx-font-size: 14px; -fx-font-style: italic;");
                    commentsList.getChildren().add(noCommentsLabel);
                } else {
                    for (Map<String, Object> comment : comments) {
                        commentsList.getChildren().add(createCommentCard(comment, groupId, commentsList, task));
                    }
                }
            }
        } catch (IOException e) {
            Label errorLabel = new Label("Error loading comments");
            errorLabel.setStyle("-fx-text-fill: #f44336; -fx-font-size: 14px;");
            commentsList.getChildren().add(errorLabel);
        }

        commentsBox.getChildren().add(commentsList);

        // Add form for new comment (only for members)
        if (isMember) {
            HBox addCommentBox = new HBox(10);
            addCommentBox.setAlignment(Pos.CENTER_LEFT);

            TextArea commentField = new TextArea();
            commentField.setPromptText("Add comment...");
            commentField.setPrefRowCount(3);
            commentField.setPrefWidth(600);
            commentField.setWrapText(true);
            commentField.setStyle("-fx-background-color: white; " +
                    "-fx-border-color: #e0e0e0; " +
                    "-fx-border-width: 1; " +
                    "-fx-border-radius: 4; " +
                    "-fx-background-radius: 4; " +
                    "-fx-padding: 10; " +
                    "-fx-font-size: 14px;");
            commentField.focusedProperty().addListener((obs, oldVal, newVal) -> {
                commentField.setStyle(newVal ?
                        "-fx-background-color: white; " +
                                "-fx-border-color: #000000; " +
                                "-fx-border-width: 1; " +
                                "-fx-border-radius: 4; " +
                                "-fx-background-radius: 4; " +
                                "-fx-padding: 10; " +
                                "-fx-font-size: 14px;" :
                        "-fx-background-color: white; " +
                                "-fx-border-color: #e0e0e0; " +
                                "-fx-border-width: 1; " +
                                "-fx-border-radius: 4; " +
                                "-fx-background-radius: 4; " +
                                "-fx-padding: 10; " +
                                "-fx-font-size: 14px;");
            });

            Button addButton = new Button("Add");
            addButton.setStyle("-fx-background-color: #000000; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-size: 14px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-padding: 10 20; " +
                    "-fx-background-radius: 4; " +
                    "-fx-cursor: hand;");
            addButton.setOnMouseEntered(e -> addButton.setStyle(
                    "-fx-background-color: #333333; " +
                            "-fx-text-fill: white; " +
                            "-fx-font-size: 14px; " +
                            "-fx-font-weight: bold; " +
                            "-fx-padding: 10 20; " +
                            "-fx-background-radius: 4; " +
                            "-fx-cursor: hand;"));
            addButton.setOnMouseExited(e -> addButton.setStyle(
                    "-fx-background-color: #000000; " +
                            "-fx-text-fill: white; " +
                            "-fx-font-size: 14px; " +
                            "-fx-font-weight: bold; " +
                            "-fx-padding: 10 20; " +
                            "-fx-background-radius: 4; " +
                            "-fx-cursor: hand;"));
            addButton.setOnAction(e -> {
                String text = commentField.getText() != null ? commentField.getText().trim() : "";
                if (text.isEmpty()) {
                    showAlert("Error", "Enter comment text", Alert.AlertType.ERROR);
                    return;
                }
                addComment(taskId, text, commentsList, commentField);
            });

            addCommentBox.getChildren().addAll(commentField, addButton);
            commentsBox.getChildren().add(addCommentBox);
        }

        return commentsBox;
    }

    private VBox createCommentCard(Map<String, Object> comment, Long groupId, VBox commentsList, Map<String, Object> task) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(12));
        card.setStyle("-fx-background-color: #f9f9f9; " +
                "-fx-border-color: #e0e0e0; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 4; " +
                "-fx-background-radius: 4;");

        // Header with user name and date
        HBox headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Long userId = ((Number) comment.get("userId")).longValue();
        String userName = "User";
        try {
            var userResponse = ApiClient.get("/users/" + userId);
            if (userResponse.isSuccessful()) {
                String userBody = userResponse.body() != null ? userResponse.body().string() : "{}";
                Map<String, Object> user = gson.fromJson(userBody, new TypeToken<Map<String, Object>>(){}.getType());
                userName = (String) user.getOrDefault("name", "User");
            }
        } catch (IOException e) {
            // Ignore
        }

        Label nameLabel = new Label(userName);
        nameLabel.setStyle("-fx-text-fill: #000000; -fx-font-size: 14px; -fx-font-weight: bold;");

        String createdAt = (String) comment.getOrDefault("createdAt", "");
        if (!createdAt.isEmpty() && createdAt.length() > 10) {
            createdAt = createdAt.substring(0, 10) + " " + createdAt.substring(11, 16);
        }
        Label dateLabel = new Label(createdAt);
        dateLabel.setStyle("-fx-text-fill: #757575; -fx-font-size: 12px;");

        HBox.setHgrow(nameLabel, Priority.ALWAYS);
        headerBox.getChildren().addAll(nameLabel, dateLabel);

        // Comment text
        String text = (String) comment.getOrDefault("text", "");
        Label textLabel = new Label(text);
        textLabel.setWrapText(true);
        textLabel.setStyle("-fx-text-fill: #000000; -fx-font-size: 14px;");

        card.getChildren().addAll(headerBox, textLabel);

        // Delete button (only for author or administrator)
        Long currentUserId = ApiClient.getCurrentUserId();
        boolean isAuthor = currentUserId != null && currentUserId.equals(userId);
        boolean isTeacher = checkIfTeacher(groupId);

        if (isAuthor || isTeacher) {
            HBox actionsBox = new HBox(10);
            actionsBox.setAlignment(Pos.CENTER_RIGHT);
            actionsBox.setPadding(new Insets(5, 0, 0, 0));

            Button deleteButton = new Button("Delete");
            deleteButton.setStyle("-fx-background-color: transparent; " +
                    "-fx-text-fill: #f44336; " +
                    "-fx-font-size: 12px; " +
                    "-fx-cursor: hand; " +
                    "-fx-border-color: #f44336; " +
                    "-fx-border-width: 1; " +
                    "-fx-border-radius: 4; " +
                    "-fx-background-radius: 4; " +
                    "-fx-padding: 5 10;");
            deleteButton.setOnMouseEntered(e -> deleteButton.setStyle(
                    "-fx-background-color: #f44336; " +
                            "-fx-text-fill: white; " +
                            "-fx-font-size: 12px; " +
                            "-fx-cursor: hand; " +
                            "-fx-border-color: #f44336; " +
                            "-fx-border-width: 1; " +
                            "-fx-border-radius: 4; " +
                            "-fx-background-radius: 4; " +
                            "-fx-padding: 5 10;"));
            deleteButton.setOnMouseExited(e -> deleteButton.setStyle(
                    "-fx-background-color: transparent; " +
                            "-fx-text-fill: #f44336; " +
                            "-fx-font-size: 12px; " +
                            "-fx-cursor: hand; " +
                            "-fx-border-color: #f44336; " +
                            "-fx-border-width: 1; " +
                            "-fx-border-radius: 4; " +
                            "-fx-background-radius: 4; " +
                            "-fx-padding: 5 10;"));

            Long commentId = ((Number) comment.get("commentId")).longValue();
            Long taskIdForComment = ((Number) task.get("taskId")).longValue();
            deleteButton.setOnAction(e -> deleteComment(commentId, taskIdForComment, commentsList, groupId));

            actionsBox.getChildren().add(deleteButton);
            card.getChildren().add(actionsBox);
        }

        return card;
    }

    private void addComment(Long taskId, String text, VBox commentsList, TextArea commentField) {
        try {
            Long userId = ApiClient.getCurrentUserId();
            if (userId == null) {
                showAlert("Error", "User is not authorized", Alert.AlertType.ERROR);
                return;
            }

            Map<String, Object> request = new java.util.HashMap<>();
            request.put("taskId", taskId);
            request.put("userId", userId);
            request.put("text", text);

            var response = ApiClient.post("/comments", request);

            if (response.isSuccessful()) {
                commentField.clear();
                // Update comment list
                commentsList.getChildren().clear();
                var commentsResponse = ApiClient.get("/comments/task/" + taskId);
                if (commentsResponse.isSuccessful()) {
                    String body = commentsResponse.body() != null ? commentsResponse.body().string() : "[]";
                    Type listType = new TypeToken<List<Map<String, Object>>>(){}.getType();
                    List<Map<String, Object>> comments = gson.fromJson(body, listType);

                    Long groupId = ((Number) group.get("groupId")).longValue();
                    Map<String, Object> taskMap = new java.util.HashMap<>();
                    taskMap.put("taskId", taskId);
                    for (Map<String, Object> comment : comments) {
                        commentsList.getChildren().add(createCommentCard(comment, groupId, commentsList, taskMap));
                    }
                }
            } else {
                String errorBody = response.body() != null ? response.body().string() : "";
                showAlert("Error", "Failed to add comment. Code: " + response.code() +
                        (errorBody.isEmpty() ? "" : "\n" + errorBody), Alert.AlertType.ERROR);
            }
        } catch (IOException e) {
            showAlert("Error", "Connection error: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void deleteComment(Long commentId, Long taskId, VBox commentsList, Long groupId) {
        try {
            Long userId = ApiClient.getCurrentUserId();
            if (userId == null) {
                showAlert("Error", "User is not authorized", Alert.AlertType.ERROR);
                return;
            }

            var response = ApiClient.delete("/comments/" + commentId + "?userId=" + userId);

            if (response.isSuccessful()) {
                // Update comment list
                commentsList.getChildren().clear();
                var commentsResponse = ApiClient.get("/comments/task/" + taskId);
                if (commentsResponse.isSuccessful()) {
                    String body = commentsResponse.body() != null ? commentsResponse.body().string() : "[]";
                    Type listType = new TypeToken<List<Map<String, Object>>>(){}.getType();
                    List<Map<String, Object>> comments = gson.fromJson(body, listType);

                    if (comments.isEmpty()) {
                        Label noCommentsLabel = new Label("No comments yet");
                        noCommentsLabel.setStyle("-fx-text-fill: #757575; -fx-font-size: 14px; -fx-font-style: italic;");
                        commentsList.getChildren().add(noCommentsLabel);
                    } else {
                        Map<String, Object> task = Map.of("taskId", taskId);
                        for (Map<String, Object> comment : comments) {
                            commentsList.getChildren().add(createCommentCard(comment, groupId, commentsList, task));
                        }
                    }
                }
            } else {
                String errorBody = response.body() != null ? response.body().string() : "";
                showAlert("Error", "Failed to delete comment. Code: " + response.code() +
                        (errorBody.isEmpty() ? "" : "\n" + errorBody), Alert.AlertType.ERROR);
            }
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

