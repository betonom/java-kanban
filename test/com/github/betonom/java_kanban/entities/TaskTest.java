package com.github.betonom.java_kanban.entities;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

class TaskTest {
    @Test
    void shouldTasksEqualsWhenIdEquals() {
        Task task1 = new Task("name1", "descriprion");
        Task task2 = new Task("name2", "descriprion");
        task1.setId(1);
        task2.setId(1);
        Assertions.assertEquals(task1, task2);
    }
}