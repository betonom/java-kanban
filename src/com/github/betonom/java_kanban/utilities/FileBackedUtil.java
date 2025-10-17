package com.github.betonom.java_kanban.utilities;

import com.github.betonom.java_kanban.entities.*;

public class FileBackedUtil {
    public static String toString(Task task) {
        if (task == null) {
            return null;
        }
        String result = switch (task.getType()) {
            case TASK, EPIC -> String.format("%d,%s,%s,%s,%s,",
                    task.getId(),
                    task.getType() == TaskType.TASK ? TaskType.TASK : TaskType.EPIC,
                    task.getName(),
                    task.getStatus(),
                    task.getDescription());
            case SUBTASK -> {
                Subtask subtask = (Subtask) task;
                yield String.format("%d,%s,%s,%s,%s,%d",
                        subtask.getId(),
                        TaskType.SUBTASK,
                        subtask.getName(),
                        subtask.getStatus(),
                        subtask.getDescription(),
                        subtask.getEpicId());
            }
        };
        return result;

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
