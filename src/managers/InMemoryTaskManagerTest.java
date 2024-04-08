package managers;

import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    protected void beforeEach() throws Exception {
        taskManager = new InMemoryTaskManager();
        taskManager.deleteAllTasksAllTypes();
        InMemoryTaskManager.nextId = 1;
        InMemoryTaskManager.historyManager = new InMemoryHistoryManager();
        initTasks();
    }

}