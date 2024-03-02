package managers;

import tasks.*;
import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> history = new ArrayList<>(10);
    private static final int HISTORY_SIZE = 10;

    @Override
    public void add(Task task) {
        if (history.size() == HISTORY_SIZE) {
            history.remove(0);
        }
        history.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }

}