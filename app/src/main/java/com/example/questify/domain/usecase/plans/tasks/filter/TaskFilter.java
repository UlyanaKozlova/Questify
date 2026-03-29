package com.example.questify.domain.usecase.plans.tasks.filter;

import com.example.questify.domain.model.enums.Difficulty;
import com.example.questify.domain.model.enums.Priority;

public class TaskFilter {
    private final Priority priority;
    private final Difficulty difficulty;
    private final Long deadlineBefore;
    private final Boolean isDone;

    public TaskFilter(Priority priority, Difficulty difficulty, Long deadlineBefore, Boolean isDone) {
        this.priority = priority;
        this.difficulty = difficulty;
        this.deadlineBefore = deadlineBefore;
        this.isDone = isDone;
    }

    public boolean isEmpty() {
        return priority == null
                && difficulty == null
                && deadlineBefore == null
                && isDone == null;
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

    public Boolean getIsDone() {
        return isDone;
    }
}