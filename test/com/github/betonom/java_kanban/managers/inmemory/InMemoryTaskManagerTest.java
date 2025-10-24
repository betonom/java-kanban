package com.github.betonom.java_kanban.managers.inmemory;

import com.github.betonom.java_kanban.entities.Epic;
import com.github.betonom.java_kanban.entities.Subtask;
import com.github.betonom.java_kanban.entities.Task;
import com.github.betonom.java_kanban.entities.TaskStatus;
import com.github.betonom.java_kanban.managers.Managers;
import com.github.betonom.java_kanban.managers.TaskManager;
import com.github.betonom.java_kanban.managers.TaskManagerTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

public class InMemoryTaskManagerTest extends TaskManagerTest<TaskManager> {

    @BeforeEach
    protected void beforeEach() {
        taskManager = Managers.getDefault();

        newTask = new Task("name", "description");
        newTask.setDuration(Duration.ofMinutes(5));
        newTask.setStartTime(LocalDateTime.of(2000, 1, 1, 2, 1));
        taskManager.createNewTask(newTask);

        newEpic = new Epic("name", "description");
        taskManager.createNewEpic(newEpic);

        newSubtask = new Subtask("name", "description", newEpic.getId());
        newSubtask.setDuration(Duration.ofMinutes(5));
        newSubtask.setStartTime(LocalDateTime.of(2000, 1, 1, 1, 1));
        taskManager.createNewSubtask(newSubtask);
    }

    @Test
    void shouldGetIntoHistoryWhenUsedGetTaskById() {

        taskManager.getTaskById(newTask.getId());

        Assertions.assertEquals(newTask, taskManager.getHistory().get(0),
                "Задачи не совпадают или задача не была добавлена");
    }

    @Test
    void shouldGetIntoHistoryWhenUsedGetEpicById() {

        taskManager.getEpicById(newEpic.getId());

        Assertions.assertEquals(newEpic, taskManager.getHistory().get(0),
                "Эпики не совпадают или эпик не был добавлен");
    }

    @Test
    void shouldNotSaveSubtaskInEpicWhenSubtaskIsTheEpic() {
        Subtask newSubtask = new Subtask("name", "description", newEpic.getId());
        taskManager.createNewSubtask(newSubtask);

        Epic updatedEpic = new Epic("nameUpdated", "descriptionUpdated");
        updatedEpic.setId(newEpic.getId());
        for (Integer subtaskId : newEpic.getSubtasksId()) {
            updatedEpic.getSubtasksId().add(subtaskId);
        }
        updatedEpic.getSubtasksId().add(newEpic.getId());
        taskManager.updateEpic(updatedEpic);

        Epic savedEpic = taskManager.getEpicById(updatedEpic.getId());

        Assertions.assertNotEquals("nameUpdated", savedEpic.getName(),
                "Эпик не может быть добавлен в себя же в качестве подзадачи");
    }

    @Test
    void shouldGetIntoHistoryWhenUsedGetSubtaskById() {

        taskManager.getSubtaskById(newSubtask.getId());

        Assertions.assertEquals(newSubtask, taskManager.getHistory().get(0),
                "Подзадачи не совпадают или подзадача не была добавлена");
    }


    @Test
    void shouldNotSaveSubtaskWhenSubtaskIdEqualsEpicId() {
        Subtask newSubtask = new Subtask("name", "description", newEpic.getId());
        taskManager.createNewSubtask(newSubtask);

        Subtask updatedSubtask = new Subtask("nameUpdated",
                "descriptionUpdated", newSubtask.getId());
        updatedSubtask.setId(newSubtask.getId());
        taskManager.updateSubtask(updatedSubtask);

        Subtask savedSubtask = taskManager.getSubtaskById(updatedSubtask.getId());

        Assertions.assertEquals("name", savedSubtask.getName(),
                "Подзадача не может быть собственным эпиком");
    }

    @Test
    void shouldBeToDoWhenTaskCreated() {
        Assertions.assertEquals(TaskStatus.TO_DO, newTask.getStatus(),
                "При создании присваивается не статус TO_DO");
    }

    @Test
    void shouldEpicStatusBeInProgressWhenSubtasksStatusIsDifferent() {
        Subtask subtask1 = new Subtask("name 1", "description 1", newEpic.getId());
        Subtask subtask2 = new Subtask("name 2", "description 2", newEpic.getId());
        taskManager.createNewSubtask(subtask1);
        taskManager.createNewSubtask(subtask2);

        Assertions.assertEquals(TaskStatus.TO_DO, newEpic.getStatus(),
                "Статус не TO_DO при наличии всех подзадач со статусом TO_DO");

        Subtask subtask1c = new Subtask("name 1", "description 1", newEpic.getId());
        subtask1c.setId(subtask1.getId());
        subtask1c.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask1c);

