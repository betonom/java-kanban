package com.github.betonom.java_kanban.managers.filebacked;

import com.github.betonom.java_kanban.entities.*;
import com.github.betonom.java_kanban.exceptions.ManagerSaveException;
import com.github.betonom.java_kanban.managers.inmemory.InMemoryTaskManager;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public void createNewTask(Task task) {
        super.createNewTask(task);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }

    @Override
    public void createNewEpic(Epic epic) {
        super.createNewEpic(epic);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void clearSubtasks() {
        super.clearSubtasks();
        save();
    }

    @Override
    public void createNewSubtask(Subtask subtask) {
        super.createNewSubtask(subtask);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);

        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            while (br.ready()) {
                String taskLine = br.readLine();
                Task task = fileBackedTaskManager.fromString(taskLine);
                if (task == null) {
                    continue;
                }
                if (task.getClass() == Task.class) {
                    fileBackedTaskManager.createNewTask(task);
                }
                if (task.getClass() == Epic.class) {
                    Epic epic = (Epic) task;
                    fileBackedTaskManager.createNewEpic(epic);
                }
                if (task.getClass() == Subtask.class) {
                    Subtask subtask = (Subtask) task;
                    fileBackedTaskManager.createNewSubtask(subtask);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileBackedTaskManager;
    }

    private void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            bw.write("id,type,name,status,description,epic\n");

            for (Task task : tasks.values()) {
                bw.write(toString(task) + "\n");
            }

            for (Epic epic : epics.values()) {
                bw.write(toString(epic) + "\n");
            }

            for (Subtask subtask : subtasks.values()) {
                bw.write(toString(subtask) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла проблема с сохранением в файл!");
        }
    }

    private String toString(Task task) {
        if (task == null) {
            return null;
        }
        if (task.getClass() == Subtask.class) {
            Subtask subtask = (Subtask) task;
            return String.format("%d,%s,%s,%s,%s,%d", subtask.getId(), TaskType.SUBTASK, subtask.getName(), subtask.getStatus(),
                    subtask.getDescription(), subtask.getEpicId());
        }
        if (task.getClass() == Task.class) {
            return String.format("%d,%s,%s,%s,%s,", task.getId(), TaskType.TASK, task.getName(), task.getStatus(),
                    task.getDescription());
        }
        if (task.getClass() == Epic.class) {
            Epic epic = (Epic) task;
            return String.format("%d,%s,%s,%s,%s,", task.getId(), TaskType.EPIC, task.getName(), task.getStatus(),
                    task.getDescription());
        }

        return null;
    }

    private Task fromString(String value) {
        String[] taskArray = value.split(",");
        TaskType taskType;
        try {
            taskType = TaskType.valueOf(taskArray[1]);
        } catch (Exception e) {
            return null;
        }

        if (taskType == TaskType.TASK) {
            Task task = new Task(taskArray[2], taskArray[4]);
            task.setId(Integer.parseInt(taskArray[0]));
            task.setStatus(TaskStatus.valueOf(taskArray[3]));

            return task;
        }

        if (taskType == TaskType.EPIC) {
            Epic epic = new Epic(taskArray[2], taskArray[4]);
            epic.setId(Integer.parseInt(taskArray[0]));
            epic.setStatus(TaskStatus.valueOf(taskArray[3]));

            return epic;
        }

        if (taskType == TaskType.SUBTASK) {
            Subtask subtask = new Subtask(taskArray[2], taskArray[4], Integer.parseInt(taskArray[5]));
            subtask.setId(Integer.parseInt(taskArray[0]));
            subtask.setStatus(TaskStatus.valueOf(taskArray[3]));

            return subtask;
        }

        return null;
    }
}
