public class Main {
    public static void main(String[] args) {
        // ТЕСТИРОВАНИЕ

        // Новый TasksManager для вызовов и распечатать списки всех задач
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        System.out.println("Список задач пуст");
        System.out.println(inMemoryTaskManager);

        // Создать 2x Task и распечатать списки всех задач
        System.out.println("\nСоздать 2x Task");
        inMemoryTaskManager.createTask(new Task("Task1", "DescrT1", "NEW"));
        inMemoryTaskManager.createTask(new Task("Task2", "Descr2", "NEW"));
        System.out.println(inMemoryTaskManager);

        // Создать 1x Epic с 2х Subtask и распечатать списки всех задач
        System.out.println("\nСоздать 1x Epic с 2х Subtask");
        inMemoryTaskManager.createEpic(new Epic("Epic1", "DescrEp1"));
        inMemoryTaskManager.createSubtask(new Subtask("Subtask1", "DescrSt1", "NEW",
                inMemoryTaskManager.getEpicIdByName("Epic1")));
        inMemoryTaskManager.createSubtask(new Subtask("Subtask2", "DescrSt2", "NEW",
                inMemoryTaskManager.getEpicIdByName("Epic1")));
        System.out.println(inMemoryTaskManager);

        // Создать 1х Epic с 1х Subtask и распечатать списки всех задач
        System.out.println("\nСоздать 1x Epic с 1х Subtask");
        inMemoryTaskManager.createEpic(new Epic("Epic2", "DescrEp2"));
        inMemoryTaskManager.createSubtask(new Subtask("Subtask3", "DescrSt3", "NEW",
                inMemoryTaskManager.getEpicIdByName("Epic2")));
        System.out.println(inMemoryTaskManager);

        // Распечатать списки всех задач по типам
        System.out.println("\nРаспечатать списки всех задач по типам");
        System.out.println("Обычные задачи");
        System.out.println(inMemoryTaskManager.getTasks());
        System.out.println("Эпики");
        System.out.println(inMemoryTaskManager.getEpics());
        System.out.println("Подзадачи");
        System.out.println(inMemoryTaskManager.getSubtasks());

        // Получение списка всех подзадач определённого эпика
        System.out.println("\nПолучение списка всех подзадач определённого эпика");
        inMemoryTaskManager.printEpicSubtasks(inMemoryTaskManager.getEpicById(inMemoryTaskManager.getEpicIdByName("Epic1")));

        // Обновить обычную задачу
        System.out.println("\nОбновить обычную задачу");
        System.out.println("Было");
        System.out.println(inMemoryTaskManager.getTasks());
        inMemoryTaskManager.updateUsualTask(new Task("Task01", "DescrT01", "NEW"),
                inMemoryTaskManager.getUsualTaskIdByName("Task1")); // Полное обновление
        System.out.println("Стало");
        System.out.println(inMemoryTaskManager.getTasks());

        // Обновить Epic
        System.out.println("\nОбновить Epic");
        System.out.println("Было");
        System.out.println(inMemoryTaskManager.getEpics());
        inMemoryTaskManager.updateEpic(new Epic("Epic02", "DescrEp02"),
                inMemoryTaskManager.getEpicIdByName("Epic2")); // Полное обновление
        System.out.println("Стало");
        System.out.println(inMemoryTaskManager.getEpics());

        // Обновить Subtask
        System.out.println("\nОбновить Subtask");
        System.out.println("Было");
        System.out.println("Эпики");
        System.out.println(inMemoryTaskManager.getEpics());
        System.out.println("Подзадачи");
        System.out.println(inMemoryTaskManager.getSubtasks());
        System.out.println("Стало");
        inMemoryTaskManager.updateSubtask(new Subtask("Subtask03", "DescrSt03", "NEW",
                inMemoryTaskManager.getEpicIdByName("Epic02")),
                inMemoryTaskManager.getSubtaskIdByName("Subtask3")); // Полное обновление
        System.out.println("Эпики");
        System.out.println(inMemoryTaskManager.getEpics());
        System.out.println("Подзадачи");
        System.out.println(inMemoryTaskManager.getSubtasks());

        // Изменить статус у обычной задачи
        System.out.println("\nИзменить статус у обычной задачи");
        System.out.println("Было");
        System.out.println(inMemoryTaskManager.getTasks());
        inMemoryTaskManager.getUsualTaskById(inMemoryTaskManager.getUsualTaskIdByName("Task2")).setStatus("DONE"); // Частичное обновление
        System.out.println("Стало");
        System.out.println(inMemoryTaskManager.getTasks());

        // Изменить статус у Subtask
        System.out.println("\nИзменить статус у Subtask");
        System.out.println("Было");
        System.out.println("Эпики");
        System.out.println(inMemoryTaskManager.getEpics());
        System.out.println("Подзадачи");
        System.out.println(inMemoryTaskManager.getSubtasks());
        inMemoryTaskManager.getSubtaskById(inMemoryTaskManager.getSubtaskIdByName("Subtask2")).setStatus("DONE"); // Частичное обновление
        inMemoryTaskManager.getEpicById(inMemoryTaskManager.getSubtasks().get(inMemoryTaskManager.getSubtaskIdByName("Subtask2"))
                .getIdEpic()).setStatus(inMemoryTaskManager.checkStatusEpic(inMemoryTaskManager.getEpicById(inMemoryTaskManager.getSubtasks()
                .get(inMemoryTaskManager.getSubtaskIdByName("Subtask2")).getIdEpic()))); // Обновление статуса Epic
        System.out.println("Стало");
        System.out.println("Эпики");
        System.out.println(inMemoryTaskManager.getEpics());
        System.out.println("Подзадачи");
        System.out.println(inMemoryTaskManager.getSubtasks());


        // Удалить 1х обычную задачу
        System.out.println("\nУдалить 1х обычную задачу");
        System.out.println("Было");
        System.out.println(inMemoryTaskManager.getTasks());
        inMemoryTaskManager.deleteTaskById(inMemoryTaskManager.getUsualTaskIdByName("Task2"));
        System.out.println("Стало");
        System.out.println(inMemoryTaskManager.getTasks());

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
        System.out.println(inMemoryTaskManager.getEpics());
        System.out.println("Подзадачи");
        System.out.println(inMemoryTaskManager.getSubtasks());
        inMemoryTaskManager.deleteEpicById(inMemoryTaskManager.getEpicIdByName("Epic1"));
        System.out.println("Стало");
        System.out.println("Эпики");
        System.out.println(inMemoryTaskManager.getEpics());
        System.out.println("Подзадачи");
        System.out.println(inMemoryTaskManager.getSubtasks());

        // Удалить всё
        System.out.println("\nУдалить всё");
        System.out.println("Было");
        System.out.println(inMemoryTaskManager);
        inMemoryTaskManager.deleteAllTasksAllTypes();
        System.out.println("Стало");
        System.out.println(inMemoryTaskManager);

    }
}