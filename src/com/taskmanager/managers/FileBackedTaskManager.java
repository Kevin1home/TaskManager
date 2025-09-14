package com.taskmanager.managers;

import com.taskmanager.tasks.*;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * FileBackedTaskManager extends InMemoryTaskManager and adds persistence to a CSV file if available.
 * <p>
 * All tasks, epics, subtasks, and their history are automatically saved to the file on each modification.
 * </p>
 */
public class FileBackedTaskManager extends InMemoryTaskManager {

    private static final Logger LOGGER = Logger.getLogger(FileBackedTaskManager.class.getName());
    private final Path path;


    /** Constructor with file path for persistence */
    public FileBackedTaskManager(Path path) {
        this.path = path;
    }

    /** Constructor without file persistence */
    public FileBackedTaskManager() {
        this.path = null;
    }

    /**
     * Saves current state of tasks, epics, subtasks and history to the file.
     * Throws ManagerSaveException in case of failure.
     */
    protected void save() {

        if (path == null) {
            LOGGER.warning("No path available. Skipping save-to-file operation");
            return;
        }

        try (FileWriter fileWriter = new FileWriter(String.valueOf(path))) {
            LOGGER.info("Saving tasks to file: " + path);

            String firstLine = String.join(",", "id", "type", "name", "status",
                    "description", "startTime", "duration", "endTime", "epic\n");
            fileWriter.write(firstLine);

            if (!tasks.isEmpty()) {
                for (Task task : tasks.values()) {
                    fileWriter.write(toString(task));
                    fileWriter.write("\n");
                }
            }

            if (!subtasks.isEmpty()) {
                for (Subtask subtask : subtasks.values()) {
                    fileWriter.write(toString(subtask));
                    fileWriter.write("\n");
                }
            }

            if (!epics.isEmpty()) {
                for (Epic epic : epics.values()) {
                    fileWriter.write(toString(epic));
                    fileWriter.write("\n");
                }
            }

            fileWriter.write("\n");

            if (!historyManager.getHistory().isEmpty()) {
                fileWriter.write(historyToString(historyManager));
            } else {
                fileWriter.write("\n");
            }

            LOGGER.info("Tasks successfully saved to file.");

        } catch (IOException exception) {
            LOGGER.log(Level.SEVERE, "Failed to save tasks to file: " + path, exception);
            throw new ManagerSaveException("Error writing to file", exception);
        }
    }

    /** Adds a task read from file to memory */
    private static void addTaskFromFile(Task task) {
        LOGGER.info("Adding task from file: " + task);

        switch (task.getType()) {
            case "TASK":
                tasks.put(task.getId(), task);
                break;
            case "EPIC":
                epics.put(task.getId(), (Epic) task);
                break;
            case "SUBTASK":
                subtasks.put(task.getId(), (Subtask) task);
            default:
                LOGGER.warning("Unknown task type while loading: " + task.getType());
        }

        if (nextId <= task.getId()) {
            nextId = task.getId() + 1;
        }
    }

    /** Rebuilds history from CSV line of IDs */
    private static void addHistoryFromFile(String historyLine) {

        if (!historyLine.isEmpty()) {
            String[] historyIds = historyLine.split(",");

            for (int i = historyIds.length - 1; i >= 0; i--) {
                if (!historyIds[i].equals("null")) {

                    int historyId = Integer.parseInt(historyIds[i]);

                    if (tasks.containsKey(historyId)) {
                        historyManager.add(tasks.get(historyId));

                    } else if (epics.containsKey(historyId)) {
                        historyManager.add(epics.get(historyId));

                    } else if (subtasks.containsKey(historyId)) {
                        historyManager.add(subtasks.get(historyId));

                    } else {
                        LOGGER.warning("History ID " + historyId + " not found.");
                    }
                }
            }
        }
    }

    /**
     * Loads tasks and history from a CSV file.
     * @param path Path to CSV file
     * @return FileBackedTaskManager instance
     */
    public static FileBackedTaskManager loadFromFile(Path path) {
        LOGGER.info("Loading tasks from file: " + path);
        try {
            if (Files.size(path) != 0) {
                List<String> lines = Files.readAllLines(path);

                int linesCount = 0;
                for (String line : lines) {
                    linesCount++;
                    if (!line.startsWith("id")) {
                        if (line.isEmpty()) {
                            break;
                        }
                        Task task = fromString(line);

                        if (task != null) {
                            addTaskFromFile(task);
                        }
                    }
                }
                String historyLine = lines.get(linesCount);
                addHistoryFromFile(historyLine);
            }

        } catch (IOException exception) {
            LOGGER.log(Level.SEVERE, "Failed to load tasks from file: " + path, exception);
            throw new ManagerSaveException("Failed to load tasks from file", exception);
        }
        return new FileBackedTaskManager(path);
    }

    //--------------------------
    // Overrides for persistence
    //--------------------------

