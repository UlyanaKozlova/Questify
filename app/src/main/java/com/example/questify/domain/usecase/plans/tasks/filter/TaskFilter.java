package com.example.questify.domain.usecase.plans.tasks.filter;

import com.example.questify.domain.model.enums.Difficulty;
import com.example.questify.domain.model.enums.Priority;

public class TaskFilter {
    private final Priority priority;
    private final Difficulty difficulty;
    private final Long startDate;
    private final Long endDate;
    private final Boolean isDone;
    private final String projectGlobalId;

    public TaskFilter(Priority priority,
                      Difficulty difficulty,
                      Long startDate,
                      Long endDate,
                      Boolean isDone,
                      String projectGlobalId) {
        this.priority = priority;
        this.difficulty = difficulty;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isDone = isDone;
        this.projectGlobalId = projectGlobalId;
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

    public String getProjectGlobalId() {
        return projectGlobalId;
    }
}