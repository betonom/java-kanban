package com.github.betonom.java_kanban.entities;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class SubtaskTest {
    @Test
    void shouldSubtasksEqualsWhenIdEquals() {
        Subtask subtask1 = new Subtask("name1", "descriprion", 3);
        Subtask subtask2 = new Subtask("name2", "descriprion", 1);
        subtask1.setId(1);
        subtask2.setId(1);
        Assertions.assertEquals(subtask1, subtask2);
    }
}