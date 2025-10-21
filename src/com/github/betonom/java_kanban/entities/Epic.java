package com.github.betonom.java_kanban.entities;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtasksId;

    public Epic(String name, String description) {
        super(name, description);

        subtasksId = new ArrayList<>();
    }

    public TaskType getType() {
        return TaskType.EPIC;
    }

    public ArrayList<Integer> getSubtasksId() {
        return subtasksId;
    }
}
