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
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

public class StatisticsController {
    private final BorderPane root;
    private final MainController mainController;
    private final Long groupId;
    private final Gson gson = new Gson();

    public StatisticsController(MainController mainController, Long groupId) {
        this.mainController = mainController;
        this.groupId = groupId;
        this.root = new BorderPane();
        createUI();
        loadStatistics();
    }

    private void createUI() {
        // Top bar
        HBox topBar = new HBox(10);
        topBar.setPadding(new Insets(15));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle(Styles.topBar());

        Button backButton = new Button("← Back");
        backButton.setStyle(Styles.whiteButton());
        backButton.setOnMouseEntered(e -> backButton.setStyle(
                "-fx-background-color: #f5f5f5; " +
                        "-fx-text-fill: " + Styles.PRIMARY_COLOR + "; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 10 20; " +
                        "-fx-background-radius: 8; " +
                        "-fx-cursor: hand; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 3);"));
        backButton.setOnMouseExited(e -> backButton.setStyle(Styles.whiteButton()));
        backButton.setOnAction(e -> {
            // Return to group details
            mainController.showGroupScreen();
        });

        Label titleLabel = new Label("📊 Group Statistics");
        titleLabel.setFont(new Font(20));
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        HBox.setHgrow(titleLabel, Priority.ALWAYS);
        topBar.getChildren().addAll(backButton, titleLabel);
        root.setTop(topBar);

        // Main content
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: " + Styles.BACKGROUND_COLOR + ";");

        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: " + Styles.BACKGROUND_COLOR + ";");

        scrollPane.setContent(content);
        root.setCenter(scrollPane);
    }

    private void loadStatistics() {
        try {
            var response = ApiClient.get("/statistics/group/" + groupId + "/overall");
            if (response.isSuccessful()) {
                String body = response.body() != null ? response.body().string() : "{}";
                Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
                Map<String, Object> stats = gson.fromJson(body, mapType);

                ScrollPane scrollPane = (ScrollPane) root.getCenter();
                VBox content = (VBox) scrollPane.getContent();
                content.getChildren().clear();

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
            }
        } catch (IOException e) {
            showAlert("Error", "Failed to load statistics", Alert.AlertType.ERROR);
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
                String shortDate = date.substring(5); // YYYY-MM-DD -> MM-DD
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
                String shortDate = date.substring(5); // YYYY-MM-DD -> MM-DD
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
                series.getData().add(new XYChart.Data<>(name.length() > 15 ? name.substring(0, 15) + "..." : name, count));
            });
        }
        chart.getData().add(series);

        return chart;
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

