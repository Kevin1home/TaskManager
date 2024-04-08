package tasks;

import main_manager.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EpicTest {

    TaskManager taskManager;
    Epic epic;

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefaultWithoutSaving();
        // Create 1x Epic without Subtasks
        epic = new Epic("Epic", "DescrEp");
        taskManager.createEpic(epic);
    }

    @Test
    void shouldReturnStatusNewByCreatedEpicWithoutSubtasks() {
        // Expected and actual Status
        TaskStatus expectedStatus = TaskStatus.NEW;
        TaskStatus actualStatus = epic.getStatus();

        // Assertion
        Assertions.assertEquals(expectedStatus, actualStatus);
    }

    @Test
    void shouldReturnStatusNewByCreatedEpicWithSubtasksWithStatusNewEach() {
        // For created Epic create 2x Subtasks with status NEW each
        Subtask subtask1 = new Subtask("Subtask1", "DescrSt1", TaskStatus.NEW, epic.getId());
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Subtask2", "DescrSt2", TaskStatus.NEW, epic.getId());
        taskManager.createSubtask(subtask2);

        // Expected and actual Status
        TaskStatus expectedStatus = TaskStatus.NEW;
        TaskStatus actualStatus = epic.getStatus();

        // Assertion
        Assertions.assertEquals(expectedStatus, actualStatus);
    }

    @Test
    void shouldReturnStatusDoneByCreatedEpicWithSubtasksWithStatusDoneEach() {
        // For created Epic create 2x Subtasks with status DONE each
        Subtask subtask1 = new Subtask("Subtask1", "DescrSt1", TaskStatus.DONE, epic.getId());
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Subtask2", "DescrSt2", TaskStatus.DONE, epic.getId());
        taskManager.createSubtask(subtask2);

        // Expected and actual Status
        TaskStatus expectedStatus = TaskStatus.DONE;
        TaskStatus actualStatus = epic.getStatus();

        // Assertion
        Assertions.assertEquals(expectedStatus, actualStatus);
    }

    @Test
    void shouldReturnStatusInProgressByCreatedEpicWithSubtasksWithStatusDoneAndNew() {
        // For created Epic create 2x Subtasks with status DONE and NEW
        Subtask subtask1 = new Subtask("Subtask1", "DescrSt1", TaskStatus.DONE, epic.getId());
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Subtask2", "DescrSt2", TaskStatus.NEW, epic.getId());
        taskManager.createSubtask(subtask2);

        // Expected and actual Status
        TaskStatus expectedStatus = TaskStatus.IN_PROGRESS;
        TaskStatus actualStatus = epic.getStatus();

        // Assertion
        Assertions.assertEquals(expectedStatus, actualStatus);
    }

    @Test
    void shouldReturnStatusInProgressByCreatedEpicWithSubtasksWithStatusInProgressEach() {
        // For created Epic create 2x Subtasks with status DONE and NEW
        Subtask subtask1 = new Subtask("Subtask1", "DescrSt1", TaskStatus.IN_PROGRESS, epic.getId());
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Subtask2", "DescrSt2", TaskStatus.IN_PROGRESS, epic.getId());
        taskManager.createSubtask(subtask2);

        // Expected and actual Status
        TaskStatus expectedStatus = TaskStatus.IN_PROGRESS;
        TaskStatus actualStatus = epic.getStatus();

        // Assertion
        Assertions.assertEquals(expectedStatus, actualStatus);
    }

}