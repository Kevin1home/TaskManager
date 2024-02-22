public class Main {
    public static void main(String[] args) {
        // ТЕСТИРОВАНИЕ

        // Новый TasksManager для вызовов
        TasksManager tasksManager = new TasksManager();

        // Создать 2x Task
        Task task1 = new Task("Task1", "DescrT1", "NEW");
        tasksManager.createTask(task1);
        Task task2 = new Task("Task2", "Descr2", "NEW");
        tasksManager.createTask(task2);

        // Создать 1x Epic с 2х Subtask
        Epic epic1 = new Epic("Epic1", "DescrEp1", "NEW");
        tasksManager.createEpic(epic1);
        Subtask subtask1 = new Subtask("Subtask1", "DescrSt1", "NEW", epic1.getId());
        tasksManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Subtask2", "DescrSt2", "NEW", epic1.getId());
        tasksManager.createSubtask(subtask2);

        // Создать 1х Epic с 1х Subtask
        Epic epic2 = new Epic("Epic2", "DescrEp2", "NEW");
        tasksManager.createEpic(epic2);
        Subtask subtask3 = new Subtask("Subtask3", "DescrSt3", "NEW", epic2.getId());
        tasksManager.createSubtask(subtask3);
        Subtask subtask4 = new Subtask("Subtask4", "DescrSt4", "NEW", epic2.getId());
        tasksManager.createSubtask(subtask4);

        // Распечатать списки всех задач
        System.out.println(tasksManager);

        // Распечатать списки всех задач по типам
        System.out.println(tasksManager.getTasks());
        System.out.println(tasksManager.getEpics());
        System.out.println(tasksManager.getSubtasks());

        // Поменять обычную задачу
        Task task01 = new Task("Task01", "DescrT01", "NEW");
        tasksManager.updateUsualTask(task01, task1.getId()); // Новые данные и существующий ID

        // Изменить статусы
        // Распечатать списки всех задач


        // Удалить 1х задачу
        // Распечатать списки всех задач

    }
}