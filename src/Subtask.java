public class Subtask extends Task {
    int idEpic;

    public Subtask(String name, String description, TaskStatus status, int idEpic) {
        super(name, description, status);
        this.idEpic = idEpic;
    }

    @Override
    public String toString() {
        return "Subtask{" +
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