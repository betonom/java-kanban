public class Subtask extends Task{
    private int epic_id;

    public Subtask(String name, String description, TaskStatus status, int epic_id) {
        super(name, description, status);

        this.epic_id = epic_id;
    }

    public int getEpic_id() {
        return epic_id;
    }


}
