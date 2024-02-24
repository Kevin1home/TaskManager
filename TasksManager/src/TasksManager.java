import java.util.ArrayList;
import java.util.HashMap;

public class TasksManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int nextId = 1;

    public void printEpicSubtasks(Epic epic) {
        System.out.println("subtasks = " + epic.subtasks);
    }

    public void createTask(Task task) {
        task.setId(generateId()); // создать новый ID и поменять
        tasks.put(task.getId(), task);
    }

    public void createEpic(Epic epic) {
        epic.setId(generateId()); // создать новый ID и поменять
        epics.put(epic.getId(), epic);
    }

    public void createSubtask(Subtask subtask) {
        subtask.setId(generateId()); // создать новый ID и поменять
        Epic epic = epics.get(subtask.getIdEpic());
        epic.subtasks.add(subtask);
        subtasks.put(subtask.getId(), subtask);
        epic.setStatus(checkStatusEpic(epic));
    }

    public void updateUsualTask(Task task, int id) { // Новые данные в существующий ID
        tasks.remove(id);
        tasks.put(id, task);
        // tasks.replace(id, task);
        task.setId(id);
    }

    public void updateEpic(Epic epic, int id) { // Новые данные в существующий ID
        epics.replace(id, epic);
        epic.setId(id);
    }

    public void updateSubtask(Subtask subtask, int id) { // Новые данные в существующий ID
        Epic epic = epics.get(subtask.getIdEpic());
        epic.subtasks.remove(subtasks.get(id));
        epic.subtasks.add(subtask);
        subtasks.replace(id, subtask);
        subtask.setId(id);
        epic.setStatus(checkStatusEpic(epic));
    }

    public String checkStatusEpic(Epic epic) {
        if (epic.subtasks.isEmpty()) {
            return "NEW";
        }
        ArrayList<String> statuses = new ArrayList<>();
        for (Subtask subtask : epic.subtasks) {
            if (subtask.getStatus().equals("NEW")) {
                statuses.add("NEW");
            } else if (subtask.getStatus().equals("IN_PROGRESS")) {
                statuses.add("IN_PROGRESS");
            } else if (subtask.getStatus().equals("DONE")) {
                statuses.add("DONE");
            }
        }
        if (statuses.contains("NEW") && !(statuses.contains("IN_PROGRESS")) && !(statuses.contains("DONE"))) {
            return "NEW";
        }
        else if (!(statuses.contains("NEW")) && !(statuses.contains("IN_PROGRESS")) && statuses.contains("DONE")) {
            return "DONE";
        }
        else {
            return "IN_PROGRESS";
        }
    }

    public Task getUsualTaskById(int id) {
        return tasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public int getEpicIdByName(String epicName) {
        for (Epic epic : epics.values()) {
            if (epic.getName().equals(epicName)) {
                return epic.getId();
            }
        }
        return 0;
    }

    public int getUsualTaskIdByName(String taskName) {
        for (Task task : tasks.values()) {
            if (task.getName().equals(taskName)) {
                return task.getId();
            }
        }
        return 0;
    }

    public int getSubtaskIdByName(String subtaskName) {
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getName().equals(subtaskName)) {
                return subtask.getId();
            }
        }
        return 0;
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    public void deleteEpicById(int id) {
        ArrayList<Integer> subtasksIdsToDelete = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            if (subtask.idEpic == id) {
                subtasksIdsToDelete.add(subtask.getId());
            }
        }
        for (Integer subtaskIdToDelete : subtasksIdsToDelete) {
            subtasks.remove(subtaskIdToDelete);
        }
        epics.get(id).subtasks.clear();
        epics.remove(id);
    }

    public void deleteSubtaskById(int id) {
        Epic epic = epics.get(subtasks.get(id).getIdEpic());
        epic.subtasks.remove(subtasks.get(id));
        subtasks.remove(id);
        epic.setStatus(checkStatusEpic(epic));
    }

    public void deleteAllTasksAllTypes() {
        deleteAllUsualTasks();
        deleteAllEpics();
    }

    public void deleteAllUsualTasks() {
        tasks.clear();
    }

    public void deleteAllEpics() {
        epics.clear();
        deleteAllSubtasks();
    }

    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.subtasks.clear();
            epic.setStatus(checkStatusEpic(epic));
        }
    }

    public int generateId () {
        return nextId++;
    }

    @Override
    public String toString() {
        return "AllTasks{" + "\n" +
                "tasks=" + tasks +
                ", " + "\n" + "epics=" + epics +
                ", " + "\n" + "subtasks=" + subtasks +
                '}';
    }

    public void setTasks(HashMap<Integer, Task> tasks) {
        this.tasks = tasks;
    }

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public void setEpics(HashMap<Integer, Epic> epics) {
        this.epics = epics;
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(HashMap<Integer, Subtask> subtasks) {
        this.subtasks = subtasks;
    }
}
