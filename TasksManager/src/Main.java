public class Main {
    public static void main(String[] args) {
        // ТЕСТИРОВАНИЕ

        // Новый TasksManager для вызовов и распечатать списки всех задач
        TasksManager tasksManager = new TasksManager();
        System.out.println("Список задач пуст");
        System.out.println(tasksManager);

        // Создать 2x Task и распечатать списки всех задач
        System.out.println("\nСоздать 2x Task");
        tasksManager.createTask(new Task("Task1", "DescrT1", "NEW"));
        tasksManager.createTask(new Task("Task2", "Descr2", "NEW"));
        System.out.println(tasksManager);

        // Создать 1x Epic с 2х Subtask и распечатать списки всех задач
        System.out.println("\nСоздать 1x Epic с 2х Subtask");
        tasksManager.createEpic(new Epic("Epic1", "DescrEp1"));
        tasksManager.createSubtask(new Subtask("Subtask1", "DescrSt1", "NEW",
                tasksManager.getEpicIdByName("Epic1")));
        tasksManager.createSubtask(new Subtask("Subtask2", "DescrSt2", "NEW",
                tasksManager.getEpicIdByName("Epic1")));
        System.out.println(tasksManager);

        // Создать 1х Epic с 1х Subtask и распечатать списки всех задач
        System.out.println("\nСоздать 1x Epic с 1х Subtask");
        tasksManager.createEpic(new Epic("Epic2", "DescrEp2"));
        tasksManager.createSubtask(new Subtask("Subtask3", "DescrSt3", "NEW",
                tasksManager.getEpicIdByName("Epic2")));
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
        tasksManager.printEpicSubtasks(tasksManager.getEpicById(tasksManager.getEpicIdByName("Epic1")));

        // Обновить обычную задачу
        System.out.println("\nОбновить обычную задачу");
        System.out.println("Было");
        System.out.println(tasksManager.getTasks());
        tasksManager.updateUsualTask(new Task("Task01", "DescrT01", "NEW"),
                tasksManager.getUsualTaskIdByName("Task1")); // Полное обновление
        System.out.println("Стало");
        System.out.println(tasksManager.getTasks());

        // Обновить Epic
        System.out.println("\nОбновить Epic");
        System.out.println("Было");
        System.out.println(tasksManager.getEpics());
        tasksManager.updateEpic(new Epic("Epic02", "DescrEp02"),
                tasksManager.getEpicIdByName("Epic2")); // Полное обновление
        System.out.println("Стало");
        System.out.println(tasksManager.getEpics());

        // Обновить Subtask
        System.out.println("\nОбновить Subtask");
        System.out.println("Было");
        System.out.println("Эпики");
        System.out.println(tasksManager.getEpics());
        System.out.println("Подзадачи");
        System.out.println(tasksManager.getSubtasks());
        System.out.println("Стало");
        tasksManager.updateSubtask(new Subtask("Subtask03", "DescrSt03", "NEW", 
                tasksManager.getEpicIdByName("Epic02")),
                tasksManager.getSubtaskIdByName("Subtask3")); // Полное обновление
        System.out.println("Эпики");
        System.out.println(tasksManager.getEpics());
        System.out.println("Подзадачи");
        System.out.println(tasksManager.getSubtasks());

        // Изменить статус у обычной задачи
        System.out.println("\nИзменить статус у обычной задачи");
        System.out.println("Было");
        System.out.println(tasksManager.getTasks());
        tasksManager.getUsualTaskById(tasksManager.getUsualTaskIdByName("Task2")).setStatus("DONE"); // Частичное обновление
        System.out.println("Стало");
        System.out.println(tasksManager.getTasks());

        // Изменить статус у Subtask
        System.out.println("\nИзменить статус у Subtask");
        System.out.println("Было");
        System.out.println("Эпики");
        System.out.println(tasksManager.getEpics());
        System.out.println("Подзадачи");
        System.out.println(tasksManager.getSubtasks());
        tasksManager.getSubtaskById(tasksManager.getSubtaskIdByName("Subtask2")).setStatus("DONE"); // Частичное обновление
        tasksManager.getEpicById(tasksManager.getSubtasks().get(tasksManager.getSubtaskIdByName("Subtask2"))
                .getIdEpic()).setStatus(tasksManager.checkStatusEpic(tasksManager.getEpicById(tasksManager.getSubtasks()
                .get(tasksManager.getSubtaskIdByName("Subtask2")).getIdEpic()))); // Обновление статуса Epic
        System.out.println("Стало");
        System.out.println("Эпики");
        System.out.println(tasksManager.getEpics());
        System.out.println("Подзадачи");
        System.out.println(tasksManager.getSubtasks());


        // Удалить 1х обычную задачу
        System.out.println("\nУдалить 1х обычную задачу");
        System.out.println("Было");
        System.out.println(tasksManager.getTasks());
        tasksManager.deleteTaskById(tasksManager.getUsualTaskIdByName("Task2"));
        System.out.println("Стало");
        System.out.println(tasksManager.getTasks());

        /*
        // Удалить все подзадачи (при использовании проверить на совместимость с другими проверками)
        System.out.println("\nУдалить все подзадачи");
        System.out.println("Было");
        System.out.println("Эпики");
        System.out.println(tasksManager.getEpics());
        System.out.println("Подзадачи");
        System.out.println(tasksManager.getSubtasks());
        tasksManager.deleteAllSubtasks();
        System.out.println("Стало");
        System.out.println("Эпики");
        System.out.println(tasksManager.getEpics());
        System.out.println("Подзадачи");
        System.out.println(tasksManager.getSubtasks());
        */

        // Удалить 1х Epic
        System.out.println("\nУдалить 1х Epic");
        System.out.println("Было");
        System.out.println("Эпики");
        System.out.println(tasksManager.getEpics());
        System.out.println("Подзадачи");
        System.out.println(tasksManager.getSubtasks());
        tasksManager.deleteEpicById(tasksManager.getEpicIdByName("Epic1"));
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