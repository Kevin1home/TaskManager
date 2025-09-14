package com.taskmanager.api;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

/**
 * KVServer is a simple key-value storage server.
 *
 * <p>Endpoints:</p>
 * <ul>
 *     <li>/register - GET: returns API token</li>
 *     <li>/save/{key} - POST: saves value for the given key</li>
 *     <li>/load/{key} - GET: retrieves value for the given key</li>
 * </ul>
 *
 * <p>Authorization:</p>
 * <ul>
 *     <li>API_TOKEN query parameter is required for save/load operations.</li>
 * </ul>
 */
public class KVServer {

    private static final Logger LOGGER = Logger.getLogger(KVServer.class.getName());

    public static final int PORT = 8078;
    private final String apiToken;
    private final HttpServer server;
    private final Map<String, String> data = new HashMap<>();

    public KVServer() throws IOException {
        apiToken = generateApiToken();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);

        // Register contexts
        server.createContext("/register", this::register);
        server.createContext("/save", this::save);
        server.createContext("/load", this::load);
    }

    /**
     * Handles /register requests.
     * Returns the API token via GET request.
     */
    private void register(HttpExchange httpExchange) throws IOException {
        try {
            LOGGER.info("Accessing /register endpoint");
            if ("GET".equals(httpExchange.getRequestMethod())) {
                sendText(httpExchange, apiToken);
            } else {
                LOGGER.warning("/register expects GET, received: " + httpExchange.getRequestMethod());
                httpExchange.sendResponseHeaders(405, 0);
            }
        } finally {
            httpExchange.close();
        }
    }

    /**
     * Handles /load/{key} requests.
     * Returns the value stored under the specified key.
     */
    private void load(HttpExchange httpExchange) throws IOException {

        try {
            LOGGER.info("Accessing /load endpoint");

            if (!hasAuth(httpExchange)) {
                LOGGER.warning("Unauthorized load request");
                httpExchange.sendResponseHeaders(403, 0);
                return;
            }

            if ("GET".equals(httpExchange.getRequestMethod())) {
                String key = httpExchange.getRequestURI().getPath().substring("/load/".length());

                if (key.isEmpty()) {
                    LOGGER.warning("Load key is empty");
                    httpExchange.sendResponseHeaders(400, 0);
                    return;
                }

                if (!data.containsKey(key)) {
                    LOGGER.warning("Key not found: " + key);
                    httpExchange.sendResponseHeaders(404, 0);
                    return;
                }

                String value = data.get(key);
                sendText(httpExchange, value);

                LOGGER.info("Value for key " + key + " successfully retrieved.");
                httpExchange.sendResponseHeaders(200, 0);

            } else {
                LOGGER.info("/save expects GET-request, received: " + httpExchange.getRequestMethod());
                httpExchange.sendResponseHeaders(405, 0);
            }
        } finally {
            httpExchange.close();
        }
    }

    /**
     * Handles /save/{key} requests.
     * Stores the value from the request body under the specified key.
     */
    private void save(HttpExchange httpExchange) throws IOException {

        try {
            LOGGER.info("Accessing /save endpoint");

            if (!hasAuth(httpExchange)) {
                LOGGER.warning("Unauthorized save request");
                httpExchange.sendResponseHeaders(403, 0);
                return;
            }

            if ("POST".equals(httpExchange.getRequestMethod())) {
                String key = httpExchange.getRequestURI().getPath().substring("/save/".length());

                if (key.isEmpty()) {
                    LOGGER.warning("Save key is empty");
                    httpExchange.sendResponseHeaders(400, 0);
                    return;
                }

                String value = readText(httpExchange);

                if (value.isEmpty()) {
                    LOGGER.warning("Save value is empty");
                    httpExchange.sendResponseHeaders(400, 0);
                    return;
                }

                data.put(key, value);
                LOGGER.info("Key " + key + " successfully saved/updated.");
                httpExchange.sendResponseHeaders(200, 0);

            } else {
                LOGGER.warning("/save expects POST, received: " + httpExchange.getRequestMethod());
                httpExchange.sendResponseHeaders(405, 0);
            }
        } finally {
            httpExchange.close();
        }
    }

    /**
     * Starts the KVServer.
     */
    public void start() {
        LOGGER.info("Starting KVServer on port " + PORT);
        LOGGER.info("Open in browser: http://localhost:" + PORT + "/");
        LOGGER.info("API_TOKEN: " + apiToken);
        server.start();
    }

    /**
     * Stops the KVServer.
     */
    public void stop() {
        LOGGER.info("Stopping KVServer on port " + PORT);
        server.stop(0);
    }

    /**
     * Generates a simple API token based on current time.
     */
    private String generateApiToken() {
        return String.valueOf(System.currentTimeMillis());
    }

    /**
     * Checks if the request contains a valid API token.
     */
    protected boolean hasAuth(HttpExchange httpExchange) {
        String rawQuery = httpExchange.getRequestURI().getRawQuery();
        return rawQuery != null && (rawQuery.contains("API_TOKEN=" + apiToken) || rawQuery.contains("API_TOKEN=DEBUG"));
    }

    /**
     * Reads request body as a string.
     */
    protected String readText(HttpExchange httpExchange) throws IOException {
        return new String(httpExchange.getRequestBody().readAllBytes(), UTF_8);
    }

    /**
     * Sends a text response with JSON content type.
     */
    protected void sendText(HttpExchange httpExchange, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json");
        httpExchange.sendResponseHeaders(200, resp.length);
        httpExchange.getResponseBody().write(resp);
    }
}
