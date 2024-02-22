import java.util.HashMap;

public class TasksManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int nextId = 1;
    public TasksManager() {
    }

    public void createTask(Task task) {
        task.setId(generateId(task.getId())); // получить ID, создать новый и поменять на новый
        HashMap<Integer, Task> tasks = getTasks();
        tasks.put(task.getId(), task); // добавить в setTasks новую задачу с её ID
        setTasks(tasks);
    }

    public void createEpic(Epic epic) {
        epic.setId(generateId(epic.getId())); // получить ID, создать новый и поменять на новый
        HashMap<Integer, Epic> epics = getEpics();
        epics.put(epic.getId(), epic); // добавить в setTasks новую задачу с её ID
        setEpics(epics);
    }

    public void createSubtask(Subtask subtask) {
        subtask.setId(generateId(subtask.getId())); // получить ID, создать новый и поменять на новый
        subtask.setIdEpic(generateId(subtask.getIdEpic())); // получить idEpic, скорректировать и поменять на новый
        HashMap<Integer, Subtask> subtasks = getSubtasks();
        subtasks.put(subtask.getId(), subtask); // добавить в setTasks новую задачу с её ID
        setSubtasks(subtasks);
    }

    public int generateId (int id) {
        id = nextId++;
        return id;
    }

    @Override
    public String toString() {
        return "TasksManager{" + "\n" +
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
