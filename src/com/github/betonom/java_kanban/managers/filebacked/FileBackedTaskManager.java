package com.github.betonom.java_kanban.managers.filebacked;

import com.github.betonom.java_kanban.entities.*;
import com.github.betonom.java_kanban.exceptions.ManagerSaveException;
import com.github.betonom.java_kanban.managers.inmemory.InMemoryTaskManager;
import com.github.betonom.java_kanban.utilities.TaskManagerUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;

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
                Task task = TaskManagerUtil.fromString(taskLine);
                if (task == null) {
                    continue;
                }
                if (task.getType() == TaskType.TASK) {
                    fileBackedTaskManager.tasks.put(task.getId(), task);
                }
                if (task.getType() == TaskType.EPIC) {
                    Epic epic = (Epic) task;
                    fileBackedTaskManager.epics.put(epic.getId(), epic);
                }
                if (task.getType() == TaskType.SUBTASK) {
                    Subtask subtask = (Subtask) task;

                    int epicId = subtask.getEpicId();
                    Epic epic = fileBackedTaskManager.epics.get(epicId);
                    epic.getSubtasksId().add(subtask.getId());

                    fileBackedTaskManager.subtasks.put(subtask.getId(), subtask);
                }
                if (task.getId() >= fileBackedTaskManager.taskCounter) {
                    fileBackedTaskManager.taskCounter = task.getId() + 1;
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла проблема с сохранением в файл!");
        }
        return fileBackedTaskManager;
    }

    private void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            bw.write("id,type,name,status,description,epic\n");

            for (Task task : tasks.values()) {
                bw.write(TaskManagerUtil.toString(task) + "\n");
            }

            for (Epic epic : epics.values()) {
                bw.write(TaskManagerUtil.toString(epic) + "\n");
            }

            for (Subtask subtask : subtasks.values()) {
                bw.write(TaskManagerUtil.toString(subtask) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла проблема с сохранением в файл!");
        }
    }
}
