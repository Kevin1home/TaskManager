public interface TaskManager {

    // Создать
    void createTask(Task task);
    void createEpic(Epic epic);
    void createSubtask(Subtask subtask);

    // Сгенерировать ID
    int generateId();

    // Напечатать
    void printEpicSubtasks(Epic epic);

    // Обновить статус у Epic
    String checkStatusEpic(Epic epic);

    // Обновить
    void updateUsualTask(Task task, int id); // Новые данные в существующий ID
    void updateEpic(Epic epic, int id); // Новые данные в существующий ID
    void updateSubtask(Subtask subtask, int id); // Новые данные в существующий ID

    // Получить задачу по ID
    Task getUsualTaskById(int id);
    Epic getEpicById(int id);
    Subtask getSubtaskById(int id);

    // Получить ID по имени
    int getUsualTaskIdByName(String taskName);
    int getEpicIdByName(String epicName);
    int getSubtaskIdByName(String subtaskName);

    // Удалить задачу по ID
    void deleteTaskById(int id);
    void deleteEpicById(int id);
    void deleteSubtaskById(int id);

    // Удалить все задачи конретного типа
    void deleteAllTasksAllTypes();
    void deleteAllUsualTasks();
    void deleteAllEpics();

    // Удалить все задачи
    void deleteAllSubtasks() ;

}