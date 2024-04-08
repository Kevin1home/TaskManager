package managers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tasks.*;
import tasks.TaskStatus;

import java.util.HashMap;
import java.util.List;

abstract class TaskManagerTest<T extends TaskManager> { // Testing methods of interface TaskManager

    protected T taskManager;
    protected Task task;
    protected Epic epic;
    protected Subtask subtask;

    protected void initTasks() {
        task = new Task("Task", "DescrT", TaskStatus.NEW);
        epic = new Epic("Epic1", "DescrEp1");
        // use extra: subtask = new Subtask("Subtask2", "DescrSt2", TaskStatus.NEW, epic.getId());
    }

    @Test
    protected void shouldReturnHistorySize1FromHistoryManager() { // checking method getHistoryManager()
        taskManager.createTask(task);
        taskManager.getUsualTaskById(1);
        List<Task> history = taskManager.getHistoryManager().getHistory();
        int expectedHistorySize = 1;
        int actualHistorySize = history.size();

        Assertions.assertEquals(expectedHistorySize, actualHistorySize);
    }

    @Test
    protected void shouldReturnHistorySize0FromHistoryManager() { // checking method getHistoryManager()
        List<Task> history = taskManager.getHistoryManager().getHistory();
        int expectedHistorySize = 0;
        int actualHistorySize = history.size();

        Assertions.assertEquals(expectedHistorySize, actualHistorySize);
    }

    @Test
    protected void shouldIncreaseSizeFromTasksListTo1AfterCreatingTask() { // checking method createTask(Task task)
        taskManager.createTask(task);

        int expectedSize = 1;
        int actualSize = taskManager.getTasks().size();

        Assertions.assertEquals(expectedSize, actualSize);
    }

    @Test
    protected void shouldIncreaseSizeFromTasksListTo2AfterCreatingTask() { // checking method createTask(Task task)
        taskManager.createTask(task);
        taskManager.createTask(task);

        int expectedSize = 2;
        int actualSize = taskManager.getTasks().size();

        Assertions.assertEquals(expectedSize, actualSize);
    }

    @Test
    protected void shouldIncreaseSizeFromEpicsListTo1AfterCreatingEpic() { // checking method createEpic(Epic epic)
        taskManager.createEpic(epic);

        int expectedSize = 1;
        int actualSize = taskManager.getEpics().size();

        Assertions.assertEquals(expectedSize, actualSize);
    }

    @Test
    protected void shouldIncreaseSizeFromEpicsListTo2AfterCreatingEpic() { // checking method createEpic(Epic epic)
        taskManager.createEpic(epic);
        taskManager.createEpic(epic);

        int expectedSize = 2;
        int actualSize = taskManager.getEpics().size();

        Assertions.assertEquals(expectedSize, actualSize);
    }

    @Test
    protected void shouldIncreaseSizeFromSubtasksListTo1AfterCreatingSubtask() { //checking method createSubtask(Sb sb)
        taskManager.createEpic(epic);
        subtask = new Subtask("Subtask2", "DescrSt2", TaskStatus.NEW, epic.getId());
        taskManager.createSubtask(subtask);

        int expectedSize = 1;
        int actualSize = taskManager.getSubtasks().size();

        Assertions.assertEquals(expectedSize, actualSize);
    }


    @Test
    protected void shouldIncreaseSizeFromSubtasksListTo2AfterCreatingSubtask() { //checking method createSubtask(Sb sb)
        taskManager.createEpic(epic);
        subtask = new Subtask("Subtask2", "DescrSt2", TaskStatus.NEW, epic.getId());
        taskManager.createSubtask(subtask);
        taskManager.createSubtask(subtask);

        int expectedSize = 2;
        int actualSize = taskManager.getSubtasks().size();

        Assertions.assertEquals(expectedSize, actualSize);
    }

    @Test
    protected void shouldThrowExceptionByCreatingSubtaskWithIncorrectEpicId() { //checking method createSubtask(Sb sb, int id)
        subtask = new Subtask("Subtask2", "DescrSt2",
                TaskStatus.NEW, 33);

        final IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> taskManager.createSubtask(subtask)
        );

