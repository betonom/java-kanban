package com.github.betonom.java_kanban.managers.inmemory;

import com.github.betonom.java_kanban.entities.Task;
import com.github.betonom.java_kanban.managers.HistoryManager;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private final ArrayList<Task> tasksHistory;

    public InMemoryHistoryManager() {
        tasksHistory = new ArrayList<>();
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        if (tasksHistory.size() == SIZE) {
            tasksHistory.remove(0);
        }
        tasksHistory.add(task);
    }

    public void remove(int id){
        tasksHistory.remove(id);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(tasksHistory);
    }
}
