package com.github.betonom.java_kanban.managers.inmemory;

import com.github.betonom.java_kanban.entities.Task;
import com.github.betonom.java_kanban.managers.HistoryManager;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private final ArrayList<Task> tasksHistory;
    private final int size;

    public InMemoryHistoryManager() {
        tasksHistory = new ArrayList<>();
        size = 10;
    }

    @Override
    public void add(Task task) {
        if (tasksHistory.size() == size) {
            tasksHistory.remove(0);
        }
        tasksHistory.add(task);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return tasksHistory;
    }

}
