package com.example.questify.domain.model;


public class Subtask {

    private String globalId;
    private String taskGlobalId;

    private boolean isDone;
    private String subtaskName;

    private long updatedAt;

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
