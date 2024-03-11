package managers;

import tasks.*;
import java.util.List;

public interface HistoryManager {

    void add(Task task); // Помечать задачи как просмотренные

    List<Task> getHistory(); // Возвращать список

    void remove(int id); // Удаление задачи из просмотра

}