        Assertions.assertEquals(TaskStatus.IN_PROGRESS, newEpic.getStatus(),
                "Статус не IN_PROGRESS при наличии разных статусов у подзадач");
    }

    @Test
    void shouldEpicStatusBeDoneWhenSubtasksStatusIsDone() {
        Subtask subtask1 = new Subtask("name 1", "description 1", newEpic.getId());
        taskManager.createNewSubtask(subtask1);


        Subtask subtask1c = new Subtask("name 1", "description 1", newEpic.getId());
        subtask1c.setId(subtask1.getId());
        subtask1c.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask1c);

        Subtask subtaskc = new Subtask("name 2", "description 2", newEpic.getId());
        subtaskc.setId(newSubtask.getId());
        subtaskc.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtaskc);

        Assertions.assertEquals(TaskStatus.DONE, newEpic.getStatus(),
                "Статус не DONE при наличии статусов DONE у подзадач");
    }

    @Test
    void shouldGetDifferentTypeOfTasksIntoHistory() {

        taskManager.getTaskById(newTask.getId());
        taskManager.getEpicById(newEpic.getId());
        taskManager.getSubtaskById(newSubtask.getId());

        Assertions.assertEquals(3, taskManager.getHistory().size(),
                "Задачи не добавились");
        Assertions.assertEquals(newTask, taskManager.getHistory().get(0),
                "Задачи не совпадают или задача не была добавлена");
        Assertions.assertEquals(newEpic, taskManager.getHistory().get(1),
                "Эпики не совпадают или эпик не был добавлен");
        Assertions.assertEquals(newSubtask, taskManager.getHistory().get(2),
                "Подзадачи не совпадают или подзадача не была добавлена");
    }

    @Test
    void shouldEpicDurationBeTheSumOfSubtasksDuration() {
        Subtask subtask1 = new Subtask("name 1", "description 1", newEpic.getId());
        subtask1.setDuration(Duration.ofMinutes(5));
        taskManager.createNewSubtask(subtask1);

        Assertions.assertEquals(Duration.ofMinutes(10), newEpic.getDuration(),
                "Неправильный расчет поля duration в Epic");
    }

    @Test
    void shouldEpicStartTimeBeMinOfSubtasksStartTimeAndIgnoreSubtasksStartTimeOfNull() {
        Subtask subtask1 = new Subtask("name 1", "description 1", newEpic.getId());
        subtask1.setStartTime(LocalDateTime.of(2020, 1, 1, 1, 1));
        taskManager.createNewSubtask(subtask1);

        Subtask subtask2 = new Subtask("name 2", "description 2", newEpic.getId());
        taskManager.createNewSubtask(subtask1);

        Assertions.assertEquals(LocalDateTime.of(2000, 1, 1, 1, 1), newEpic.getStartTime(),
                "Неправильный расчет поля startTime в Epic");
    }

    @Test
    void shouldEpicEndTimeBeMaxOfSubtasksStartTimePlusDurationAndIgnoreSubtasksStartTimeOfNull() {
        Subtask subtask1 = new Subtask("name 1", "description 1", newEpic.getId());
        subtask1.setStartTime(LocalDateTime.of(2020, 1, 1, 1, 1));
        subtask1.setDuration(Duration.ofMinutes(5));
        taskManager.createNewSubtask(subtask1);

        Subtask subtask2 = new Subtask("name 2", "description 2", newEpic.getId());
        taskManager.createNewSubtask(subtask1);

        Assertions.assertEquals(LocalDateTime.of(2020, 1, 1, 1, 6), newEpic.getEndTime(),
                "Неправильный расчет поля endTime в Epic");
    }

    @Test
    void shouldNotCreateOfUpdateTaskOrSubtaskIfThereIsACross() {
        Task task = new Task("taskName", "taskDesc");
        task.setId(15);
        task.setStartTime(LocalDateTime.of(2000, 1, 1, 2, 5));
        task.setDuration(Duration.ofMinutes(10));
        taskManager.createNewTask(task);

        Assertions.assertNull(taskManager.getTaskById(task.getId()),
                "Задача добавилась с пересечением");

        Task updatedTask = new Task("updatedTask", "updatedTaskDesc");
        updatedTask.setId(newTask.getId());
        updatedTask.setStartTime(LocalDateTime.of(2000, 1, 1, 1, 4));
        updatedTask.setDuration(Duration.ofMinutes(5));
        taskManager.updateTask(updatedTask);

        Assertions.assertNotEquals("updatedTask", taskManager.getTaskById(newTask.getId()).getName(),
                "Задача обновилась с пересечением");

        Subtask subtask = new Subtask("subtaskName", "subtaskDesc", newEpic.getId());
        subtask.setId(20);
        subtask.setStartTime(LocalDateTime.of(2000, 1, 1, 2, 5));
        subtask.setDuration(Duration.ofMinutes(10));
        taskManager.createNewSubtask(subtask);

        Assertions.assertNull(taskManager.getSubtaskById(subtask.getId()),
                "Подзадача добавилась с пересечением");

        Task updatedSubtask = new Subtask("updatedSubtask", "updatedSubtaskDesc", newEpic.getId());
        updatedSubtask.setId(newSubtask.getId());
        updatedSubtask.setStartTime(LocalDateTime.of(2000, 1, 1, 1, 4));
        updatedSubtask.setDuration(Duration.ofMinutes(5));
        taskManager.updateTask(updatedSubtask);

        Assertions.assertNotEquals("updatedTask", taskManager.getTaskById(newTask.getId()).getName(),
                "Подзадача обновилась с пересечением");
    }

}