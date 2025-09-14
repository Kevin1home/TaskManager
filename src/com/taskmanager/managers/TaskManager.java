package com.taskmanager.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import com.taskmanager.tasks.Epic;
import com.taskmanager.tasks.Subtask;
import com.taskmanager.tasks.Task;

/**
 * The main interface for managing tasks, epics, and subtasks.
 * Provides creation, update, retrieval, and deletion operations,
 * as well as support for task prioritization and history management.
 */
public interface TaskManager {

    HistoryManager getHistoryManager();

    void createTask(Task task);

    void createEpic(Epic epic);

    void createSubtask(Subtask subtask);

    int generateId();

    ArrayList<Subtask> getEpicSubtasks(Epic epic);

    void checkStatusEpic(Epic epic);

    void updateUsualTask(Task task, int id);

    void updateEpic(Epic epic, int id);

    void updateSubtask(Subtask subtask, int id);

    Task getUsualTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    HashMap<Integer, Task> getTasks();

    HashMap<Integer, Epic> getEpics();

    HashMap<Integer, Subtask> getSubtasks();

    TreeSet<Task> getPrioritizedTasks();

    boolean isValidDateTime(Task task);

    void deleteTaskById(int id);

    void deleteEpicById(int id);

    void deleteSubtaskById(int id);

    void deleteAllUsualTasks();

    void deleteAllEpics();

    void deleteAllSubtasks();

    void deleteAllTasksAllTypes();

    void setNextId(int nextId);

    void setHistoryManager(HistoryManager historyManager);
}
