
package com.lostfound.desktop.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.*;

public class DashboardApi {

    private static final String BASE = "http://localhost:8080/api";

    private static final HttpClient CLIENT =
            HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(5))
                    .build();

    private static final ObjectMapper JSON =
        new ObjectMapper().findAndRegisterModules();

    private static String request(
            String method, String path, Object data)
            throws Exception {

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE + path))
                .timeout(Duration.ofSeconds(15))
                .header("Content-Type", "application/json");

        if (method.equals("GET")) {
            builder.GET();
        } else {
            builder.method(method,
                    HttpRequest.BodyPublishers.ofString(
                            JSON.writeValueAsString(data)));
        }

        HttpResponse<String> response = CLIENT.send(
                builder.build(),
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200
                || response.statusCode() >= 300) {
            String message = "HTTP " + response.statusCode();

            try {
                var error = JSON.readTree(response.body());
                message = error.path("message").asText(message);
            } catch (Exception ignored) {
            }

            throw new Exception(message);
        }

        return response.body();
    }

    public static List<Map<String, Object>> items(String status)
            throws Exception {
        return JSON.readValue(
                request("GET", "/items/" + status, null),
                new TypeReference<>() {});
    }

    public static List<Map<String, Object>> claims(long userId)
        throws Exception {

    String response = request(
            "GET",
            "/claims/my?userId=" + userId,
            null
    );

    return JSON.readValue(
            response,
            new TypeReference<List<Map<String, Object>>>() {}
    );
}

    public static List<Map<String, Object>> notifications(long userId)
        throws Exception {

    String response = request(
            "GET",
            "/notifications?userId=" + userId,
            null
    );

    return JSON.readValue(
            response,
            new TypeReference<List<Map<String, Object>>>() {}
    );
}

    public static Map<String, Object> profile(long userId)
            throws Exception {
        return JSON.readValue(
                request("GET", "/profile?userId=" + userId, null),
                new TypeReference<>() {});
    }

    public static void createItem(
            long userId, String status, Map<String, Object> item)
            throws Exception {
        request("POST",
                "/items/" + status + "?userId=" + userId, item);
    }

   public static void createClaim(
        long userId, long itemId, String message)
        throws Exception {

    List<Map<String, Object>> foundItems = items("FOUND");

    boolean isFoundItem = foundItems.stream()
            .anyMatch(item ->
                    String.valueOf(item.get("itemId"))
                            .equals(String.valueOf(itemId)));

    if (!isFoundItem) {
        throw new Exception(
                "You can claim only FOUND items.");
    }

    if (message == null || message.isBlank()) {
        throw new Exception(
                "Please enter a claim message.");
    }

    request(
            "POST",
            "/claims?userId=" + userId + "&itemId=" + itemId,
            Map.of("claimMessage", message.trim())
    );
}
}
