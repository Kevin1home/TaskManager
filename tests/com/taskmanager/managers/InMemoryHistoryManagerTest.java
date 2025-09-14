package com.taskmanager.managers;

import org.junit.jupiter.api.BeforeEach;


// TODO: add tests
class InMemoryHistoryManagerTest extends HistoryManagerTest<InMemoryHistoryManager> {

    @BeforeEach
    protected void beforeEach() {
        historyManager = new InMemoryHistoryManager();
        initTasks();
    }

}