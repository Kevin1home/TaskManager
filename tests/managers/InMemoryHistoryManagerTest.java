package managers;

import org.junit.jupiter.api.BeforeEach;

class InMemoryHistoryManagerTest extends HistoryManagerTest<InMemoryHistoryManager> {

    @BeforeEach
    protected void beforeEach() {
        historyManager = new InMemoryHistoryManager();
        initTasks();
    }

}