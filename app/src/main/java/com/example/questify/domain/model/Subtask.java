package com.example.questify.domain.model;


import java.util.Objects;

public class Subtask {

    private String globalId;
    private String taskGlobalId;

    private boolean isDone;
    private String subtaskName;

    private long updatedAt;

    public Subtask(String globalId, String taskGlobalId, boolean isDone, String subtaskName, long updatedAt) {
        this.globalId = globalId;
        this.taskGlobalId = taskGlobalId;
        this.isDone = isDone;
        this.subtaskName = subtaskName;
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Subtask subtask = (Subtask) o;
        return Objects.equals(taskGlobalId, subtask.taskGlobalId)
                && Objects.equals(subtaskName, subtask.subtaskName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskGlobalId, subtaskName);
    }

    public String getGlobalId() {
        return globalId;
    }

    public void setGlobalId(String globalId) {
        this.globalId = globalId;
    }

    public String getTaskGlobalId() {
        return taskGlobalId;
    }

    public void setTaskGlobalId(String taskGlobalId) {
        this.taskGlobalId = taskGlobalId;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public String getSubtaskName() {
        return subtaskName;
    }

    public void setSubtaskName(String subtaskName) {
        this.subtaskName = subtaskName;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}
