package com.github.betonom.java_kanban.managers;

import com.github.betonom.java_kanban.entities.Epic;
import com.github.betonom.java_kanban.entities.Subtask;
import com.github.betonom.java_kanban.entities.Task;
import com.github.betonom.java_kanban.entities.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, Subtask> subtasks;

    private int taskCounter = 1;

    public TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    //Методы для Task

    public ArrayList<Task> getTasksList() {
        return new ArrayList<>(tasks.values());
    }

    public void clearTasks() {
        tasks.clear();
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public void createNewTask(Task task) {
        if (task == null) {
            return;
        }
        task.setId(taskCounter);
        taskCounter++;
        tasks.put(task.getId(), task);
    }

    public void updateTask(Task task) {
        if (task == null) {
            return;
        }
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    //Методы для Epic

    public ArrayList<Epic> getEpicsList() {
        return new ArrayList<>(epics.values());
    }

    public void clearEpics() {
        epics.clear();
        subtasks.clear();
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public void createNewEpic(Epic epic) {
        if (epic == null) {
            return;
        }
        epic.setId(taskCounter);
        taskCounter++;
        epics.put(epic.getId(), epic);
    }

    public void updateEpic(Epic epic) {
        if (epic == null) {
            return;
        }
        if (epics.containsKey(epic.getId())) {
            epic.setStatus(getStatusEpic(epic));
            epics.put(epic.getId(), epic);
        }
    }

    public void removeEpicById(int id) {
        Epic epic = epics.get(id);
        for (Integer subtaskId : epic.getSubtasksId()) {
            removeSubtaskById(subtaskId);
        }
        epics.remove(id);
    }

    public ArrayList<Subtask> getEpicSubtasks(Epic epic) {
        ArrayList<Subtask> epicSubtasks = new ArrayList<>();
        for (Integer id : epic.getSubtasksId()) {
            if (subtasks.containsKey(id)) {
                epicSubtasks.add(subtasks.get(id));
            }
        }
        return epicSubtasks;
    }

    //Методы для Subtask

    public ArrayList<Subtask> getSubtasksList() {
        return new ArrayList<>(subtasks.values());
    }

    public void clearSubtasks() {
        for (Epic epic : epics.values()) {
            epic.getSubtasksId().clear();
        }
        subtasks.clear();
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public void createNewSubtask(Subtask subtask) {
        if (subtask == null) {
            return;
        }
        int epicId = subtasks.get(subtask.getId()).getEpicId();
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        }
        subtask.setId(taskCounter);
        taskCounter++;
        subtasks.put(subtask.getId(), subtask);
        epic.getSubtasksId().add(subtask.getId());
    }

    public void updateSubtask(Subtask subtask) {
        if (subtask == null) {
            return;
        }
        if (subtasks.containsKey(subtask.getId())) {
            int subtaskId = subtask.getId();
            subtasks.put(subtaskId, subtask);

            int epicId = subtasks.get(subtaskId).getEpicId();
            Epic epic = epics.get(epicId);
            epic.setStatus(getStatusEpic(epic));
        }
    }

    public void removeSubtaskById(int id) {
        int epicId = subtasks.get(id).getEpicId();
        Epic epic = epics.get(epicId);
        if (epic != null) {
            epic.getSubtasksId().remove(id);
        }
        subtasks.remove(id);
    }

    private TaskStatus getStatusEpic(Epic epic) {
        if (epic.getSubtasksId().isEmpty()) {
            return TaskStatus.TO_DO;
        }

        boolean isAllTodo = true;
        boolean isAllDone = true;

        // Получение массива подзадач из эпика
        ArrayList<Subtask> epicSubtasks = getEpicSubtasks(epic);

        for (Subtask subtask : epicSubtasks) {

            TaskStatus subtaskStatus = subtask.getStatus();

            if (subtaskStatus == TaskStatus.IN_PROGRESS) {
                return TaskStatus.IN_PROGRESS;
            }

            if (subtaskStatus != TaskStatus.TO_DO) {
                isAllTodo = false;
            }

            if (subtaskStatus != TaskStatus.DONE) {
                isAllDone = false;
            }
        }

        if (isAllTodo) {
            return TaskStatus.TO_DO;
        } else if (isAllDone) {
            return TaskStatus.DONE;
        } else {
            return TaskStatus.IN_PROGRESS;
        }
    }
}
