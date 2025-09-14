package com.taskmanager.api;

/**
 * Enumeration of all supported API endpoints for the Task Manager service.
 *
 * <p>This enum defines the set of endpoint operations (CRUD) for tasks, epics, and subtasks.
 * It is used for request routing, logging, validation and ensuring
 * type-safety when handling API calls.</p>
 */
public enum Endpoint {
    GET,
    GET_HISTORY,
    GET_TASK,
    GET_EPIC,
    GET_SUBTASK,
    GET_SUBTASK_EPIC_ID,
    GET_TASK_ID,
    GET_EPIC_ID,
    GET_SUBTASK_ID,
    POST_TASK,
    POST_EPIC,
    POST_SUBTASK,
    PUT_TASK_ID,
    PUT_EPIC_ID,
    PUT_SUBTASK_ID,
    DELETE,
    DELETE_TASK,
    DELETE_EPIC,
    DELETE_SUBTASK,
    DELETE_TASK_ID,
    DELETE_EPIC_ID,
    DELETE_SUBTASK_ID,
    UNKNOWN
}
