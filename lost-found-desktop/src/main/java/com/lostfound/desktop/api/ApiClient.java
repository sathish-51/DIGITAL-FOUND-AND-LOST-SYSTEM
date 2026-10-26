package com.lostfound.desktop.api;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

public class ApiClient {

    private static final String BASE_URL =
            "http://localhost:8080/api/auth";

    private static final HttpClient CLIENT =
            HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(5))
                    .build();

    private static final ObjectMapper MAPPER =
            new ObjectMapper();

    public static AuthResult login(
            String email, String password) throws Exception {

        return send("/login",
                Map.of("email", email, "password", password));
    }

    public static AuthResult register(
            String name, String email, String password)
            throws Exception {

        return send("/register",
                Map.of("fullName", name,
                       "email", email,
                       "password", password));
    }

    private static AuthResult send(
            String endpoint, Map<String, String> data)
            throws Exception {

        String json = MAPPER.writeValueAsString(data);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response;

        try {
            response = CLIENT.send(
                    request,
                    HttpResponse.BodyHandlers.ofString());
        } catch (IOException ex) {
            throw new IOException(
                    "Cannot connect to Spring Boot. "
                    + "Check that the backend is running on port 8080.",
                    ex);
        }

        JsonNode body = MAPPER.readTree(response.body());

        if (response.statusCode() < 200
                || response.statusCode() >= 300) {

            String message = body.path("message").asText();

            if (message.isBlank()) {
                message = body.path("detail").asText();
            }

            if (message.isBlank()) {
                message = body.path("error").asText();
            }

            if (message.isBlank()) {
                message = "Request failed with HTTP "
                        + response.statusCode();
            }

            throw new IOException(message);
        }

        return MAPPER.treeToValue(body, AuthResult.class);
    }
}
