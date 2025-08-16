import java.util.ArrayList;

public class Epic extends Task{
    private ArrayList<Integer> subtasks_id;

    public Epic(String name, String description, int id) {
        super(name, description, id);

        subtasks_id = new ArrayList<>();
    }
}
