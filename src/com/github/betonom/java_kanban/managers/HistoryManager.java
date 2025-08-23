package com.github.betonom.java_kanban.managers;

import com.github.betonom.java_kanban.entities.Task;

import java.util.ArrayList;

public interface HistoryManager {
    void add(Task task);

    ArrayList<Task> getHistory();
}
