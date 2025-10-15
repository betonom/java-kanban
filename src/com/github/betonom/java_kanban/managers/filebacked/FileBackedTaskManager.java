package com.github.betonom.java_kanban.managers.filebacked;

import com.github.betonom.java_kanban.entities.*;
import com.github.betonom.java_kanban.exceptions.ManagerSaveException;
import com.github.betonom.java_kanban.managers.inmemory.InMemoryTaskManager;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public void createNewTask(Task task) {
        super.createNewTask(task);
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
    }

    @Override
    public void createNewEpic(Epic epic) {
        super.createNewEpic(epic);
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
    }

    @Override
    public void createNewSubtask(Subtask subtask) {
        super.createNewSubtask(subtask);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);

        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            if (br.ready())
                br.readLine();

            while (br.ready()) {
                String taskLine = br.readLine();
                Task task = fileBackedTaskManager.fromString(taskLine);

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
            bw.write("id,type,name,status,description,epic");

            for (Task task : tasks.values()) {
                bw.write(toString(task));
            }

            for (Epic epic : epics.values()) {
                bw.write(toString(epic));
            }

            for (Subtask subtask : subtasks.values()) {
                bw.write(toString(subtask));
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла проблема с сохранением в файл!");
        }
    }

    private String toString(Task task) {
        TaskType taskType = TaskType.valueOf(task.getClass().toString().toUpperCase());
        if (taskType == TaskType.SUBTASK) {
            Subtask subtask = (Subtask) task;
            return String.format("%d,%s,%s,%s,%s,$d", subtask.getId(), taskType, subtask.getName(), subtask.getStatus(),
                    subtask.getDescription(), subtask.getEpicId());

        } else {
            return String.format("%d,%s,%s,%s,%s,", task.getId(), taskType, task.getName(), task.getStatus(),
                    task.getDescription());
        }
    }

    private Task fromString(String value) {
        String[] taskArray = value.split(",");

        TaskType taskType = TaskType.valueOf(taskArray[1]);

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
