package com.example.questify.domain.model;

public class Task {

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

    public Task(String globalId,
                String projectGlobalId,
                String userGlobalId,
                boolean isDone,
                String taskName,
                String description,
                Priority priority,
                Difficulty difficulty,
                long deadline,
                long updatedAt) {
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
}
