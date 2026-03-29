package com.example.questify.domain.usecase.plans.tasks.filter;

import com.example.questify.domain.model.enums.Difficulty;
import com.example.questify.domain.model.enums.Priority;

public class TaskFilter {
    private final Priority priority;
    private final Difficulty difficulty;
    private final Long startDate;
    private final Long endDate;
    private final Boolean isDone;

    public TaskFilter(Priority priority,
                      Difficulty difficulty,
                      Long startDate,
                      Long endDate,
                      Boolean isDone) {
        this.priority = priority;
        this.difficulty = difficulty;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isDone = isDone;
    }

    public Priority getPriority() {
        return priority;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public Long getStartDate() {
        return startDate;
    }

    public Long getEndDate() {
        return endDate;
    }

    public Boolean getIsDone() {
        return isDone;
    }
}