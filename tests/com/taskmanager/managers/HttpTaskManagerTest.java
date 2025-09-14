package com.taskmanager.managers;

import com.taskmanager.api.KVServer;
import com.taskmanager.api.KVTaskClient;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.*;
import com.taskmanager.tasks.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.taskmanager.managers.HttpTaskManager.kvTaskClient;
import static com.taskmanager.managers.HttpTaskManager.load;

/**
 * Tests for {@link HttpTaskManager} that verify saving and loading of tasks, epics,
 * subtasks, and history to/from a {@link KVServer}.
 * <p>
 * This test class covers:
 * <ul>
 *     <li>Saving tasks, epics, and subtasks to KVServer</li>
 *     <li>Retrieving tasks, epics, and subtasks from KVServer</li>
 *     <li>Saving and retrieving history of accessed tasks</li>
 *     <li>Loading saved data correctly after restarting the HttpTaskManager</li>
 *     <li>Ensuring epics retain subtasks after saving/loading</li>
 * </ul>
 * <p>
 * Each test ensures data integrity by comparing fields of original and loaded tasks.
 * </p>
 */
class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {

    private KVServer kvServer;
    private final Gson gson = new Gson();

    @BeforeEach
    void setUp() throws IOException {
        kvServer = new KVServer();
        kvServer.start();

        if (kvTaskClient != null) {
            kvTaskClient = new KVTaskClient(KVServer.PORT);
        }

        taskManager = load(KVServer.PORT);
        initTasks();
    }

    @AfterEach
    void tearDown() {
        taskManager.deleteAllTasksAllTypes();
        taskManager.setNextId(1);
        taskManager.setHistoryManager(new InMemoryHistoryManager());
        kvServer.stop();
    }

    //-----------------------
    // Checking method save()
    //-----------------------

    @Test
    void shouldReturnSavedTaskFromKvServer() {
        taskManager.createTask(task);

        // Loading task from KVServer
        String taskJsonElementString = kvTaskClient.load("ID_" + task.getId());
        JsonElement taskJsonElement = JsonParser.parseString(taskJsonElementString);
        JsonObject taskJsonObjectString = taskJsonElement.getAsJsonObject();
        Task taskFromKvServer = gson.fromJson(taskJsonObjectString, Task.class);

        // Checking fields
        String expectedName = task.getName();
        String actualName = taskFromKvServer.getName();
        String expectedDescription = task.getDescription();
        String actualDescription = taskFromKvServer.getDescription();

        Assertions.assertEquals(expectedName, actualName);
        Assertions.assertEquals(expectedDescription, actualDescription);
    }

    @Test
    void shouldReturnSavedSubtaskFromKvServer() {
        taskManager.createEpic(epic);
        subtask = new Subtask("Subtask2", "DescrSt2", TaskStatus.NEW, epic.getId());
        taskManager.createSubtask(subtask);

        // Loading task from KVServer
        String subtaskJsonElementString = kvTaskClient.load("ID_" + subtask.getId());
        JsonElement subtaskJsonElement = JsonParser.parseString(subtaskJsonElementString);
        JsonObject subtaskJsonObjectString = subtaskJsonElement.getAsJsonObject();
        Subtask subtaskFromKvServer = gson.fromJson(subtaskJsonObjectString, Subtask.class);

        // Checking fields
        String expectedName = subtask.getName();
        String actualName = subtaskFromKvServer.getName();
        String expectedDescription = subtask.getDescription();
        String actualDescription = subtaskFromKvServer.getDescription();
        int expectedIdEpic = subtask.getIdEpic();
        int actualIdEpic = subtaskFromKvServer.getIdEpic();

        Assertions.assertEquals(expectedName, actualName);
        Assertions.assertEquals(expectedDescription, actualDescription);
        Assertions.assertEquals(expectedIdEpic, actualIdEpic);
    }

