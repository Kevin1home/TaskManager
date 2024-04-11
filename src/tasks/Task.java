package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class Task {
    private String name;
    private String description;
    private int id = 0;
    private TaskStatus status;
    protected TaskType type;
    protected LocalDateTime startTime;
    protected Duration duration;
    protected static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");

    public Task(String name, String description, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.type = TaskType.TASK;
        this.startTime = null;
        this.duration = null;
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW;
        this.type = TaskType.TASK;
        this.startTime = null;
        this.duration = null;
    }

    public Task(String name, String description, String startTime, long durationMinutes) {
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW;
        this.type = TaskType.TASK;
        setStartTime(startTime);
        setDuration(durationMinutes);

    }

    @Override
    public String toString() {
        return "tasks.Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status='" + status + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskType getType() {
        return type;
    }

    public Optional<LocalDateTime> getStartTime() {
        return Optional.ofNullable(startTime);
    }

    public void setStartTime(String newStartTime) {
        if (newStartTime != null) {
            this.startTime = LocalDateTime.parse(newStartTime, dateTimeFormatter);
        }
    }

    public Optional<Duration> getDuration() {
        return Optional.ofNullable(duration);
    }

    public void setDuration(long newDuration) {
        if (newDuration >= 0) {
            this.duration = Duration.ofMinutes(newDuration);
        } else {
            this.duration = null;
        }
    }

    public Optional<LocalDateTime> getEndTime(){
        if (getStartTime().isPresent() && getDuration().isPresent()) {
            return Optional.of(startTime.plus(duration));
        }
        return Optional.empty();
    }

}