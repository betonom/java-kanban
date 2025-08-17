import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("Сделал дело...", "Сделал дело - гуляй смело", TaskStatus.TO_DO);
        Task task2 = new Task("Без труда...", "Без труда не выловишь и рыбку из пруда", TaskStatus.TO_DO);

        taskManager.createNewTask(task1);
        taskManager.createNewTask(task2);

        Epic epic1 = new Epic("Переезд", "Перевезти все вещи");
        taskManager.createNewEpic(epic1);

        Subtask subtask1 = new Subtask("Диван", "Перевезти диван", TaskStatus.TO_DO, epic1.getId());
        Subtask subtask2 = new Subtask("Кровать", "Перевезти кровать", TaskStatus.TO_DO, epic1.getId());
        taskManager.createNewSubtask(subtask1);
        taskManager.createNewSubtask(subtask2);

        HashMap<Integer, Subtask> subtasks1 = new HashMap<>();
        subtasks1.put(subtask1.getId(), subtask1);
        subtasks1.put(subtask2.getId(), subtask2);
        epic1.setSubtasks(subtasks1);


        Epic epic2 = new Epic("Покушать", "Покушать надо");
        taskManager.createNewEpic(epic2);

        Subtask subtask11 = new Subtask("Овощи", "Обязательно овощей", TaskStatus.TO_DO, epic2.getId());
        taskManager.createNewSubtask(subtask11);

        HashMap<Integer, Subtask> subtasks11 = new HashMap<>();
        subtasks11.put(subtask11.getId(), subtask11);
        epic2.setSubtasks(subtasks11);

        task1.setStatus(TaskStatus.IN_PROGRESS);


        Task task1c = new Task("Сделал дело...", "Сделал дело - гуляй смело", TaskStatus.IN_PROGRESS);
        task1c.setId(task1.getId());
        taskManager.updateTask(task1c);

        Subtask subtask1c = new Subtask("Диван", "Перевезти диван", TaskStatus.DONE, epic1.getId());
        subtask1c.setId(subtask1.getId());
        taskManager.updateSubtask(subtask1c);

        Subtask subtask2c = new Subtask("Кровать", "Перевезти кровать", TaskStatus.DONE, epic1.getId());
        subtask2c.setId(subtask2.getId());
        taskManager.updateSubtask(subtask2c);



        Task task2c = new Task("Без труда...", "Без труда не выловишь и рыбку из пруда", TaskStatus.DONE);
        task2c.setId(task2.getId());
        taskManager.updateTask(task2c);






    }
}
