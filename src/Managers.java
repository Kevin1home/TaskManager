public class Managers {

    static HistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
    static TaskManager inMemoryTaskManager = new InMemoryTaskManager();

    static TaskManager getDefault() {
        return inMemoryTaskManager;
    }

    static HistoryManager getDefaultHistory() {
        return inMemoryHistoryManager;
    }

}