    @Test
    void shouldReturnSavedEpicWith1SubtaskFromKvServer() {
        taskManager.createEpic(epic);
        subtask = new Subtask("Subtask2", "DescrSt2", TaskStatus.NEW, epic.getId());
        taskManager.createSubtask(subtask);

        // Loading task from KVServer
        String epicJsonElementString = kvTaskClient.load("ID_" + epic.getId());
        JsonElement epicJsonElement = JsonParser.parseString(epicJsonElementString);
        JsonObject epicJsonObjectString = epicJsonElement.getAsJsonObject();
        Epic epicFromKvServer = gson.fromJson(epicJsonObjectString, Epic.class);

        // Checking fields
        String expectedName = epic.getName();
        String actualName = epicFromKvServer.getName();
        String expectedDescription = epic.getDescription();
        String actualDescription = epicFromKvServer.getDescription();
        int expectedSubtasksSizeByEpic = epic.subtasks.size();
        int actualSubtasksSizeByEpic = epicFromKvServer.subtasks.size();

        Assertions.assertEquals(expectedName, actualName);
        Assertions.assertEquals(expectedDescription, actualDescription);
        Assertions.assertEquals(expectedSubtasksSizeByEpic, actualSubtasksSizeByEpic);
    }

    @Test
    void shouldReturnSavedHistoryFromKvServer() {
        taskManager.createTask(task);
        taskManager.createEpic(epic);
        subtask = new Subtask("Subtask2", "DescrSt2", TaskStatus.NEW, epic.getId());
        taskManager.createSubtask(subtask);

        // Add history by get-method
        taskManager.getSubtaskById(3); // History: 3
        taskManager.getEpicById(2); // History: 3, 2
        taskManager.getUsualTaskById(1); // History: 3, 2, 1

        // Loading history from KVServer
        String historyJsonElementString = kvTaskClient.load("History");
        List<Task> history = gson.fromJson(historyJsonElementString, new TypeToken<ArrayList<Task>>() {
        }.getType());

        // Checking size
        int expectedHistorySize = taskManager.getHistoryManager().getHistory().size();
        int actualHistorySize = history.size();

        Assertions.assertEquals(expectedHistorySize, actualHistorySize);
    }

    //-----------------------
    // Checking method load()
    //-----------------------

    @Test
    void shouldLoadSavedTaskFromKvServerAfterRestartingHttpTaskManager() {
        taskManager.createTask(task); // +1 task to tasks with saving
        int expectedTasksSize = 1;
        int actualTasksSize = taskManager.getTasks().size();
        Assertions.assertEquals(expectedTasksSize, actualTasksSize);

        InMemoryTaskManager.tasks.clear(); // deleting task direct from system without saving
        expectedTasksSize = 0;
        actualTasksSize = taskManager.getTasks().size();
        Assertions.assertEquals(expectedTasksSize, actualTasksSize);

        taskManager = load(KVServer.PORT); // loading saved data
        expectedTasksSize = 1;
        actualTasksSize = taskManager.getTasks().size();
        Assertions.assertEquals(expectedTasksSize, actualTasksSize);
    }

    @Test
    void shouldLoadSavedEpicFromKvServerAfterRestartingHttpEpicManager() {
        taskManager.createEpic(epic); // +1 epic to epics with saving
        int expectedEpicsSize = 1;
        int actualEpicsSize = taskManager.getEpics().size();
        Assertions.assertEquals(expectedEpicsSize, actualEpicsSize);

        InMemoryTaskManager.epics.clear(); // deleting epic direct from system without saving
        expectedEpicsSize = 0;
        actualEpicsSize = taskManager.getEpics().size();
        Assertions.assertEquals(expectedEpicsSize, actualEpicsSize);

        taskManager = load(KVServer.PORT); // loading saved data
        expectedEpicsSize = 1;
        actualEpicsSize = taskManager.getEpics().size();
        Assertions.assertEquals(expectedEpicsSize, actualEpicsSize);
    }

