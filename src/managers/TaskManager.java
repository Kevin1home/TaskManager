package managers;

import java.util.HashMap;
import java.util.TreeSet;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

public interface TaskManager {

    // Получить объект класса с интерфесом HistoryManager
    HistoryManager getHistoryManager();

    // Создать
    void createTask(Task task);
    void createEpic(Epic epic);
    void createSubtask(Subtask subtask);

    // Сгенерировать ID
    int generateId();

    // Напечатать подзадачи Епика
    void printEpicSubtasks(Epic epic);

    // Обновить статус у tasks.Epic
    void checkStatusEpic(Epic epic);

    // Обновить
    void updateUsualTask(Task task, int id); // Новые данные в существующий ID
    void updateEpic(Epic epic, int id); // Новые данные в существующий ID
    void updateSubtask(Subtask subtask, int id); // Новые данные в существующий ID

    // Получить задачу по ID
    Task getUsualTaskById(int id);
    Epic getEpicById(int id);
    Subtask getSubtaskById(int id);

    // Получить списки задач по типу
    HashMap<Integer, Task> getTasks();
    HashMap<Integer, Epic> getEpics();
    HashMap<Integer, Subtask> getSubtasks();

    // Получить список задач по приоритету
    TreeSet<Task> getPrioritizedTasks();
    // Проверка на приоритетность
    boolean isValidDateTime(Task task);

    // Удалить задачу по ID
    void deleteTaskById(int id);
    void deleteEpicById(int id);
    void deleteSubtaskById(int id);

    // Удалить все задачи конретного типа
    void deleteAllUsualTasks();
    void deleteAllEpics();
    void deleteAllSubtasks();

    // Удалить все задачи
    void deleteAllTasksAllTypes();

    // Setters
    void setNextId(int nextId);
    void setHistoryManager(HistoryManager historyManager);

}