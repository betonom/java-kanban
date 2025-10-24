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
        newTask.setStartTime(LocalDateTime.of(2000, 1, 1, 1, 2));
        Assertions.assertDoesNotThrow(() -> {
            taskManager.createNewTask(newTask);
        });

        newEpic = new Epic("name", "description");
        taskManager.createNewEpic(newEpic);

        newSubtask = new Subtask("name", "description", newEpic.getId());
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

    //Task tests

    @Test
    void clearTasks() {
        Task newTask2 = new Task("name2", "description2");

        taskManager.createNewTask(newTask2);

        taskManager.clearTasks();

        FileBackedTaskManager tmpFbtm = FileBackedTaskManager.loadFromFile(file);

        Assertions.assertTrue(tmpFbtm.getTasksList().isEmpty(),
                "Задачи не удалилась из файла");

    }

    @Test
    void createNewTask() {
        Task newTask2 = new Task("name2", "description2");

        taskManager.createNewTask(newTask2);

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

        taskManager.updateTask(savedTask);

        FileBackedTaskManager tmpFbtm = FileBackedTaskManager.loadFromFile(file);

        Assertions.assertEquals(savedTask.getName(), tmpFbtm.getTaskById(savedTask.getId()).getName(),
                "Обновленная задача не сохранилась");
    }

    @Test
    void removeTaskById() {
        taskManager.removeTaskById(newTask.getId());

        FileBackedTaskManager tmpFbtm = FileBackedTaskManager.loadFromFile(file);

        Assertions.assertTrue(tmpFbtm.getTasksList().isEmpty(),
                "Задача не удалилась из файла");
    }

    //Epic tests

    @Test
    void clearEpics() {
        Epic newEpic2 = new Epic("name2", "description2");

        taskManager.createNewEpic(newEpic2);

        taskManager.clearEpics();

        FileBackedTaskManager tmpFbtm = FileBackedTaskManager.loadFromFile(file);

        Assertions.assertTrue(tmpFbtm.getEpicsList().isEmpty(),
                "Задачи не удалилась из файла");
    }

    @Test
    void createNewEpic() {
        Epic newEpic2 = new Epic("name2", "description2");

        taskManager.createNewEpic(newEpic2);

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

        taskManager.updateEpic(savedEpic);

        FileBackedTaskManager tmpFbtm = FileBackedTaskManager.loadFromFile(file);

        Assertions.assertEquals(savedEpic.getName(), tmpFbtm.getEpicById(savedEpic.getId()).getName(),
                "Обновленная задача не сохранилась");
    }

    @Test
    void removeEpicById() {
        taskManager.removeEpicById(newEpic.getId());

        FileBackedTaskManager tmpFbtm = FileBackedTaskManager.loadFromFile(file);

        Assertions.assertTrue(tmpFbtm.getEpicsList().isEmpty(),
                "Задача не удалилась из файла");
    }

    //Subtask tests

    @Test
    void clearSubtasks() {
        Subtask newSubtask2 = new Subtask("name2", "description2", newEpic.getId());

        taskManager.createNewSubtask(newSubtask2);

        taskManager.clearSubtasks();

        FileBackedTaskManager tmpFbtm = FileBackedTaskManager.loadFromFile(file);

        Assertions.assertTrue(tmpFbtm.getSubtasksList().isEmpty(),
                "Задачи не удалилась из файла");
    }

    @Test
    void createNewSubtask() {
        Subtask newSubtask2 = new Subtask("name2", "description2", newEpic.getId());

        taskManager.createNewSubtask(newSubtask2);

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

        taskManager.updateSubtask(savedSubtask);

        FileBackedTaskManager tmpFbtm = FileBackedTaskManager.loadFromFile(file);

        Assertions.assertEquals(savedSubtask.getName(), tmpFbtm.getSubtaskById(savedSubtask.getId()).getName(),
                "Обновленная задача не сохранилась");
    }

    @Test
    void removeSubtaskById() {
        taskManager.removeSubtaskById(newSubtask.getId());

        FileBackedTaskManager tmpFbtm = FileBackedTaskManager.loadFromFile(file);

        Assertions.assertTrue(tmpFbtm.getSubtasksList().isEmpty(),
                "Задача не удалилась из файла");
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

    @Test
    void getPrioritizedTasks() {
        FileBackedTaskManager tmpFbtm = FileBackedTaskManager.loadFromFile(file);

        List<Task> prioritizedTasksList = tmpFbtm.getPrioritizedTasks();

        Assertions.assertNotEquals(0, prioritizedTasksList.size(),
                "Задачи не сохраняются в список по приоритету или не подгрузились из файла");
        Assertions.assertEquals(newSubtask, prioritizedTasksList.get(0),
                "Задача не добавилась в список по приоритету или список не отсортирован");
        Assertions.assertEquals(newTask, prioritizedTasksList.get(1),
                "Задача не добавилась в список по приоритету или список не отсортирован");
    }

}