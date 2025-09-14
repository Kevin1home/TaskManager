package com.taskmanager.managers;

import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.taskmanager.tasks.*;

import static java.nio.file.Files.readAllLines;
import static com.taskmanager.managers.FileBackedTaskManager.loadFromFile;

/**
 * Integration tests for {@link FileBackedTaskManager}.
 * <p>
 * This class verifies the persistence mechanism of tasks, epics, and subtasks:
 * <ul>
 *   <li>Saving tasks to a CSV file including history</li>
 *   <li>Loading tasks from a CSV file</li>
 *   <li>Handling empty files and files without tasks or history</li>
 * </ul>
 * <p>
 * Temporary test files are created and deleted for each test to ensure isolation.
 * </p>
 */
class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    Path pathTest = Paths.get("resources\\SavesTest.csv");

    @BeforeEach
    protected void beforeEach() throws IOException {
        Files.createFile(pathTest);
        taskManager = loadFromFile(pathTest);
        initTasks();
    }

    @AfterEach
    protected void afterEach() throws IOException {
        taskManager.deleteAllTasksAllTypes();
        taskManager.setNextId(1);
        taskManager.setHistoryManager(new InMemoryHistoryManager());
        Files.delete(pathTest);
    }

    @Test
    void shouldReturn6LinesQntAfterSaving3TasksWithHistory() throws IOException { // checking method save()
        taskManager.createTask(task);
        taskManager.createEpic(epic);
        subtask = new Subtask("Subtask2", "DescrSt2", TaskStatus.NEW, epic.getId());
        taskManager.createSubtask(subtask);

        taskManager.getUsualTaskById(task.getId());
        taskManager.getEpicById(epic.getId());
        taskManager.getSubtaskById(subtask.getId());

        List<String> allLines = readAllLines(pathTest);

        int expectedLinesQnt = 6; // parameters, Tasks (+3), EmptyLine, history
        int actualLinesQnt = allLines.size();

        Assertions.assertEquals(expectedLinesQnt, actualLinesQnt);
    }

    @Test
    void shouldReturn6LinesQntAfterSaving3TasksWithoutHistory() throws IOException { // checking method save()
        taskManager.createTask(task);
        taskManager.createEpic(epic);
        subtask = new Subtask("Subtask2", "DescrSt2", TaskStatus.NEW, epic.getId());
        taskManager.createSubtask(subtask);

        List<String> allLines = readAllLines(pathTest);

        int expectedLinesQnt = 6; // parameters, Tasks (+3), EmptyLine, History(Empty)
        int actualLinesQnt = allLines.size();

        Assertions.assertEquals(expectedLinesQnt, actualLinesQnt);
    }


    @Test
    void shouldReturn0LinesQntAfterSaving0TasksWithoutHistory() throws IOException { // checking method save()
        List<String> allLines = readAllLines(pathTest);

        int expectedLinesQnt = 0; // new file
        int actualLinesQnt = allLines.size();

        Assertions.assertEquals(expectedLinesQnt, actualLinesQnt);
    }

    @Test
    void shouldReturn4LinesQntAfterSaving1EpicWithHistory() throws IOException { // checking method save()
        taskManager.createEpic(epic);
        taskManager.getEpicById(epic.getId());
        List<String> allLines = readAllLines(pathTest);

        int expectedLinesQnt = 4; // parameters, Epic without Subtasks, EmptyLine, history
        int actualLinesQnt = allLines.size();

        Assertions.assertEquals(expectedLinesQnt, actualLinesQnt);
    }

    @Test
    void shouldLoad3TasksWithHistoryAfterLoadingFromSavedFile() { // checking method loadFromFile()
        // saving new data
        taskManager.createTask(task);
        taskManager.createEpic(epic);
        subtask = new Subtask("Subtask2", "DescrSt2", TaskStatus.NEW, epic.getId());
        taskManager.createSubtask(subtask);

        taskManager.getUsualTaskById(task.getId());
        taskManager.getEpicById(epic.getId());
        taskManager.getSubtaskById(subtask.getId());

        // deleting data direct from System without saving
        InMemoryTaskManager.tasks.clear();
        InMemoryTaskManager.epics.clear();
        InMemoryTaskManager.subtasks.clear();
        InMemoryTaskManager.historyManager = new InMemoryHistoryManager();

        // loading saved data from file
        loadFromFile(pathTest);

        boolean isNotEmpty = !taskManager.getTasks().isEmpty() && !taskManager.getEpics().isEmpty()
                && !taskManager.getSubtasks().isEmpty() && !taskManager.getHistoryManager().getHistory().isEmpty();

        Assertions.assertTrue(isNotEmpty);
    }

    @Test
    void shouldLoadFromNewEmptyFile() { // checking method loadFromFile()

        loadFromFile(pathTest); // loading from new empty file

        boolean isEmpty = taskManager.getTasks().isEmpty() && taskManager.getEpics().isEmpty()
                && taskManager.getSubtasks().isEmpty() && taskManager.getHistoryManager().getHistory().isEmpty();

        Assertions.assertTrue(isEmpty);
    }

    @Test
    void shouldLoadFromUsedFileWithoutTasksAndHistory() { // checking method loadFromFile()
        // saving new data
        taskManager.createTask(task);
        taskManager.getUsualTaskById(task.getId());

        // deleting data with saving
        taskManager.deleteTaskById(task.getId());

        // loading from used file without tasks and history
        loadFromFile(pathTest);

        boolean isEmpty = taskManager.getTasks().isEmpty() && taskManager.getEpics().isEmpty()
                && taskManager.getSubtasks().isEmpty() && taskManager.getHistoryManager().getHistory().isEmpty();

        Assertions.assertTrue(isEmpty);
    }

    @Test
    void shouldLoadFromSavedFileWithTaskAndWithoutHistory() { // checking method loadFromFile()
        // saving new data
        taskManager.createTask(task);

        // loading from used file with task and without history file
        loadFromFile(pathTest);

        boolean isEmptyAllAndTasksNot = !taskManager.getTasks().isEmpty() && taskManager.getEpics().isEmpty()
                && taskManager.getSubtasks().isEmpty() && taskManager.getHistoryManager().getHistory().isEmpty();

        Assertions.assertTrue(isEmptyAllAndTasksNot);
    }
}