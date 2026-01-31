package com.example.questify.domain.usecase.plans.tasks.filter;

import com.example.questify.domain.model.Difficulty;
import com.example.questify.domain.model.Priority;

public class TaskFilter {
    private final Priority priority;
    private final Difficulty difficulty;
    private final Long deadlineBefore;

    public TaskFilter(Priority priority, Difficulty difficulty, Long deadlineBefore) {
        this.priority = priority;
        this.difficulty = difficulty;
        this.deadlineBefore = deadlineBefore;
    }

    public Priority getPriority() {
        return priority;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }
    public Long getDeadlineBefore() {
        return deadlineBefore;
    }
}
