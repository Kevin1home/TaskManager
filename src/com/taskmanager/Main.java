package com.taskmanager;

import com.taskmanager.api.HttpTaskServer;
import com.taskmanager.api.KVServer;
import com.google.gson.Gson;
import com.taskmanager.tasks.Epic;
import com.taskmanager.tasks.Subtask;
import com.taskmanager.tasks.Task;
import com.taskmanager.tasks.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Logger;

/**
 * Entry point used as integration testing of Task Manager API for LEARNING PURPOSES.
 * <p>
 * Starts {@link KVServer} and {@link HttpTaskServer},
 * sends HTTP requests to create and retrieve tasks, epics, and subtasks.
 * </p>
 */
public class Main {
    private static final java.util.logging.Logger logger = Logger.getLogger(Task.class.getName());

    public static void main(String[] args) throws IOException, InterruptedException {

        logger.info("Starting integration testing...");

        // Start KVServer
        KVServer kvServer = new KVServer();
        kvServer.start();
        HttpTaskServer httpTaskServer1 = new HttpTaskServer();
        httpTaskServer1.start();
        logger.info("KVServer started");

        // Create HTTP client
        HttpClient client = HttpClient.newHttpClient();
        Gson gson = new Gson();

        // Create request to create a usual task
        logger.info("Creating request to create a usual task");
        URI url1 = URI.create("http://localhost:8080/tasks/task/");
        Task task1 = new Task("Task", "DescrT", TaskStatus.NEW, "30.06.2024, 09:30", 60L);
        logger.info("Created task: " + task1);
        String jsonTask1 = gson.toJson(task1);
        logger.info("Converted task to JSON: " + jsonTask1);
        HttpRequest.BodyPublisher body1 = HttpRequest.BodyPublishers.ofString(jsonTask1);
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).POST(body1).build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        logger.info("Response: " + response1.statusCode() + " " + response1.body());

        // Create request for creating an epic
        logger.info("Creating request to create an epic");
        URI url2 = URI.create("http://localhost:8080/tasks/epic/");
        Epic epic2 = new Epic("Epic1", "DescrE1");
        logger.info("Created epic: " + epic2);
        String jsonEpic2 = gson.toJson(epic2);
        logger.info("Converted epic to JSON: " + jsonEpic2);
        HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(jsonEpic2);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(body2).build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        logger.info("Response: " + response2.statusCode() + " " + response2.body());

        // Create request for creating a subtask
        logger.info("Creating request to create a subtask");
        URI url3 = URI.create("http://localhost:8080/tasks/subtask/");
        Subtask subtask3 = new Subtask("Subtask1", "DescrS1", 2);
        logger.info("Created subtask: " + subtask3);
        String jsonSubtask2 = gson.toJson(subtask3);
        logger.info("Converted subtask to JSON: " + jsonSubtask2);
        HttpRequest.BodyPublisher body3 = HttpRequest.BodyPublishers.ofString(jsonSubtask2);
        HttpRequest request3 = HttpRequest.newBuilder().uri(url3).POST(body3).build();
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        logger.info("Response: " + response3.statusCode() + " " + response3.body());

        // Create request to get all tasks of all types
        logger.info("Creating request to get all tasks of all types");
        URI url4 = URI.create("http://localhost:8080/tasks/");
        HttpRequest request4 = HttpRequest.newBuilder().uri(url4).GET().build();
        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());
        logger.info("Response: " + response4.statusCode() + " " + response4.body());

        // Create request to get all subtasks of an epic
        logger.info("Creating request to get subtasks of epic with id=2");
        URI url5 = URI.create("http://localhost:8080/tasks/subtask/epic?id=2");
        HttpRequest request5 = HttpRequest.newBuilder().uri(url5).GET().build();
        HttpResponse<String> response5 = client.send(request5, HttpResponse.BodyHandlers.ofString());
        logger.info("Response: " + response5.statusCode() + " " + response5.body());

        // Restart HttpTaskServer
        httpTaskServer1.stop();
        HttpTaskServer httpTaskServer2 = new HttpTaskServer();
        httpTaskServer2.start();
        logger.info("HttpTaskServer restarted");

        // Create request to get all subtasks of an epic
        logger.info("Creating request to get all tasks of all types");
        URI url6 = URI.create("http://localhost:8080/tasks/");
        HttpRequest request6 = HttpRequest.newBuilder().uri(url6).GET().build();
        HttpResponse<String> response6 = client.send(request6, HttpResponse.BodyHandlers.ofString());
        logger.info("Response: " + response6.statusCode() + " " + response6.body());

        // Create request to get subtasks of epic
        logger.info("Creating request to get subtasks of epic with id=2");
        URI url7 = URI.create("http://localhost:8080/tasks/subtask/epic?id=2");
        HttpRequest request7 = HttpRequest.newBuilder().uri(url7).GET().build();
        HttpResponse<String> response7 = client.send(request7, HttpResponse.BodyHandlers.ofString());
        logger.info("Response: " + response7.statusCode() + " " + response7.body());

        // Stopping servers
        httpTaskServer2.stop();
        kvServer.stop();
        logger.info("Servers stopped, integration test finished.");
    }
}
