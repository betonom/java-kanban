package com.github.betonom.java_kanban.utilities;

import com.github.betonom.java_kanban.entities.*;

import java.time.Duration;
import java.time.LocalDateTime;

public class FileBackedUtil {
    public static String toString(Task task) {
        if (task == null) {
            return null;
        }
        return switch (task.getType()) {
            case TASK -> String.format("%d,%s,%s,%s,%s,,%s,%d",
                    task.getId(),
                    TaskType.TASK,
                    task.getName(),
                    task.getStatus(),
                    task.getDescription(),
                    task.getStartTime().toString(),
                    task.getDuration().toMinutes());

            case EPIC -> {
                Epic epic = (Epic) task;
                yield String.format("%d,%s,%s,%s,%s,,%s,%d,%s",
                        epic.getId(),
                        TaskType.EPIC,
                        epic.getName(),
                        epic.getStatus(),
                        epic.getDescription(),
                        epic.getStartTime().toString(),
                        epic.getDuration().toMinutes(),
                        epic.getEndTime().toString());
            }

            case SUBTASK -> {
                Subtask subtask = (Subtask) task;
                yield String.format("%d,%s,%s,%s,%s,%d,%s,%d",
                        subtask.getId(),
                        TaskType.SUBTASK,
                        subtask.getName(),
                        subtask.getStatus(),
                        subtask.getDescription(),
                        subtask.getEpicId(),
                        subtask.getStartTime().toString(),
                        subtask.getDuration().toMinutes());
            }
        };

    }

    public static Task fromString(String value) {
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
            task.setStartTime(LocalDateTime.parse(taskArray[6]));
            task.setDuration(Duration.ofMinutes(Integer.parseInt(taskArray[7])));

            return task;
        }

        if (taskType == TaskType.EPIC) {
            Epic epic = new Epic(taskArray[2], taskArray[4]);
            epic.setId(Integer.parseInt(taskArray[0]));
            epic.setStatus(TaskStatus.valueOf(taskArray[3]));
            epic.setStartTime(LocalDateTime.parse(taskArray[6]));
            epic.setDuration(Duration.ofMinutes(Integer.parseInt(taskArray[7])));
            epic.setEndTime(LocalDateTime.parse(taskArray[8]));

            return epic;
        }

        if (taskType == TaskType.SUBTASK) {
            Subtask subtask = new Subtask(taskArray[2], taskArray[4], Integer.parseInt(taskArray[5]));
            subtask.setId(Integer.parseInt(taskArray[0]));
            subtask.setStatus(TaskStatus.valueOf(taskArray[3]));
            subtask.setStartTime(LocalDateTime.parse(taskArray[6]));
            subtask.setDuration(Duration.ofMinutes(Integer.parseInt(taskArray[7])));

            return subtask;
        }

        return null;
    }
}
