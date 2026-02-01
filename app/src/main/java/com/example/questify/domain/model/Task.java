package com.example.questify.domain.model;

import java.util.Objects;
import java.util.UUID;

public class Task {
    private long localId;

    private String globalId;

    private String projectGlobalId;
    private String userGlobalId;

    private boolean isDone;
    private String taskName;
    private String description;

    private Priority priority;
    private Difficulty difficulty;

    private long deadline;

    private long updatedAt;

    public Task(long localId,
                String globalId,
                String projectGlobalId,
                String userGlobalId,
                boolean isDone,
                String taskName,
                String description,
                Priority priority,
                Difficulty difficulty,
                long deadline,
                long updatedAt) {
        this.localId = localId;
        this.globalId = globalId;
        this.projectGlobalId = projectGlobalId;
        this.userGlobalId = userGlobalId;
        this.isDone = isDone;
        this.taskName = taskName;
        this.description = description;
        this.priority = priority;
        this.difficulty = difficulty;
        this.deadline = deadline;
        this.updatedAt = updatedAt;
    }

    public Task(String projectGlobalId,
                String taskName,
                String description,
                Priority priority,
                Difficulty difficulty,
                long deadline) {
        this.globalId = UUID.randomUUID().toString();
        this.projectGlobalId = projectGlobalId;
        this.isDone = false;
        this.taskName = taskName;
        this.description = description;
        this.priority = priority;
        this.difficulty = difficulty;
        this.deadline = deadline;
        this.updatedAt = System.currentTimeMillis();
    }

    public Task() {
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return isDone == task.isDone
                && deadline == task.deadline
                && Objects.equals(projectGlobalId, task.projectGlobalId)
                && Objects.equals(userGlobalId, task.userGlobalId)
                && Objects.equals(taskName, task.taskName)
                && Objects.equals(description, task.description)
                && priority == task.priority
                && difficulty == task.difficulty;
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectGlobalId, userGlobalId, isDone, taskName, description, priority, difficulty, deadline);
    }

    public String getGlobalId() {
        return globalId;
    }

    public void setGlobalId(String globalId) {
        this.globalId = globalId;
    }

    public String getProjectGlobalId() {
        return projectGlobalId;
    }

    public void setProjectGlobalId(String projectGlobalId) {
        this.projectGlobalId = projectGlobalId;
    }

    public String getUserGlobalId() {
        return userGlobalId;
    }

    public void setUserGlobalId(String userGlobalId) {
        this.userGlobalId = userGlobalId;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public long getDeadline() {
        return deadline;
    }

    public void setDeadline(long deadline) {
        this.deadline = deadline;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public long getLocalId() {
        return localId;
    }

    public void setLocalId(long localId) {
        this.localId = localId;
    }
}
