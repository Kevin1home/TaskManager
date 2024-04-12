package tasks;

public class Subtask extends Task {
    private final int idEpic;

    public Subtask(String name, String description, TaskStatus status, int idEpic) {
        super(name, description, status);
        this.idEpic = idEpic;
        this.type = TaskType.SUBTASK;
    }

    public Subtask(String name, String description, int idEpic) {
        super(name, description);
        this.idEpic = idEpic;
        this.type = TaskType.SUBTASK;
    }

    public Subtask(String name, String description, String startTime, long durationMinutes, int idEpic) {
        super(name, description, startTime, durationMinutes);
        this.idEpic = idEpic;
        this.type = TaskType.SUBTASK;
    }

    public Subtask(String name, String description, TaskStatus status, String startTime,
                   long durationMinutes, int idEpic) {
        super(name, description, status, startTime, durationMinutes);
        this.idEpic = idEpic;
        this.type = TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        return "tasks.Subtask{" +
                "name='" + this.getName() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", id=" + this.getId() +
                ", status='" + this.getStatus() + '\'' +
                ", idEpic=" + idEpic +
                '}';
    }

    public int getIdEpic() {
        return idEpic;
    }

}