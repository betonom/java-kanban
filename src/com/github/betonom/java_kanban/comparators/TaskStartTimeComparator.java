package com.github.betonom.java_kanban.comparators;

import com.github.betonom.java_kanban.entities.Task;

import java.util.Comparator;

public class TaskStartTimeComparator implements Comparator<Task> {
    @Override
    public int compare(Task task1, Task task2) {
        if (task1.getStartTime().isAfter(task2.getStartTime())) {
            return 1;
        } else if (task1.getStartTime().isBefore(task2.getStartTime())) {
            return -1;
        } else {
            return 0;
        }

    }
}
