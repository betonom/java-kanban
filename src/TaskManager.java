import java.util.HashMap;

public class TaskManager {
    private HashMap<Integer, Task> tasks_id;
    private HashMap<Integer, Epic> epics_id;
    private HashMap<Integer, Subtask> subtasks_id;

    public TaskManager(){
        tasks_id = new HashMap<>();
        epics_id = new HashMap<>();
        subtasks_id = new HashMap<>();
    }




}
