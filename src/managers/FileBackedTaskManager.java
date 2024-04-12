package managers;

import tasks.*;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FileBackedTaskManager extends InMemoryTaskManager { // Логика сохранения в файл

    private final Path path;

    public FileBackedTaskManager(Path path) {
        this.path = path;
    }

    protected void save() { // Сохраняет текущее состояние менеджера в указанный файл
        try (FileWriter fileWriter = new FileWriter(String.valueOf(path))) {
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

        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка записи в файл", exception);
        }

    }

    protected String toString(Task task) {
        String startTimeString;
        String durationString;
        String endTimeString;
        if (task.getStartTime().isPresent()) {
            startTimeString  = task.getStartTime().get().format(Task.dateTimeFormatter);
        } else {
            startTimeString  = " ";
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

        if (task.getType().equals(TaskType.SUBTASK)) {
            Subtask subtask = (Subtask) task;
            return String.join(",", String.valueOf(task.getId()), String.valueOf(task.getType()),
                    task.getName(), String.valueOf(task.getStatus()), task.getDescription(), startTimeString,
                    durationString, endTimeString, String.valueOf(subtask.getIdEpic()));
        }
        return String.join(",", String.valueOf(task.getId()), String.valueOf(task.getType()), task.getName(),
                String.valueOf(task.getStatus()), task.getDescription(), startTimeString, durationString,
                endTimeString);
    }

    public String historyToString(HistoryManager manager) {
        List<Task> history = manager.getHistory();
        List<String> historyIds = new ArrayList<>();
        for (Task task : history) {
            historyIds.add(String.valueOf(task.getId()));
        }
        return String.join(",", historyIds);
    }

    private static void addTaskFromFile(Task task) {
        switch (task.getType()) {
            case TASK:
                tasks.put(task.getId(), task);
                break;
            case EPIC:
                epics.put(task.getId(), (Epic) task);
                break;
            case SUBTASK:
                subtasks.put(task.getId(), (Subtask) task);
        }
        if (nextId <= task.getId()) {
            nextId = task.getId() + 1;
        }
    }

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
                        System.out.println("История не записана");
                    }
                }
            }
        }
    }

    public static FileBackedTaskManager loadFromFile(Path path) { // Загрузка сохранений при старте
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
                        addTaskFromFile(task);
                    }
                }
                String historyLine = lines.get(linesCount);
                addHistoryFromFile(historyLine);
            }
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка записи в файл", exception);
        }
        return new FileBackedTaskManager(path);
    }

    public static Task fromString(String value) { // id,type,name,status,description, startTime, duration, endTime, epic
        String[] taskValues = value.split(",");
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
        }
        return null;
    }

    @Override
    public void createTask(Task task) {
        if (!isValidDateTime(task)) {
            return;
        }
        task.setId(generateId()); // создать новый ID и поменять
        tasks.put(task.getId(), task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        if (!isValidDateTime(epic)) {
            return;
        }
        epic.setId(generateId()); // создать новый ID и поменять
        epics.put(epic.getId(), epic);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) throws IllegalArgumentException {
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
        save();
    }

    @Override
    public void updateUsualTask(Task task, int id) { // Новые данные в существующий ID
        tasks.replace(id, task);
        task.setId(id);
        save();
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
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask, int id) throws IllegalArgumentException { // Новые данные в существующий ID
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
        save();
    }

    @Override
    public Task getUsualTaskById(int id) throws IllegalArgumentException {
        if (!tasks.containsKey(id)) {
            throw new IllegalArgumentException("Usual Task with such ID does not exist");
        }
        historyManager.add(tasks.get(id));
        save();
        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) throws IllegalArgumentException {
        if (!epics.containsKey(id)) {
            throw new IllegalArgumentException("Epic with such ID does not exist");
        }
        historyManager.add(epics.get(id));
        save();
        return epics.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) throws IllegalArgumentException {
        if (!subtasks.containsKey(id)) {
            throw new IllegalArgumentException("Subtask with such ID does not exist");
        }
        historyManager.add(subtasks.get(id));
        save();
        return subtasks.get(id);
    }
    public void deleteTaskById(int id) throws IllegalArgumentException {
        if (!tasks.containsKey(id)) {
            throw new IllegalArgumentException("Task with such ID does not exist");
        }
        tasks.remove(id);
        historyManager.remove(id);
        save();
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
        save();
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
        save();
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
        save();
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
        save();
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
        save();
    }

    @Override
    public void deleteAllTasksAllTypes() {
        deleteAllUsualTasks();
        deleteAllEpics();
        save();
    }

}
