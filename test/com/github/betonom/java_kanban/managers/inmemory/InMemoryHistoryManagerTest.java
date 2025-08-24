package com.github.betonom.java_kanban.managers.inmemory;

import com.github.betonom.java_kanban.entities.Task;
import com.github.betonom.java_kanban.managers.HistoryManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class InMemoryHistoryManagerTest {

    static HistoryManager historyManager;
    static Task newTask;

    @BeforeEach
    void beforeEach() {
        historyManager = Managers.getDefaultHistory();
        newTask = new Task("name", "description");
    }

    @Test
    void add() {

        historyManager.add(newTask);

        Assertions.assertEquals(1, historyManager.getHistory().size(), "История задач не изменилась");
        Assertions.assertEquals(newTask, historyManager.getHistory().get(0), "Задачи не совпадают");
    }

    @Test
    void shouldDeleteOldTaskWhenOverflow() {
        Task anotherTask = new Task("another name", "another description");

        historyManager.add(newTask);

        for (int i = 0; i < historyManager.SIZE; i++) {
            historyManager.add(anotherTask);
        }

        Task oldTask = historyManager.getHistory().get(0);

        Assertions.assertNotEquals(1, oldTask.getId(),
                "Первый элемент в истории не удаляется при переполнении");
    }

    @Test
    void shouldSaveOldAndNewVersionsOfSameTasks() {
        historyManager.add(newTask);
        Task updatedTask = new Task("nameUpdated", "descriptionUpdated");
        updatedTask.setId(newTask.getId());
        historyManager.add(updatedTask);

        ArrayList<Task> history = historyManager.getHistory();

        Assertions.assertEquals(updatedTask.getName(), history.get(1).getName());
        Assertions.assertEquals(newTask.getName(), history.get(0).getName());
    }
}