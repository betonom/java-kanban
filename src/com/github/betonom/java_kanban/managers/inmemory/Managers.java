package com.github.betonom.java_kanban.managers.inmemory;

import com.github.betonom.java_kanban.managers.HistoryManager;
import com.github.betonom.java_kanban.managers.TaskManager;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
