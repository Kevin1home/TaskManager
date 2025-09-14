package com.taskmanager.api;

import com.google.gson.*;
import com.taskmanager.managers.InMemoryHistoryManager;
import com.taskmanager.managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.taskmanager.tasks.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.logging.Logger;

import static com.taskmanager.managers.HttpTaskManager.load;

/**
 * Integration tests for {@link HttpTaskServer}.
 * <p>
 * This class verifies the REST API of Task Manager:
 * <ul>
 *   <li>Creating tasks, epics, and subtasks (POST)</li>
 *   <li>Retrieving tasks by ID and by type (GET)</li>
 *   <li>Updating tasks, epics, and subtasks (PUT)</li>
 *   <li>Deleting tasks of all types and by ID (DELETE)</li>
 *   <li>Checking prioritized tasks and history endpoints</li>
 * </ul>
 * <p>
 * Servers used:
 * <ul>
 *   <li>{@link KVServer} – for storing data</li>
 *   <li>{@link HttpTaskServer} – for providing HTTP API</li>
 * </ul>
 * </p>
 */
class HttpTaskServerTest {

    private KVServer kvServer;
    private HttpTaskServer httpTaskServer;
    private HttpClient httpClient;
    TaskManager taskManager = load(KVServer.PORT);

    private final Gson gson = new Gson();
    private static final Logger logger = Logger.getLogger(HttpTaskServerTest.class.getName());

    private String jsonTask;
    private String jsonEpic;
    private String jsonSubtask;

    @BeforeEach
    void setUp() throws IOException, InterruptedException {
        logger.info("Starting KVServer and HttpTaskServer before test...");
        kvServer = new KVServer();
        kvServer.start();
        httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();
        httpClient = HttpClient.newHttpClient();

        convertTasksToJson();

        logger.info("Creating test data (Task, Epic, Subtask)...");
        // creating task
        URI url1 = URI.create("http://localhost:8080/tasks/task");
        HttpRequest.BodyPublisher body1 = HttpRequest.BodyPublishers.ofString(jsonTask);
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).POST(body1).build();
        httpClient.send(request1, HttpResponse.BodyHandlers.ofString());
        // creating epic
        URI url2 = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(jsonEpic);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(body2).build();
        httpClient.send(request2, HttpResponse.BodyHandlers.ofString());
        // creating subtask
        URI url3 = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest.BodyPublisher body3 = HttpRequest.BodyPublishers.ofString(jsonSubtask);
        HttpRequest request3 = HttpRequest.newBuilder().uri(url3).POST(body3).build();
        httpClient.send(request3, HttpResponse.BodyHandlers.ofString());

