package com.github.betonom.java_kanban.managers.filebacked;

import com.github.betonom.java_kanban.entities.Epic;
import com.github.betonom.java_kanban.entities.Subtask;
import com.github.betonom.java_kanban.entities.Task;
import com.github.betonom.java_kanban.exceptions.ManagerSaveException;
import com.github.betonom.java_kanban.managers.TaskManager;
import com.github.betonom.java_kanban.managers.TaskManagerTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

class FileBackedTaskManagerTest extends TaskManagerTest<TaskManager> {
    static File file;

    @BeforeEach
    protected void beforeEach() {
        Assertions.assertDoesNotThrow(() -> {
            file = File.createTempFile("tmp", ".txt");
        });

        Assertions.assertDoesNotThrow(() -> {
            taskManager = FileBackedTaskManager.loadFromFile(file);
        }, "Подгрузка из существующего файла не должна вызывать исключение");

        newTask = new Task("name", "description");
        newTask.setDuration(Duration.ofMinutes(5));
        newTask.setStartTime(LocalDateTime.of(2000, 1, 1, 2, 2));
        Assertions.assertDoesNotThrow(() -> {
            taskManager.createNewTask(newTask);
        });

        newEpic = new Epic("name", "description");
        taskManager.createNewEpic(newEpic);

        newSubtask = new Subtask("name", "description", newEpic.getId());
        newSubtask.setDuration(Duration.ofMinutes(5));
        newSubtask.setStartTime(LocalDateTime.of(2000, 1, 1, 1, 1));
        taskManager.createNewSubtask(newSubtask);
    }

    @Test
    void shouldThrowsManagerSaveExceptionWhenThereIsNotAFile() {
        file = new File("test.txt");

        Assertions.assertThrows(ManagerSaveException.class, () -> {
            FileBackedTaskManager.loadFromFile(file);
        }, "Подгрузка из несуществующего файла должна вызывать исключение");
    }

    //Общие тесты

    @Test
    void shouldLoadFromEmptyFile() throws IOException {
        File file = File.createTempFile("tmp", ".txt");

        taskManager = new FileBackedTaskManager(file);

        Assertions.assertTrue(taskManager.getTasksList().isEmpty(), "Что-то пошло не так.");
        Assertions.assertTrue(taskManager.getEpicsList().isEmpty(), "Что-то пошло не так.");
        Assertions.assertTrue(taskManager.getSubtasksList().isEmpty(), "Что-то пошло не так.");
    }

    @Test
    void shouldSaveDifferentTypesOfTasks() {
        //В beforeEach уже созданы разные типы задач

        FileBackedTaskManager tmpFbtm = FileBackedTaskManager.loadFromFile(file);

        Assertions.assertEquals(newTask, tmpFbtm.getTaskById(newTask.getId()),
                "Задача не сохранилась или не подгрузилась");
        Assertions.assertEquals(newEpic, tmpFbtm.getEpicById(newEpic.getId()),
                "Эпик не сохранился или не подгрузился");
        Assertions.assertEquals(newSubtask, tmpFbtm.getSubtaskById(newSubtask.getId()),
                "Подзадача не сохранилась или не подгрузилась");
    }
}