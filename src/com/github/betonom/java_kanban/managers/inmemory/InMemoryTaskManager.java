package com.github.betonom.java_kanban.managers.inmemory;

import com.github.betonom.java_kanban.entities.Epic;
import com.github.betonom.java_kanban.entities.Subtask;
import com.github.betonom.java_kanban.entities.Task;
import com.github.betonom.java_kanban.entities.TaskStatus;
import com.github.betonom.java_kanban.exceptions.HasInteractionException;
import com.github.betonom.java_kanban.exceptions.NotFoundException;
import com.github.betonom.java_kanban.managers.HistoryManager;
import com.github.betonom.java_kanban.managers.Managers;
import com.github.betonom.java_kanban.managers.TaskManager;
import com.github.betonom.java_kanban.utilities.TaskManagerUtil;

import java.sql.SQLOutput;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Integer, Task> tasks;
    protected final HashMap<Integer, Epic> epics;
    protected final HashMap<Integer, Subtask> subtasks;
    protected final HistoryManager historyManager;
    protected final TreeSet<Task> prioritizedTasks;

    protected int taskCounter = 1;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
        prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    }

    //Методы для Task

    @Override
    public ArrayList<Task> getTasksList() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void clearTasks() {
        getTasksList().stream()
                .peek(task -> historyManager.remove(task.getId()))
                .filter(task -> task.getStartTime() != null)
                .peek(task -> prioritizedTasks.remove(task))
                .collect(Collectors.toList());

        tasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task == null) {
            throw new NotFoundException("Задача не найдена");
        }
        historyManager.add(task);
        return task;
    }

    @Override
    public void createNewTask(Task task) {
        if (task == null) {
            return;
        }
        if (isTaskCrossAnyOfTasks(task)) {
            throw new HasInteractionException("Задача пересекается с другими задачами");
        }

        task.setId(taskCounter);
        taskCounter++;
        if (task.getStartTime() != null)
            prioritizedTasks.add(task);

        tasks.put(task.getId(), task);
    }

    @Override
    public void updateTask(Task task) {
        if (task == null) {
            return;
        }
        if (isTaskCrossAnyOfTasks(task)) {
            throw new HasInteractionException("Задача пересекается с другими задачами");
        }
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else {
            throw new NotFoundException("Задача для обновления не найдена");
        }
    }

    @Override
    public void removeTaskById(int id) {
        historyManager.remove(id);
        if (tasks.get(id).getStartTime() != null)
            prioritizedTasks.remove(tasks.get(id));
        tasks.remove(id);
    }

    //Методы для Epic

    @Override
    public ArrayList<Epic> getEpicsList() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void clearEpics() {
        epics.values().stream()
                .peek(task -> historyManager.remove(task.getId()))
                .collect(Collectors.toList());
        ;

        subtasks.values().stream()
                .peek(task -> historyManager.remove(task.getId()))
                .filter(task -> task.getStartTime() != null)
                .peek(task -> prioritizedTasks.remove(task))
                .collect(Collectors.toList());
        ;
        epics.clear();
        subtasks.clear();
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            throw new NotFoundException("Задача не найдена");
        }
        historyManager.add(epic);
        return epic;
    }

    @Override
    public void createNewEpic(Epic epic) {
        if (epic == null) {
            return;
        }

        if (epic.getSubtasksId() == null) {
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

            setEpicTimeVariables(epic);

            epics.put(epic.getId(), epic);
        } else {
            throw new NotFoundException("Эпик для обновления не найден");
        }
    }

    @Override
    public void removeEpicById(int id) {
        historyManager.remove(id);


        Epic epic = epics.get(id);
        ArrayList<Integer> subtasksId = epic.getSubtasksId();
        while (!subtasksId.isEmpty()) {
            removeSubtaskById(subtasksId.getFirst());
        }

        epics.remove(id);
    }

    public ArrayList<Subtask> getEpicSubtasks(Epic epic) {
        return new ArrayList<>(
                epic.getSubtasksId().stream()
                        .map(id -> subtasks.get(id))
                        .collect(Collectors.toList())
        );
    }

    //Методы для Subtask

    @Override
    public ArrayList<Subtask> getSubtasksList() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void clearSubtasks() {
        subtasks.values().stream()
                .peek(task -> historyManager.remove(task.getId()))
                .filter(task -> task.getStartTime() != null)
                .peek(task -> prioritizedTasks.remove(task))
                .collect(Collectors.toList());
        ;

        epics.values().stream()
                .peek(epic -> epic.getSubtasksId().clear())
                .peek(epic -> setEpicTimeVariables(epic))
                .collect(Collectors.toList());
        ;

        subtasks.clear();
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            throw new NotFoundException("Подзадача не найдена");
        }
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public void createNewSubtask(Subtask subtask) {
        if (subtask == null) {
            return;
        }
        if (isTaskCrossAnyOfTasks(subtask)) {
            throw new HasInteractionException("Подзадача пересекается с другими задачами");
        }
        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        }

        subtask.setId(taskCounter);
        taskCounter++;
        if (subtask.getStartTime() != null)
            prioritizedTasks.add(subtask);

        subtasks.put(subtask.getId(), subtask);
        epic.getSubtasksId().add(subtask.getId());

        setEpicTimeVariables(epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask == null) {
            return;
        }
        if (isTaskCrossAnyOfTasks(subtask)) {
            throw new HasInteractionException("Подзадача пересекается с другими задачами");
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

            setEpicTimeVariables(epic);
        } else {
            throw new NotFoundException("Подзадача для обновления не найдена");
        }
    }

    @Override
    public void removeSubtaskById(int id) {
        historyManager.remove(id);
        if (subtasks.get(id).getStartTime() != null)
            prioritizedTasks.remove(subtasks.get(id));

        int epicId = subtasks.get(id).getEpicId();
        Epic epic = epics.get(epicId);
        if (epic != null) {
            epic.getSubtasksId().remove((Integer) id);
            epic.setStatus(getStatusEpic(epic));

            setEpicTimeVariables(epic);
        }
        subtasks.remove(id);
    }

    // Общие методы

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
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

    private void setEpicTimeVariables(Epic epic) {
        epic.setDuration(Duration.ofSeconds(0));
        if (epic.getSubtasksId().isEmpty()) {
            return;
        }

        List<Subtask> epicSubtasks = getEpicSubtasks(epic).stream()
                .filter(subtask -> subtask.getStartTime() != null)
                .peek(subtask -> epic.setDuration(epic.getDuration().plus(subtask.getDuration())))
                .sorted(Comparator.comparing(Task::getStartTime))
                .collect(Collectors.toList());

        if (epicSubtasks.isEmpty()) {
            return;
        }
        epic.setStartTime(epicSubtasks.getFirst().getStartTime());
        epic.setEndTime(epicSubtasks.getLast().getEndTime());
    }

    public boolean isTaskCrossAnyOfTasks(Task task) {
        List<Task> tasksList = getPrioritizedTasks();

        if (tasksList.size() == 0) {
            return false;
        }

        Optional<Task> opt = tasksList.stream()
                .filter((task1 -> TaskManagerUtil.isTasksCross(task, task1)))
                .findAny();

        if (opt.isPresent()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
