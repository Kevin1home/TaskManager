package com.taskmanager.managers;

import com.taskmanager.tasks.*;

import java.util.List;

/**
 * HistoryManager provides functionality for tracking recently viewed tasks.
 * <p>
 * It is responsible for:
 * <ul>
 *     <li>Adding tasks to the history of views</li>
 *     <li>Returning a list of viewed tasks</li>
 *     <li>Removing tasks from the history by ID</li>
 * </ul>
 * <p>
 */
public interface HistoryManager {

    void add(Task task);

    List<Task> getHistory();

    void remove(int id);

}
