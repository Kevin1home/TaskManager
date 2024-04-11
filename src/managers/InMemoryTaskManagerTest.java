package managers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    protected void beforeEach() {
        taskManager = new InMemoryTaskManager();
        initTasks();
    }

    @AfterEach
    protected void afterEach() {
        taskManager.deleteAllTasksAllTypes();
        InMemoryTaskManager.nextId = 1;
        InMemoryTaskManager.historyManager = new InMemoryHistoryManager();
    }

}