    @Override
    public void createTask(Task task) {
        if (!isValidDateTime(task)) {
            LOGGER.warning("Task has invalid datetime: " + task);
            return;
        }
        task.setId(generateId());
        tasks.put(task.getId(), task);
        LOGGER.info("Created task: " + task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        if (!isValidDateTime(epic)) {
            LOGGER.warning("Epic has invalid datetime: " + epic);
            return;
        }
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        LOGGER.info("Created epic: " + epic);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) throws IllegalArgumentException {
        if (!isValidDateTime(subtask)) {
            LOGGER.warning("Subtask has invalid datetime: " + subtask);
            return;
        }
        if (!epics.containsKey(subtask.getIdEpic())) {
            LOGGER.severe("Epic with ID=" + subtask.getIdEpic() + " not found. Cannot create subtask.");
            throw new IllegalArgumentException("Epic with such ID not exist.");
        }

        subtask.setId(generateId());
        Epic epic = epics.get(subtask.getIdEpic());
        epic.subtasks.add(subtask);
        subtasks.put(subtask.getId(), subtask);

        checkStatusEpic(epic);
        epic.checkDataTimeDurationEpic();
        LOGGER.info("Created subtask: " + subtask);
        save();
    }

    @Override
    public void updateUsualTask(Task task, int id) throws IllegalArgumentException { // Новые данные в существующий ID
        if (!tasks.containsKey(id)) {
            LOGGER.warning("Task with ID=" + id + " not found for update.");
            throw new IllegalArgumentException("Usual Task with such ID not exist.");
        }

        tasks.replace(id, task);
        task.setId(id);
        LOGGER.info("Updated task with ID=" + id + ": " + task);
        save();
    }

    @Override
    public void updateEpic(Epic epic, int id) throws IllegalArgumentException { // Новые данные в существующий ID
        if (!epics.containsKey(id)) {
            LOGGER.warning("Epic with ID=" + id + " not found for update.");
            throw new IllegalArgumentException("Epic with such ID not exist.");
        }

        ArrayList<Subtask> savedSubtasks = getEpicById(id).subtasks;
        epics.replace(id, epic);
        epic.setId(id);

        for (Subtask subtask : savedSubtasks) {
            getEpicById(id).subtasks.add(subtask);
        }

        checkStatusEpic(epic);
        epic.checkDataTimeDurationEpic();
        LOGGER.info("Updated epic with ID=" + id + ": " + epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask, int id) throws IllegalArgumentException {//Новые данные в существующий ID
        if (!subtasks.containsKey(id)) {
            LOGGER.warning("Subtask with ID=" + id + " not found for update.");
            throw new IllegalArgumentException("Subtask with such ID not exist.");
        }
        if (!epics.containsKey(subtask.getIdEpic())) {
            LOGGER.warning("Epic with ID=" + id + " not found.");
            throw new IllegalArgumentException("Epic with such ID not exist.");
        }

        Epic epic = epics.get(subtask.getIdEpic());
        epic.subtasks.remove(subtasks.get(id));
        epic.subtasks.add(subtask);
        subtasks.replace(id, subtask);
        subtask.setId(id);

        checkStatusEpic(epic);
        epic.checkDataTimeDurationEpic();
        LOGGER.info("Updated subtask with ID=" + id + ": " + subtask);
        save();
    }

    @Override
    public Task getUsualTaskById(int id) throws IllegalArgumentException {
        if (!tasks.containsKey(id)) {
            LOGGER.warning("Usual task with ID=" + id + " not found.");
            throw new IllegalArgumentException("Usual Task with such ID not exist.");
        }

        historyManager.add(tasks.get(id));
        save();
        LOGGER.info("Received task with ID=" + id + ": " + tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) throws IllegalArgumentException {
        if (!epics.containsKey(id)) {
            LOGGER.warning("Epic with ID=" + id + " not found.");
            throw new IllegalArgumentException("Epic with such ID not exist.");
        }

        historyManager.add(epics.get(id));
        save();
        LOGGER.info("Received epic with ID=" + id + ": " + epics.get(id));
        return epics.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) throws IllegalArgumentException {
        if (!subtasks.containsKey(id)) {
            LOGGER.warning("Subtask with ID=" + id + " not found.");
            throw new IllegalArgumentException("Subtask with such ID not exist.");
        }

        historyManager.add(subtasks.get(id));
        save();
        LOGGER.info("Received subtask with ID=" + id + ": " + subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public void deleteTaskById(int id) throws IllegalArgumentException {
        if (!tasks.containsKey(id)) {
            LOGGER.warning("Usual task with ID=" + id + " not found.");
            throw new IllegalArgumentException("Task with such ID not exist.");
        }

        tasks.remove(id);
        historyManager.remove(id);
        LOGGER.info("Usual task with ID=" + id + " deleted.");
        save();
    }

    @Override
    public void deleteEpicById(int id) throws IllegalArgumentException {
        if (!epics.containsKey(id)) {
            LOGGER.warning("Epic with ID=" + id + " not found.");
            throw new IllegalArgumentException("Epic with such ID not exist.");
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
        LOGGER.info("Epic with ID=" + id + " deleted.");
        save();
    }

    @Override
    public void deleteSubtaskById(int id) throws IllegalArgumentException {
        if (!subtasks.containsKey(id)) {
            LOGGER.warning("Subtask with ID=" + id + " not found.");
            throw new IllegalArgumentException("Subtask with such ID not exist.");
        }

        Epic epic = epics.get(subtasks.get(id).getIdEpic());
        epic.subtasks.remove(subtasks.get(id));
        subtasks.remove(id);
        historyManager.remove(id);

        checkStatusEpic(epic);
        epic.checkDataTimeDurationEpic();
        LOGGER.info("Subtask with ID=" + id + " deleted.");
        save();
    }

    @Override
    public void deleteAllUsualTasks() {
        if (tasks.isEmpty()) {
            LOGGER.warning("List of usual tasks is already empty.");
            return;
        }
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
        }
        tasks.clear();
        LOGGER.info("All usual tasks deleted.");
        save();
    }

    @Override
    public void deleteAllEpics() {
        if (epics.isEmpty()) {
            LOGGER.warning("List of epics is already empty.");
        }
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
        }
        epics.clear();
        LOGGER.info("All epics deleted.");
        deleteAllSubtasks();
        LOGGER.info("All subtasks deleted.");
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        if (subtasks.isEmpty()) {
            LOGGER.warning("List of subtasks is already empty.");
        }
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
        }
        subtasks.clear();
        LOGGER.info("All subtasks deleted.");

        for (Epic epic : epics.values()) {
            epic.subtasks.clear();
            checkStatusEpic(epic);
            epic.checkDataTimeDurationEpic();
        }
        save();
    }

    @Override
    public void deleteAllTasksAllTypes() {
        deleteAllUsualTasks();
        deleteAllEpics();
        save();
    }

    //------------------------
    // Private helpers methods
    //------------------------

    /** Converts history into CSV string of task IDs */
    private String historyToString(HistoryManager manager) {

        List<Task> history = manager.getHistory();
        List<String> historyIds = new ArrayList<>();

        for (Task task : history) {
            historyIds.add(String.valueOf(task.getId()));
        }
        return String.join(",", historyIds);
    }

    /** Converts a CSV line to Task/Epic/Subtask object */
    private static Task fromString(String value) { // id,type,name,status,description, startTime, duration, endTime, epic

        String[] taskValues = value.split(",");
        LOGGER.fine("Parsing task from line: " + value);

        switch (taskValues[1]) {
            case "TASK":
                Task task = new Task(taskValues[2], taskValues[4], TaskStatus.valueOf(taskValues[3]));

                if (!taskValues[5].isBlank()) {
                    task.setStartTime(taskValues[5]);
                }
                if (!taskValues[6].isBlank()) {
                    task.setDuration(Long.parseLong(taskValues[6]));
                }
                task.setId(Integer.parseInt(taskValues[0]));
                return task;

            case "SUBTASK":
                Task subtask = new Subtask(taskValues[2], taskValues[4], TaskStatus.valueOf(taskValues[3])
                        , Integer.parseInt(taskValues[8]));

                if (!taskValues[5].isBlank()) {
                    subtask.setStartTime(taskValues[5]);
                }
                if (!taskValues[6].isBlank()) {
                    subtask.setDuration(Long.parseLong(taskValues[6]));
                }
                subtask.setId(Integer.parseInt(taskValues[0]));
                return subtask;

            case "EPIC":
                Epic epic = new Epic(taskValues[2], taskValues[4], TaskStatus.valueOf(taskValues[3]));
                epic.setId(Integer.parseInt(taskValues[0]));

                if (!subtasks.isEmpty()) {

                    List<Subtask> subtasksEpic = subtasks.values().stream()
                            .filter(st -> epic.getId() == st.getIdEpic())
                            .collect(Collectors.toList());

                    epic.subtasks.addAll(subtasksEpic);
                    epic.checkDataTimeDurationEpic();
                }
                return epic;
            default:
                LOGGER.warning("Unknown task type in line: " + value);
                return null;
        }
    }

    /** Converts a Task object to CSV string */
    private String toString(Task task) {

        String startTimeString;
        String durationString;
        String endTimeString;

        if (task.getStartTime().isPresent()) {
            startTimeString = task.getStartTime().get().format(Task.dateTimeFormatter);
        } else {
            startTimeString = " ";
        }

        if (task.getDuration().isPresent()) {
            durationString = String.valueOf(task.getDuration().get().toMinutes());
        } else {
            durationString = " ";
        }

        if (task.getEndTime().isPresent()) {
            endTimeString = task.getEndTime().get().format(Task.dateTimeFormatter);
        } else {
            endTimeString = " ";
        }

        if (task.getType().equals("SUBTASK")) {
            Subtask subtask = (Subtask) task;
            return String.join(",", String.valueOf(task.getId()), String.valueOf(task.getType()),
                    task.getName(), String.valueOf(task.getStatus()), task.getDescription(), startTimeString,
                    durationString, endTimeString, String.valueOf(subtask.getIdEpic()));
        }

        return String.join(",", String.valueOf(task.getId()), String.valueOf(task.getType()), task.getName(),
                String.valueOf(task.getStatus()), task.getDescription(), startTimeString, durationString,
                endTimeString);
    }
}
