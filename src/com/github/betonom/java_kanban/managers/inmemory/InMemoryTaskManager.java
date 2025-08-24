package com.github.betonom.java_kanban.managers.inmemory;

import com.github.betonom.java_kanban.entities.Epic;
import com.github.betonom.java_kanban.entities.Subtask;
import com.github.betonom.java_kanban.entities.Task;
import com.github.betonom.java_kanban.entities.TaskStatus;
import com.github.betonom.java_kanban.managers.HistoryManager;
import com.github.betonom.java_kanban.managers.TaskManager;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subtasks;
    private final HistoryManager historyManager;

    private int taskCounter = 1;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
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
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
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
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
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
            if (epic.getSubtasksId().contains(epic.getId())) {
                return;
            }
            epic.setStatus(getStatusEpic(epic));
            epics.put(epic.getId(), epic);
        }
    }

    @Override
    public void removeEpicById(int id) {
        Epic epic = epics.get(id);
        ArrayList<Integer> subtasksId = epic.getSubtasksId();
        while (!subtasksId.isEmpty()) {
            removeSubtaskById(subtasksId.get(0));
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
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
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
            //проверка на возможность добавления в эпик подзадачи самой подзадачи
            if (subtask.getId() == subtask.getEpicId()) {
                return;
            }
            //проверка на возможное несоответствие epic id у нового и старого subtack
            if (subtasks.get(subtask.getId()).getEpicId() != subtask.getEpicId()) {
                return;
            }
            int subtaskId = subtask.getId();
            subtasks.put(subtaskId, subtask);

            int epicId = subtask.getEpicId();
            Epic epic = epics.get(epicId);
            if (epic == null) {
                return;
            }
            epic.setStatus(getStatusEpic(epic));
        }
    }

    @Override
    public void removeSubtaskById(int id) {
        int epicId = subtasks.get(id).getEpicId();
        Epic epic = epics.get(epicId);
        if (epic != null) {
            epic.getSubtasksId().remove((Integer) id);
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

    @Override
    public ArrayList<Task> getHistory() {
        return historyManager.getHistory();
    }
}
