package com.github.betonom.java_kanban.managers.inmemory;

import com.github.betonom.java_kanban.managers.HistoryManager;
import com.github.betonom.java_kanban.managers.Managers;
import com.github.betonom.java_kanban.managers.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class ManagersTest {

    @Test
    void getDefault() {
        TaskManager taskManager = Managers.getDefault();

        Assertions.assertNotNull(taskManager, "Возвращается null");
    }

    @Test
    void getDefaultHistory() {
        HistoryManager historyManager = Managers.getDefaultHistory();

        Assertions.assertNotNull(historyManager, "Возвращается null");
    }
}