        logger.info("Test data created successfully.");
    }

    @AfterEach
    void tearDown() {
        // stopping Servers
        logger.info("Stopping KVServer and HttpTaskServer after test...");
        kvServer.stop();
        httpTaskServer.stop();
        // manual deleting of all changes in Program
        taskManager.deleteAllTasksAllTypes();
        taskManager.setNextId(1);
        taskManager.setHistoryManager(new InMemoryHistoryManager());

        logger.info("Test environment cleaned up.");
    }

    private void convertTasksToJson() {
        Task task = new Task("Task", "DescrT", TaskStatus.NEW, "30.06.2024, 09:30", 60L);
        task.setId(1);
        jsonTask = gson.toJson(task);

        Epic epic = new Epic("Epic1", "DescrEp1");
        epic.setId(2);
        jsonEpic = gson.toJson(epic);

        Subtask subtask = new Subtask("Subtask2", "DescrSt2", TaskStatus.NEW, "01.06.2024, 12:30", 120L, 2);
        subtask.setId(3);
        jsonSubtask = gson.toJson(subtask);
    }

    // check endpoint GET
    @Test
    void shouldReturnListOfPrioritizedTasks() throws IOException, InterruptedException {
        // getting prioritized list of tasks
        URI url4 = URI.create("http://localhost:8080/tasks/");
        HttpRequest request4 = HttpRequest.newBuilder().uri(url4).GET().build();
        HttpResponse<String> response = httpClient.send(request4, HttpResponse.BodyHandlers.ofString());
        // converting to JsonArray
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonArray jsonArray = jsonElement.getAsJsonArray();

        int expectedSize = 2; // +1 Task and +1 Subtask (there is no Epics in Prioritized Tasks)
        int actualSize = jsonArray.size();
        String expectedLastTask = jsonTask;
        String actualLastTask = String.valueOf(jsonArray.get(1));

        Assertions.assertEquals(expectedSize, actualSize);
        Assertions.assertEquals(expectedLastTask, actualLastTask);
    }

    // check endpoint GET_HISTORY
    @Test
    void shouldReturnHistory() throws IOException, InterruptedException {
        // adding task to history by method get
        URI url4 = URI.create("http://localhost:8080/tasks/task?id=1");
        HttpRequest request4 = HttpRequest.newBuilder().uri(url4).GET().build();
        httpClient.send(request4, HttpResponse.BodyHandlers.ofString());
        // adding epic to history by method get
        URI url5 = URI.create("http://localhost:8080/tasks/epic?id=2");
        HttpRequest request5 = HttpRequest.newBuilder().uri(url5).GET().build();
        httpClient.send(request5, HttpResponse.BodyHandlers.ofString());
        // adding subtask to history by method get
        URI url6 = URI.create("http://localhost:8080/tasks/subtask?id=3");
        HttpRequest request6 = HttpRequest.newBuilder().uri(url6).GET().build();
        httpClient.send(request6, HttpResponse.BodyHandlers.ofString());

        // getting history
        URI url7 = URI.create("http://localhost:8080/tasks/history");
        HttpRequest request7 = HttpRequest.newBuilder().uri(url7).GET().build();
        HttpResponse<String> response = httpClient.send(request7, HttpResponse.BodyHandlers.ofString());
        // converting to JsonArray
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonArray jsonArray = jsonElement.getAsJsonArray();

        int expectedSize = 3; // History: 1, 2, 3
        int actualSize = jsonArray.size();
        String expectedLastTask = jsonSubtask;
        String actualLastTask = String.valueOf(jsonArray.get(2));

        Assertions.assertEquals(expectedSize, actualSize);
        Assertions.assertEquals(expectedLastTask, actualLastTask);
    }

    // check endpoint GET_TASK
    @Test
    void shouldReturnListOfTasks() throws IOException, InterruptedException {
        // getting tasks
        URI url4 = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request4 = HttpRequest.newBuilder().uri(url4).GET().build();
        HttpResponse<String> response = httpClient.send(request4, HttpResponse.BodyHandlers.ofString());

        // converting to JsonObject
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject().get("1").getAsJsonObject();

        String expectedTask = jsonTask;
        String actualTask = jsonObject.toString();

        Assertions.assertEquals(expectedTask, actualTask);
    }

    // check endpoint GET_EPIC
    @Test
    void shouldReturnListOfEpics() throws IOException, InterruptedException {
        // getting epics
        URI url4 = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request4 = HttpRequest.newBuilder().uri(url4).GET().build();
        HttpResponse<String> response = httpClient.send(request4, HttpResponse.BodyHandlers.ofString());

        // converting to JsonObject
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject().get("2").getAsJsonObject();

        // rewriting Epic cause of the lack of data by preparing in setUp
        Epic epic = new Epic("Epic1", "DescrEp1");
        epic.setId(2);
        Subtask subtask = new Subtask("Subtask2", "DescrSt2", TaskStatus.NEW, "01.06.2024, 12:30", 120L, 2);
        subtask.setId(3);
        epic.subtasks.add(subtask);
        epic.checkDataTimeDurationEpic();
        jsonEpic = gson.toJson(epic);

        String expectedEpic = jsonEpic;
        String actualEpic = jsonObject.toString();

        Assertions.assertEquals(expectedEpic, actualEpic);
    }

    // check endpoint GET_SUBTASK
    @Test
    void shouldReturnListOfSubtasks() throws IOException, InterruptedException {
        // getting subtasks
        URI url4 = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request4 = HttpRequest.newBuilder().uri(url4).GET().build();
        HttpResponse<String> response = httpClient.send(request4, HttpResponse.BodyHandlers.ofString());

        // converting to JsonObject
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject().get("3").getAsJsonObject();

        String  expectedSubtask = jsonSubtask;
        String actualSubtask = jsonObject.toString();

        Assertions.assertEquals(expectedSubtask, actualSubtask);
    }

    // check endpoint GET_SUBTASK_EPIC_ID
    @Test
    void shouldReturnSubtasksOfEpic() throws IOException, InterruptedException {
        // getting epic
        URI url4 = URI.create("http://localhost:8080/tasks/subtask/epic?id=2");
        HttpRequest request4 = HttpRequest.newBuilder().uri(url4).GET().build();
        HttpResponse<String> response = httpClient.send(request4, HttpResponse.BodyHandlers.ofString());

        // converting to JsonArray
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonArray jsonArray = jsonElement.getAsJsonArray();

        int expectedSize = 1;
        int actualSize = jsonArray.size();
        String expectedSubtask = jsonSubtask;
        String actualLastSubtask = String.valueOf(jsonArray.get(0));

        Assertions.assertEquals(expectedSize, actualSize);
        Assertions.assertEquals(expectedSubtask, actualLastSubtask);
    }

    // check endpoint GET_TASK_ID
    @Test
    void shouldReturnTaskById() throws IOException, InterruptedException {
        // getting task
        URI url4 = URI.create("http://localhost:8080/tasks/task?id=1");
        HttpRequest request4 = HttpRequest.newBuilder().uri(url4).GET().build();
        HttpResponse<String> response = httpClient.send(request4, HttpResponse.BodyHandlers.ofString());

        // converting to JsonObject
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        String  expectedTask = jsonTask;
        String actualTask = jsonObject.toString();

        Assertions.assertEquals(expectedTask, actualTask);
    }

    // check endpoint GET_EPIC_ID
    @Test
    void shouldReturnEpicById() throws IOException, InterruptedException {
        // getting epic
        URI url4 = URI.create("http://localhost:8080/tasks/epic?id=2");
        HttpRequest request4 = HttpRequest.newBuilder().uri(url4).GET().build();
        HttpResponse<String> response = httpClient.send(request4, HttpResponse.BodyHandlers.ofString());

        // converting to JsonObject
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        // rewriting Epic cause of the lack of data by preparing in setUp
        Epic epic = new Epic("Epic1", "DescrEp1");
        epic.setId(2);
        Subtask subtask = new Subtask("Subtask2", "DescrSt2", TaskStatus.NEW, "01.06.2024, 12:30", 120L, 2);
        subtask.setId(3);
        epic.subtasks.add(subtask);
        epic.checkDataTimeDurationEpic();
        jsonEpic = gson.toJson(epic);

        String expectedEpic = jsonEpic;
        String actualEpic = jsonObject.toString();

        Assertions.assertEquals(expectedEpic, actualEpic);
    }

    // check endpoint GET_SUBTASK_ID
    @Test
    void shouldReturnSubtaskById() throws IOException, InterruptedException {
        // getting subtask
        URI url4 = URI.create("http://localhost:8080/tasks/subtask?id=3");
        HttpRequest request4 = HttpRequest.newBuilder().uri(url4).GET().build();
        HttpResponse<String> response = httpClient.send(request4, HttpResponse.BodyHandlers.ofString());

        // converting to JsonObject
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        String  expectedSubtask = jsonSubtask;
        String actualSubtask = jsonObject.toString();

        Assertions.assertEquals(expectedSubtask, actualSubtask);
    }

    // check endpoint POST_TASK
    @Test
    void shouldCorrectlySaveAllFieldsOfTask() throws IOException, InterruptedException {
        // getting task
        URI url4 = URI.create("http://localhost:8080/tasks/task?id=1");
        HttpRequest request4 = HttpRequest.newBuilder().uri(url4).GET().build();
        HttpResponse<String> response = httpClient.send(request4, HttpResponse.BodyHandlers.ofString());

        // converting to JsonObject
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        String expectedName = gson.fromJson(jsonTask, Task.class).getName();
        String actualName = gson.fromJson(jsonObject.toString(), Task.class).getName();

        String expectedDescription = gson.fromJson(jsonTask, Task.class).getDescription();
        String actualDescription = gson.fromJson(jsonObject.toString(), Task.class).getDescription();

        int expectedId = gson.fromJson(jsonTask, Task.class).getId();
        int actualId = gson.fromJson(jsonObject.toString(), Task.class).getId();

        TaskStatus expectedTaskStatus = gson.fromJson(jsonTask, Task.class).getStatus();
        TaskStatus actualTaskStatus = gson.fromJson(jsonObject.toString(), Task.class).getStatus();

        String expectedType = gson.fromJson(jsonTask, Task.class).getType();
        String actualType = gson.fromJson(jsonObject.toString(), Task.class).getType();

        LocalDateTime expectedStartTime = gson.fromJson(jsonTask, Task.class).getStartTime().get();
        LocalDateTime actualStartTime = gson.fromJson(jsonObject.toString(), Task.class).getStartTime().get();

        Duration expectedDuration = gson.fromJson(jsonTask, Task.class).getDuration().get();
        Duration actualDuration = gson.fromJson(jsonObject.toString(), Task.class).getDuration().get();

        Assertions.assertEquals(expectedName, actualName);
        Assertions.assertEquals(expectedDescription, actualDescription);
        Assertions.assertEquals(expectedId, actualId);
        Assertions.assertEquals(expectedTaskStatus, actualTaskStatus);
        Assertions.assertEquals(expectedType, actualType);
        Assertions.assertEquals(expectedStartTime, actualStartTime);
        Assertions.assertEquals(expectedDuration, actualDuration);
    }

    // check endpoint POST_EPIC
    @Test
    void shouldCorrectlySaveAllFieldsOfEpic() throws IOException, InterruptedException {
        // getting epic
        URI url4 = URI.create("http://localhost:8080/tasks/epic?id=2");
        HttpRequest request4 = HttpRequest.newBuilder().uri(url4).GET().build();
        HttpResponse<String> response = httpClient.send(request4, HttpResponse.BodyHandlers.ofString());

        // converting to JsonObject
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        // rewriting Epic cause of the lack of data by preparing in setUp
        Epic epic = new Epic("Epic1", "DescrEp1");
        epic.setId(2);
        Subtask subtask = new Subtask("Subtask2", "DescrSt2", TaskStatus.NEW, "01.06.2024, 12:30", 120L, 2);
        subtask.setId(3);
        epic.subtasks.add(subtask);
        epic.checkDataTimeDurationEpic();
        jsonEpic = gson.toJson(epic);

        String expectedName = gson.fromJson(jsonEpic, Epic.class).getName();
        String actualName = gson.fromJson(jsonObject.toString(), Epic.class).getName();

        String expectedDescription = gson.fromJson(jsonEpic, Epic.class).getDescription();
        String actualDescription = gson.fromJson(jsonObject.toString(), Epic.class).getDescription();

        int expectedId = gson.fromJson(jsonEpic, Epic.class).getId();
        int actualId = gson.fromJson(jsonObject.toString(), Epic.class).getId();

        TaskStatus expectedTaskStatus = gson.fromJson(jsonEpic, Epic.class).getStatus();
        TaskStatus actualTaskStatus = gson.fromJson(jsonObject.toString(), Epic.class).getStatus();

        String expectedType = gson.fromJson(jsonEpic, Epic.class).getType();
        String actualType = gson.fromJson(jsonObject.toString(), Epic.class).getType();

        LocalDateTime expectedStartTime = gson.fromJson(jsonEpic, Epic.class).getStartTime().get();
        LocalDateTime actualStartTime = gson.fromJson(jsonObject.toString(), Epic.class).getStartTime().get();

        Duration expectedDuration = gson.fromJson(jsonEpic, Epic.class).getDuration().get();
        Duration actualDuration = gson.fromJson(jsonObject.toString(), Epic.class).getDuration().get();

        Assertions.assertEquals(expectedName, actualName);
        Assertions.assertEquals(expectedDescription, actualDescription);
        Assertions.assertEquals(expectedId, actualId);
        Assertions.assertEquals(expectedTaskStatus, actualTaskStatus);
        Assertions.assertEquals(expectedType, actualType);
        Assertions.assertEquals(expectedStartTime, actualStartTime);
        Assertions.assertEquals(expectedDuration, actualDuration);
    }

    // check endpoint POST_SUBTASK
    @Test
    void shouldCorrectlySaveAllFieldsOfSubtask() throws IOException, InterruptedException {
        // getting subtask
        URI url4 = URI.create("http://localhost:8080/tasks/subtask?id=3");
        HttpRequest request4 = HttpRequest.newBuilder().uri(url4).GET().build();
        HttpResponse<String> response = httpClient.send(request4, HttpResponse.BodyHandlers.ofString());

        // converting to JsonObject
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        String expectedName = gson.fromJson(jsonSubtask, Subtask.class).getName();
        String actualName = gson.fromJson(jsonObject.toString(), Subtask.class).getName();

        String expectedDescription = gson.fromJson(jsonSubtask, Subtask.class).getDescription();
        String actualDescription = gson.fromJson(jsonObject.toString(), Subtask.class).getDescription();

        int expectedId = gson.fromJson(jsonSubtask, Subtask.class).getId();
        int actualId = gson.fromJson(jsonObject.toString(), Subtask.class).getId();

        TaskStatus expectedTaskStatus = gson.fromJson(jsonSubtask, Subtask.class).getStatus();
        TaskStatus actualTaskStatus = gson.fromJson(jsonObject.toString(), Subtask.class).getStatus();

        String expectedType = gson.fromJson(jsonSubtask, Subtask.class).getType();
        String actualType = gson.fromJson(jsonObject.toString(), Subtask.class).getType();

        LocalDateTime expectedStartTime = gson.fromJson(jsonSubtask, Subtask.class).getStartTime().get();
        LocalDateTime actualStartTime = gson.fromJson(jsonObject.toString(), Subtask.class).getStartTime().get();

        Duration expectedDuration = gson.fromJson(jsonSubtask, Subtask.class).getDuration().get();
        Duration actualDuration = gson.fromJson(jsonObject.toString(), Subtask.class).getDuration().get();

        Assertions.assertEquals(expectedName, actualName);
        Assertions.assertEquals(expectedDescription, actualDescription);
        Assertions.assertEquals(expectedId, actualId);
        Assertions.assertEquals(expectedTaskStatus, actualTaskStatus);
        Assertions.assertEquals(expectedType, actualType);
        Assertions.assertEquals(expectedStartTime, actualStartTime);
        Assertions.assertEquals(expectedDuration, actualDuration);
    }

    // check endpoint PUT_TASK_ID
    @Test
    void shouldUpdateTask() throws IOException, InterruptedException {
        // updating already created task
        Task updateForTask = new Task("TaskNew", "DescrTNew", TaskStatus.IN_PROGRESS, "15.10.2024, 10:40", 170L);
        String jsonUpdateForTask = gson.toJson(updateForTask);
        URI url4 = URI.create("http://localhost:8080/tasks/task?id=1");
        HttpRequest.BodyPublisher body4 = HttpRequest.BodyPublishers.ofString(jsonUpdateForTask);
        HttpRequest request4 = HttpRequest.newBuilder().uri(url4).PUT(body4).build();
        httpClient.send(request4, HttpResponse.BodyHandlers.ofString());

        // getting updated Task
        URI url5 = URI.create("http://localhost:8080/tasks/task?id=1");
        HttpRequest request5 = HttpRequest.newBuilder().uri(url5).GET().build();
        HttpResponse<String> response = httpClient.send(request5, HttpResponse.BodyHandlers.ofString());

        // converting to JsonObject
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        // rewriting Task cause of the lack of id info by preparing updateForTask
        updateForTask.setId(1);
        jsonUpdateForTask = gson.toJson(updateForTask);

        String expectedTask = jsonUpdateForTask;
        String actualTask = jsonObject.toString();

        Assertions.assertEquals(expectedTask, actualTask);
    }

    // check endpoint PUT_EPIC_ID
    @Test
    void shouldUpdateEpic() throws IOException, InterruptedException {
        // updating already created epic
        Epic updateForEpic = new Epic("EpicNew", "DescrTNew");
        String jsonUpdateForEpic = gson.toJson(updateForEpic);
        URI url4 = URI.create("http://localhost:8080/tasks/epic?id=2");
        HttpRequest.BodyPublisher body4 = HttpRequest.BodyPublishers.ofString(jsonUpdateForEpic);
        HttpRequest request4 = HttpRequest.newBuilder().uri(url4).PUT(body4).build();
        httpClient.send(request4, HttpResponse.BodyHandlers.ofString());

        // getting updated Epic
        URI url5 = URI.create("http://localhost:8080/tasks/epic?id=2");
        HttpRequest request5 = HttpRequest.newBuilder().uri(url5).GET().build();
        HttpResponse<String> response = httpClient.send(request5, HttpResponse.BodyHandlers.ofString());

        // converting to JsonObject
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        // rewriting Epic cause of the lack of data by preparing updateForEpic
        updateForEpic.setId(2);
        Subtask subtask = new Subtask("Subtask2", "DescrSt2", TaskStatus.NEW, "01.06.2024, 12:30", 120L, 2);
        subtask.setId(3);
        updateForEpic.subtasks.add(subtask);
        updateForEpic.checkDataTimeDurationEpic();
        jsonUpdateForEpic = gson.toJson(updateForEpic);

        String expectedEpic = jsonUpdateForEpic;
        String actualEpic = jsonObject.toString();

        Assertions.assertEquals(expectedEpic, actualEpic);
    }

    // check endpoint PUT_SUBTASK_ID
    @Test
    void shouldUpdateSubtask() throws IOException, InterruptedException {
        // updating already created subtask
        Subtask updateForSubtask = new Subtask("SubtaskNew", "DescrTNew", TaskStatus.IN_PROGRESS, "15.10.2024, 10:40",
                170L, 2);
        String jsonUpdateForSubtask = gson.toJson(updateForSubtask);
        URI url4 = URI.create("http://localhost:8080/tasks/subtask?id=3");
        HttpRequest.BodyPublisher body4 = HttpRequest.BodyPublishers.ofString(jsonUpdateForSubtask);
        HttpRequest request4 = HttpRequest.newBuilder().uri(url4).PUT(body4).build();
        httpClient.send(request4, HttpResponse.BodyHandlers.ofString());

        // getting updated Subtask
        URI url5 = URI.create("http://localhost:8080/tasks/subtask?id=3");
        HttpRequest request5 = HttpRequest.newBuilder().uri(url5).GET().build();
        HttpResponse<String> response = httpClient.send(request5, HttpResponse.BodyHandlers.ofString());

        // converting to JsonObject
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        // rewriting Subtask cause of the lack of id info by preparing updateForSubtask
        updateForSubtask.setId(3);
        jsonUpdateForSubtask = gson.toJson(updateForSubtask);

        String expectedSubtask = jsonUpdateForSubtask;
        String actualSubtask = jsonObject.toString();

        Assertions.assertEquals(expectedSubtask, actualSubtask);
    }

    // check endpoint DELETE
    @Test
    void shouldDeleteAllTasksAllTypes() throws IOException, InterruptedException {
        // deleting created Task, Epic and Subtask in 1 request
        URI url4 = URI.create("http://localhost:8080/tasks/");
        HttpRequest request4 = HttpRequest.newBuilder().uri(url4).DELETE().build();
        httpClient.send(request4, HttpResponse.BodyHandlers.ofString());

        // getting list of tasks
        URI url5 = URI.create("http://localhost:8080/tasks/");
        HttpRequest request5 = HttpRequest.newBuilder().uri(url5).GET().build();
        HttpResponse<String> response = httpClient.send(request5, HttpResponse.BodyHandlers.ofString());

        // converting to JsonArray
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonArray jsonArray = jsonElement.getAsJsonArray();

        int expectedSize = 0;
        int actualSize = jsonArray.size();

        Assertions.assertEquals(expectedSize, actualSize);
    }

    // check endpoint DELETE_TASK
    @Test
    void shouldDeleteAllUsualTasks() throws IOException, InterruptedException {
        // deleting created Task in 1 request
        URI url4 = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request4 = HttpRequest.newBuilder().uri(url4).DELETE().build();
        httpClient.send(request4, HttpResponse.BodyHandlers.ofString());

        // getting tasks
        URI url5 = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request5 = HttpRequest.newBuilder().uri(url5).GET().build();
        HttpResponse<String> response = httpClient.send(request5, HttpResponse.BodyHandlers.ofString());

        // converting to JsonObject
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        boolean isEmpty = (jsonObject.get("0") == null);

        Assertions.assertTrue(isEmpty);
    }

    // check endpoint DELETE_EPIC
    @Test
    void shouldDeleteAllEpics() throws IOException, InterruptedException {
        // deleting created Epic in 1 request
        URI url4 = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request4 = HttpRequest.newBuilder().uri(url4).DELETE().build();
        httpClient.send(request4, HttpResponse.BodyHandlers.ofString());

        // getting epics
        URI url5 = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request5 = HttpRequest.newBuilder().uri(url5).GET().build();
        HttpResponse<String> response = httpClient.send(request5, HttpResponse.BodyHandlers.ofString());

        // converting to JsonObject
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        boolean isEmpty = (jsonObject.get("0") == null);

        Assertions.assertTrue(isEmpty);
    }

    // check endpoint DELETE_SUBTASK
    @Test
    void shouldDeleteAllSubtasks() throws IOException, InterruptedException {
        // deleting created Subtask in 1 request
        URI url4 = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request4 = HttpRequest.newBuilder().uri(url4).DELETE().build();
        httpClient.send(request4, HttpResponse.BodyHandlers.ofString());

        // getting subtasks
        URI url5 = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request5 = HttpRequest.newBuilder().uri(url5).GET().build();
        HttpResponse<String> response = httpClient.send(request5, HttpResponse.BodyHandlers.ofString());

        // converting to JsonObject
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        boolean isEmpty = (jsonObject.get("0") == null);

        Assertions.assertTrue(isEmpty);
    }

    // check endpoint DELETE_TASK_ID
    @Test
    void shouldDeleteChosenUsualTask() throws IOException, InterruptedException {
        // deleting created Task
        URI url4 = URI.create("http://localhost:8080/tasks/task?id=1");
        HttpRequest request4 = HttpRequest.newBuilder().uri(url4).DELETE().build();
        httpClient.send(request4, HttpResponse.BodyHandlers.ofString());

        // getting tasks
        URI url5 = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request5 = HttpRequest.newBuilder().uri(url5).GET().build();
        HttpResponse<String> response = httpClient.send(request5, HttpResponse.BodyHandlers.ofString());

        // converting to JsonObject
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        boolean isEmpty = (jsonObject.get("0") == null);

        Assertions.assertTrue(isEmpty);
    }

    // check endpoint DELETE_EPIC_ID
    @Test
    void shouldDeleteChosenEpic() throws IOException, InterruptedException {
        // deleting created Epic
        URI url4 = URI.create("http://localhost:8080/tasks/epic?id=2");
        HttpRequest request4 = HttpRequest.newBuilder().uri(url4).DELETE().build();
        httpClient.send(request4, HttpResponse.BodyHandlers.ofString());

        // getting epics
        URI url5 = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request5 = HttpRequest.newBuilder().uri(url5).GET().build();
        HttpResponse<String> response = httpClient.send(request5, HttpResponse.BodyHandlers.ofString());

        // converting to JsonObject
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        boolean isEmpty = (jsonObject.get("0") == null);

        Assertions.assertTrue(isEmpty);
    }

    // check endpoint DELETE_SUBTASK_ID
    @Test
    void shouldDeleteChosenSubtask() throws IOException, InterruptedException {
        // deleting created Subtask
        URI url4 = URI.create("http://localhost:8080/tasks/subtask?id=3");
        HttpRequest request4 = HttpRequest.newBuilder().uri(url4).DELETE().build();
        httpClient.send(request4, HttpResponse.BodyHandlers.ofString());

        // getting subtasks
        URI url5 = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request5 = HttpRequest.newBuilder().uri(url5).GET().build();
        HttpResponse<String> response = httpClient.send(request5, HttpResponse.BodyHandlers.ofString());

        // converting to JsonObject
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        boolean isEmpty = (jsonObject.get("0") == null);

        Assertions.assertTrue(isEmpty);
    }
}
