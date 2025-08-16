public class Subtask extends Task{
    private int epic_id;

    public Subtask(String name, String description, int id, int epic_id) {
        super(name, description, id);

        this.epic_id = epic_id;
    }
}
