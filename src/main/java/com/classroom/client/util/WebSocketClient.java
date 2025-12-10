package com.classroom.client.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import okhttp3.*;
import okio.ByteString;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.Consumer;

public class WebSocketClient {
    private static final String WS_URL = "ws://localhost:8080/notifications";
    private static final Gson gson = new Gson();
    private static WebSocket webSocket;
    private static OkHttpClient client = new OkHttpClient();
    private static Consumer<Map<String, Object>> notificationHandler;

    public static void connect(Consumer<Map<String, Object>> handler) {
        notificationHandler = handler;
        try {
            Request request = new Request.Builder()
                    .url(WS_URL)
                    .build();

            webSocket = client.newWebSocket(request, new WebSocketListener() {
                @Override
                public void onOpen(WebSocket webSocket, Response response) {
                    System.out.println("✅ WebSocket connected successfully to: " + WS_URL);
                    // Don't show alert to avoid disturbing user
                    // Just log to console
                }

                @Override
                public void onMessage(WebSocket webSocket, String text) {
                    System.out.println("📥 Received WebSocket message: " + text);

                    try {
                        Type type = new TypeToken<Map<String, Object>>(){}.getType();
                        Map<String, Object> notification = gson.fromJson(text, type);
                        System.out.println("✅ Parsed notification: " + notification);

                        // Handle notifications in JavaFX Application Thread
                        Platform.runLater(() -> {
                            if (notificationHandler != null) {
                                notificationHandler.accept(notification);
                            } else {
                                System.err.println("⚠️ notificationHandler is null");
                            }
                        });
                    } catch (Exception e) {
                        System.err.println("❌ Error parsing notification: " + e.getMessage());
                        e.printStackTrace();
                    }
                }

                @Override
                public void onMessage(WebSocket webSocket, ByteString bytes) {
                    onMessage(webSocket, bytes.utf8());
                }

                @Override
                public void onClosing(WebSocket webSocket, int code, String reason) {
                    webSocket.close(1000, null);
                }

                @Override
                public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                    System.err.println("❌ WebSocket error: " + t.getMessage());
                    if (response != null) {
                        System.err.println("Response code: " + response.code());
                    }
                    Platform.runLater(() -> {
                        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                                javafx.scene.control.Alert.AlertType.ERROR);
                        alert.setTitle("WebSocket Error");
                        alert.setHeaderText(null);
                        alert.setContentText("WebSocket connection error: " + t.getMessage());
                        alert.show();
                    });
                }
            });
        } catch (Exception e) {
            System.err.println("Failed to connect WebSocket: " + e.getMessage());
        }
    }

    public static void disconnect() {
        if (webSocket != null) {
            webSocket.close(1000, "Client disconnect");
            webSocket = null;
        }
    }

    public static boolean isConnected() {
        return webSocket != null;
    }
}

