package com.github.betonom.java_kanban.managers.inmemory;

import com.github.betonom.java_kanban.entities.Task;
import com.github.betonom.java_kanban.managers.HistoryManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

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
    void remove() {
        historyManager.add(newTask);

        Assertions.assertEquals(1, historyManager.getHistory().size(), "История задач не изменилась");
        Assertions.assertEquals(newTask, historyManager.getHistory().get(0), "Задачи не совпадают");

        historyManager.remove(newTask.getId());
        Assertions.assertEquals(0, historyManager.getHistory().size(), "История задач не изменилась");

    }

    @Test
    void shouldNotSaveOldAndNewVersionsOfSameTasks() {
        historyManager.add(newTask);
        Task updatedTask = new Task("nameUpdated", "descriptionUpdated");
        updatedTask.setId(newTask.getId());
        historyManager.add(updatedTask);

        List<Task> history = historyManager.getHistory();

        Assertions.assertNotEquals(2, history.size(), "Не удаляется дубликат задачи");
    }


}