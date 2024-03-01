public class Main {
    public static void main(String[] args) {

        // ТЕСТИРОВАНИЕ


        // Новый TasksManager для вызовов и распечатать списки всех задач
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        System.out.println("Список задач пуст");
        System.out.println(inMemoryTaskManager);

        // Создать 2x Task и распечатать списки всех задач
        System.out.println("\nСоздать 2x Task");
        Task task1 = new Task("Task1", "DescrT1", "NEW");
        inMemoryTaskManager.createTask(task1);
        Task task2 = new Task("Task2", "Descr2", "NEW");
        inMemoryTaskManager.createTask(task2);
        System.out.println(inMemoryTaskManager);

        // Создать 1x Epic с 2х Subtask и распечатать списки всех задач
        System.out.println("\nСоздать 1x Epic с 2х Subtask");
        Epic epic1 = new Epic("Epic1", "DescrEp1");
        inMemoryTaskManager.createEpic(epic1);
        Subtask subtask1E1 = new Subtask("Subtask1", "DescrSt1", "NEW", epic1.getId());
        inMemoryTaskManager.createSubtask(subtask1E1);
        Subtask subtask2E1 = new Subtask("Subtask2", "DescrSt2", "NEW", epic1.getId());
        inMemoryTaskManager.createSubtask(subtask2E1);
        System.out.println(inMemoryTaskManager);

        // Создать 1х Epic с 1х Subtask и распечатать списки всех задач
        System.out.println("\nСоздать 1x Epic с 1х Subtask");
        Epic epic2 = new Epic("Epic2", "DescrEp2");
        inMemoryTaskManager.createEpic(epic2);
        Subtask subtask1E2 = new Subtask("Subtask3", "DescrSt3", "NEW", epic2.getId());
        inMemoryTaskManager.createSubtask(subtask1E2);
        System.out.println(inMemoryTaskManager);

        // Проверка историй
        System.out.println("\nПроверка историй");
        System.out.println(inMemoryTaskManager.getHistory());
        for (int i = 0; i < 2; i++) {
            inMemoryTaskManager.getUsualTaskById(1);
            System.out.println(inMemoryTaskManager.getHistory());
            inMemoryTaskManager.getUsualTaskById(2);
            System.out.println(inMemoryTaskManager.getHistory());
            inMemoryTaskManager.getEpicById(3);
            System.out.println(inMemoryTaskManager.getHistory());
            inMemoryTaskManager.getSubtaskById(4);
            System.out.println(inMemoryTaskManager.getHistory());
            inMemoryTaskManager.getSubtaskById(5);
            System.out.println(inMemoryTaskManager.getHistory());
            inMemoryTaskManager.getEpicById(6);
            System.out.println(inMemoryTaskManager.getHistory());
            inMemoryTaskManager.getSubtaskById(7);
            System.out.println(inMemoryTaskManager.getHistory());
        }

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
        inMemoryTaskManager.printEpicSubtasks(epic1);

        // Обновить обычную задачу
        System.out.println("\nОбновить обычную задачу");
        System.out.println("Было");
        System.out.println(inMemoryTaskManager.getTasks());
        Task updateTask1 = new Task("Task01", "DescrT01", "NEW");
        inMemoryTaskManager.updateUsualTask(updateTask1, task1.getId()); // Полное обновление
        System.out.println("Стало");
        System.out.println(inMemoryTaskManager.getTasks());

        // Обновить Epic
        System.out.println("\nОбновить Epic");
        System.out.println("Было");
        System.out.println(inMemoryTaskManager.getEpics());
        Epic updateEpic2= new Epic("Epic02", "DescrEp02");
        inMemoryTaskManager.updateEpic(updateEpic2, epic2.getId()); // Полное обновление
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
        Subtask updateSubtask1E2 = new Subtask("Subtask03", "DescrSt03", "NEW", epic2.getId());
        inMemoryTaskManager.updateSubtask(updateSubtask1E2, subtask1E2.getId()); // Полное обновление
        System.out.println("Эпики");
        System.out.println(inMemoryTaskManager.getEpics());
        System.out.println("Подзадачи");
        System.out.println(inMemoryTaskManager.getSubtasks());

        // Изменить статус у обычной задачи
        System.out.println("\nИзменить статус у обычной задачи");
        System.out.println("Было");
        System.out.println(inMemoryTaskManager.getTasks());
        task2.setStatus("DONE"); // Частичное обновление
        System.out.println("Стало");
        System.out.println(inMemoryTaskManager.getTasks());

        // Изменить статус у Subtask
        System.out.println("\nИзменить статус у Subtask");
        System.out.println("Было");
        System.out.println("Эпики");
        System.out.println(inMemoryTaskManager.getEpics());
        System.out.println("Подзадачи");
        System.out.println(inMemoryTaskManager.getSubtasks());
        subtask2E1.setStatus("DONE"); // Частичное обновление
        inMemoryTaskManager.checkStatusEpic(epic1); // Обновление статуса Epic
        System.out.println("Стало");
        System.out.println("Эпики");
        System.out.println(inMemoryTaskManager.getEpics());
        System.out.println("Подзадачи");
        System.out.println(inMemoryTaskManager.getSubtasks());

        // Удалить 1х обычную задачу
        System.out.println("\nУдалить 1х обычную задачу");
        System.out.println("Было");
        System.out.println(inMemoryTaskManager.getTasks());
        inMemoryTaskManager.deleteTaskById(task2.getId());
        System.out.println("Стало");
        System.out.println(inMemoryTaskManager.getTasks());

        // Удалить 1х Epic
        System.out.println("\nУдалить 1х Epic");
        System.out.println("Было");
        System.out.println("Эпики");
        System.out.println(inMemoryTaskManager.getEpics());
        System.out.println("Подзадачи");
        System.out.println(inMemoryTaskManager.getSubtasks());
        inMemoryTaskManager.deleteEpicById(epic1.getId());
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