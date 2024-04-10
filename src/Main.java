import main_manager.Managers;
import managers.*;
import tasks.*;

public class Main {
    public static void main(String[] args) {


/*

        System.out.println("ТЕСТИРОВАНИЕ (сохранение, 1 часть)");

        TaskManager fileBackedTaskManager = Managers.getDefault();


        // Новый TasksManager для вызовов и распечатать списки всех задач
        System.out.println("Список задач пуст");
        System.out.println(fileBackedTaskManager);

        // Создать ошибочный 1х tasks.Subtask
        System.out.println("\nСоздать ошибочный 1х tasks.Subtask");

        try {
        Subtask subtaskWrong1 = new Subtask("Subtask2", "DescrSt2", TaskStatus.NEW, 33);
        fileBackedTaskManager.createSubtask(subtaskWrong1);
        } catch (IllegalArgumentException exception) {
            System.out.println(exception.getMessage());
        }

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

        // Создать ошибочный 1х tasks.Subtask
        System.out.println("\nСоздать ошибочный 1х tasks.Subtask");
        try {
        Subtask subtaskWrong2 = new Subtask("Subtask2", "DescrSt2", TaskStatus.NEW, 33);
        fileBackedTaskManager.createSubtask(subtaskWrong2);
        } catch (IllegalArgumentException exception) {
            System.out.println(exception.getMessage());
        }
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
        try {
            fileBackedTaskManager.getUsualTaskById(1);
        } catch (IllegalArgumentException exception) {
            System.out.println(exception.getMessage());
        }
        System.out.println(fileBackedTaskManager.getHistoryManager().getHistory());
        try {
            fileBackedTaskManager.getUsualTaskById(1);
        } catch (IllegalArgumentException exception) {
            System.out.println(exception.getMessage());
        }
        System.out.println(fileBackedTaskManager.getHistoryManager().getHistory());
        try {
            fileBackedTaskManager.getUsualTaskById(2);
        } catch (IllegalArgumentException exception) {
            System.out.println(exception.getMessage());
        }
        System.out.println(fileBackedTaskManager.getHistoryManager().getHistory());
        try {
            fileBackedTaskManager.getEpicById(3);
        } catch (IllegalArgumentException exception) {
            System.out.println(exception.getMessage());
        }
        System.out.println(fileBackedTaskManager.getHistoryManager().getHistory());
        try {
            fileBackedTaskManager.getUsualTaskById(1);
        } catch (IllegalArgumentException exception) {
            System.out.println(exception.getMessage());
        }
        System.out.println(fileBackedTaskManager.getHistoryManager().getHistory());
        try {
            fileBackedTaskManager.getSubtaskById(4);
        } catch (IllegalArgumentException exception) {
            System.out.println(exception.getMessage());
        }
        System.out.println(fileBackedTaskManager.getHistoryManager().getHistory());
        try {
            fileBackedTaskManager.getSubtaskById(5);
        } catch (IllegalArgumentException exception) {
            System.out.println(exception.getMessage());
        }
        System.out.println(fileBackedTaskManager.getHistoryManager().getHistory());
        try {
            fileBackedTaskManager.getUsualTaskById(2);
        } catch (IllegalArgumentException exception) {
            System.out.println(exception.getMessage());
        }
        System.out.println(fileBackedTaskManager.getHistoryManager().getHistory());
        try {
            fileBackedTaskManager.getUsualTaskById(1);
        } catch (IllegalArgumentException exception) {
            System.out.println(exception.getMessage());
        }
        System.out.println(fileBackedTaskManager.getHistoryManager().getHistory());
        try {
            fileBackedTaskManager.getEpicById(6);
        } catch (IllegalArgumentException exception) {
            System.out.println(exception.getMessage());
        }
        System.out.println(fileBackedTaskManager.getHistoryManager().getHistory());
        try {
            fileBackedTaskManager.getUsualTaskById(1);
        } catch (IllegalArgumentException exception) {
            System.out.println(exception.getMessage());
        }
        System.out.println(fileBackedTaskManager.getHistoryManager().getHistory());
        try {
            fileBackedTaskManager.getUsualTaskById(1);
        } catch (IllegalArgumentException exception) {
            System.out.println(exception.getMessage());
        }
        System.out.println(fileBackedTaskManager.getHistoryManager().getHistory());

        // Удалить все обычные задачи
        System.out.println("\nУдалить все обычные задачу");
        System.out.println("Было");
        System.out.println(fileBackedTaskManager.getTasks());
        System.out.println(fileBackedTaskManager.getHistoryManager().getHistory());
        try {
            fileBackedTaskManager.deleteAllUsualTasks();
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
        System.out.println("Стало");
        System.out.println(fileBackedTaskManager.getTasks());
        System.out.println(fileBackedTaskManager.getHistoryManager().getHistory());

        // Удалить все обычные задачи ещё раз
        System.out.println("\nУдалить все обычные задачу");
        System.out.println("Было");
        System.out.println(fileBackedTaskManager.getTasks());
        System.out.println(fileBackedTaskManager.getHistoryManager().getHistory());
        try {
            fileBackedTaskManager.deleteAllUsualTasks();
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
        System.out.println("Стало");
        System.out.println(fileBackedTaskManager.getTasks());
        System.out.println(fileBackedTaskManager.getHistoryManager().getHistory());

        // Удалить 1х обычную задачу
        System.out.println("\nУдалить 1х обычную задачу");
        System.out.println("Было");
        System.out.println(fileBackedTaskManager.getTasks());
        System.out.println(fileBackedTaskManager.getHistoryManager().getHistory());
        try {
            fileBackedTaskManager.deleteTaskById(task2.getId());
            fileBackedTaskManager.getHistoryManager().remove(task2.getId());
        } catch (IllegalArgumentException exception) {
            System.out.println(exception.getMessage());
        }
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
        try {
            fileBackedTaskManager.getUsualTaskById(1);
        } catch (IllegalArgumentException exception) {
            System.out.println(exception.getMessage());
        }
        System.out.println(fileBackedTaskManager.getHistoryManager().getHistory());
        try {
            fileBackedTaskManager.getEpicById(3);
        } catch (IllegalArgumentException exception) {
            System.out.println(exception.getMessage());
        }
        System.out.println(fileBackedTaskManager.getHistoryManager().getHistory());


*/





        System.out.println("ТЕСТИРОВАНИЕ (сохранение, 2 часть)");
        TaskManager fileBackedTaskManager = Managers.getDefault();
        System.out.println(fileBackedTaskManager);
        System.out.println(fileBackedTaskManager.getHistoryManager().getHistory());

        fileBackedTaskManager.createTask(new Task("Task1", "DescrT1", TaskStatus.NEW));


        System.out.println(fileBackedTaskManager);
        System.out.println(fileBackedTaskManager.getHistoryManager().getHistory());




    }

}