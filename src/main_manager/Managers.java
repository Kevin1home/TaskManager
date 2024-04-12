package main_manager;

import managers.*;

import java.nio.file.Paths;

import static managers.FileBackedTaskManager.loadFromFile;


public class Managers {

    public static TaskManager getDefault() {
        return loadFromFile(Paths.get("resources\\Saves.csv"));
    }

    public static TaskManager getDefaultWithoutSaving() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

}