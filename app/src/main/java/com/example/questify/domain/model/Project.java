package com.example.questify.domain.model;


import androidx.annotation.NonNull;

import java.util.Objects;
import java.util.UUID;

public class Project {

    private String globalId;
    private String userGlobalId;
    private String projectName;

    private long updatedAt;

    public Project(String globalId, String userGlobalId, String projectName, long updatedAt) {
        checkProjectName(projectName);
        this.globalId = globalId;
        this.userGlobalId = userGlobalId;
        this.projectName = projectName;
        this.updatedAt = updatedAt;
    }

    public Project(String projectName) {
        checkProjectName(projectName);
        this.globalId = UUID.randomUUID().toString();
        this.projectName = projectName;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return Objects.equals(userGlobalId, project.userGlobalId) && Objects.equals(projectName, project.projectName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userGlobalId, projectName);
    }

    @NonNull
    @Override
    public String toString() {
        return projectName;
    }

    private void checkProjectName(String projectName) {
        if (projectName.length() < 3) {
            throw new IllegalArgumentException("Название проекта должно состоять не менее чем из 3 символов.");
        }
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
        checkProjectName(projectName);
        this.projectName = projectName;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}
