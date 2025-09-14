package com.taskmanager.api;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * KVTaskClient is a client for interacting with a KVServer.
 *
 * <p>It supports:</p>
 * <ul>
 *     <li>Registering and retrieving an API token</li>
 *     <li>Saving JSON values to the server by key</li>
 *     <li>Loading JSON values from the server by key</li>
 * </ul>
 */
public class KVTaskClient {
    private static final Logger LOGGER = Logger.getLogger(KVTaskClient.class.getName());

    private final String baseUrl;
    private final HttpClient httpClient;
    private String apiToken;

    /**
     * Initializes a KVTaskClient for the server running on the specified port.
     * @param kvServerPort The port on which the KVServer is running.
     */
    public KVTaskClient(int kvServerPort) {
        this.baseUrl = "http://localhost:" + kvServerPort + "/";
        this.httpClient = HttpClient.newHttpClient();
        this.apiToken = getApiToken();
    }

    /**
     * Fetches the API token from the KVServer.
     * @return API token as a String, or "-1" if retrieval failed.
     */
    private String getApiToken() {

        URI urlFinal = URI.create(baseUrl + "register");
        HttpRequest request = HttpRequest.newBuilder().uri(urlFinal).GET().build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                LOGGER.info("Successfully retrieved API token.");
                return response.body();
            } else {
                LOGGER.warning("Failed to retrieve API token. Status code: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Error during getApiToken request. Check the server URL and try again.", e);
        }
        return apiToken = "-1";
    }

    /**
     * Saves a JSON string under the specified key on the KVServer.
     *
     * @param key  The key under which the JSON will be stored.
     * @param json The JSON string to store.
     */
    public void put(String key, String json) {

        URI urlFinal = URI.create(baseUrl + "save/" + key + "?API_TOKEN=" + apiToken);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(urlFinal).POST(body).build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            LOGGER.info("PUT request completed. Status code: " + response.statusCode());
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            LOGGER.log(Level.SEVERE, "Error during PUT request. Check the server URL and try again.", e);
        }
    }

    /**
     * Loads a JSON string stored under the specified key from the KVServer.
     *
     * @param key The key of the value to retrieve.
     * @return JSON string if found; empty string if not found or error occurred.
     */
    public String load(String key) {

        URI urlFinal = URI.create(baseUrl + "load/" + key + "?API_TOKEN=" + apiToken);
        HttpRequest request = HttpRequest.newBuilder().uri(urlFinal).GET().build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                LOGGER.info("LOAD request successful for key: " + key);
                return response.body();
            } else if (response.statusCode() == 404) {
                return "";
            } else {
                LOGGER.warning("LOAD request returned status code: " + response.statusCode());
                return "";
            }
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            LOGGER.log(Level.SEVERE, "Error during LOAD request. Check the server URL and try again.", e);
        }
        return "";
    }
}
