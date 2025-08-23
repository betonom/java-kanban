package com.github.betonom.java_kanban.managers.inmemory;

import com.github.betonom.java_kanban.entities.Task;
import com.github.betonom.java_kanban.managers.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class InMemoryHistoryManagerTest {

    static TaskManager taskManager;

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    void add() {
        Task newTask = new Task("name", "description");
        taskManager.createNewTask(newTask);

        taskManager.getTaskById(newTask.getId());

        Assertions.assertEquals(taskManager.getHistory().size(), 1, "История задач не изменилась");
        Assertions.assertEquals(taskManager.getHistory().get(0), newTask, "Задачи не совпадают");
    }

    @Test
    void shouldDeleteOldTaskWhenOverflow() {
        final int size = 10;
        Task newTask = new Task("name", "description");
        taskManager.createNewTask(newTask);
        Task anotherTask = new Task("another name", "another description");
        taskManager.createNewTask(anotherTask);

        taskManager.getTaskById(newTask.getId());

        for (int i = 0; i < size; i++) {
            taskManager.getTaskById(anotherTask.getId());
        }

        Task oldTask = taskManager.getHistory().get(0);

        Assertions.assertNotEquals(oldTask.getId(), 1,
                "Первый элемент в истории не удаляется при переполнении");
    }

    @Test
    void shouldSaveOldAndNewVersionsOfSameTasks() {
        Task newTask = new Task("name", "description");
        taskManager.createNewTask(newTask);

        taskManager.getTaskById(newTask.getId());

        Task updatedTask = new Task("nameUpdated", "descriptionUpdated");
        updatedTask.setId(newTask.getId());
        taskManager.updateTask(updatedTask);

        taskManager.getTaskById(updatedTask.getId());

        ArrayList<Task> history = taskManager.getHistory();

        Assertions.assertEquals(updatedTask.getName(), history.get(1).getName());
        Assertions.assertEquals(newTask.getName(), history.get(0).getName());
    }
}