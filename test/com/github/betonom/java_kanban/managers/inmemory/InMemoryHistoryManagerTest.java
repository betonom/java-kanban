package com.github.betonom.java_kanban.managers.inmemory;

import com.github.betonom.java_kanban.entities.Task;
import com.github.betonom.java_kanban.managers.HistoryManager;
import com.github.betonom.java_kanban.managers.Managers;
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
        newTask.setId(1);
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
    void shouldSaveOrderWhenDeleteTaskInTheMiddle() {
        Task newTask2 = new Task("name2", "description2");
        newTask2.setId(2);
        Task newTask3 = new Task("name3", "description3");
        newTask3.setId(3);

        historyManager.add(newTask);
        historyManager.add(newTask2);
        historyManager.add(newTask3);

        Assertions.assertEquals(3, historyManager.getHistory().size(), "Задачи не добавились");

        historyManager.remove(newTask2.getId());

        Assertions.assertEquals(2, historyManager.getHistory().size(), "Задача не удалилась");
        Assertions.assertEquals(List.of(newTask, newTask3), historyManager.getHistory(), "Порядок не сохранился");
    }

    @Test
    void shouldSaveOrderWhenDeleteTaskInTheBeginning() {
        Task newTask2 = new Task("name2", "description2");
        newTask2.setId(2);
        Task newTask3 = new Task("name3", "description3");
        newTask3.setId(3);

        historyManager.add(newTask);
        historyManager.add(newTask2);
        historyManager.add(newTask3);

        Assertions.assertEquals(3, historyManager.getHistory().size(), "Задачи не добавились");

        historyManager.remove(newTask3.getId());

        Assertions.assertEquals(2, historyManager.getHistory().size(), "Задача не удалилась");
        Assertions.assertEquals(List.of(newTask, newTask2), historyManager.getHistory(), "Порядок не сохранился");
    }

    @Test
    void shouldSaveOrderWhenDeleteTaskInTheEnd() {
        Task newTask2 = new Task("name2", "description2");
        newTask2.setId(2);
        Task newTask3 = new Task("name3", "description3");
        newTask3.setId(3);

        historyManager.add(newTask);
        historyManager.add(newTask2);
        historyManager.add(newTask3);

        Assertions.assertEquals(3, historyManager.getHistory().size(), "Задачи не добавились");

        historyManager.remove(newTask.getId());

        Assertions.assertEquals(2, historyManager.getHistory().size(), "Задача не удалилась");
        Assertions.assertEquals(List.of(newTask2, newTask3), historyManager.getHistory(), "Порядок не сохранился");
    }

    @Test
    void shouldDeleteOldVersionsOfTheTask() {
        historyManager.add(newTask);
        Task updatedTask = new Task("nameUpdated", "descriptionUpdated");
        updatedTask.setId(newTask.getId());
        historyManager.add(updatedTask);

        List<Task> history = historyManager.getHistory();

        Assertions.assertNotEquals(2, history.size(), "Не удаляется дубликат задачи");
        Assertions.assertEquals("nameUpdated", history.get(0).getName(), "Задача не обновилась");
    }
}