package com.taskmanager.main_manager;

import com.taskmanager.api.KVServer;
import com.taskmanager.managers.*;

import java.nio.file.Paths;

import static com.taskmanager.managers.FileBackedTaskManager.loadFromFile;
import static com.taskmanager.managers.HttpTaskManager.load;

/**
 * Managers is a factory class that provides pre-configured instances of TaskManager and HistoryManager.
 *
 * <p>It supports different types of TaskManager depending on storage strategy:</p>
 * <ul>
 *     <li>In-memory only</li>
 *     <li>File-backed persistence</li>
 *     <li>HTTP KVServer-backed persistence</li>
 * </ul>
 */
public class Managers {

    /**
     * Returns a default TaskManager backed by an HTTP KVServer.
     * This is suitable for distributed or networked storage.
     * @return TaskManager instance backed by KVServer.
     */
    public static TaskManager getDefault() {
        return load(KVServer.PORT);
    }

    /**
     * Returns a TaskManager with persistence to a local file.
     * Useful for testing or local storage.
     * @return File-backed TaskManager instance.
     */
    public static TaskManager getDefaultWithSavingToFile() {
        return loadFromFile(Paths.get("resources\\Saves.csv"));
    }

    /**
     * Returns an in-memory TaskManager without persistence.
     * Suitable for short-lived tasks or testing.
     * @return In-memory TaskManager instance.
     */
    public static TaskManager getDefaultWithoutSaving() {
        return new InMemoryTaskManager();
    }

    /**
     * Returns an in-memory HistoryManager.
     * @return HistoryManager instance.
     */
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
