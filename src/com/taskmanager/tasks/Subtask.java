package com.taskmanager.tasks;

import java.util.logging.Logger;

/**
 * Represents a Subtask, which belongs to a specific Epic.
 * Inherits properties from {@link Task}.
 */
public class Subtask extends Task {
    private static final Logger logger = Logger.getLogger(Subtask.class.getName());
    private final int idEpic;

    public Subtask(String name, String description, TaskStatus status, int idEpic) {
        super(name, description, status);
        this.idEpic = idEpic;
        type = String.valueOf(TaskType.SUBTASK);
    }

    public Subtask(String name, String description, int idEpic) {
        super(name, description);
        this.idEpic = idEpic;
        type = String.valueOf(TaskType.SUBTASK);
    }

    public Subtask(String name, String description, String startTime, long durationMinutes, int idEpic) {
        super(name, description, startTime, durationMinutes);
        this.idEpic = idEpic;
        type = String.valueOf(TaskType.SUBTASK);
    }

    public Subtask(String name, String description, TaskStatus status, String startTime, long durationMinutes,
                   int idEpic) {
        super(name, description, status, startTime, durationMinutes);
        this.idEpic = idEpic;
        type = String.valueOf(TaskType.SUBTASK);
    }

    public int getIdEpic() {
        return idEpic;
    }

    @Override
    public String toString() {
        return "tasks.Subtask{" +
                "name='" + this.getName() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", id=" + this.getId() +
                ", status='" + this.getStatus() + '\'' +
                ", idEpic=" + idEpic +
                ", type='" + this.getType() + '\'' +
                ", startTime=" + this.getStartTime() + '\'' +
                ", duration=" + this.getDuration() + '\'' +
                ", endTime=" + this.getEndTime() + '\'' +
                '}';
    }
}
