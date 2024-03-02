public class Main {
    public static void main(String[] args) {

        // ТЕСТИРОВАНИЕ


        // Новый TasksManager для вызовов и распечатать списки всех задач
        System.out.println("Список задач пуст");
        System.out.println(Managers.getDefault());

        // Создать 2x Task и распечатать списки всех задач
        System.out.println("\nСоздать 2x Task");
        Task task1 = new Task("Task1", "DescrT1", TaskStatus.NEW);
        Managers.getDefault().createTask(task1);
        Task task2 = new Task("Task2", "Descr2", TaskStatus.NEW);
        Managers.getDefault().createTask(task2);
        System.out.println(Managers.getDefault());

        // Создать 1x Epic с 2х Subtask и распечатать списки всех задач
        System.out.println("\nСоздать 1x Epic с 2х Subtask");
        Epic epic1 = new Epic("Epic1", "DescrEp1");
        Managers.getDefault().createEpic(epic1);
        Subtask subtask1E1 = new Subtask("Subtask1", "DescrSt1", TaskStatus.NEW, epic1.getId());
        Managers.getDefault().createSubtask(subtask1E1);
        Subtask subtask2E1 = new Subtask("Subtask2", "DescrSt2", TaskStatus.NEW, epic1.getId());
        Managers.getDefault().createSubtask(subtask2E1);
        System.out.println(Managers.getDefault());

        // Создать 1х Epic с 1х Subtask и распечатать списки всех задач
        System.out.println("\nСоздать 1x Epic с 1х Subtask");
        Epic epic2 = new Epic("Epic2", "DescrEp2");
        Managers.getDefault().createEpic(epic2);
        Subtask subtask1E2 = new Subtask("Subtask3", "DescrSt3", TaskStatus.NEW, epic2.getId());
        Managers.getDefault().createSubtask(subtask1E2);
        System.out.println(Managers.getDefault());

        // Проверка историй
        System.out.println("\nПроверка историй");
        System.out.println(Managers.getDefaultHistory().getHistory());
        for (int i = 0; i < 2; i++) {
            Managers.getDefault().getUsualTaskById(1);
            System.out.println(Managers.getDefaultHistory().getHistory());
            Managers.getDefault().getUsualTaskById(2);
            System.out.println(Managers.getDefaultHistory().getHistory());
            Managers.getDefault().getEpicById(3);
            System.out.println(Managers.getDefaultHistory().getHistory());
            Managers.getDefault().getSubtaskById(4);
            System.out.println(Managers.getDefaultHistory().getHistory());
            Managers.getDefault().getSubtaskById(5);
            System.out.println(Managers.getDefaultHistory().getHistory());
            Managers.getDefault().getEpicById(6);
            System.out.println(Managers.getDefaultHistory().getHistory());
            Managers.getDefault().getSubtaskById(7);
            System.out.println(Managers.getDefaultHistory().getHistory());
        }

        // Распечатать списки всех задач по типам
        System.out.println("\nРаспечатать списки всех задач по типам");
        System.out.println("Обычные задачи");
        System.out.println(Managers.getDefault().getTasks());
        System.out.println("Эпики");
        System.out.println(Managers.getDefault().getEpics());
        System.out.println("Подзадачи");
        System.out.println(Managers.getDefault().getSubtasks());

        // Получение списка всех подзадач определённого эпика
        System.out.println("\nПолучение списка всех подзадач определённого эпика");
        Managers.getDefault().printEpicSubtasks(epic1);

        // Обновить обычную задачу
        System.out.println("\nОбновить обычную задачу");
        System.out.println("Было");
        System.out.println(Managers.getDefault().getTasks());
        Task updateTask1 = new Task("Task01", "DescrT01", TaskStatus.NEW);
        Managers.getDefault().updateUsualTask(updateTask1, task1.getId()); // Полное обновление
        System.out.println("Стало");
        System.out.println(Managers.getDefault().getTasks());

        // Обновить Epic
        System.out.println("\nОбновить Epic");
        System.out.println("Было");
        System.out.println(Managers.getDefault().getEpics());
        Epic updateEpic2= new Epic("Epic02", "DescrEp02");
        Managers.getDefault().updateEpic(updateEpic2, epic2.getId()); // Полное обновление
        System.out.println("Стало");
        System.out.println(Managers.getDefault().getEpics());

        // Обновить Subtask
        System.out.println("\nОбновить Subtask");
        System.out.println("Было");
        System.out.println("Эпики");
        System.out.println(Managers.getDefault().getEpics());
        System.out.println("Подзадачи");
        System.out.println(Managers.getDefault().getSubtasks());
        System.out.println("Стало");
        Subtask updateSubtask1E2 = new Subtask("Subtask03", "DescrSt03", TaskStatus.NEW, epic2.getId());
        Managers.getDefault().updateSubtask(updateSubtask1E2, subtask1E2.getId()); // Полное обновление
        System.out.println("Эпики");
        System.out.println(Managers.getDefault().getEpics());
        System.out.println("Подзадачи");
        System.out.println(Managers.getDefault().getSubtasks());

        // Изменить статус у обычной задачи
        System.out.println("\nИзменить статус у обычной задачи");
        System.out.println("Было");
        System.out.println(Managers.getDefault().getTasks());
        task2.setStatus(TaskStatus.DONE); // Частичное обновление
        System.out.println("Стало");
        System.out.println(Managers.getDefault().getTasks());

        // Изменить статус у Subtask
        System.out.println("\nИзменить статус у Subtask");
        System.out.println("Было");
        System.out.println("Эпики");
        System.out.println(Managers.getDefault().getEpics());
        System.out.println("Подзадачи");
        System.out.println(Managers.getDefault().getSubtasks());
        subtask2E1.setStatus(TaskStatus.DONE); // Частичное обновление
        Managers.getDefault().checkStatusEpic(epic1); // Обновление статуса Epic
        System.out.println("Стало");
        System.out.println("Эпики");
        System.out.println(Managers.getDefault().getEpics());
        System.out.println("Подзадачи");
        System.out.println(Managers.getDefault().getSubtasks());

        // Удалить 1х обычную задачу
        System.out.println("\nУдалить 1х обычную задачу");
        System.out.println("Было");
        System.out.println(Managers.getDefault().getTasks());
        Managers.getDefault().deleteTaskById(task2.getId());
        System.out.println("Стало");
        System.out.println(Managers.getDefault().getTasks());

        // Удалить 1х Epic
        System.out.println("\nУдалить 1х Epic");
        System.out.println("Было");
        System.out.println("Эпики");
        System.out.println(Managers.getDefault().getEpics());
        System.out.println("Подзадачи");
        System.out.println(Managers.getDefault().getSubtasks());
        Managers.getDefault().deleteEpicById(epic1.getId());
        System.out.println("Стало");
        System.out.println("Эпики");
        System.out.println(Managers.getDefault().getEpics());
        System.out.println("Подзадачи");
        System.out.println(Managers.getDefault().getSubtasks());

        // Удалить всё
        System.out.println("\nУдалить всё");
        System.out.println("Было");
        System.out.println(Managers.getDefault());
        Managers.getDefault().deleteAllTasksAllTypes();
        System.out.println("Стало");
        System.out.println(Managers.getDefault());

    }
}