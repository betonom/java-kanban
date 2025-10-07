package com.github.betonom.java_kanban.managers;

import com.github.betonom.java_kanban.entities.Epic;
import com.github.betonom.java_kanban.entities.Subtask;
import com.github.betonom.java_kanban.entities.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    ArrayList<Task> getTasksList();

    void clearTasks();

    Task getTaskById(int id);

    void createNewTask(Task task);

    void updateTask(Task task);

    void removeTaskById(int id);

    ArrayList<Epic> getEpicsList();

    void clearEpics();

    Epic getEpicById(int id);

    void createNewEpic(Epic epic);

    void updateEpic(Epic epic);

    void removeEpicById(int id);

    ArrayList<Subtask> getSubtasksList();

    void clearSubtasks();

    Subtask getSubtaskById(int id);

    void createNewSubtask(Subtask subtask);

    void updateSubtask(Subtask subtask);

    void removeSubtaskById(int id);

    List<Task> getHistory();
}
