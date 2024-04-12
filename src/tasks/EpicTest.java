package tasks;

import main_manager.Managers;
import managers.InMemoryHistoryManager;
import managers.TaskManager;
import org.junit.jupiter.api.*;

class EpicTest {

    TaskManager taskManager;
    Epic epic;

    @BeforeEach
    protected void beforeEach() {
        taskManager = Managers.getDefaultWithoutSaving();
        epic = new Epic("Epic", "DescrEp"); // Create 1x Epic without Subtasks
        taskManager.createEpic(epic);
    }

    @AfterEach
    protected void afterEach() {
        taskManager.deleteAllTasksAllTypes();
        taskManager.setNextId(1);
        taskManager.setHistoryManager(new InMemoryHistoryManager());
    }

    @Test
    protected void shouldReturnStatusNewByCreatedEpicWithoutSubtasks() {
        // Expected and actual Status
        TaskStatus expectedStatus = TaskStatus.NEW;
        TaskStatus actualStatus = epic.getStatus();

        // Assertion
        Assertions.assertEquals(expectedStatus, actualStatus);
    }

    @Test
    protected void shouldReturnStatusNewByCreatedEpicWithSubtasksWithStatusNewEach() {
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
    protected void shouldReturnStatusDoneByCreatedEpicWithSubtasksWithStatusDoneEach() {
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
    protected void shouldReturnStatusInProgressByCreatedEpicWithSubtasksWithStatusDoneAndNew() {
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
    protected void shouldReturnStatusInProgressByCreatedEpicWithSubtasksWithStatusInProgressEach() {
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

    @Test
    protected void shouldReturnStartTimeByEpicAsBySubtask() { // checking method checkStartTimeEpic()
        Subtask subtask1 = new Subtask("Subtask1", "DescrSt1",
                "01.06.2024, 09:30", 60L, epic.getId());
        taskManager.createSubtask(subtask1);
        epic.checkStartTimeEpic();

        String expectedStartTime = "01.06.2024, 09:30";
        String actualStartTime;
        if (epic.getStartTime().isPresent()) {
            actualStartTime = epic.getStartTime().get().format(Task.dateTimeFormatter);
        } else {
            actualStartTime = null;
        }

        Assertions.assertEquals(expectedStartTime, actualStartTime);

    }

    @Test
    protected void shouldReturnEarliestStartTimeByEpic() { // checking method checkStartTimeEpic()
        Subtask subtask1 = new Subtask("Subtask1", "DescrSt1",
                "02.06.2024, 13:30", 60L, epic.getId());
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Subtask2", "DescrSt2",
                "02.06.2024, 10:30", 80L, epic.getId());
        taskManager.createSubtask(subtask2);
        Subtask subtask3 = new Subtask("Subtask2", "DescrSt2",
                "01.06.2024, 09:30", 120L, epic.getId());
        taskManager.createSubtask(subtask3);
        epic.checkStartTimeEpic();

        String expectedStartTime = "01.06.2024, 09:30";
        String actualStartTime;
        if (epic.getStartTime().isPresent()) {
            actualStartTime = epic.getStartTime().get().format(Task.dateTimeFormatter);
        } else {
            actualStartTime = null;
        }

        Assertions.assertEquals(expectedStartTime, actualStartTime);

    }

    @Test
    protected void shouldReturnStartTimeByEpicWithSubtaskWithoutDateTimeParameters() { // checking method checkStartTimeEpic()
        Subtask subtask1 = new Subtask("Subtask1", "DescrSt1",
                "01.06.2024, 09:30", 60L, epic.getId());
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Subtask2", "DescrSt2", epic.getId());
        taskManager.createSubtask(subtask2);
        epic.checkStartTimeEpic();

        String expectedStartTime = "01.06.2024, 09:30";
        String actualStartTime;
        if (epic.getStartTime().isPresent()) {
            actualStartTime = epic.getStartTime().get().format(Task.dateTimeFormatter);
        } else {
            actualStartTime = null;
        }

        Assertions.assertEquals(expectedStartTime, actualStartTime);

    }

    @Test
    protected void shouldReturnStartTimeNullByEpicWithSubtaskWithNullDateTimeParameters() { // checking method checkStartTimeEpic()
        Subtask subtask1 = new Subtask("Subtask1", "DescrSt1",
                null, 60L, epic.getId());
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Subtask2", "DescrSt2", epic.getId());
        taskManager.createSubtask(subtask2);
        epic.checkStartTimeEpic();

        String expectedStartTime = null;
        String actualStartTime;
        if (epic.getStartTime().isPresent()) {
            actualStartTime = epic.getStartTime().get().format(Task.dateTimeFormatter);
        } else {
            actualStartTime = null;
        }

        Assertions.assertEquals(expectedStartTime, actualStartTime);

    }

    @Test
    protected void shouldReturnDurationByEpicWithSubtasks() { // checking method checkDurationEpic()
        Subtask subtask1 = new Subtask("Subtask1", "DescrSt1",
                "02.06.2024, 13:30", 60L, epic.getId());
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Subtask2", "DescrSt2",
                "02.06.2024, 10:30", 80L, epic.getId());
        taskManager.createSubtask(subtask2);
        Subtask subtask3 = new Subtask("Subtask2", "DescrSt2",
                "01.06.2024, 09:30", 120L, epic.getId());
        taskManager.createSubtask(subtask3);
        epic.checkDurationEpic();

        long expectedDuration = 260L; // 60 + 80 + 120
        long actualDuration;
        if (epic.getDuration().isPresent()) {
            actualDuration = epic.getDuration().get().toMinutes();
        } else {
            actualDuration = -1;
        }

        Assertions.assertEquals(expectedDuration, actualDuration);

    }

    @Test
    protected void shouldReturnDuration0ByEpicWithSubtaskWithNegativeDuration() { // checking method checkDurationEpic()
        Subtask subtask1 = new Subtask("Subtask1", "DescrSt1",
                "02.06.2024, 13:30", -60, epic.getId());
        taskManager.createSubtask(subtask1);
        epic.checkDurationEpic();

        long expectedDuration = 0L;
        long actualDuration;
        if (epic.getDuration().isPresent()) {
            actualDuration = epic.getDuration().get().toMinutes();
        } else {
            actualDuration = -1L;
        }

        Assertions.assertEquals(expectedDuration, actualDuration);

    }

    @Test
    protected void shouldReturnDurationByEpicWithSubtaskWithNegativeDuratioAndNormalDuration() { // checking method checkDurationEpic()
        Subtask subtask1 = new Subtask("Subtask1", "DescrSt1",
                "02.06.2024, 13:30", -60L, epic.getId());
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Subtask2", "DescrSt2",
                "02.06.2024, 10:30", 80L, epic.getId());
        taskManager.createSubtask(subtask2);
        epic.checkDurationEpic();

        long expectedDuration = 80L; // (-60 => 0) + 80
        long actualDuration;
        if (epic.getDuration().isPresent()) {
            actualDuration = epic.getDuration().get().toMinutes();
        } else {
            actualDuration = -1L;
        }

        Assertions.assertEquals(expectedDuration, actualDuration);

    }

    @Test
    protected void shouldReturnEndTimeByEpicAsBySubtask() { // checking method checkEndTimeEpic()
        Subtask subtask1 = new Subtask("Subtask1", "DescrSt1",
                "01.06.2024, 09:30", 60L, epic.getId());
        taskManager.createSubtask(subtask1);
        epic.checkEndTimeEpic();

        String expectedEndTime = "01.06.2024, 10:30";
        String actualEndTime;
        if (epic.getEndTime().isPresent()) {
            actualEndTime = epic.getEndTime().get().format(Task.dateTimeFormatter);
        } else {
            actualEndTime = null;
        }

        Assertions.assertEquals(expectedEndTime, actualEndTime);

    }

    @Test
    protected void shouldReturnLatestEndTimeByEpic() { // checking method checkEndTimeEpic()
        Subtask subtask1 = new Subtask("Subtask1", "DescrSt1",
                "01.06.2024, 09:30", 60L, epic.getId());
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Subtask2", "DescrSt2",
                "02.06.2024, 10:30", 80L, epic.getId());
        taskManager.createSubtask(subtask2);
        Subtask subtask3 = new Subtask("Subtask2", "DescrSt2",
                "02.06.2024, 13:30", 120L, epic.getId());
        taskManager.createSubtask(subtask3);
        epic.checkEndTimeEpic();

        String expectedEndTime = "02.06.2024, 15:30";
        String actualSEndTime;
        if (epic.getEndTime().isPresent()) {
            actualSEndTime = epic.getEndTime().get().format(Task.dateTimeFormatter);
        } else {
            actualSEndTime = null;
        }

        Assertions.assertEquals(expectedEndTime, actualSEndTime);

    }

