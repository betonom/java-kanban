package com.github.betonom.java_kanban.entities;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class EpicTest {

    @Test
    void shouldEpicsEqualsWhenIdEquals() {
        Epic epic1 = new Epic("name1", "descriprion");
        Epic epic2 = new Epic("name2", "descriprion");
        epic1.setId(1);
        epic2.setId(1);
        Assertions.assertEquals(epic1, epic2);
    }
}