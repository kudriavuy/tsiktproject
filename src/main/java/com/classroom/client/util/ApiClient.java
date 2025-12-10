package com.classroom.client.util;

import com.google.gson.Gson;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class ApiClient {
    private static final String BASE_URL = "http://localhost:8080/api";
    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new Gson();
    private static String authToken = null;
    private static Long currentUserId = null;
    private static final String TOKEN_FILE = System.getProperty("user.home") + File.separator + ".classroom_token";
    private static final String USER_DATA_FILE = System.getProperty("user.home") + File.separator + ".classroom_user.json";

    static {
        // Load token and userId on startup
        loadSavedCredentials();
    }

    private static void loadSavedCredentials() {
        try {
            // Load token
            Path tokenPath = Paths.get(TOKEN_FILE);
            if (Files.exists(tokenPath)) {
                authToken = Files.readString(tokenPath).trim();
                if (authToken.isEmpty()) {
                    authToken = null;
                }
            }

            // Load userId
            Path userDataPath = Paths.get(USER_DATA_FILE);
            if (Files.exists(userDataPath)) {
                String userDataJson = Files.readString(userDataPath);
                @SuppressWarnings("unchecked")
                Map<String, Object> userData = gson.fromJson(userDataJson, Map.class);
                if (userData != null && userData.get("userId") != null) {
                    currentUserId = ((Number) userData.get("userId")).longValue();
                }
            }

            // Validate token
            if (authToken != null) {
                validateToken();
            }
        } catch (IOException e) {
            // If failed to load, just continue without token
            authToken = null;
            currentUserId = null;
        }
    }

    private static void validateToken() {
        try {
            // Validate token by making request to server
            var response = get("/oauth2/user");
            if (!response.isSuccessful() || response.code() == 401) {
                // Token is invalid, clear it
                authToken = null;
                currentUserId = null;
                clearSavedCredentials();
            } else {
                // Token is valid, get userId from response
                try {
                    Map<String, Object> result = parseResponse(response);
                    if (Boolean.TRUE.equals(result.get("success"))) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> user = (Map<String, Object>) result.get("user");
                        if (user != null && user.get("userId") != null) {
                            currentUserId = ((Number) user.get("userId")).longValue();
                            saveUserData();
                        }
                    }
                } catch (Exception e) {
                    // Ignore parsing errors
                }
            }
        } catch (IOException e) {
            // If failed to validate, keep token (may be network issue)
        }
    }

    private static void saveUserData() {
        try {
            Map<String, Object> userData = new HashMap<>();
            userData.put("userId", currentUserId);
            String json = gson.toJson(userData);
            Files.writeString(Paths.get(USER_DATA_FILE), json);
        } catch (IOException e) {
            // Ignore save errors
        }
    }

    private static void clearSavedCredentials() {
        try {
            Files.deleteIfExists(Paths.get(TOKEN_FILE));
            Files.deleteIfExists(Paths.get(USER_DATA_FILE));
        } catch (IOException e) {
            // Ignore delete errors
        }
    }

    public static void setAuthToken(String token) {
        authToken = token;
        // Save token to file
        if (token != null && !token.isEmpty()) {
            try {
                Files.writeString(Paths.get(TOKEN_FILE), token);
            } catch (IOException e) {
                // Ignore save errors
            }
        } else {
            clearSavedCredentials();
        }
    }

    public static String getAuthToken() {
        return authToken;
    }

    public static void setCurrentUserId(Long userId) {
        currentUserId = userId;
        // Save userId to file
        if (userId != null) {
            saveUserData();
        }
    }

    public static void clearAuth() {
        authToken = null;
        currentUserId = null;
        clearSavedCredentials();
    }

    public static boolean isAuthenticated() {
        return authToken != null && !authToken.isEmpty();
    }

    public static Long getCurrentUserId() {
        return currentUserId;
    }

    public static Response post(String endpoint, Object body) throws IOException {
        RequestBody requestBody = RequestBody.create(
                gson.toJson(body),
                MediaType.parse("application/json")
        );

        Request.Builder requestBuilder = new Request.Builder()
                .url(BASE_URL + endpoint)
                .post(requestBody);

        if (authToken != null) {
            requestBuilder.addHeader("Authorization", "Bearer " + authToken);
        }

        return client.newCall(requestBuilder.build()).execute();
    }

    public static Response get(String endpoint) throws IOException {
        Request.Builder requestBuilder = new Request.Builder()
                .url(BASE_URL + endpoint);

        if (authToken != null) {
            requestBuilder.addHeader("Authorization", "Bearer " + authToken);
        }

        return client.newCall(requestBuilder.build()).execute();
    }

    public static Response put(String endpoint, Object body) throws IOException {
        RequestBody requestBody = RequestBody.create(
                gson.toJson(body),
                MediaType.parse("application/json")
        );

        Request.Builder requestBuilder = new Request.Builder()
                .url(BASE_URL + endpoint)
                .put(requestBody);

        if (authToken != null) {
            requestBuilder.addHeader("Authorization", "Bearer " + authToken);
        }

        return client.newCall(requestBuilder.build()).execute();
    }

    public static Response delete(String endpoint) throws IOException {
        Request.Builder requestBuilder = new Request.Builder()
                .url(BASE_URL + endpoint)
                .delete();

        if (authToken != null) {
            requestBuilder.addHeader("Authorization", "Bearer " + authToken);
        }

        return client.newCall(requestBuilder.build()).execute();
    }

    public static Response uploadFile(String endpoint, java.io.File file) throws IOException {
        RequestBody fileBody = RequestBody.create(
                file,
                MediaType.parse("application/octet-stream")
        );

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), fileBody)
                .build();

        Request.Builder requestBuilder = new Request.Builder()
                .url(BASE_URL + endpoint)
                .post(requestBody);

        if (authToken != null) {
            requestBuilder.addHeader("Authorization", "Bearer " + authToken);
        }

        return client.newCall(requestBuilder.build()).execute();
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> parseResponse(Response response) throws IOException {
        String body = response.body() != null ? response.body().string() : "{}";
        return gson.fromJson(body, Map.class);
    }
}

