package main_manager;

import api.KVServer;
import managers.*;

import java.nio.file.Paths;

import static managers.FileBackedTaskManager.loadFromFile;
import static managers.HttpTaskManager.load;


public class Managers {

    public static TaskManager getDefault() {
        return load(KVServer.PORT);
    }

    public static TaskManager getDefaultWithSavingToFile() {
        return loadFromFile(Paths.get("resources\\Saves.csv"));
    }

    public static TaskManager getDefaultWithoutSaving() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

}