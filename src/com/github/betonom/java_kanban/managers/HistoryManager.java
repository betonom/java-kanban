package com.github.betonom.java_kanban.managers;

import com.github.betonom.java_kanban.entities.Task;

import java.util.ArrayList;

public interface HistoryManager {
    public static final int SIZE = 10;

    void add(Task task);

    void remove(int id);

    ArrayList<Task> getHistory();
}
