package com.taskmanager.api;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.taskmanager.main_manager.Managers;
import com.taskmanager.managers.TaskManager;
import com.taskmanager.tasks.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * HTTP server for handling Task Manager API requests.
 * <p>
 * Supports CRUD operations on Tasks, Epics, and Subtasks via REST-like endpoints.
 */
// TODO: decomposition
public class HttpTaskServer {

    private static final Logger LOGGER = Logger.getLogger(HttpTaskServer.class.getName());

    HttpServer httpServer;
    private static final int PORT = 8080;
    private final Charset DEFAULT_CHARSET= StandardCharsets.UTF_8;
    private final Gson gson;
    private final TaskManager taskManager;

    public HttpTaskServer() throws IOException {
        this.httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler());

        this.gson = new Gson();
        this.taskManager = Managers.getDefault();
    }

    public void start() {
        httpServer.start();
        LOGGER.warning("Started server on port: " + PORT);
    }

    public void stop() {
        LOGGER.warning("Stopped server on port: " + PORT);
        httpServer.stop(0);
    }

    /**
     * Main request handler that maps HTTP requests to {@link Endpoint} and delegates
     * to appropriate handler methods.
     */
    class TaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {

            String method = httpExchange.getRequestMethod();
            String path = httpExchange.getRequestURI().getPath();
            String query = httpExchange.getRequestURI().getQuery();

            LOGGER.info(() -> String.format("Received request: method=%s, path=%s, query=%s", method, path, query));

            Endpoint endpoint = getEndpoint(method, path, query);

            try {
                switch (endpoint) {
                    case GET:
                        handleGetPrioritizedTasks(httpExchange);
                        break;
                    case GET_HISTORY:
                        handleGetHistoryManager(httpExchange);
                        break;
                    case GET_TASK:
                        handleGetTasks(httpExchange);
                        break;
                    case GET_EPIC:
                        handleGetEpics(httpExchange);
                        break;
                    case GET_SUBTASK:
                        handleGetSubtasks(httpExchange);
                        break;
                    case GET_SUBTASK_EPIC_ID:
                        handleGetEpicSubtasks(httpExchange, query);
                        break;
                    case GET_TASK_ID:
                        handleGetUsualTaskById(httpExchange, query);
                        break;
                    case GET_EPIC_ID:
                        handleGetEpicById(httpExchange, query);
                        break;
                    case GET_SUBTASK_ID:
                        handleGetSubtaskById(httpExchange, query);

                        break;
                    case POST_TASK:
                        handleCreateTask(httpExchange);
                        break;
                    case POST_EPIC:
                        handleCreateEpic(httpExchange);
                        break;
                    case POST_SUBTASK:
                        handleCreateSubtask(httpExchange);
                        break;

                    case PUT_TASK_ID:
                        handleUpdateUsualTask(httpExchange, query);
                        break;
                    case PUT_EPIC_ID:
                        handleUpdateEpic(httpExchange, query);
                        break;
                    case PUT_SUBTASK_ID:
                        handleUpdateSubtask(httpExchange, query);

                        break;
                    case DELETE:
                        handleDeleteAllTasksAllTypes(httpExchange);
                        break;
                    case DELETE_TASK:
                        handleDeleteAllUsualTasks(httpExchange);
                        break;
                    case DELETE_EPIC:
                        handleDeleteAllEpics(httpExchange);
                        break;
                    case DELETE_SUBTASK:
                        handleDeleteAllSubtasks(httpExchange);
                        break;
                    case DELETE_TASK_ID:
                        handleDeleteTaskById(httpExchange, query);
                        break;
                    case DELETE_EPIC_ID:
                        handleDeleteEpicById(httpExchange, query);
                        break;
                    case DELETE_SUBTASK_ID:
                        handleDeleteSubtaskById(httpExchange, query);
                        break;

                    default:
                        LOGGER.warning("Unknown endpoint requested: " + path);
                        writeResponse(httpExchange, "Endpoint not found", 404);
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Unexpected server error", e);
                writeResponse(httpExchange, "Internal server error", 500);
            }
        }
    }

    //--------------
    // GET handlers
    //--------------

    private void handleGetPrioritizedTasks(HttpExchange httpExchange) throws IOException {
        LOGGER.info("Fetching prioritized tasks");
        String prioritizedTasks = gson.toJson(taskManager.getPrioritizedTasks());
        writeResponse(httpExchange, prioritizedTasks, 200);
    }

    private void handleGetHistoryManager(HttpExchange httpExchange) throws IOException {
        LOGGER.info("Fetching task history");
        String historyManager = gson.toJson(taskManager.getHistoryManager().getHistory());
        writeResponse(httpExchange, historyManager, 200);
    }

    private void handleGetTasks(HttpExchange httpExchange) throws IOException {
        LOGGER.info("Fetching all tasks");
        String tasks = gson.toJson(taskManager.getTasks());
        writeResponse(httpExchange, tasks, 200);
    }

    private void handleGetEpics(HttpExchange httpExchange) throws IOException {
        LOGGER.info("Fetching all epics");
        String epics = gson.toJson(taskManager.getEpics());
        writeResponse(httpExchange, epics, 200);
    }

    private void handleGetSubtasks(HttpExchange httpExchange) throws IOException {
        LOGGER.info("Fetching all subtasks");
        String subtasks = gson.toJson(taskManager.getSubtasks());
        writeResponse(httpExchange, subtasks, 200);
    }

    private void handleGetEpicSubtasks(HttpExchange httpExchange, String query) throws IOException {

        try {
            int epicId = Integer.parseInt(query.substring(3));
            LOGGER.info("Fetching subtasks for epic id=" + epicId);
            Epic epic = taskManager.getEpicById(epicId);
            String subtasksEpicJsonString = gson.toJson(taskManager.getEpicSubtasks(epic));
            writeResponse(httpExchange, subtasksEpicJsonString, 200);

        } catch (NumberFormatException exception) {
            writeResponse(httpExchange, "Invalid ID", 400);
        } catch (IllegalArgumentException exception) {
            writeResponse(httpExchange, exception.getMessage(), 404);
        }
    }

    private void handleGetUsualTaskById(HttpExchange httpExchange, String query) throws IOException {

        try {
            int taskId = Integer.parseInt(query.substring(3));
            LOGGER.info("Fetching task id=" + taskId);
            Task task = taskManager.getUsualTaskById(taskId);
            String taskJsonString = gson.toJson(task);
            writeResponse(httpExchange, taskJsonString, 200);
        } catch (NumberFormatException exception) {
            writeResponse(httpExchange, "Invalid ID", 400);
        } catch (IllegalArgumentException exception) {
            writeResponse(httpExchange, exception.getMessage(), 404);
        }
    }

    private void handleGetEpicById(HttpExchange httpExchange, String query) throws IOException {

        try {
            int epicId = Integer.parseInt(query.substring(3));
            LOGGER.info("Fetching epic id=" + epicId);
            Epic epic = taskManager.getEpicById(epicId);
            String epicJsonString = gson.toJson(epic);
            writeResponse(httpExchange, epicJsonString, 200);

        } catch (NumberFormatException exception) {
            writeResponse(httpExchange, "Invalid ID", 400);
        } catch (IllegalArgumentException exception) {
            writeResponse(httpExchange, exception.getMessage(), 404);
        }
    }

    private void handleGetSubtaskById(HttpExchange httpExchange, String query) throws IOException {

        try {
            int subtaskId = Integer.parseInt(query.substring(3));
            LOGGER.info("Fetching subtask id=" + subtaskId);
            Subtask subtask = taskManager.getSubtaskById(subtaskId);
            String subtaskJsonString = gson.toJson(subtask);
            writeResponse(httpExchange, subtaskJsonString, 200);

        } catch (NumberFormatException exception) {
            writeResponse(httpExchange, "Invalid ID", 400);
        } catch (IllegalArgumentException exception) {
            writeResponse(httpExchange, exception.getMessage(), 404);
        }
    }

    //--------------
    // POST handlers
    //--------------

    private void handleCreateTask(HttpExchange httpExchange) throws IOException {

        InputStream inputStream = httpExchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);

        try {
            Task task = gson.fromJson(body, Task.class);
            LOGGER.info("Creating new task: " + task.getName());
            if (task.getName().isBlank() || task.getDescription().isBlank()) {
                writeResponse(httpExchange, "Task name and description cannot be empty", 400);
                return;
            }

            String name = task.getName();
            String description = task.getDescription();

            if (task.getStatus() == null && (task.getStartTime().isEmpty() || task.getDuration().isEmpty())) {
                taskManager.createTask(new Task(name, description));
                writeResponse(httpExchange, "Task successfully added", 201);

            } else if (task.getStatus() == null && (task.getStartTime().isPresent()
                    && task.getDuration().isPresent())) {
                LocalDateTime startTime = task.getStartTime().get();
                Duration duration = task.getDuration().get();
                taskManager.createTask(new Task(name, description, startTime.format(Task.dateTimeFormatter),
                        duration.toMinutes()));
                writeResponse(httpExchange, "Task successfully added", 201);
            }

            TaskStatus taskStatus = task.getStatus();

            if (task.getStatus() != null
                    && (task.getStartTime().isEmpty() || task.getDuration().isEmpty())) {
                taskManager.createTask(new Task(name, description, taskStatus));
                writeResponse(httpExchange, "Task successfully added", 201);

            } else if (task.getStatus() != null
                    && (task.getStartTime().isPresent() && task.getDuration().isPresent())) {

                LocalDateTime startTime = task.getStartTime().get();
                Duration duration = task.getDuration().get();

                taskManager.createTask(new Task(name, description, taskStatus, startTime.format(Task.dateTimeFormatter),
                        duration.toMinutes()));
                writeResponse(httpExchange, "Task successfully added", 201);

            } else {
                writeResponse(httpExchange, "Task creation fails", 404);
            }

        } catch (JsonSyntaxException exception) {
            writeResponse(httpExchange, "Invalid JSON format", 400);
        }
    }

    private void handleCreateEpic(HttpExchange httpExchange) throws IOException {

        InputStream inputStream = httpExchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);

        try {
            Epic epic = gson.fromJson(body, Epic.class);
            LOGGER.info("Creating new epic: " + epic.getName());
            if (epic.getName().isBlank() | epic.getDescription().isBlank()) {
                writeResponse(httpExchange, "Epic name and description cannot be empty", 400);
                return;
            }

            String name = epic.getName();
            String description = epic.getDescription();

            if (epic.getStatus() == null) {
                taskManager.createEpic(new Epic(name, description));
                writeResponse(httpExchange, "Epic successfully added", 201);
                return;
            }

            TaskStatus taskStatus = epic.getStatus();

            taskManager.createEpic(new Epic(name, description, taskStatus));
            writeResponse(httpExchange, "Epic successfully added", 201);

        } catch (JsonSyntaxException exception) {
            writeResponse(httpExchange, "Invalid JSON format", 400);
        }
    }

    private void handleCreateSubtask(HttpExchange httpExchange) throws IOException {

        InputStream inputStream = httpExchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);

        try {
            Subtask subtask = gson.fromJson(body, Subtask.class);
            LOGGER.info("Creating new subtask: " + subtask.getName() + " for epic id=" + subtask.getIdEpic());
            if (subtask.getName().isBlank() || subtask.getDescription().isBlank() || subtask.getIdEpic() == 0) {
                writeResponse(httpExchange, "Subtask name, description and epic ID cannot be empty", 400);
                return;
            }

            String name = subtask.getName();
            String description = subtask.getDescription();
            int idEpic = subtask.getIdEpic();

            if (subtask.getStatus() == null
                    && (subtask.getStartTime().isEmpty() || subtask.getDuration().isEmpty())) {
                taskManager.createSubtask(new Subtask(name, description, idEpic));
                writeResponse(httpExchange, "Subtask successfully added", 201);

            } else if (subtask.getStatus() == null
                    && (subtask.getStartTime().isPresent() && subtask.getDuration().isPresent())) {
                LocalDateTime startTime = subtask.getStartTime().get();
                Duration duration = subtask.getDuration().get();
                taskManager.createSubtask(new Subtask(name, description, startTime.format(Task.dateTimeFormatter),
                        duration.toMinutes(), idEpic));
                writeResponse(httpExchange, "Subtask successfully added", 201);
            }

            TaskStatus taskStatus = subtask.getStatus();

            if (subtask.getStatus() != null
                    && (subtask.getStartTime().isEmpty() || subtask.getDuration().isEmpty())) {
                taskManager.createSubtask(new Subtask(name, description, taskStatus, idEpic));
                writeResponse(httpExchange, "Subtask successfully added", 201);

            } else if (subtask.getStatus() != null
                    && (subtask.getStartTime().isPresent() && subtask.getDuration().isPresent())) {
                LocalDateTime startTime = subtask.getStartTime().get();
                Duration duration = subtask.getDuration().get();
                taskManager.createSubtask(new Subtask(name, description, taskStatus,
                        startTime.format(Task.dateTimeFormatter), duration.toMinutes(), idEpic));
                writeResponse(httpExchange, "Subtask successfully added", 201);

            } else {
                writeResponse(httpExchange, "Subtask creation fails", 404);
            }

        } catch (JsonSyntaxException exception) {
            writeResponse(httpExchange, "Invalid JSON format", 400);
        } catch (IllegalArgumentException exception) {
            writeResponse(httpExchange, exception.getMessage(), 404);
        }
    }

    //--------------
    // PUT handlers
    //--------------

    private void handleUpdateUsualTask(HttpExchange httpExchange, String query) throws IOException {

        InputStream inputStream = httpExchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);

        try {
            int taskId = Integer.parseInt(query.substring(3));
            Task task = gson.fromJson(body, Task.class);

            if (task.getName().isBlank() || task.getDescription().isBlank()) {
                writeResponse(httpExchange, "Task name and description cannot be empty", 400);
                return;
            }

            taskManager.updateUsualTask(task, taskId);
            writeResponse(httpExchange, "Task successfully updated", 201);

        } catch (NumberFormatException exception) {
            writeResponse(httpExchange, "Invalid ID", 400);
        } catch (JsonSyntaxException exception) {
            writeResponse(httpExchange, "Invalid JSON format", 400);
        } catch (IllegalArgumentException exception) {
            writeResponse(httpExchange, exception.getMessage(), 404);
        }
    }

    private void handleUpdateEpic(HttpExchange httpExchange, String query) throws IOException {

        InputStream inputStream = httpExchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);

        try {
            int epicId = Integer.parseInt(query.substring(3));
            Epic epic = gson.fromJson(body, Epic.class);

            if (epic.getName().isBlank() || epic.getDescription().isBlank()) {
                writeResponse(httpExchange, "Epic name and description cannot be empty", 400);
                return;
            }

            taskManager.updateEpic(epic, epicId);
            writeResponse(httpExchange, "Epic successfully updated", 201);

        } catch (NumberFormatException exception) {
            writeResponse(httpExchange, "Invalid ID", 400);
        } catch (JsonSyntaxException exception) {
            writeResponse(httpExchange, "Invalid JSON format", 400);
        } catch (IllegalArgumentException exception) {
            writeResponse(httpExchange, exception.getMessage(), 404);
        }
    }

    private void handleUpdateSubtask(HttpExchange httpExchange, String query) throws IOException {

        InputStream inputStream = httpExchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);

        try {
            int subtaskId = Integer.parseInt(query.substring(3));
            Subtask subtask = gson.fromJson(body, Subtask.class);

            if (subtask.getName().isBlank() || subtask.getDescription().isBlank()) {
                writeResponse(httpExchange, "Subtask name and description cannot be empty", 400);
                return;
            }

            taskManager.updateSubtask(subtask, subtaskId);
            writeResponse(httpExchange, "Subtask successfully updated", 201);

        } catch (NumberFormatException exception) {
            writeResponse(httpExchange, "Invalid ID", 400);
        } catch (JsonSyntaxException exception) {
            writeResponse(httpExchange, "Invalid JSON format", 400);
        } catch (IllegalArgumentException exception) {
            writeResponse(httpExchange, exception.getMessage(), 404);
        }
    }

    //--------------
    // DELETE handlers
    //--------------

    private void handleDeleteAllTasksAllTypes(HttpExchange httpExchange) throws IOException {
        LOGGER.info("Deleting all tasks of all types");
        taskManager.deleteAllTasksAllTypes();
        writeResponse(httpExchange, "All tasks (any type) deleted", 200);
    }

    private void handleDeleteAllUsualTasks(HttpExchange httpExchange) throws IOException {
        LOGGER.info("Deleting all tasks");
        taskManager.deleteAllUsualTasks();
        writeResponse(httpExchange, "All tasks deleted", 200);
    }

    private void handleDeleteAllEpics(HttpExchange httpExchange) throws IOException {
        LOGGER.info("Deleting all epics");
        taskManager.deleteAllEpics();
        writeResponse(httpExchange, "All epics deleted", 200);
    }

    private void handleDeleteAllSubtasks(HttpExchange httpExchange) throws IOException {
        LOGGER.info("Deleting all subtasks");
        taskManager.deleteAllSubtasks();
        writeResponse(httpExchange, "All subtasks deleted", 200);
    }

    private void handleDeleteTaskById(HttpExchange httpExchange, String query) throws IOException {

        try {
            int taskId = Integer.parseInt(query.substring(3));
            LOGGER.info("Deleting task id=" + taskId);
            taskManager.deleteTaskById(taskId);
            writeResponse(httpExchange, "Task deleted successfully", 200);

        } catch (NumberFormatException exception) {
            writeResponse(httpExchange, "Invalid ID", 400);
        } catch (IllegalArgumentException exception) {
            writeResponse(httpExchange, exception.getMessage(), 404);
        }
    }

    private void handleDeleteEpicById(HttpExchange httpExchange, String query) throws IOException {

        try {
            int epicId = Integer.parseInt(query.substring(3));
            LOGGER.info("Deleting epic id=" + epicId);
            taskManager.deleteEpicById(epicId);
            writeResponse(httpExchange, "Epic deleted successfully", 200);

        } catch (NumberFormatException exception) {
            writeResponse(httpExchange, "Invalid ID", 400);
        } catch (IllegalArgumentException exception) {
            writeResponse(httpExchange, exception.getMessage(), 404);
        }
    }

    private void handleDeleteSubtaskById(HttpExchange httpExchange, String query) throws IOException {

        try {
            int subtaskId = Integer.parseInt(query.substring(3));
            LOGGER.info("Deleting subtask id=" + subtaskId);
            taskManager.deleteSubtaskById(subtaskId);
            writeResponse(httpExchange, "Subtask deleted successfully", 200);

        } catch (NumberFormatException exception) {
            writeResponse(httpExchange, "Invalid ID", 400);
        } catch (IllegalArgumentException exception) {
            writeResponse(httpExchange, exception.getMessage(), 404);
        }
    }

    //----------------
    // Utility methods
    //----------------

    private Endpoint getEndpoint(String method, String path, String query) {

        String[] pathParts = path.split("/");

        switch (method) {
            case "GET":
                if (pathParts.length == 2 && query == null) {
                    return Endpoint.GET; // TreeSet<Task> getPrioritizedTasks();
                } else if (pathParts.length == 3 && pathParts[2].equals("history") && query == null) {
                    return Endpoint.GET_HISTORY; // HistoryManager getHistoryManager();
                } else if (pathParts.length == 3 && pathParts[2].equals("task") && query == null) {
                    return Endpoint.GET_TASK; // HashMap<Integer, Task> getTasks();
                } else if (pathParts.length == 3 && pathParts[2].equals("epic") && query == null) {
                    return Endpoint.GET_EPIC; // HashMap<Integer, Epic> getEpics();
                } else if (pathParts.length == 3 && pathParts[2].equals("subtask") && query == null) {
                    return Endpoint.GET_SUBTASK; // HashMap<Integer, Subtask> getSubtasks();
                } else if (pathParts.length == 4 && pathParts[2].equals("subtask") && pathParts[3].equals("epic")
                        && query.startsWith("id=")) {
                    return Endpoint.GET_SUBTASK_EPIC_ID; // ArrayList<Subtask> getEpicSubtasks(Epic epic)
                } else if (pathParts.length == 3 && pathParts[2].equals("task") && query.startsWith("id=")) {
                    return Endpoint.GET_TASK_ID; // Task getUsualTaskById(int id);
                } else if (pathParts.length == 3 && pathParts[2].equals("epic") && query.startsWith("id=")) {
                    return Endpoint.GET_EPIC_ID; // Epic getEpicById(int id);
                } else if (pathParts.length == 3 && pathParts[2].equals("subtask") && query.startsWith("id=")) {
                    return Endpoint.GET_SUBTASK_ID; // Subtask getSubtaskById(int id);
                } else {
                    return Endpoint.UNKNOWN;
                }

            case "POST":
                if (pathParts.length == 3 && pathParts[2].equals("task") && query == null) {
                    return Endpoint.POST_TASK; // void createTask(Task task);
                } else if (pathParts.length == 3 && pathParts[2].equals("epic") && query == null) {
                    return Endpoint.POST_EPIC; // void createEpic(Epic epic);
                } else if (pathParts.length == 3 && pathParts[2].equals("subtask") && query == null) {
                    return Endpoint.POST_SUBTASK; // void createSubtask(Subtask subtask);
                } else {
                    return Endpoint.UNKNOWN;
                }

            case "PUT":
                if (pathParts.length == 3 && pathParts[2].equals("task") && query.startsWith("id=")) {
                    return Endpoint.PUT_TASK_ID; // void updateUsualTask(Task task, int id);
                } else if (pathParts.length == 3 && pathParts[2].equals("epic") && query.startsWith("id=")) {
                    return Endpoint.PUT_EPIC_ID; // void updateEpic(Epic epic, int id);
                } else if (pathParts.length == 3 && pathParts[2].equals("subtask") && query.startsWith("id=")) {
                    return Endpoint.PUT_SUBTASK_ID; // void updateSubtask(Subtask subtask, int id);
                } else {
                    return Endpoint.UNKNOWN;
                }

            case "DELETE":
                if (pathParts.length == 2 && query == null) {
                    return Endpoint.DELETE; // void deleteAllTasksAllTypes();
                } else if (pathParts.length == 3 && pathParts[2].equals("task") && query == null) {
                    return Endpoint.DELETE_TASK; // void deleteAllUsualTasks();
                } else if (pathParts.length == 3 && pathParts[2].equals("epic") && query == null) {
                    return Endpoint.DELETE_EPIC; // void deleteAllEpics();
                } else if (pathParts.length == 3 && pathParts[2].equals("subtask") && query == null) {
                    return Endpoint.DELETE_SUBTASK; // void deleteAllSubtasks();
                } else if (pathParts.length == 3 && pathParts[2].equals("task") && query.startsWith("id=")) {
                    return Endpoint.DELETE_TASK_ID; // void deleteTaskById(int id);
                } else if (pathParts.length == 3 && pathParts[2].equals("epic") && query.startsWith("id=")) {
                    return Endpoint.DELETE_EPIC_ID; // void deleteEpicById(int id);
                } else if (pathParts.length == 3 && pathParts[2].equals("subtask") && query.startsWith("id=")) {
                    return Endpoint.DELETE_SUBTASK_ID; // void deleteSubtaskById(int id);
                } else {
                    return Endpoint.UNKNOWN;
                }

            default:
                return Endpoint.UNKNOWN;
        }
    }

    private void writeResponse(HttpExchange httpExchange, String response, int responseCode) throws IOException {

        if (response.isBlank()) {
            httpExchange.sendResponseHeaders(responseCode, 0);
            return;
        }

        byte[] bytes = response.getBytes(DEFAULT_CHARSET);
        httpExchange.sendResponseHeaders(responseCode, bytes.length);

        try (OutputStream outputStream = httpExchange.getResponseBody()) {
            outputStream.write(bytes);
        }
        httpExchange.close();
    }
}
