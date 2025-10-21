package com.github.betonom.java_kanban.managers;

import com.github.betonom.java_kanban.managers.filebacked.FileBackedTaskManager;
import com.github.betonom.java_kanban.managers.inmemory.InMemoryHistoryManager;
import com.github.betonom.java_kanban.managers.inmemory.InMemoryTaskManager;

import java.io.File;


public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getFileBackedTaskManager(File file) {
        return FileBackedTaskManager.loadFromFile(file);
    }
}
