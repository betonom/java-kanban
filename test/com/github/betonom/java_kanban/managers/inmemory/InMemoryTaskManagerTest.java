package com.github.betonom.java_kanban.managers.inmemory;

import com.github.betonom.java_kanban.entities.Epic;
import com.github.betonom.java_kanban.entities.Subtask;
import com.github.betonom.java_kanban.entities.Task;
import com.github.betonom.java_kanban.entities.TaskStatus;
import com.github.betonom.java_kanban.managers.Managers;
import com.github.betonom.java_kanban.managers.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

class InMemoryTaskManagerTest {

    static TaskManager taskManager;
    static Task newTask;
    static Epic newEpic;
    static Subtask newSubtask;

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();

        newTask = new Task("name", "description");
        taskManager.createNewTask(newTask);

        newEpic = new Epic("name", "description");
        taskManager.createNewEpic(newEpic);

        newSubtask = new Subtask("name", "description", newEpic.getId());
        newSubtask.setDuration(Duration.ofMinutes(5));
        newSubtask.setStartTime(LocalDateTime.of(2000, 1, 1, 1, 1));
        taskManager.createNewSubtask(newSubtask);
    }

    //Тесты Task

    @Test
    void shouldGetIntoHistoryWhenUsedGetTaskById() {

        taskManager.getTaskById(newTask.getId());

        Assertions.assertEquals(newTask, taskManager.getHistory().get(0),
                "Задачи не совпадают или задача не была добавлена");
    }

    @Test
    void createNewTask() {

        Task savedTask = taskManager.getTaskById(newTask.getId());

        Assertions.assertNotNull(savedTask, "Задача не найдена");
        Assertions.assertEquals(newTask, savedTask, "Задачи не совпадают");

        ArrayList<Task> tasks = taskManager.getTasksList();

        Assertions.assertNotNull(tasks, "Задачи не сохраняются");
        Assertions.assertEquals(newTask, tasks.get(0), "Задачи не совпадают");

    }

    @Test
    void updateTask() {

        Task updatedTask = new Task("nameUpdated", "descriptionUpdated");
        updatedTask.setId(newTask.getId());
        taskManager.updateTask(updatedTask);

        Task savedTask = taskManager.getTaskById(newTask.getId());

        Assertions.assertNotNull(savedTask, "Задача не найдена");
        Assertions.assertEquals(updatedTask.getName(), savedTask.getName(), "Имена не совпадают");
        Assertions.assertEquals(updatedTask.getDescription(), savedTask.getDescription(),
                "Описания не совпадают");

    }

    @Test
    void removeTaskById() {

        Task savedTask = taskManager.getTaskById(newTask.getId());

        Assertions.assertNotNull(savedTask, "Задача не найдена");

        taskManager.removeTaskById(newTask.getId());

        savedTask = taskManager.getTaskById(newTask.getId());

        Assertions.assertNull(savedTask, "Задача не удалена");
    }

    //Тесты Epic

    @Test
    void shouldGetIntoHistoryWhenUsedGetEpicById() {

        taskManager.getEpicById(newEpic.getId());

        Assertions.assertEquals(newEpic, taskManager.getHistory().get(0),
                "Эпики не совпадают или эпик не был добавлен");
    }

    @Test
    void createNewEpic() {
        Epic savedEpic = taskManager.getEpicById(newEpic.getId());

        Assertions.assertNotNull(savedEpic, "Задача не найдена");
        Assertions.assertEquals(newEpic, savedEpic, "Задачи не совпадают");

        ArrayList<Epic> epics = taskManager.getEpicsList();

        Assertions.assertNotNull(epics, "Задачи не сохраняются");
        Assertions.assertEquals(newEpic, epics.get(0), "Задачи не совпадают");
    }

    @Test
    void updateEpic() {
        Subtask subtask1 = new Subtask("name 1", "description 1", newEpic.getId());
        Subtask subtask2 = new Subtask("name 2", "description 2", newEpic.getId());
        taskManager.createNewSubtask(subtask1);
        taskManager.createNewSubtask(subtask2);

        Assertions.assertEquals(3, newEpic.getSubtasksId().size(),
                "Подзадачи не привязываются к эпику");

        Epic updatedEpic = new Epic("nameUpdated", "descriptionUpdated");
        updatedEpic.setId(newEpic.getId());
        for (Integer subtaskId : newEpic.getSubtasksId()) {
            updatedEpic.getSubtasksId().add(subtaskId);
        }
        taskManager.updateEpic(updatedEpic);

        Epic savedEpic = taskManager.getEpicById(newEpic.getId());

        Assertions.assertNotNull(savedEpic, "Задача не найдена");
        Assertions.assertEquals(updatedEpic.getName(), savedEpic.getName(), "Имена не совпадают");
        Assertions.assertEquals(updatedEpic.getDescription(), savedEpic.getDescription(),
                "Описания не совпадают");
        Assertions.assertEquals(updatedEpic.getSubtasksId(), savedEpic.getSubtasksId(),
                "Привязанные сабтаски не совпадают");
    }

    @Test
    void removeEpicById() {
        Subtask subtask1 = new Subtask("name 1", "description 1", newEpic.getId());
        Subtask subtask2 = new Subtask("name 2", "description 2", newEpic.getId());
        taskManager.createNewSubtask(subtask1);
        taskManager.createNewSubtask(subtask2);

        taskManager.removeEpicById(newEpic.getId());

        Assertions.assertNull(taskManager.getEpicById(newEpic.getId()), "Эпик не удалён");
        Assertions.assertNull(taskManager.getSubtaskById(subtask1.getId()),
                "Подзадачи не удаляются вместе с эпиком 1");
        Assertions.assertNull(taskManager.getSubtaskById(subtask2.getId()),
                "Подзадачи не удаляются вместе с эпиком 2");
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

    //Тесты Subtask

    @Test
    void shouldGetIntoHistoryWhenUsedGetSubtaskById() {

        taskManager.getSubtaskById(newSubtask.getId());

        Assertions.assertEquals(newSubtask, taskManager.getHistory().get(0),
                "Подзадачи не совпадают или подзадача не была добавлена");
    }

    @Test
    void createNewSubtask() {
        Subtask savedSubtask = taskManager.getSubtaskById(newSubtask.getId());

        Assertions.assertNotNull(savedSubtask, "Задача не найдена");
        Assertions.assertEquals(newSubtask, savedSubtask, "Задачи не совпадают");

        ArrayList<Subtask> subtasks = taskManager.getSubtasksList();

        Assertions.assertNotNull(subtasks, "Задачи не сохраняются");
        Assertions.assertEquals(newSubtask, subtasks.get(0), "Задачи не совпадают");

        ArrayList<Integer> subtasksId = newEpic.getSubtasksId();

        Assertions.assertNotNull(subtasksId,
                "Подзадача не добавилась в соответствующий эпик");

        Subtask subtaskFromEpic = taskManager.getSubtaskById(subtasksId.get(0));

        Assertions.assertEquals(newSubtask, subtaskFromEpic,
                "Подзадача и подзадача, добавленная в эпик, не совпадают");
    }

    @Test
    void updateSubtask() {
        Subtask updatedSubtask = new Subtask("nameUpdated",
                "descriptionUpdated", newSubtask.getEpicId());
        updatedSubtask.setId(newSubtask.getId());
        taskManager.updateSubtask(updatedSubtask);

        Subtask savedSubtask = taskManager.getSubtaskById(newSubtask.getId());


        Assertions.assertNotNull(savedSubtask, "Задача не найдена");
        Assertions.assertEquals(updatedSubtask.getName(), savedSubtask.getName(),
                "Имена не совпадают");
        Assertions.assertEquals(updatedSubtask.getDescription(), savedSubtask.getDescription(),
                "Описания не совпадают");
        Assertions.assertEquals(updatedSubtask.getEpicId(), savedSubtask.getEpicId(),
                "Привязанные эпики не совпадают");
    }

    @Test
    void removeSubtaskById() {
        taskManager.removeSubtaskById(newSubtask.getId());

        Assertions.assertNull(taskManager.getSubtaskById(newSubtask.getId()),
                "Подзадача не удалена");
        Assertions.assertEquals(0, newEpic.getSubtasksId().size(),
                "Подзадача не удалена из эпика");
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

    //Общие тесты

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

}