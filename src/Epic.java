import java.util.HashMap;

public class Epic extends Task{
    private HashMap<Integer, Subtask> subtasks;

    public Epic(String name, String description) {
        super(name, description, TaskStatus.TO_DO);

        subtasks = new HashMap<>();
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(HashMap<Integer, Subtask> subtasks) {
        this.subtasks = subtasks;
    }
}