    @Test
    void shouldLoadSavedSubtaskFromKvServerAfterRestartingHttpSubtaskManager() {
        taskManager.createEpic(epic);
        subtask = new Subtask("Subtask2", "DescrSt2", TaskStatus.NEW, epic.getId());
        taskManager.createSubtask(subtask); // +1 subtask to subtasks with saving
        int expectedSubtasksSize = 1;
        int actualSubtasksSize = taskManager.getSubtasks().size();
        Assertions.assertEquals(expectedSubtasksSize, actualSubtasksSize);

        InMemoryTaskManager.subtasks.clear(); // deleting subtask direct from system without saving
        expectedSubtasksSize = 0;
        actualSubtasksSize = taskManager.getSubtasks().size();
        Assertions.assertEquals(expectedSubtasksSize, actualSubtasksSize);

        taskManager = load(KVServer.PORT); // loading saved data
        expectedSubtasksSize = 1;
        actualSubtasksSize = taskManager.getSubtasks().size();
        Assertions.assertEquals(expectedSubtasksSize, actualSubtasksSize);
    }

    @Test
    void shouldLoadSavedEpicsSubtaskFromKvServerAfterRestartingHttpSubtaskManager() {
        taskManager.createEpic(epic);
        subtask = new Subtask("Subtask2", "DescrSt2", TaskStatus.NEW, epic.getId());
        taskManager.createSubtask(subtask); // +1 subtask to epics subtasks with saving

        int expectedSubtasksByEpic = 1;
        int actualSubtasksByEpic = taskManager.getEpicSubtasks(epic).size();
        Assertions.assertEquals(expectedSubtasksByEpic, actualSubtasksByEpic);

        taskManager = load(KVServer.PORT); // loading saved data

        int expectedSubtasksByEpicAfterReloading = 1;
        int actualSubtasksByEpicAfterReloading = taskManager.getEpicSubtasks(epic).size();
        Assertions.assertEquals(expectedSubtasksByEpicAfterReloading, actualSubtasksByEpicAfterReloading);
    }

    @Test
    void shouldLoadSavedHistoryFromKvServerAfterRestartingHttpSubtaskManager() {
        taskManager.createTask(task);
        taskManager.createEpic(epic);
        subtask = new Subtask("Subtask2", "DescrSt2", TaskStatus.NEW, epic.getId());
        taskManager.createSubtask(subtask);

        // Add history by get-method
        taskManager.getSubtaskById(3); // History: 3
        taskManager.getEpicById(2); // History: 3, 2
        taskManager.getUsualTaskById(1); // History: 3, 2, 1

        int expectedHistorySize = 3;
        int actualHistorySize = taskManager.getHistoryManager().getHistory().size();
        Assertions.assertEquals(expectedHistorySize, actualHistorySize);

        taskManager = load(KVServer.PORT); // loading saved data

        int expectedHistorySizeAfterReloading = 3;
        int actualHistorySizeAfterReloading = taskManager.getHistoryManager().getHistory().size();
        Assertions.assertEquals(expectedHistorySizeAfterReloading, actualHistorySizeAfterReloading);
    }

    @Test
    void shouldLoadSavedHistoryFromKvServerAfterRestartingHttpSubtaskManagerInCorrectQueue() {
        taskManager.createTask(task);
        taskManager.createEpic(epic);
        subtask = new Subtask("Subtask2", "DescrSt2", TaskStatus.NEW, epic.getId());
        taskManager.createSubtask(subtask);

        // Add history by get-method
        taskManager.getSubtaskById(3); // History: 3
        taskManager.getEpicById(2); // History: 3, 2
        taskManager.getUsualTaskById(1); // History: 3, 2, 1

        int expectedFirstId = 3;
        int expectedLastId = 1;
        int actualFirstId = taskManager.getHistoryManager().getHistory().get(0).getId();
        int actualLastId = taskManager.getHistoryManager().getHistory().get(2).getId();

        Assertions.assertEquals(expectedFirstId, actualFirstId);
        Assertions.assertEquals(expectedLastId, actualLastId);

        taskManager = load(KVServer.PORT); // loading saved data

        int expectedFirstIdAfterReloading = 3;
        int expectedLastIdAfterReloading = 1;
        int actualFirstIdAfterReloading = taskManager.getHistoryManager().getHistory().get(0).getId();
        int actualLastIdAfterReloading = taskManager.getHistoryManager().getHistory().get(2).getId();

        Assertions.assertEquals(expectedFirstIdAfterReloading, actualFirstIdAfterReloading);
        Assertions.assertEquals(expectedLastIdAfterReloading, actualLastIdAfterReloading);
    }

}