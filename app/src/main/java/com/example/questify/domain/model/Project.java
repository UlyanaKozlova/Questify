package com.example.questify.domain.model;


public class Project {

    private String globalId;
    private String userGlobalId;
    private String projectName;

    private long updatedAt;

    public Project(String globalId, String userGlobalId, String projectName, long updatedAt) {
        this.globalId = globalId;
        this.userGlobalId = userGlobalId;
        this.projectName = projectName;
        this.updatedAt = updatedAt;
    }

    public String getGlobalId() {
        return globalId;
    }

    public void setGlobalId(String globalId) {
        this.globalId = globalId;
    }

    public String getUserGlobalId() {
        return userGlobalId;
    }

    public void setUserGlobalId(String userGlobalId) {
        this.userGlobalId = userGlobalId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}
