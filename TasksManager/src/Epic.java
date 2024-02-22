import java.util.ArrayList;

public class Epic extends Task {
    ArrayList<Subtask> subtasks = new ArrayList<>();
    public Epic(String name, String description, String status) {
        super(name, description, status);
    }
    @Override
    public String toString() {
        return "Epic{" +
                "name='" + this.getName() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", id=" + this.getId() +
                ", status='" + this.getStatus() + '\'' +
                '}';
    }

    public void printSubtasks() {
        System.out.println("subtasks = " + subtasks);
    }

}
