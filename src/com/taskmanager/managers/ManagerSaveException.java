package com.taskmanager.managers;

/**
 * Custom runtime exception thrown when saving or loading tasks fails.
 */
public class ManagerSaveException extends RuntimeException {

    public ManagerSaveException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