        Assertions.assertEquals("Epic with such ID does not exist", exception.getMessage());
    }

    @Test
    protected void shouldReturnNextIdNumber2() { //checking method generateId()
        int initialIdInSystemIs = 1;
        int expectedIdNumber = initialIdInSystemIs + 1;
        taskManager.generateId(); // +1
        int actualIdNumber = taskManager.generateId(); // cause 'return nextId++' (please see method generateId())

        Assertions.assertEquals(expectedIdNumber, actualIdNumber);
    }

    @Test
    protected void shouldReturnNextIdNumber3() { //checking method generateId()
        int initialIdInSystemIs = 1;
        int expectedIdNumber = initialIdInSystemIs + 2;
        taskManager.generateId(); // +1
        taskManager.generateId(); // +1
        int actualIdNumber = taskManager.generateId(); // cause 'return nextId++' (please see method generateId())

        Assertions.assertEquals(expectedIdNumber, actualIdNumber);
    }

    @Test
    protected void shouldReturnCorrectedNameOfChosenTask() { // checking method updateUsualTask(Task task, int id)
        taskManager.createTask(task);

        String expectedName = "NewTaskText";
        Task updatedTaskData = new Task("NewTaskText", "DescrT", TaskStatus.NEW);
        taskManager.updateUsualTask(updatedTaskData, task.getId());
        String actualName = taskManager.getTasks().get(task.getId()).getName();

        Assertions.assertEquals(expectedName, actualName);
    }

    @Test
    protected void shouldReturnCorrectedDescriptionOfChosenTask() { //checking method updateUsualTask(Task task, int id)
        taskManager.createTask(task);

        String expectedDescription = "NewTaskDescrT";
        Task updatedTaskData = new Task("Task", "NewTaskDescrT", TaskStatus.NEW);
        taskManager.updateUsualTask(updatedTaskData, task.getId());
        String actualDescription = taskManager.getTasks().get(task.getId()).getDescription();

        Assertions.assertEquals(expectedDescription, actualDescription);
    }

    @Test
    protected void shouldReturnCorrectedStatusOfChosenTask() { //checking method updateUsualTask(Task task, int id)
        taskManager.createTask(task);

        TaskStatus expectedStatus = TaskStatus.DONE;
        Task updatedTaskData = new Task("Task", "DescrT", TaskStatus.DONE);
        taskManager.updateUsualTask(updatedTaskData, task.getId());
        TaskStatus actualStatus = taskManager.getTasks().get(task.getId()).getStatus();

        Assertions.assertEquals(expectedStatus, actualStatus);
    }

    @Test
    protected void shouldReturnCorrectedNameOfChosenEpic() { // checking method updateEpic(Epic epic, int id)
        taskManager.createEpic(epic);

        String expectedName = "NewEpicText";
        Epic updatedEpicData = new Epic("NewEpicText", "DescrEp");
        taskManager.updateEpic(updatedEpicData, epic.getId());
        String actualName = taskManager.getEpics().get(epic.getId()).getName();

        Assertions.assertEquals(expectedName, actualName);
    }

    @Test
    protected void shouldReturnCorrectedDescriptionOfChosenEpic() { // checking method updateEpic(Epic epic, int id)
        taskManager.createEpic(epic);

        String expectedDescription = "NewEpicDescrEp";
        Epic updatedEpicData = new Epic("Epic1", "NewEpicDescrEp");
        taskManager.updateEpic(updatedEpicData, epic.getId());
        String actualDescription = taskManager.getEpics().get(epic.getId()).getDescription();

        Assertions.assertEquals(expectedDescription, actualDescription);
    }

    @Test
    protected void shouldReturnCorrectedNameOfChosenSubtask() { //checking method updateSubtask(Subtask subtask, int id)
        taskManager.createEpic(epic);
        subtask = new Subtask("Subtask2", "DescrSt2", TaskStatus.NEW, epic.getId());
        taskManager.createSubtask(subtask);

        String expectedName = "NewSubtaskText";
        Subtask updatedSTData = new Subtask("NewSubtaskText", "DescrSt2", TaskStatus.NEW, epic.getId());
        taskManager.updateSubtask(updatedSTData, subtask.getId());
        String actualName = taskManager.getSubtasks().get(subtask.getId()).getName();

        Assertions.assertEquals(expectedName, actualName);
    }

    @Test
    protected void shouldReturnCorrectedDescriptionOfChosenSubtask() { //checking method updateSubtask(Sb sb, int id)
        taskManager.createEpic(epic);
        subtask = new Subtask("Subtask2", "DescrSt2", TaskStatus.NEW, epic.getId());
        taskManager.createSubtask(subtask);

        String expectedDescription = "NewSDescrSt2";
        Subtask updatedSTData = new Subtask("Subtask2", "NewSDescrSt2", TaskStatus.NEW, epic.getId());
        taskManager.updateSubtask(updatedSTData, subtask.getId());
        String actualDescription = taskManager.getSubtasks().get(subtask.getId()).getDescription();

        Assertions.assertEquals(expectedDescription, actualDescription);
    }

    @Test
    protected void shouldReturnCorrectedStatusOfChosenSubtask() { //checking method updateSubtask(Sb sb, int id)
        taskManager.createEpic(epic);
        subtask = new Subtask("Subtask2", "DescrSt2", TaskStatus.NEW, epic.getId());
        taskManager.createSubtask(subtask);

        TaskStatus expectedStatus = TaskStatus.DONE;
        Subtask updatedSTData = new Subtask("Subtask2", "DescrSt2", TaskStatus.DONE, epic.getId());
        taskManager.updateSubtask(updatedSTData, subtask.getId());
        TaskStatus actualStatus = taskManager.getSubtasks().get(subtask.getId()).getStatus();

        Assertions.assertEquals(expectedStatus, actualStatus);
    }

    @Test
    protected void shouldThrowExceptionByUpdatingSubtaskWithIncorrectEpicId() { //checking method updateSubtask(Sb sb, int id)
        taskManager.createEpic(epic);
        subtask = new Subtask("Subtask2", "DescrSt2", TaskStatus.NEW, epic.getId());
        taskManager.createSubtask(subtask);
        Subtask updatedSTData = new Subtask("Subtask2", "DescrSt2", TaskStatus.DONE, 33);

        final IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> taskManager.updateSubtask(updatedSTData, subtask.getId())
        );

        Assertions.assertEquals("Epic with such ID does not exist", exception.getMessage());
    }

    @Test
    protected void shouldReturnRightIdOfTask() { // checking method getUsualTaskById(int id)
        taskManager.createTask(task);

        int expectedId = 1;
        int actualId = taskManager.getTasks().get(task.getId()).getId();

        Assertions.assertEquals(expectedId, actualId);
    }

    @Test
    protected void shouldThrowExceptionByInputWrongTaskId() { // checking method getUsualTaskById(int id)
        taskManager.createTask(task); // Id = 1

        final IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> taskManager.getUsualTaskById(33)
        );

        Assertions.assertEquals("Usual Task with such ID does not exist", exception.getMessage());
    }

    @Test
    protected void shouldReturnRightIdOfEpic() { // checking method getEpicById(int id)
        taskManager.createEpic(epic);

        int expectedId = 1;
        int actualId = taskManager.getEpics().get(epic.getId()).getId();

        Assertions.assertEquals(expectedId, actualId);
    }

    @Test
    protected void shouldThrowExceptionByInputWrongEpicId() { // checking method getEpicById(int id)
        taskManager.createEpic(epic); // Id = 1

        final IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> taskManager.getEpicById(33)
        );

        Assertions.assertEquals("Epic with such ID does not exist", exception.getMessage());
    }

    @Test
    protected void shouldReturnRightIdOfSubtask() { // checking method getSubtaskById(int id)
        taskManager.createEpic(epic);
        subtask = new Subtask("Subtask2", "DescrSt2", TaskStatus.NEW, epic.getId());
        taskManager.createSubtask(subtask);

        int expectedId = 2; // 1st is Epic
        int actualId = taskManager.getSubtasks().get(subtask.getId()).getId();

        Assertions.assertEquals(expectedId, actualId);
    }

    @Test
    protected void shouldThrowExceptionByInputWrongSubtaskId() { // checking method getSubtaskById(int id)
        taskManager.createEpic(epic);
        subtask = new Subtask("Subtask2", "DescrSt2", TaskStatus.NEW, epic.getId());
        taskManager.createSubtask(subtask); // Id = 1

        final IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> taskManager.getSubtaskById(33)
        );

        Assertions.assertEquals("Subtask with such ID does not exist", exception.getMessage());
    }

    @Test
    protected void shouldReturnCreatedTaskFromTasks() { // checking method getTasks()
        taskManager.createTask(task);
        HashMap<Integer, Task> tasks = taskManager.getTasks();

        Task expectedTask = task;
        Task actualTask = tasks.get(1);

        Assertions.assertEquals(expectedTask, actualTask);
    }

    @Test
    protected void shouldReturnCreatedEpicFromEpics() { // checking method getEpics()
        taskManager.createEpic(epic);
        HashMap<Integer, Epic> epics = taskManager.getEpics();

        Task expectedEpic = epic;
        Task actualEpic = epics.get(1);

        Assertions.assertEquals(expectedEpic, actualEpic);
    }

    @Test
    protected void shouldReturnCreatedSubtaskFromSubtasks() { // checking method getSubtasks()
        taskManager.createEpic(epic);
        subtask = new Subtask("Subtask2", "DescrSt2", TaskStatus.NEW, epic.getId());
        taskManager.createSubtask(subtask);
        HashMap<Integer, Subtask> subtasks = taskManager.getSubtasks();

        Task expectedSubtask = subtask;
        Task actualSubtask = subtasks.get(2);

        Assertions.assertEquals(expectedSubtask, actualSubtask);
    }

    @Test
    protected void shouldReturnEmptyListAfterDeleting1Task() { // checking method deleteTaskById(int id)
        taskManager.createTask(task); // +1 Task
        taskManager.deleteTaskById(task.getId()); // -1 Task
        HashMap<Integer, Task> tasks = taskManager.getTasks();

        Boolean listIsEmptyExpected = true;
        Boolean listIsEmptyActual = tasks.isEmpty();

        Assertions.assertEquals(listIsEmptyExpected, listIsEmptyActual);
    }

    @Test
    protected void shouldThrowExceptionByDeletingWrongTaskId() { // checking method deleteTaskById(int id)
        taskManager.createTask(task); // Id = 1

        final IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> taskManager.deleteTaskById(33)
        );

        Assertions.assertEquals("Task with such ID does not exist", exception.getMessage());

    }

    @Test
    protected void shouldReturnEmptyListAfterDeleting1Epic() { // checking method deleteEpicById(int id)
        taskManager.createEpic(epic); // +1 Epic
        taskManager.deleteEpicById(epic.getId()); // -1 Epic
        HashMap<Integer, Epic> epics = taskManager.getEpics();

        Boolean listIsEmptyExpected = true;
        Boolean listIsEmptyActual = epics.isEmpty();

        Assertions.assertEquals(listIsEmptyExpected, listIsEmptyActual);
    }

    @Test
    protected void shouldThrowExceptionByDeletingWrongEpicId() { // checking method deleteEpicById(int id)
        taskManager.createEpic(epic); // Id = 1

        final IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> taskManager.deleteEpicById(33)
        );

        Assertions.assertEquals("Epic with such ID does not exist", exception.getMessage());

    }

    @Test
    protected void shouldReturnEmptyListAfterDeleting1Subtask() { // checking method deleteSubtaskById(int id)
        taskManager.createEpic(epic);
        subtask = new Subtask("Subtask2", "DescrSt2", TaskStatus.NEW, epic.getId());
        taskManager.createSubtask(subtask); // +1 Subtask
        taskManager.deleteSubtaskById(subtask.getId()); // -1 Subtask
        HashMap<Integer, Subtask> subtasks = taskManager.getSubtasks();

        Boolean listIsEmptyExpected = true;
        Boolean listIsEmptyActual = subtasks.isEmpty();

        Assertions.assertEquals(listIsEmptyExpected, listIsEmptyActual);
    }

    @Test
    protected void shouldThrowExceptionByDeletingWrongSubtaskId() { // checking method deleteSubtaskById(int id)
        taskManager.createEpic(epic); // Id = 1
        subtask = new Subtask("Subtask2", "DescrSt2", TaskStatus.NEW, epic.getId());
        taskManager.createSubtask(subtask); // Id = 2

        final IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> taskManager.deleteSubtaskById(33)
        );

        Assertions.assertEquals("Subtask with such ID does not exist", exception.getMessage());

    }

    @Test
    protected void shouldReturnEmptyListAfterDeletingAllTasks() { // checking method deleteAllUsualTasks()
        taskManager.createTask(task); // +1 Task
        taskManager.createTask(task); // +1 Task
        taskManager.deleteAllUsualTasks(); // -2 Tasks
        HashMap<Integer, Task> tasks = taskManager.getTasks();

        Boolean listIsEmptyExpected = true;
        Boolean listIsEmptyActual = tasks.isEmpty();

        Assertions.assertEquals(listIsEmptyExpected, listIsEmptyActual);
    }

    @Test
    protected void shouldReturnEmptyListAfterDeletingAllEpics() { // checking method deleteAllEpics()
        taskManager.createEpic(epic); // +1 Epic
        taskManager.createEpic(epic); // +1 Epic
        taskManager.deleteAllEpics(); // -2 Epics
        HashMap<Integer, Epic> epics = taskManager.getEpics();

        Boolean listIsEmptyExpected = true;
        Boolean listIsEmptyActual = epics.isEmpty();

        Assertions.assertEquals(listIsEmptyExpected, listIsEmptyActual);
    }

    @Test
    protected void shouldReturnEmptyListAfterDeletingAllSubtasks() { // checking method deleteAllSubtasks()
        taskManager.createEpic(epic);
        subtask = new Subtask("Subtask2", "DescrSt2", TaskStatus.NEW, epic.getId());
        taskManager.createSubtask(subtask); // +1 Subtask
        taskManager.createSubtask(subtask); // +1 Subtask
        taskManager.deleteAllSubtasks(); // -2 Subtasks
        HashMap<Integer, Subtask> subtasks = taskManager.getSubtasks();

        Boolean listIsEmptyExpected = true;
        Boolean listIsEmptyActual = subtasks.isEmpty();

        Assertions.assertEquals(listIsEmptyExpected, listIsEmptyActual);
    }

    @Test
    protected void shouldReturnEmptyListAfterDeletingAllTasksAllTypes() { // checking method deleteAllTasksAllTypes()
        taskManager.createTask(task); // +1 Task
        taskManager.createEpic(epic); // +1 Epic
        subtask = new Subtask("Subtask2", "DescrSt2", TaskStatus.NEW, epic.getId());
        taskManager.createSubtask(subtask); // +1 Subtask
        taskManager.deleteAllTasksAllTypes(); // -1 Task -1 Epic -1 Subtask
        HashMap<Integer, Task> tasks = taskManager.getTasks();
        HashMap<Integer, Epic> epics = taskManager.getEpics();
        HashMap<Integer, Subtask> subtasks = taskManager.getSubtasks();

        Boolean listsAreEmptyExpected = true;
        Boolean listsAreEmptyActual = tasks.isEmpty() && epics.isEmpty() && subtasks.isEmpty();

        Assertions.assertEquals(listsAreEmptyExpected, listsAreEmptyActual);
    }

}