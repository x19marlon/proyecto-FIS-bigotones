package com.openlib.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiClient {
    private static final String BASE_URL = "http://localhost:8080/api";
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public static <T> T post(String path, Object body, Class<T> responseType) throws Exception {
        String json = mapper.writeValueAsString(body);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() >= 400) {
            throw new RuntimeException("API Error " + response.statusCode() + ": " + response.body());
        }
        
        return mapper.readValue(response.body(), responseType);
    }

    public static <T> T get(String path, Class<T> responseType) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 400) {
            throw new RuntimeException("API Error " + response.statusCode() + ": " + response.body());
        }

        return mapper.readValue(response.body(), responseType);
    }

    /** DELETE request — devuelve true si el servidor respondió con 2xx */
    public static boolean delete(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode() >= 200 && response.statusCode() < 300;
    }

    /** GET con query string ya formada, ej. "/books/search?q=java&cat=Software" */
    public static <T> T getWithQuery(String pathAndQuery, Class<T> responseType) throws Exception {
        return get(pathAndQuery, responseType);
    }
}

