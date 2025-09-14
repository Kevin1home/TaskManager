package com.taskmanager.managers;

import com.taskmanager.api.KVTaskClient;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.taskmanager.tasks.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * HttpTaskManager extends FileBackedTaskManager and adds persistence using KVServer.
 * <p>
 * Tasks, epics, subtasks, and history are saved to the server via {@link KVTaskClient}.
 * On load, data is reconstructed from the server state.
 * </p>
 */
public class HttpTaskManager extends FileBackedTaskManager {

    private static final Logger LOGGER = Logger.getLogger(HttpTaskManager.class.getName());
    static final Gson gson = new Gson();
    public static KVTaskClient kvTaskClient = null;


    @Override
    protected void save() {
        if (kvTaskClient == null) {
            LOGGER.warning("KVTaskClient not initialized. Skipping save.");
            return;
        }

        LOGGER.info("Saving data to KVServer...");

        if (!tasks.isEmpty()) {
            for (Task task : tasks.values()) {
                String jsonTask = gson.toJson(task);
                kvTaskClient.put("ID_" + task.getId(), jsonTask);
                LOGGER.info("Usual task successfully saved to KVServer: " + jsonTask);
            }
        }

        if (!epics.isEmpty()) {
            for (Epic epic : epics.values()) {
                String jsonEpic = gson.toJson(epic);
                kvTaskClient.put("ID_" + epic.getId(), jsonEpic);
                LOGGER.info("Epic successfully saved to KVServer: " + jsonEpic);
            }
        }

        if (!subtasks.isEmpty()) {
            for (Subtask subtask : subtasks.values()) {
                String jsonSubtask = gson.toJson(subtask);
                kvTaskClient.put("ID_" + subtask.getId(), jsonSubtask);
                LOGGER.info("Subtask successfully saved to KVServer: " + jsonSubtask);
            }
        }

        if (!historyManager.getHistory().isEmpty()) {
            String jsonHistory = gson.toJson(historyManager.getHistory());
            kvTaskClient.put("History", jsonHistory);

            LOGGER.info("Saved: " + jsonHistory);

        }

        String jsonId = gson.toJson(nextId);
        kvTaskClient.put("NextId", jsonId);
        LOGGER.info("Last saved ID: " + jsonId);
    }

    /**
     * Loads task manager data from KVServer and reconstructs tasks, epics, subtasks, and history.
     *
     * @param kvServerPort port where KVServer is running
     * @return HttpTaskManager instance
     */
    public static HttpTaskManager load(int kvServerPort) {
        if (kvTaskClient == null) {
            kvTaskClient = new KVTaskClient(kvServerPort);
            LOGGER.info("KVTaskClient initialized on port " + kvServerPort);
        }

        String jsonNextId = kvTaskClient.load("NextId");

        if (!jsonNextId.isBlank() && !jsonNextId.equals("1")) {
            JsonElement jsonElementNextId = JsonParser.parseString(jsonNextId);
            nextId = jsonElementNextId.getAsInt();

            for (int i = 1; i < nextId; i++) {
                String jsonElementString = kvTaskClient.load("ID_" + i);

                if (!jsonElementString.isBlank()) {
                    JsonElement jsonElement = JsonParser.parseString(jsonElementString);
                    LOGGER.info("Received from key ID_" + i + ": " + jsonElement);

                    if (!jsonElement.isJsonObject()) { // проверяем, точно ли мы получили JSON-объект
                        LOGGER.warning("Received answer is not object.");
                        LOGGER.warning("Not awaited server answer.");
                        LOGGER.warning("Data loading is interrupted.");

                        return new HttpTaskManager();
                    }

                    JsonObject jsonObjectSomeTask = jsonElement.getAsJsonObject();
                    String taskType = jsonObjectSomeTask.get("type").getAsString();

                    switch (taskType) {
                        case "TASK":
                            Task task = gson.fromJson(jsonObjectSomeTask, Task.class);
                            tasks.put(task.getId(), task);
                            break;
                        case "EPIC":
                            Epic epic = gson.fromJson(jsonObjectSomeTask, Epic.class);
                            epics.put(epic.getId(), epic);
                            break;
                        case "SUBTASK":
                            Subtask subtask = gson.fromJson(jsonObjectSomeTask, Subtask.class);
                            subtasks.put(subtask.getId(), subtask);
                            break;
                        default:
                            LOGGER.warning("Error! Received incorrect task type.");
                            LOGGER.warning("Data loading is interrupted.");
                            return new HttpTaskManager();
                    }
                }
            }

            String jsonHistory = kvTaskClient.load("History");

            if (!jsonHistory.isBlank()) {
                List<Task> history = gson.fromJson(jsonHistory, new TypeToken<ArrayList<Task>>() {
                }.getType());

                for (Task task : history) {
                    historyManager.add(task);
                }
            }
        }
        return new HttpTaskManager();
    }
}
