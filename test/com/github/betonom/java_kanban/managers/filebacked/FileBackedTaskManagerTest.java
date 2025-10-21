package com.github.betonom.java_kanban.managers.filebacked;

import com.github.betonom.java_kanban.entities.Epic;
import com.github.betonom.java_kanban.entities.Subtask;
import com.github.betonom.java_kanban.entities.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

class FileBackedTaskManagerTest {
    static File file;
    static FileBackedTaskManager fileBackedTaskManager;
    static Task newTask;
    static Epic newEpic;
    static Subtask newSubtask;

    @BeforeEach
    void beforeEach() throws IOException {

        file = File.createTempFile("tmp", ".txt");

        fileBackedTaskManager = new FileBackedTaskManager(file);

        newTask = new Task("name", "description");
        fileBackedTaskManager.createNewTask(newTask);

        newEpic = new Epic("name", "description");
        fileBackedTaskManager.createNewEpic(newEpic);

        newSubtask = new Subtask("name", "description", newEpic.getId());
        fileBackedTaskManager.createNewSubtask(newSubtask);
    }

    @Test
    void shouldLoadFromEmptyFile() throws IOException {
        File file = File.createTempFile("tmp", ".txt");

        fileBackedTaskManager = new FileBackedTaskManager(file);

        Assertions.assertTrue(fileBackedTaskManager.getTasksList().isEmpty(), "Что-то пошло не так.");
        Assertions.assertTrue(fileBackedTaskManager.getEpicsList().isEmpty(), "Что-то пошло не так.");
        Assertions.assertTrue(fileBackedTaskManager.getSubtasksList().isEmpty(), "Что-то пошло не так.");
    }

    //Task tests

    @Test
    public void clearTasks() {
        Task newTask2 = new Task("name2", "description2");

        fileBackedTaskManager.createNewTask(newTask2);

        fileBackedTaskManager.clearTasks();

        FileBackedTaskManager tmpFbtm = FileBackedTaskManager.loadFromFile(file);

        Assertions.assertTrue(tmpFbtm.getTasksList().isEmpty(),
                "Задачи не удалилась из файла");

    }

    @Test
    void createNewTask() {
        Task newTask2 = new Task("name2", "description2");

        fileBackedTaskManager.createNewTask(newTask2);

        FileBackedTaskManager tmpFbtm = FileBackedTaskManager.loadFromFile(file);

        Assertions.assertEquals(newTask, tmpFbtm.getTaskById(newTask.getId()),
                "Задача не сохранилась или не подгрузилась");
        Assertions.assertEquals(newTask2, tmpFbtm.getTaskById(newTask2.getId()),
                "Задача не сохранилась или не подгрузилась");
    }

    @Test
    void updateTask() {
        Task savedTask = new Task("updatedName", "updatedDescription");
        savedTask.setId(newTask.getId());

        fileBackedTaskManager.updateTask(savedTask);

        FileBackedTaskManager tmpFbtm = FileBackedTaskManager.loadFromFile(file);

        Assertions.assertEquals(savedTask.getName(), tmpFbtm.getTaskById(savedTask.getId()).getName(),
                "Обновленная задача не сохранилась");
    }

    @Test
    void removeTaskById() {
        fileBackedTaskManager.removeTaskById(newTask.getId());

        FileBackedTaskManager tmpFbtm = FileBackedTaskManager.loadFromFile(file);

        Assertions.assertTrue(tmpFbtm.getTasksList().isEmpty(),
                "Задача не удалилась из файла");
    }

    //Epic tests

    @Test
    void clearEpics() {
        Epic newEpic2 = new Epic("name2", "description2");

        fileBackedTaskManager.createNewEpic(newEpic2);

        fileBackedTaskManager.clearEpics();

        FileBackedTaskManager tmpFbtm = FileBackedTaskManager.loadFromFile(file);

        Assertions.assertTrue(tmpFbtm.getEpicsList().isEmpty(),
                "Задачи не удалилась из файла");
    }

    @Test
    void createNewEpic() {
        Epic newEpic2 = new Epic("name2", "description2");

        fileBackedTaskManager.createNewEpic(newEpic2);

        FileBackedTaskManager tmpFbtm = FileBackedTaskManager.loadFromFile(file);

        Assertions.assertEquals(newEpic, tmpFbtm.getEpicById(newEpic.getId()),
                "Задача не сохранилась или не подгрузилась");
        Assertions.assertEquals(newEpic2, tmpFbtm.getEpicById(newEpic2.getId()),
                "Задача не сохранилась или не подгрузилась");
    }

    @Test
    void updateEpic() {
        Epic savedEpic = new Epic("updatedName", "updatedDescription");
        savedEpic.setId(newEpic.getId());

        fileBackedTaskManager.updateEpic(savedEpic);

        FileBackedTaskManager tmpFbtm = FileBackedTaskManager.loadFromFile(file);

        Assertions.assertEquals(savedEpic.getName(), tmpFbtm.getEpicById(savedEpic.getId()).getName(),
                "Обновленная задача не сохранилась");
    }

    @Test
    void removeEpicById() {
        fileBackedTaskManager.removeEpicById(newEpic.getId());

        FileBackedTaskManager tmpFbtm = FileBackedTaskManager.loadFromFile(file);

        Assertions.assertTrue(tmpFbtm.getEpicsList().isEmpty(),
                "Задача не удалилась из файла");
    }

    //Subtask tests

    @Test
    void clearSubtasks() {
        Subtask newSubtask2 = new Subtask("name2", "description2", newEpic.getId());

        fileBackedTaskManager.createNewSubtask(newSubtask2);

        fileBackedTaskManager.clearSubtasks();

        FileBackedTaskManager tmpFbtm = FileBackedTaskManager.loadFromFile(file);

        Assertions.assertTrue(tmpFbtm.getSubtasksList().isEmpty(),
                "Задачи не удалилась из файла");
    }

    @Test
    void createNewSubtask() {
        Subtask newSubtask2 = new Subtask("name2", "description2", newEpic.getId());

        fileBackedTaskManager.createNewSubtask(newSubtask2);

        FileBackedTaskManager tmpFbtm = FileBackedTaskManager.loadFromFile(file);

        Assertions.assertEquals(newSubtask, tmpFbtm.getSubtaskById(newSubtask.getId()),
                "Задача не сохранилась или не подгрузилась");
        Assertions.assertEquals(newSubtask2, tmpFbtm.getSubtaskById(newSubtask2.getId()),
                "Задача не сохранилась или не подгрузилась");
    }

    @Test
    void updateSubtask() {
        Subtask savedSubtask = new Subtask("updatedName", "updatedDescription", newEpic.getId());
        savedSubtask.setId(newSubtask.getId());

        fileBackedTaskManager.updateSubtask(savedSubtask);

        FileBackedTaskManager tmpFbtm = FileBackedTaskManager.loadFromFile(file);

        Assertions.assertEquals(savedSubtask.getName(), tmpFbtm.getSubtaskById(savedSubtask.getId()).getName(),
                "Обновленная задача не сохранилась");
    }

    @Test
    void removeSubtaskById() {
        fileBackedTaskManager.removeSubtaskById(newSubtask.getId());

        FileBackedTaskManager tmpFbtm = FileBackedTaskManager.loadFromFile(file);

        Assertions.assertTrue(tmpFbtm.getSubtasksList().isEmpty(),
                "Задача не удалилась из файла");
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