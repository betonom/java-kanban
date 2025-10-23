package com.github.betonom.java_kanban.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtasksId;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);

        subtasksId = new ArrayList<>();
    }

    public TaskType getType() {
        return TaskType.EPIC;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime (LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public ArrayList<Integer> getSubtasksId() {
        return subtasksId;
    }
}
