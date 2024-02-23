public class Main {
    public static void main(String[] args) {
        // ТЕСТИРОВАНИЕ

        // Новый TasksManager для вызовов и распечатать списки всех задач
        TasksManager tasksManager = new TasksManager();
        System.out.println("Список задач пуст");
        System.out.println(tasksManager);

        // Создать 2x Task и распечатать списки всех задач
        System.out.println("\nСоздать 2x Task");
        Task task1 = new Task("Task1", "DescrT1", "NEW");
        tasksManager.createTask(task1);
        Task task2 = new Task("Task2", "Descr2", "NEW");
        tasksManager.createTask(task2);
        System.out.println(tasksManager);

        // Создать 1x Epic с 2х Subtask и распечатать списки всех задач
        System.out.println("\nСоздать 1x Epic с 2х Subtask");
        Epic epic1 = new Epic("Epic1", "DescrEp1", "NEW");
        tasksManager.createEpic(epic1);
        Subtask subtask1 = new Subtask("Subtask1", "DescrSt1", "NEW", epic1.getId());
        tasksManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Subtask2", "DescrSt2", "NEW", epic1.getId());
        tasksManager.createSubtask(subtask2);
        System.out.println(tasksManager);

        // Создать 1х Epic с 1х Subtask и распечатать списки всех задач
        System.out.println("\nСоздать 1x Epic с 1х Subtask");
        Epic epic2 = new Epic("Epic2", "DescrEp2", "NEW");
        tasksManager.createEpic(epic2);
        Subtask subtask3 = new Subtask("Subtask3", "DescrSt3", "NEW", epic2.getId());
        tasksManager.createSubtask(subtask3);
        System.out.println(tasksManager);

        // Распечатать списки всех задач по типам
        System.out.println("\nРаспечатать списки всех задач по типам");
        System.out.println("Обычные задачи");
        System.out.println(tasksManager.getTasks());
        System.out.println("Эпики");
        System.out.println(tasksManager.getEpics());
        System.out.println("Подзадачи");
        System.out.println(tasksManager.getSubtasks());

        // Получение списка всех подзадач определённого эпика
        System.out.println("\nПолучение списка всех подзадач определённого эпика");
        tasksManager.printEpicSubtasks(epic2);

        // Поменять обычную задачу
        System.out.println("\nПоменять обычную задачу");
        System.out.println("Было");
        System.out.println(tasksManager.getTasks());
        Task task01 = new Task("Task01", "DescrT01", "NEW");
        tasksManager.updateUsualTask(task01, task1.getId()); // Новые данные и существующий ID
        System.out.println("Стало");
        System.out.println(tasksManager.getTasks());

        // Поменять Epic
        System.out.println("\nПоменять Epic");
        System.out.println("Было");
        System.out.println(tasksManager.getEpics());
        Epic epic02 = new Epic("Epic02", "DescrEp02");
        tasksManager.updateEpic(epic02, epic2.getId()); // Новые данные и существующий ID
        System.out.println("Стало");
        System.out.println(tasksManager.getEpics());

        // Поменять Subtask
        System.out.println("\nПоменять Subtask");
        System.out.println("Было");
        System.out.println("Эпики");
        System.out.println(tasksManager.getEpics());
        System.out.println("Подзадачи");
        System.out.println(tasksManager.getSubtasks());
        Subtask subtask03 = new Subtask("Subtask03", "DescrSt03", "NEW", epic2.getId());
        System.out.println("Стало");
        tasksManager.updateSubtask(subtask03, subtask3.getId()); // Новые данные и существующий ID
        System.out.println("Эпики");
        System.out.println(tasksManager.getEpics());
        System.out.println("Подзадачи");
        System.out.println(tasksManager.getSubtasks());

        // Изменить статус у обычной задачи
        System.out.println("\nПоменять статус у обычной задачи");
        System.out.println("Было");
        System.out.println(tasksManager.getTasks());
        Task task001 = new Task("Task01", "DescrT01", "DONE");
        tasksManager.updateUsualTask(task001, task01.getId()); // Новые данные и существующий ID
        System.out.println("Стало");
        System.out.println(tasksManager.getTasks());

        // Изменить статус у Subtask
        System.out.println("\nПоменять статус у Subtask");
        System.out.println("Было");
        System.out.println("Эпики");
        System.out.println(tasksManager.getEpics());
        System.out.println("Подзадачи");
        System.out.println(tasksManager.getSubtasks());
        Subtask subtask02 = new Subtask("Subtask2", "DescrSt2", "DONE", epic1.getId());
        tasksManager.updateSubtask(subtask02, subtask03.getId()); // Новые данные и существующий ID
        System.out.println("Стало");
        System.out.println("Эпики");
        System.out.println(tasksManager.getEpics());
        System.out.println("Подзадачи");
        System.out.println(tasksManager.getSubtasks());


        // Удалить 1х обычную задачу
        System.out.println("\nУдалить 1х обычную задачу");
        System.out.println("Было");
        System.out.println(tasksManager.getTasks());
        tasksManager.deleteTaskById(task2.getId());
        System.out.println("Стало");
        System.out.println(tasksManager.getTasks());

        // Удалить 1х Epic
        System.out.println("\nУдалить 1х Epic");
        System.out.println("Было");
        System.out.println("Эпики");
        System.out.println(tasksManager.getEpics());
        System.out.println("Подзадачи");
        System.out.println(tasksManager.getSubtasks());
        tasksManager.deleteEpicById(epic02.getId());
        System.out.println("Стало");
        System.out.println("Эпики");
        System.out.println(tasksManager.getEpics());
        System.out.println("Подзадачи");
        System.out.println(tasksManager.getSubtasks());

        // Удалить всё
        System.out.println("\nУдалить всё");
        System.out.println("Было");
        System.out.println(tasksManager);
        tasksManager.deleteAllTasksAllTypes();
        System.out.println("Стало");
        System.out.println(tasksManager);
    }
}