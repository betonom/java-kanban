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

        Subtask subtasks = new Subtask("name 2", "description 2", newEpic.getId());
        subtasks.setId(newSubtask.getId());
        subtasks.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtasks);

        Assertions.assertEquals(TaskStatus.DONE, newEpic.getStatus(),
                "Статус не DONE при наличии статусов DONE у подзадач");
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
        taskManager.createNewSubtask(subtask2);

        Assertions.assertEquals(LocalDateTime.of(2020, 1, 1, 1, 6), newEpic.getEndTime(),
                "Неправильный расчет поля endTime в Epic");
    }



}