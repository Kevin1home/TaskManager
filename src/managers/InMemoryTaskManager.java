package managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import tasks.*;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int nextId = 1;
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public void createTask(Task task) {
        task.setId(generateId()); // создать новый ID и поменять
        tasks.put(task.getId(), task);
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(generateId()); // создать новый ID и поменять
        epics.put(epic.getId(), epic);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        subtask.setId(generateId()); // создать новый ID и поменять
        Epic epic = epics.get(subtask.getIdEpic());
        epic.subtasks.add(subtask);
        subtasks.put(subtask.getId(), subtask);
        checkStatusEpic(epic);
    }

    @Override
    public int generateId() {
        return nextId++;
    }

    @Override
    public void printEpicSubtasks(Epic epic) {
        System.out.println("subtasks = " + epic.subtasks);
    }

    @Override
    public void checkStatusEpic(Epic epic) {
        if (epic.subtasks.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
        }
        List<TaskStatus> statuses = new ArrayList<>();
        for (Subtask subtask : epic.subtasks) {
            if (subtask.getStatus().equals(TaskStatus.NEW)) {
                statuses.add(TaskStatus.NEW);
            } else if (subtask.getStatus().equals(TaskStatus.IN_PROGRESS)) {
                statuses.add(TaskStatus.IN_PROGRESS);
            } else if (subtask.getStatus().equals(TaskStatus.DONE)) {
                statuses.add(TaskStatus.DONE);
            }
        }
        if (statuses.contains(TaskStatus.NEW) && !(statuses.contains(TaskStatus.IN_PROGRESS)) && !(statuses.contains(TaskStatus.DONE))) {
            epic.setStatus(TaskStatus.NEW);
        } else if (!(statuses.contains(TaskStatus.NEW)) && !(statuses.contains(TaskStatus.IN_PROGRESS)) && statuses.contains(TaskStatus.DONE)) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    @Override
    public void updateUsualTask(Task task, int id) { // Новые данные в существующий ID
        tasks.replace(id, task);
        task.setId(id);
    }

    @Override
    public void updateEpic(Epic epic, int id) { // Новые данные в существующий ID
        epics.replace(id, epic);
        epic.setId(id);
    }

    @Override
    public void updateSubtask(Subtask subtask, int id) { // Новые данные в существующий ID
        Epic epic = epics.get(subtask.getIdEpic());
        epic.subtasks.remove(subtasks.get(id));
        epic.subtasks.add(subtask);
        subtasks.replace(id, subtask);
        subtask.setId(id);
        checkStatusEpic(epic);
    }

    @Override
    public Task getUsualTaskById(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public int getUsualTaskIdByName(String taskName) {
        for (Task task : tasks.values()) {
            if (task.getName().equals(taskName)) {
                return task.getId();
            }
        }
        return 0;
    }

    @Override
    public int getEpicIdByName(String epicName) {
        for (Epic epic : epics.values()) {
            if (epic.getName().equals(epicName)) {
                return epic.getId();
            }
        }
        return 0;
    }

    @Override
    public int getSubtaskIdByName(String subtaskName) {
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getName().equals(subtaskName)) {
                return subtask.getId();
            }
        }
        return 0;
    }

    @Override
    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    @Override
    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    @Override
    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        List<Integer> subtasksIdsToDelete = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getIdEpic() == id) {
                subtasksIdsToDelete.add(subtask.getId());
            }
        }
        for (Integer subtaskIdToDelete : subtasksIdsToDelete) {
            subtasks.remove(subtaskIdToDelete);
        }
        epics.get(id).subtasks.clear();
        epics.remove(id);
    }

    @Override
    public void deleteSubtaskById(int id) {
        Epic epic = epics.get(subtasks.get(id).getIdEpic());
        epic.subtasks.remove(subtasks.get(id));
        subtasks.remove(id);
        checkStatusEpic(epic);
    }

    @Override
    public void deleteAllUsualTasks() {
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        deleteAllSubtasks();
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.subtasks.clear();
            checkStatusEpic(epic);
        }
    }

    @Override
    public void deleteAllTasksAllTypes() {
        deleteAllUsualTasks();
        deleteAllEpics();
    }

    @Override
    public String toString() {
        return "AllTasks{" + "\n" +
                "tasks=" + tasks +
                ", " + "\n" + "epics=" + epics +
                ", " + "\n" + "subtasks=" + subtasks +
                '}';
    }

}