    @Test
    protected void shouldReturnEndTimeByEpicWithSubtaskWithoutDateTimeParameters() { // checking method checkEndTimeEpic()
        Subtask subtask1 = new Subtask("Subtask1", "DescrSt1",
                "01.06.2024, 09:30", 60L, epic.getId());
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Subtask2", "DescrSt2", epic.getId());
        taskManager.createSubtask(subtask2);
        epic.checkEndTimeEpic();

        String expectedEndTime = "01.06.2024, 10:30";
        String actualSEndTime;
        if (epic.getEndTime().isPresent()) {
            actualSEndTime = epic.getEndTime().get().format(Task.dateTimeFormatter);
        } else {
            actualSEndTime = null;
        }

        Assertions.assertEquals(expectedEndTime, actualSEndTime);

    }

    @Test
    protected void shouldReturnEndTimeNullByEpicWithSubtaskWithNullDateTimeParameters() { // checking method checkEndTimeEpic()
        Subtask subtask1 = new Subtask("Subtask1", "DescrSt1",
                null, 60L, epic.getId());
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Subtask2", "DescrSt2", epic.getId());
        taskManager.createSubtask(subtask2);
        epic.checkEndTimeEpic();

        String expectedEndTime = null;
        String actualEndTime;
        if (epic.getEndTime().isPresent()) {
            actualEndTime = epic.getEndTime().get().format(Task.dateTimeFormatter);
        } else {
            actualEndTime = null;
        }

        Assertions.assertEquals(expectedEndTime, actualEndTime);

    }

}