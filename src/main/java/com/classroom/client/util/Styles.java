package com.classroom.client.util;

public class Styles {
    // Colors
    public static final String PRIMARY_COLOR = "#1976d2";
    public static final String PRIMARY_DARK = "#1565c0";
    public static final String PRIMARY_LIGHT = "#42a5f5";
    public static final String SECONDARY_COLOR = "#4caf50";
    public static final String ERROR_COLOR = "#d32f2f";
    public static final String BACKGROUND_COLOR = "#f5f7fa";
    public static final String CARD_BACKGROUND = "#ffffff";
    public static final String TEXT_PRIMARY = "#212121";
    public static final String TEXT_SECONDARY = "#757575";
    public static final String BORDER_COLOR = "#e0e0e0";

    // Top Bar
    public static String topBar() {
        return "-fx-background-color: linear-gradient(to right, " + PRIMARY_COLOR + ", " + PRIMARY_LIGHT + "); " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);";
    }

    // Buttons
    public static String primaryButton() {
        return "-fx-background-color: " + PRIMARY_COLOR + "; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 10 20; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2);";
    }

    public static String primaryButtonHover() {
        return "-fx-background-color: " + PRIMARY_DARK + "; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 10 20; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 3);";
    }

    public static String secondaryButton() {
        return "-fx-background-color: " + SECONDARY_COLOR + "; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 10 20; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2);";
    }

    public static String whiteButton() {
        return "-fx-background-color: white; " +
                "-fx-text-fill: " + PRIMARY_COLOR + "; " +
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 10 20; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);";
    }

    public static String errorButton() {
        return "-fx-background-color: " + ERROR_COLOR + "; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 10 20; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2);";
    }

    // Cards
    public static String card() {
        return "-fx-background-color: " + CARD_BACKGROUND + "; " +
                "-fx-border-color: " + BORDER_COLOR + "; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 12; " +
                "-fx-background-radius: 12; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 2); " +
                "-fx-cursor: hand;";
    }

    public static String cardHover() {
        return "-fx-background-color: " + CARD_BACKGROUND + "; " +
                "-fx-border-color: " + PRIMARY_LIGHT + "; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 12; " +
                "-fx-background-radius: 12; " +
                "-fx-effect: dropshadow(gaussian, rgba(25,118,210,0.2), 15, 0, 0, 4); " +
                "-fx-cursor: hand;";
    }

    // Input Fields
    public static String inputField() {
        return "-fx-background-color: white; " +
                "-fx-border-color: " + BORDER_COLOR + "; " +
                "-fx-border-width: 1.5; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8; " +
                "-fx-padding: 12; " +
                "-fx-font-size: 14px;";
    }

    public static String inputFieldFocused() {
        return "-fx-background-color: white; " +
                "-fx-border-color: " + PRIMARY_COLOR + "; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8; " +
                "-fx-padding: 12; " +
                "-fx-font-size: 14px; " +
                "-fx-effect: dropshadow(gaussian, rgba(25,118,210,0.2), 5, 0, 0, 1);";
    }

    // Labels
    public static String titleLabel() {
        return "-fx-text-fill: " + TEXT_PRIMARY + "; " +
                "-fx-font-size: 24px; " +
                "-fx-font-weight: bold;";
    }

    public static String subtitleLabel() {
        return "-fx-text-fill: " + TEXT_SECONDARY + "; " +
                "-fx-font-size: 14px;";
    }

    public static String headingLabel() {
        return "-fx-text-fill: " + PRIMARY_COLOR + "; " +
                "-fx-font-size: 18px; " +
                "-fx-font-weight: bold;";
    }

    // Background
    public static String background() {
        return "-fx-background-color: " + BACKGROUND_COLOR + ";";
    }

    // Photo View
    public static String photoView() {
        return "-fx-border-radius: 75; " +
                "-fx-background-radius: 75; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 3);";
    }
}

