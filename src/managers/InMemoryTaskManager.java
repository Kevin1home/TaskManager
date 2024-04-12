package managers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import main_manager.Managers;
import tasks.*;

public class InMemoryTaskManager implements TaskManager {
    protected static final HashMap<Integer, Task> tasks = new HashMap<>();
    protected static final HashMap<Integer, Epic> epics = new HashMap<>();
    protected static final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected static int nextId = 1;
    protected static HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public void createTask(Task task) {
        if (!isValidDateTime(task)) {
            return;
        }
        task.setId(generateId()); // создать новый ID и поменять
        tasks.put(task.getId(), task);
    }

    @Override
    public void createEpic(Epic epic) {
        if (!isValidDateTime(epic)) {
            return;
        }
        epic.setId(generateId()); // создать новый ID и поменять
        epics.put(epic.getId(), epic);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        if (!isValidDateTime(subtask)) {
            return;
        }
        if (!epics.containsKey(subtask.getIdEpic())) {
            throw new IllegalArgumentException("Epic with such ID does not exist");
        }
        subtask.setId(generateId()); // создать новый ID и поменять
        Epic epic = epics.get(subtask.getIdEpic());
        epic.subtasks.add(subtask);
        subtasks.put(subtask.getId(), subtask);
        checkStatusEpic(epic);
        epic.checkDataTimeDurationEpic();
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
        if (statuses.contains(TaskStatus.NEW) && !(statuses.contains(TaskStatus.IN_PROGRESS))
                && !(statuses.contains(TaskStatus.DONE))) {
            epic.setStatus(TaskStatus.NEW);
        } else if (!(statuses.contains(TaskStatus.NEW)) && !(statuses.contains(TaskStatus.IN_PROGRESS))
                && statuses.contains(TaskStatus.DONE)) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    @Override
    public void updateUsualTask(Task task, int id) { // Новые данные в существующий ID
        if (!tasks.containsKey(id)) {
            throw new IllegalArgumentException("Usual Task with such ID does not exist");
        }
        tasks.replace(id, task);
        task.setId(id);
    }

    @Override
    public void updateEpic(Epic epic, int id) { // Новые данные в существующий ID
        ArrayList<Subtask> savedSubtasks = getEpicById(id).subtasks;
        epics.replace(id, epic);
        epic.setId(id);
        for (Subtask subtask : savedSubtasks) {
            getEpicById(id).subtasks.add(subtask);
        }
        checkStatusEpic(epic);
        epic.checkDataTimeDurationEpic();
    }

    @Override
    public void updateSubtask(Subtask subtask, int id) throws IllegalArgumentException {//Новые данные в существующий ID
        if (!epics.containsKey(subtask.getIdEpic())) {
            throw new IllegalArgumentException("Epic with such ID does not exist");
        }
        Epic epic = epics.get(subtask.getIdEpic());
        epic.subtasks.remove(subtasks.get(id));
        epic.subtasks.add(subtask);
        subtasks.replace(id, subtask);
        subtask.setId(id);
        checkStatusEpic(epic);
        epic.checkDataTimeDurationEpic();
    }

    @Override
    public Task getUsualTaskById(int id) throws IllegalArgumentException {
        if (!tasks.containsKey(id)) {
            throw new IllegalArgumentException("Usual Task with such ID does not exist");
        }
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) throws IllegalArgumentException {
        if (!epics.containsKey(id)) {
            throw new IllegalArgumentException("Epic with such ID does not exist");
        }
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) throws IllegalArgumentException {
        if (!subtasks.containsKey(id)) {
            throw new IllegalArgumentException("Subtask with such ID does not exist");
        }
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
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
    public TreeSet<Task> getPrioritizedTasks() {
        TreeSet<Task> prioritizedTasks = new TreeSet<>( (t1, t2) -> {
            if (t1.getStartTime().isPresent() && t2.getStartTime().isPresent()) {
                if (t1.getStartTime().get().getYear() == t2.getStartTime().get().getYear()) {
                    if (t1.getStartTime().get().getMonthValue() == t2.getStartTime().get().getMonthValue()) {
                        if (t1.getStartTime().get().getDayOfMonth() == t2.getStartTime().get().getDayOfMonth()) {
                            if (t1.getStartTime().get().getHour() == t2.getStartTime().get().getHour()) {
                                if (t1.getStartTime().get().getMinute() == t2.getStartTime().get().getMinute()) {
                                    return -1;
                                }
                                return t1.getStartTime().get().getMinute() - t2.getStartTime().get().getMinute();
                            }
                            return t1.getStartTime().get().getHour() - t2.getStartTime().get().getHour();
                        }
                        return t1.getStartTime().get().getDayOfMonth() - t2.getStartTime().get().getDayOfMonth();
                    }
                    return t1.getStartTime().get().getMonthValue() - t2.getStartTime().get().getMonthValue();
                }
                return t1.getStartTime().get().getYear() - t2.getStartTime().get().getYear();
            } else {
                return -1;
            }
        });
        prioritizedTasks.addAll(tasks.values());
        prioritizedTasks.addAll(subtasks.values());
        return prioritizedTasks;
    }

    @Override
    public boolean isValidDateTime(Task provedTask) {
        if (provedTask.getStartTime().isPresent() && provedTask.getDuration().isPresent()
                && provedTask.getEndTime().isPresent()) {
            LocalDateTime provedTaskStartTime = provedTask.getStartTime().get();
            LocalDateTime provedTaskEndTime = provedTask.getEndTime().get();
            Duration provedTaskDuration = provedTask.getDuration().get();

            if (!((provedTaskStartTime.isBefore(LocalDateTime.MIN)
                    && provedTaskStartTime.isAfter(LocalDateTime.MAX) && provedTaskEndTime.isBefore(LocalDateTime.MIN)
                    && provedTaskEndTime.isAfter(LocalDateTime.MAX)) || provedTaskDuration.isNegative())) {

                TreeSet<Task> prioritizedTasks = getPrioritizedTasks();

                for (Task task : prioritizedTasks) {

                    if (task.getStartTime().isPresent() && task.getDuration().isPresent()
                            && task.getEndTime().isPresent()) {
                        LocalDateTime taskStartTime = task.getStartTime().get();
                        LocalDateTime taskEndTime = task.getEndTime().get();

                        if (!((provedTaskStartTime.isBefore(taskStartTime)
                                && provedTaskEndTime.isBefore(taskStartTime))
                                    || (provedTaskStartTime.isAfter(taskEndTime)
                                        && provedTaskEndTime.isAfter(taskEndTime)))) {
                            return false;
                        }
                    }
                }
                return true;
            }
            return false;
        }
        return true;
    }

    @Override
    public void deleteTaskById(int id) throws IllegalArgumentException {
        if (!tasks.containsKey(id)) {
          throw new IllegalArgumentException("Task with such ID does not exist");
        }
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpicById(int id) throws IllegalArgumentException {
        if (!epics.containsKey(id)) {
            throw new IllegalArgumentException("Epic with such ID does not exist");
        }
        List<Integer> subtasksIdsToDelete = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getIdEpic() == id) {
                subtasksIdsToDelete.add(subtask.getId());
            }
        }
        for (Integer subtaskIdToDelete : subtasksIdsToDelete) {
            subtasks.remove(subtaskIdToDelete);
            historyManager.remove(subtaskIdToDelete);
        }
        epics.get(id).subtasks.clear();
        epics.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteSubtaskById(int id) throws IllegalArgumentException {
        if (!subtasks.containsKey(id)) {
            throw new IllegalArgumentException("Subtask with such ID does not exist");
        }
        Epic epic = epics.get(subtasks.get(id).getIdEpic());
        epic.subtasks.remove(subtasks.get(id));
        subtasks.remove(id);
        historyManager.remove(id);
        checkStatusEpic(epic);
        epic.checkDataTimeDurationEpic();
    }

    @Override
    public void deleteAllUsualTasks() {
        if (tasks.isEmpty()) {
            System.out.println("List of Tasks is already empty");
        }
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
        }
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        if (epics.isEmpty()) {
            System.out.println("List of Epics is already empty");
        }
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
        }
        epics.clear();
        deleteAllSubtasks();
    }

    @Override
    public void deleteAllSubtasks() {
        if (subtasks.isEmpty()) {
            System.out.println("List of Subtasks is already empty");
        }
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.subtasks.clear();
            checkStatusEpic(epic);
            epic.checkDataTimeDurationEpic();
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
    @Override
    public void setNextId(int nextId) {
        InMemoryTaskManager.nextId = nextId;
    }

    @Override
    public void setHistoryManager(HistoryManager historyManager) {
        InMemoryTaskManager.historyManager = historyManager;
    }
}