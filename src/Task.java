public class Task {
    protected String name;
    protected String description;
    protected int id;
    protected TaskStatus status;

    public Task (String name, String description, int id){
        this.name = name;
        this.description = description;
        this.id = id;
        status = TaskStatus.TO_DO;
    }
}
