import java.util.Objects;

public class Task {
    protected String name;
    protected String description;
    protected int id;
    protected TaskStatus status;

    public Task (String name, String description, TaskStatus status){
        this.name = name;
        this.description = description;
        this.id = TaskManager.taskCounter;
        this.status = status;

        TaskManager.taskCounter++;

    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status);
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null) return false;
        if(this.getClass() != obj.getClass()) return false;

        Task task = (Task) obj;
        return Objects.equals(this.name, task.name) &&
                Objects.equals(this.description, task.description) &&
                (this.id == task.id) &&
                Objects.equals(this.status, task.status);
    }

    public int getId() {
        return id;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskStatus getStatus(){
        return this.status;
    }
}
