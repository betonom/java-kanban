import com.github.betonom.java_kanban.entities.Epic;
import com.github.betonom.java_kanban.entities.Subtask;
import com.github.betonom.java_kanban.entities.Task;
import com.github.betonom.java_kanban.entities.TaskStatus;
import com.github.betonom.java_kanban.managers.inmemory.Managers;
import com.github.betonom.java_kanban.managers.TaskManager;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("Сделал дело...", "Сделал дело - гуляй смело");
        Task task2 = new Task("Без труда...", "Без труда не выловишь и рыбку из пруда");

        taskManager.createNewTask(task1);
        taskManager.createNewTask(task2);

        Epic epic1 = new Epic("Переезд", "Перевезти все вещи");
        taskManager.createNewEpic(epic1);

        Subtask subtask1 = new Subtask("Диван", "Перевезти диван", epic1.getId());
        Subtask subtask2 = new Subtask("Кровать", "Перевезти кровать", epic1.getId());
        taskManager.createNewSubtask(subtask1);
        taskManager.createNewSubtask(subtask2);


        Epic epic2 = new Epic("Покушать", "Покушать надо");
        taskManager.createNewEpic(epic2);

        Subtask subtask11 = new Subtask("Овощи", "Обязательно овощей", epic2.getId());
        taskManager.createNewSubtask(subtask11);


        Task task1c = new Task("Сделал дело...", "Сделал дело - гуляй смело");
        task1c.setStatus(TaskStatus.IN_PROGRESS);
        task1c.setId(task1.getId());
        taskManager.updateTask(task1c);

        Subtask subtask1c = new Subtask("Диван", "Перевезти диван", epic1.getId());
        subtask1c.setStatus(TaskStatus.DONE);
        subtask1c.setId(subtask1.getId());
        taskManager.updateSubtask(subtask1c);

        Subtask subtask2c = new Subtask("Кровать", "Перевезти кровать", epic1.getId());
        subtask2c.setStatus(TaskStatus.DONE);
        subtask2c.setId(subtask2.getId());
        taskManager.updateSubtask(subtask2c);

        taskManager.getTaskById(1);
        taskManager.getEpicById(3);
        taskManager.getSubtaskById(4);
        taskManager.getSubtaskById(4);
        taskManager.getSubtaskById(4);
        taskManager.getSubtaskById(4);
        taskManager.getSubtaskById(4);
        taskManager.getSubtaskById(4);
        taskManager.getSubtaskById(4);
        taskManager.getSubtaskById(4);
        taskManager.getSubtaskById(4);

        ArrayList<Task> history = taskManager.getHistory();

        Task task2c = new Task("Без труда...", "Без труда не выловишь и рыбку из пруда");
        task2c.setStatus(TaskStatus.DONE);
        task2c.setId(task2.getId());
        taskManager.updateTask(task2c);
    }
}
