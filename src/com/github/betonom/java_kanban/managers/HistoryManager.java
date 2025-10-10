package com.github.betonom.java_kanban.managers;

import com.github.betonom.java_kanban.entities.Task;

import java.util.List;

public interface HistoryManager {

    void add(Task task);

    void remove(int id);

    List<Task> getHistory();
}
