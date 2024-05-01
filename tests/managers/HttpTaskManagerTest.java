package managers;

import api.KVServer;
import api.KVTaskClient;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.*;
import tasks.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static managers.HttpTaskManager.kvTaskClient;
import static managers.HttpTaskManager.load;

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

    // checking method save()
    @Test
    void shouldReturnSavedTaskFromKvServer() {
        taskManager.createTask(task);

        // загружаем задачу из KVServer
        String taskJsonElementString = kvTaskClient.load("ID_" + task.getId());
        JsonElement taskJsonElement = JsonParser.parseString(taskJsonElementString);
        JsonObject taskJsonObjectString = taskJsonElement.getAsJsonObject();
        Task taskFromKvServer = gson.fromJson(taskJsonObjectString, Task.class);

        // проверяем поля
        String expectedName = task.getName();
        String actualName = taskFromKvServer.getName();
        String expectedDescription = task.getDescription();
        String actualDescription = taskFromKvServer.getDescription();

        Assertions.assertEquals(expectedName, actualName);
        Assertions.assertEquals(expectedDescription, actualDescription);
    }

    // checking method save()
    @Test
    void shouldReturnSavedSubtaskFromKvServer() {
        taskManager.createEpic(epic);
        subtask = new Subtask("Subtask2", "DescrSt2", TaskStatus.NEW, epic.getId());
        taskManager.createSubtask(subtask);

        // загружаем задачу из KVServer
        String subtaskJsonElementString = kvTaskClient.load("ID_" + subtask.getId());
        JsonElement subtaskJsonElement = JsonParser.parseString(subtaskJsonElementString);
        JsonObject subtaskJsonObjectString = subtaskJsonElement.getAsJsonObject();
        Subtask subtaskFromKvServer = gson.fromJson(subtaskJsonObjectString, Subtask.class);

        // проверяем поля
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

    // checking method save()
    @Test
    void shouldReturnSavedEpicWith1SubtaskFromKvServer() {
        taskManager.createEpic(epic);
        subtask = new Subtask("Subtask2", "DescrSt2", TaskStatus.NEW, epic.getId());
        taskManager.createSubtask(subtask);

        // загружаем задачу из KVServer
        String epicJsonElementString = kvTaskClient.load("ID_" + epic.getId());
        JsonElement epicJsonElement = JsonParser.parseString(epicJsonElementString);
        JsonObject epicJsonObjectString = epicJsonElement.getAsJsonObject();
        Epic epicFromKvServer = gson.fromJson(epicJsonObjectString, Epic.class);

        // проверяем поля
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

    // checking method save()
    @Test
    void shouldReturnSavedHistoryFromKvServer() {
        taskManager.createTask(task);
        taskManager.createEpic(epic);
        subtask = new Subtask("Subtask2", "DescrSt2", TaskStatus.NEW, epic.getId());
        taskManager.createSubtask(subtask);

        // добавляем историю с помощью метода get
        taskManager.getSubtaskById(3); // History: 3
        taskManager.getEpicById(2); // History: 3, 2
        taskManager.getUsualTaskById(1); // History: 3, 2, 1

        // загружаем историю из KVServer
        String historyJsonElementString = kvTaskClient.load("History");
        List<Task> history = gson.fromJson(historyJsonElementString, new TypeToken<ArrayList<Task>>() {
        }.getType());

        // проверяем размер
        int expectedHistorySize = taskManager.getHistoryManager().getHistory().size();
        int actualHistorySize = history.size();

        Assertions.assertEquals(expectedHistorySize, actualHistorySize);
    }

    // checking method load()
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

    // checking method load()
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

    // checking method load()
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

    // checking method load()
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

    // checking method load()
    @Test
    void shouldLoadSavedHistoryFromKvServerAfterRestartingHttpSubtaskManager() {
        taskManager.createTask(task);
        taskManager.createEpic(epic);
        subtask = new Subtask("Subtask2", "DescrSt2", TaskStatus.NEW, epic.getId());
        taskManager.createSubtask(subtask);

        // добавляем историю с помощью метода get
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

    // checking method load()
    @Test
    void shouldLoadSavedHistoryFromKvServerAfterRestartingHttpSubtaskManagerInCorrectQueue() {
        taskManager.createTask(task);
        taskManager.createEpic(epic);
        subtask = new Subtask("Subtask2", "DescrSt2", TaskStatus.NEW, epic.getId());
        taskManager.createSubtask(subtask);

        // добавляем историю с помощью метода get
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