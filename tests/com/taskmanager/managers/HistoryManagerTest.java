package com.taskmanager.managers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import com.taskmanager.tasks.Epic;
import com.taskmanager.tasks.Subtask;
import com.taskmanager.tasks.Task;
import com.taskmanager.tasks.TaskStatus;

/**
 * Abstract tests for implementations of {@link HistoryManager}.
 * <p>
 * This class verifies the history management functionality, including:
 * <ul>
 *     <li>Adding tasks, epics, and subtasks to history</li>
 *     <li>Preventing duplicates in history</li>
 *     <li>Removing tasks by ID from different positions in history</li>
 *     <li>Handling empty history correctly</li>
 * </ul>
 * <p>
 * Concrete subclasses should provide a specific {@code HistoryManager} implementation.
 * </p>
 */
abstract class HistoryManagerTest<T extends HistoryManager> {

    T historyManager;
    Task task1;
    Task task2;
    Epic epic;
    Subtask subtask;

    protected void initTasks() {
        task1 = new Task("Task", "DescrT", TaskStatus.NEW);
        task1.setId(1);
        task2 = new Task("Task", "DescrT", TaskStatus.NEW);
        task2.setId(2);
        epic = new Epic("Epic1", "DescrEp1");
        task2.setId(3);
        // For creating tests: subtask = new Subtask("Subtask2", "DescrSt2", TaskStatus.NEW, epic.getId());
        // For creating tests: subtask.setId(4);
    }

    @Test
    void shouldAddNewIdsToHistory() { // checking method add(Task task)
        historyManager.add(task1);
        historyManager.add(epic);
        subtask = new Subtask("Subtask2", "DescrSt2", TaskStatus.NEW, epic.getId());
        subtask.setId(4);
        historyManager.add(subtask);

        int expectedSize = 3;
        int actualSize = historyManager.getHistory().size();

        Assertions.assertEquals(expectedSize, actualSize);
    }

    @Test
    void shouldReturnOnly1IdIfDuplicates() { // checking method add(Task task)
        historyManager.add(task1); // +1
        historyManager.add(epic); // +1
        historyManager.add(task1); // +0

        int expectedSize = 2;
        int actualSize = historyManager.getHistory().size();

        Assertions.assertEquals(expectedSize, actualSize);
    }

    @Test
    void shouldReturnHistoryWith2Tasks() { // checking method getHistory()
        historyManager.add(task1);

        int expectedSize = 1;
        int actualSize = historyManager.getHistory().size();

        Assertions.assertEquals(expectedSize, actualSize);
    }

    @Test
    void shouldReturnEmptyHistory() { // checking method getHistory()

        int expectedSize = 0;
        int actualSize = historyManager.getHistory().size();

        Assertions.assertEquals(expectedSize, actualSize);
    }

    @Test
    void shouldReturn1IdInHistoryAfterAdding2AndRemove1() { // checking method remove(int id)
        historyManager.add(task1); // +1
        historyManager.add(task2); // +1
        historyManager.remove(task1.getId()); // -1

        int expectedSize = 1;
        int actualSize = historyManager.getHistory().size();

        Assertions.assertEquals(expectedSize, actualSize);
    }

    @Test
    void shouldRemoveIdFromMiddleOfHistory() { // checking method remove(int id)
        historyManager.add(task1); // +1
        historyManager.add(epic); // +1
        historyManager.add(task2); // +1
        historyManager.remove(epic.getId()); // -1

        boolean isTrue = historyManager.getHistory().get(0).equals(task1)
                && historyManager.getHistory().get(1).equals(task2);

        Assertions.assertTrue(isTrue);
    }

    @Test
    void shouldRemoveIdFromBeginOfHistory() { // checking method remove(int id)
        historyManager.add(task1); // +1
        historyManager.add(epic); // +1
        historyManager.add(task2); // +1
        historyManager.remove(task1.getId()); // -1

        boolean isTrue = historyManager.getHistory().get(0).equals(epic)
                && historyManager.getHistory().get(1).equals(task2);

        Assertions.assertTrue(isTrue);
    }

    @Test
    void shouldRemoveIdFromEndOfHistory() { // checking method remove(int id)
        historyManager.add(task1); // +1
        historyManager.add(epic); // +1
        historyManager.add(task2); // +1
        historyManager.remove(task2.getId()); // -1

        boolean isTrue = historyManager.getHistory().get(0).equals(task1)
                && historyManager.getHistory().get(1).equals(epic);

        Assertions.assertTrue(isTrue);
    }

    @Test
    void shouldReturn0IdInHistoryAfterAdding0AndRemove1() { // checking method remove(int id)
        historyManager.remove(task1.getId()); // 0 - 1 should be 0

        int expectedSize = 0;
        int actualSize = historyManager.getHistory().size();

        Assertions.assertEquals(expectedSize, actualSize);
    }
}
