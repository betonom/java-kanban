package com.github.betonom.java_kanban.managers;

import com.github.betonom.java_kanban.entities.Epic;
import com.github.betonom.java_kanban.entities.Subtask;
import com.github.betonom.java_kanban.entities.Task;
import com.github.betonom.java_kanban.entities.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, Subtask> subtasks;

    private int taskCounter = 1;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    //Методы для Task

    @Override
    public ArrayList<Task> getTasksList() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void clearTasks() {
        tasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    @Override
    public void createNewTask(Task task) {
        if (task == null) {
            return;
        }
        task.setId(taskCounter);
        taskCounter++;
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateTask(Task task) {
        if (task == null) {
            return;
        }
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    //Методы для Epic

    @Override
    public ArrayList<Epic> getEpicsList() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void clearEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    @Override
    public void createNewEpic(Epic epic) {
        if (epic == null) {
            return;
        }
        epic.setId(taskCounter);
        taskCounter++;
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic == null) {
            return;
        }
        if (epics.containsKey(epic.getId())) {
            epic.setStatus(getStatusEpic(epic));
            epics.put(epic.getId(), epic);
        }
    }

    @Override
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

    @Override
    public ArrayList<Subtask> getSubtasksList() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void clearSubtasks() {
        for (Epic epic : epics.values()) {
            epic.getSubtasksId().clear();
        }
        subtasks.clear();
    }

    @Override
    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    @Override
    public void createNewSubtask(Subtask subtask) {
        if (subtask == null) {
            return;
        }
        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        }
        subtask.setId(taskCounter);
        taskCounter++;
        subtasks.put(subtask.getId(), subtask);
        epic.getSubtasksId().add(subtask.getId());
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask == null) {
            return;
        }
        if (subtasks.containsKey(subtask.getId())) {
            //проверка на возможное несоответствие epic id у нового и старого subtack
            if(subtasks.get(subtask.getId()).getEpicId() != subtask.getEpicId()){
                return;
            }
            int subtaskId = subtask.getId();
            subtasks.put(subtaskId, subtask);

            int epicId = subtask.getEpicId();
            Epic epic = epics.get(epicId);
            epic.setStatus(getStatusEpic(epic));
        }
    }

    @Override
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
