package com.github.betonom.java_kanban.managers;

import com.github.betonom.java_kanban.entities.Epic;
import com.github.betonom.java_kanban.entities.Subtask;
import com.github.betonom.java_kanban.entities.Task;
import com.github.betonom.java_kanban.entities.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

abstract public class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    protected Task newTask;
    protected Epic newEpic;
    protected Subtask newSubtask;

    @BeforeEach
    protected abstract void beforeEach();

    // Тесты Task

    @Test
    void clearTasks() {
        taskManager.clearTasks();

        Assertions.assertEquals(0, taskManager.getTasksList().size(),
                "Задачи не удалились");
    }

    @Test
    void getTaskById() {
        Assertions.assertEquals(newTask, taskManager.getTaskById(newTask.getId()),
                "Задача не найдена");
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

    // Тесты Epic

    @Test
    void clearEpics() {
        taskManager.clearEpics();

        Assertions.assertEquals(0, taskManager.getEpicsList().size(),
                "Задачи не удалились");
        Assertions.assertEquals(0, taskManager.getSubtasksList().size(),
                "Задачи не удалились");
    }

    @Test
    void getEpicById() {
        Assertions.assertEquals(newEpic, taskManager.getEpicById(newEpic.getId()),
                "Задача не найдена");
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

    // Тесты Subtask

    @Test
    void clearSubtasks() {
        taskManager.clearSubtasks();

        Assertions.assertEquals(0, taskManager.getSubtasksList().size(),
                "Задачи не удалились");
    }

    @Test
    void getSubtaskById() {
        Assertions.assertEquals(newSubtask, taskManager.getSubtaskById(newSubtask.getId()),
                "Задача не найдена");
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

    // Разные тесты

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

    // Тесты пересечения задач

    @Test
    void getPrioritizedTasks() {
        List<Task> prioritizedTasksList = taskManager.getPrioritizedTasks();

        Assertions.assertNotEquals(0, prioritizedTasksList.size(),
                "Задачи не сохраняются в список по приоритету");
        Assertions.assertEquals(newSubtask, prioritizedTasksList.get(0),
                "Задача не добавилась в список по приоритету или список не отсортирован");
        Assertions.assertEquals(newTask, prioritizedTasksList.get(1),
                "Задача не добавилась в список по приоритету или список не отсортирован");
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

    // Тесты истории

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
    void shouldGetIntoHistoryWhenUsedGetSubtaskById() {

        taskManager.getSubtaskById(newSubtask.getId());

        Assertions.assertEquals(newSubtask, taskManager.getHistory().get(0),
                "Подзадачи не совпадают или подзадача не была добавлена");
    }

}