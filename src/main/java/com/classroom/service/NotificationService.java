package com.classroom.service;

import com.classroom.websocket.NotificationWebSocketHandler;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationWebSocketHandler webSocketHandler;
    private final Gson gson = new Gson();

    public void sendNotification(Long groupId, String type, String message, Long userId) {
        Map<String, Object> notification = Map.of(
                "type", type,
                "message", message,
                "groupId", groupId,
                "userId", userId
        );

        // Send via regular WebSocket (for JavaFX)
        String notificationJson = gson.toJson(notification);
        System.out.println("📤 Sending notification via WebSocket: " + notificationJson);
        webSocketHandler.sendNotificationToAll(notificationJson);
    }
}

