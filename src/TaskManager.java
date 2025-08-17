import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, Subtask> subtasks;

    public static int taskCounter = 1;

    public TaskManager(){
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    //Методы для Task

    public ArrayList<Task> getTasksList() {
        return (ArrayList<Task>) tasks.values();
    }

    public void clearTasks(){
        tasks.clear();
    }

    public Task getTaskById(int id){
        return tasks.get(id);
    }

    public void createNewTask(Task task){
        if(task == null){
            return;
        }
        tasks.put(task.getId(), task);
    }

    public void updateTask(Task task){
        if(task == null){
            return;
        }
        if(tasks.containsKey(task.getId())){
            tasks.put(task.getId(), task);
        }

    }

    public void removeTaskById(int id){
        tasks.remove(id);
    }

    //Методы для Epic

    public ArrayList<Epic> getEpicsList() {
        return (ArrayList<Epic>) epics.values();
    }

    public void clearEpics(){
        epics.clear();
    }

    public Epic getEpicById(int id){
        return epics.get(id);
    }

    public void createNewEpic(Epic epic){
        tasks.put(epic.getId(), epic);
    }

    public void updateEpic(Epic epic){
        if(epic == null){
            return;
        }
        if(epics.containsKey(epic.getId())){
            epic.setStatus(getStatusEpic(epic));
            epics.put(epic.getId(), epic);

        }

    }

    public void removeEpicById(int id){
        epics.remove(id);
    }

    private TaskStatus getStatusEpic(Epic epic){
        boolean allTODO = true;
        boolean allDONE = true;
        for(Subtask subtask : epic.getSubtasks()){
            TaskStatus subtaskStatus = subtask.getStatus();

            if(subtaskStatus == TaskStatus.IN_PROGRESS){
                return TaskStatus.IN_PROGRESS;
            }

            if(subtaskStatus != TaskStatus.TO_DO){
                allTODO = false;
            }

            if(subtaskStatus != TaskStatus.DONE){
                allDONE = false;
            }
        }

        if(allTODO){
            return TaskStatus.TO_DO;
        } else if(allDONE){
            return TaskStatus.DONE;
        } else {
            return TaskStatus.IN_PROGRESS;
        }

    }

    public ArrayList<Subtask> getEpicSubtasks(Epic epic) {
        return epic.getSubtasks();
    }

    //Методы для Subtask

    public ArrayList<Subtask> getSubtasksList() {
        return (ArrayList<Subtask>) subtasks.values();
    }

    public void clearSubtasks(){
        subtasks.clear();
    }

    public Subtask getSubtaskById(int id){
        return subtasks.get(id);
    }

    public void createNewSubtask(Subtask subtask){
        if(subtask == null){
            return;
        }
        tasks.put(subtask.getId(), subtask);
    }

    public void updateSubtask(Subtask subtask){
        if(subtask == null){
            return;
        }
        if(subtasks.containsKey(subtask.getId())){
            Epic epic = getEpicById(subtask.getEpic_id());
            epic.setStatus(getStatusEpic(epic));
            subtasks.put(subtask.getId(), subtask);
        }
    }

    public void removeSubtaskById(int id){
        subtasks.remove(id);
    }


}
