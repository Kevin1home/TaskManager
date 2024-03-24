import main_manager.Managers;
import managers.*;
import tasks.*;

public class Main {
    public static void main(String[] args) {


        System.out.println("ТЕСТИРОВАНИЕ (сохранение, 1 часть)");

        TaskManager fileBackedTaskManager = Managers.getDefault();


        // Новый TasksManager для вызовов и распечатать списки всех задач
        System.out.println("Список задач пуст");
        System.out.println(fileBackedTaskManager);

        // Создать 2x tasks.Task и распечатать списки всех задач
        System.out.println("\nСоздать 2x tasks.Task");
        Task task1 = new Task("Task1", "DescrT1", TaskStatus.NEW);
        fileBackedTaskManager.createTask(task1);
        Task task2 = new Task("Task2", "Descr2", TaskStatus.NEW);
        fileBackedTaskManager.createTask(task2);
        System.out.println(fileBackedTaskManager);

        // Создать 1x tasks.Epic с 2х tasks.Subtask и распечатать списки всех задач
        System.out.println("\nСоздать 1x tasks.Epic с 2х tasks.Subtask");
        Epic epic1 = new Epic("Epic1", "DescrEp1");
        fileBackedTaskManager.createEpic(epic1);
        Subtask subtask1E1 = new Subtask("Subtask1", "DescrSt1", TaskStatus.NEW, epic1.getId());
        fileBackedTaskManager.createSubtask(subtask1E1);
        Subtask subtask2E1 = new Subtask("Subtask2", "DescrSt2", TaskStatus.NEW, epic1.getId());
        fileBackedTaskManager.createSubtask(subtask2E1);
        System.out.println(fileBackedTaskManager);

        // Создать 1х tasks.Epic с 1х tasks.Subtask и распечатать списки всех задач
        System.out.println("\nСоздать 1x tasks.Epic с 1х tasks.Subtask");
        Epic epic2 = new Epic("Epic2", "DescrEp2");
        fileBackedTaskManager.createEpic(epic2);
        Subtask subtask1E2 = new Subtask("Subtask3", "DescrSt3", TaskStatus.NEW, epic2.getId());
        fileBackedTaskManager.createSubtask(subtask1E2);
        System.out.println(fileBackedTaskManager);

        // Проверка историй
        System.out.println("\nПроверка историй");
        System.out.println(fileBackedTaskManager.getHistoryManager().getHistory());
        fileBackedTaskManager.getUsualTaskById(1);
        System.out.println(fileBackedTaskManager.getHistoryManager().getHistory());
        fileBackedTaskManager.getUsualTaskById(1);
        System.out.println(fileBackedTaskManager.getHistoryManager().getHistory());
        fileBackedTaskManager.getUsualTaskById(2);
        System.out.println(fileBackedTaskManager.getHistoryManager().getHistory());
        fileBackedTaskManager.getEpicById(3);
        System.out.println(fileBackedTaskManager.getHistoryManager().getHistory());
        fileBackedTaskManager.getUsualTaskById(1);
        System.out.println(fileBackedTaskManager.getHistoryManager().getHistory());
        fileBackedTaskManager.getSubtaskById(4);
        System.out.println(fileBackedTaskManager.getHistoryManager().getHistory());
        fileBackedTaskManager.getSubtaskById(5);
        System.out.println(fileBackedTaskManager.getHistoryManager().getHistory());
        fileBackedTaskManager.getUsualTaskById(2);
        System.out.println(fileBackedTaskManager.getHistoryManager().getHistory());
        fileBackedTaskManager.getUsualTaskById(1);
        System.out.println(fileBackedTaskManager.getHistoryManager().getHistory());
        fileBackedTaskManager.getEpicById(6);
        System.out.println(fileBackedTaskManager.getHistoryManager().getHistory());
        fileBackedTaskManager.getUsualTaskById(1);
        System.out.println(fileBackedTaskManager.getHistoryManager().getHistory());
        fileBackedTaskManager.getUsualTaskById(1);
        System.out.println(fileBackedTaskManager.getHistoryManager().getHistory());

        // Удалить 1х обычную задачу
        System.out.println("\nУдалить 1х обычную задачу");
        System.out.println("Было");
        System.out.println(fileBackedTaskManager.getTasks());
        System.out.println(fileBackedTaskManager.getHistoryManager().getHistory());
        fileBackedTaskManager.deleteTaskById(task2.getId());
        fileBackedTaskManager.getHistoryManager().remove(task2.getId());
        System.out.println("Стало");
        System.out.println(fileBackedTaskManager.getTasks());
        System.out.println(fileBackedTaskManager.getHistoryManager().getHistory());

        // Изменить статус у обычной задачи
        System.out.println("\nИзменить статус у обычной задачи");
        System.out.println("Было");
        System.out.println(fileBackedTaskManager.getTasks());
        task1.setStatus(TaskStatus.DONE); // Частичное обновление
        System.out.println("Стало");
        System.out.println(fileBackedTaskManager.getTasks());

        // Изменить статус у tasks.Subtask
        System.out.println("\nИзменить статус у tasks.Subtask");
        System.out.println("Было");
        System.out.println("Эпики");
        System.out.println(fileBackedTaskManager.getEpics());
        System.out.println("Подзадачи");
        System.out.println(fileBackedTaskManager.getSubtasks());
        subtask1E2.setStatus(TaskStatus.DONE); // Частичное обновление
        fileBackedTaskManager.checkStatusEpic(epic2); // Обновление статуса tasks.Epic
        System.out.println("Стало");
        System.out.println("Эпики");
        System.out.println(fileBackedTaskManager.getEpics());
        System.out.println("Подзадачи");
        System.out.println(fileBackedTaskManager.getSubtasks());

        // Заполнение историй
        System.out.println("\nЗаполнение истории");
        System.out.println(fileBackedTaskManager.getHistoryManager().getHistory());
        fileBackedTaskManager.getUsualTaskById(1);
        System.out.println(fileBackedTaskManager.getHistoryManager().getHistory());
        fileBackedTaskManager.getEpicById(3);
        System.out.println(fileBackedTaskManager.getHistoryManager().getHistory());




/*
        System.out.println("ТЕСТИРОВАНИЕ (сохранение, 2 часть)");
        TaskManager fileBackedTaskManager = Managers.getDefault();
        System.out.println(fileBackedTaskManager);
        System.out.println(fileBackedTaskManager.getHistoryManager().getHistory());

        */




        // ШАБЛОНЫ ДЛЯ ТЕСТИРОВАНИЯ

    /*
    // ТЕСТИРОВАНИЕ (история просмотров)

    // Новый TasksManager для вызовов и распечатать списки всех задач
        System.out.println("Список задач пуст");
    TaskManager taskManager = Managers.getDefault();
        System.out.println(taskManager);

    // Создать 2x tasks.Task и распечатать списки всех задач
        System.out.println("\nСоздать 2x tasks.Task");
    Task task1 = new Task("Task1", "DescrT1", TaskStatus.NEW);
        taskManager.createTask(task1);
    Task task2 = new Task("Task2", "Descr2", TaskStatus.NEW);
        taskManager.createTask(task2);
        System.out.println(taskManager);

    // Создать 1x tasks.Epic с 3х tasks.Subtask и распечатать списки всех задач
        System.out.println("\nСоздать 1x tasks.Epic с 2х tasks.Subtask");
    Epic epic1 = new Epic("Epic1", "DescrEp1");
        taskManager.createEpic(epic1);
    Subtask subtask1E1 = new Subtask("Subtask1", "DescrSt1", TaskStatus.NEW, epic1.getId());
        taskManager.createSubtask(subtask1E1);
    Subtask subtask2E1 = new Subtask("Subtask2", "DescrSt2", TaskStatus.NEW, epic1.getId());
        taskManager.createSubtask(subtask2E1);
    Subtask subtask3E1 = new Subtask("Subtask3", "DescrSt3", TaskStatus.NEW, epic1.getId());
        taskManager.createSubtask(subtask3E1);
        System.out.println(taskManager);

    // Создать 1х tasks.Epic с 0х tasks.Subtask и распечатать списки всех задач
        System.out.println("\nСоздать 1x tasks.Epic с 1х tasks.Subtask");
    Epic epic2 = new Epic("Epic2", "DescrEp2");
        taskManager.createEpic(epic2);
        System.out.println(taskManager);

    // Проверка историй
        System.out.println("\nПроверка историй");
        System.out.println(taskManager.getHistoryManager().getHistory());
        taskManager.getUsualTaskById(1);
        System.out.println(taskManager.getHistoryManager().getHistory());
        taskManager.getUsualTaskById(1);
        System.out.println(taskManager.getHistoryManager().getHistory());
        taskManager.getUsualTaskById(2);
        System.out.println(taskManager.getHistoryManager().getHistory());
        taskManager.getEpicById(3);
        System.out.println(taskManager.getHistoryManager().getHistory());
        taskManager.getUsualTaskById(1);
        System.out.println(taskManager.getHistoryManager().getHistory());
        taskManager.getSubtaskById(4);
        System.out.println(taskManager.getHistoryManager().getHistory());
        taskManager.getSubtaskById(5);
        System.out.println(taskManager.getHistoryManager().getHistory());
        taskManager.getUsualTaskById(2);
        System.out.println(taskManager.getHistoryManager().getHistory());
        taskManager.getUsualTaskById(1);
        System.out.println(taskManager.getHistoryManager().getHistory());
        taskManager.getSubtaskById(6);
        System.out.println(taskManager.getHistoryManager().getHistory());
        taskManager.getEpicById(7);
        System.out.println(taskManager.getHistoryManager().getHistory());
        taskManager.getUsualTaskById(1);
        System.out.println(taskManager.getHistoryManager().getHistory());
        taskManager.getUsualTaskById(1);
        System.out.println(taskManager.getHistoryManager().getHistory());

    // Удалить 1х обычную задачу
        System.out.println("\nУдалить 1х обычную задачу");
        System.out.println("Было");
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getHistoryManager().getHistory());
        taskManager.deleteTaskById(task2.getId());
        taskManager.getHistoryManager().remove(task2.getId());
        System.out.println("Стало");
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getHistoryManager().getHistory());

    // Удалить 1х Epic
        System.out.println("\nУдалить 1х tasks.Epic");
        System.out.println("Было");
        System.out.println("Эпики");
        System.out.println(taskManager.getEpics());
        System.out.println("Подзадачи");
        System.out.println(taskManager.getSubtasks());
        System.out.println(taskManager.getHistoryManager().getHistory());
        taskManager.deleteEpicById(epic1.getId());
        System.out.println("Стало");
        System.out.println("Эпики");
        System.out.println(taskManager.getEpics());
        System.out.println("Подзадачи");
        System.out.println(taskManager.getSubtasks());
        System.out.println(taskManager.getHistoryManager().getHistory());

*/

    /*
        // ТЕСТИРОВАНИЕ (Шаблоны)

        // Новый TasksManager для вызовов и распечатать списки всех задач
        System.out.println("Список задач пуст");
        TaskManager taskManager = Managers.getDefault();
        System.out.println(taskManager);

        // Создать 2x tasks.Task и распечатать списки всех задач
        System.out.println("\nСоздать 2x tasks.Task");
        Task task1 = new Task("Task1", "DescrT1", TaskStatus.NEW);
        taskManager.createTask(task1);
        Task task2 = new Task("Task2", "Descr2", TaskStatus.NEW);
        taskManager.createTask(task2);
        System.out.println(taskManager);

        // Создать 1x tasks.Epic с 3х tasks.Subtask и распечатать списки всех задач
        System.out.println("\nСоздать 1x tasks.Epic с 2х tasks.Subtask");
        Epic epic1 = new Epic("Epic1", "DescrEp1");
        taskManager.createEpic(epic1);
        Subtask subtask1E1 = new Subtask("Subtask1", "DescrSt1", TaskStatus.NEW, epic1.getId());
        taskManager.createSubtask(subtask1E1);
        Subtask subtask2E1 = new Subtask("Subtask2", "DescrSt2", TaskStatus.NEW, epic1.getId());
        taskManager.createSubtask(subtask2E1);
        Subtask subtask3E1 = new Subtask("Subtask3", "DescrSt3", TaskStatus.NEW, epic1.getId());
        taskManager.createSubtask(subtask3E1);
        System.out.println(taskManager);

        // Создать 1х tasks.Epic с 0х tasks.Subtask и распечатать списки всех задач
        System.out.println("\nСоздать 1x tasks.Epic с 1х tasks.Subtask");
        Epic epic2 = new Epic("Epic2", "DescrEp2");
        taskManager.createEpic(epic2);
        System.out.println(taskManager);

        // Создать 1x tasks.Epic с 2х tasks.Subtask и распечатать списки всех задач
        System.out.println("\nСоздать 1x tasks.Epic с 2х tasks.Subtask");
        Epic epic1 = new Epic("Epic1", "DescrEp1");
        taskManager.createEpic(epic1);
        Subtask subtask1E1 = new Subtask("Subtask1", "DescrSt1", TaskStatus.NEW, epic1.getId());
        taskManager.createSubtask(subtask1E1);
        Subtask subtask2E1 = new Subtask("Subtask2", "DescrSt2", TaskStatus.NEW, epic1.getId());
        taskManager.createSubtask(subtask2E1);
        System.out.println(taskManager);

        // Создать 1х tasks.Epic с 1х tasks.Subtask и распечатать списки всех задач
        System.out.println("\nСоздать 1x tasks.Epic с 1х tasks.Subtask");
        Epic epic2 = new Epic("Epic2", "DescrEp2");
        taskManager.createEpic(epic2);
        Subtask subtask1E2 = new Subtask("Subtask3", "DescrSt3", TaskStatus.NEW, epic2.getId());
        taskManager.createSubtask(subtask1E2);
        System.out.println(taskManager);

        // Проверка историй
        System.out.println("\nПроверка историй");
        System.out.println(taskManager.getHistoryManager().getHistory());
        taskManager.getUsualTaskById(1);
        System.out.println(taskManager.getHistoryManager().getHistory());
        taskManager.getUsualTaskById(1);
        System.out.println(taskManager.getHistoryManager().getHistory());
        taskManager.getUsualTaskById(2);
        System.out.println(taskManager.getHistoryManager().getHistory());
        taskManager.getEpicById(3);
        System.out.println(taskManager.getHistoryManager().getHistory());
        taskManager.getUsualTaskById(1);
        System.out.println(taskManager.getHistoryManager().getHistory());

        // Распечатать списки всех задач по типам
        System.out.println("\nРаспечатать списки всех задач по типам");
        System.out.println("Обычные задачи");
        System.out.println(taskManager.getTasks());
        System.out.println("Эпики");
        System.out.println(taskManager.getEpics());
        System.out.println("Подзадачи");
        System.out.println(taskManager.getSubtasks());

        // Получение списка всех подзадач определённого эпика
        System.out.println("\nПолучение списка всех подзадач определённого эпика");
        taskManager.printEpicSubtasks(epic1);

        // Обновить обычную задачу
        System.out.println("\nОбновить обычную задачу");
        System.out.println("Было");
        System.out.println(taskManager.getTasks());
        Task updateTask1 = new Task("Task01", "DescrT01", TaskStatus.NEW);
        taskManager.updateUsualTask(updateTask1, task1.getId()); // Полное обновление
        System.out.println("Стало");
        System.out.println(taskManager.getTasks());

        // Обновить tasks.Epic
        System.out.println("\nОбновить tasks.Epic");
        System.out.println("Было");
        System.out.println(taskManager.getEpics());
        Epic updateEpic2= new Epic("Epic02", "DescrEp02");
        taskManager.updateEpic(updateEpic2, epic2.getId()); // Полное обновление
        System.out.println("Стало");
        System.out.println(taskManager.getEpics());

        // Обновить tasks.Subtask
        System.out.println("\nОбновить tasks.Subtask");
        System.out.println("Было");
        System.out.println("Эпики");
        System.out.println(taskManager.getEpics());
        System.out.println("Подзадачи");
        System.out.println(taskManager.getSubtasks());
        System.out.println("Стало");
        Subtask updateSubtask1E2 = new Subtask("Subtask03", "DescrSt03", TaskStatus.NEW, epic2.getId());
        taskManager.updateSubtask(updateSubtask1E2, subtask1E2.getId()); // Полное обновление
        System.out.println("Эпики");
        System.out.println(taskManager.getEpics());
        System.out.println("Подзадачи");
        System.out.println(taskManager.getSubtasks());

        // Изменить статус у обычной задачи
        System.out.println("\nИзменить статус у обычной задачи");
        System.out.println("Было");
        System.out.println(taskManager.getTasks());
        task2.setStatus(TaskStatus.DONE); // Частичное обновление
        System.out.println("Стало");
        System.out.println(taskManager.getTasks());

        // Изменить статус у tasks.Subtask
        System.out.println("\nИзменить статус у tasks.Subtask");
        System.out.println("Было");
        System.out.println("Эпики");
        System.out.println(taskManager.getEpics());
        System.out.println("Подзадачи");
        System.out.println(taskManager.getSubtasks());
        subtask2E1.setStatus(TaskStatus.DONE); // Частичное обновление
        taskManager.checkStatusEpic(epic1); // Обновление статуса tasks.Epic
        System.out.println("Стало");
        System.out.println("Эпики");
        System.out.println(taskManager.getEpics());
        System.out.println("Подзадачи");
        System.out.println(taskManager.getSubtasks());

        // Удалить 1х обычную задачу
        System.out.println("\nУдалить 1х обычную задачу");
        System.out.println("Было");
        System.out.println(taskManager.getTasks());
        taskManager.deleteTaskById(task2.getId());
        System.out.println("Стало");
        System.out.println(taskManager.getTasks());

        // Удалить 1х Epic
        System.out.println("\nУдалить 1х tasks.Epic");
        System.out.println("Было");
        System.out.println("Эпики");
        System.out.println(taskManager.getEpics());
        System.out.println("Подзадачи");
        System.out.println(taskManager.getSubtasks());
        taskManager.deleteEpicById(epic1.getId());
        System.out.println("Стало");
        System.out.println("Эпики");
        System.out.println(taskManager.getEpics());
        System.out.println("Подзадачи");
        System.out.println(taskManager.getSubtasks());

        // Удалить всё
        System.out.println("\nУдалить всё");
        System.out.println("Было");
        System.out.println(taskManager);
        taskManager.deleteAllTasksAllTypes();
        System.out.println("Стало");
        System.out.println(taskManager);
        */

    }

}