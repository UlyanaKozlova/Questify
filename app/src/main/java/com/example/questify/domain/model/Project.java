package com.example.questify.domain.model;

import androidx.annotation.NonNull;

import com.example.questify.R;

import java.util.Objects;
import java.util.UUID;

public class Project {

    private static final String DEFAULT_COLOR = "#FF6200EE";
    private String globalId;
    private String userGlobalId;
    private String projectName;
    private String color;
    private long updatedAt;

    public Project(String globalId, String userGlobalId, String projectName, String color, long updatedAt) {
        this.globalId = globalId;
        this.userGlobalId = userGlobalId;
        this.projectName = projectName;
        this.color = color;
        this.updatedAt = updatedAt;
    }

    public Project(String projectName, String color) {
        checkProjectName(projectName);
        this.globalId = UUID.randomUUID().toString();
        this.projectName = projectName;
        this.color = color;
    }

    public Project(String projectName) {
        this(projectName, DEFAULT_COLOR);
    }

    private static void checkProjectName(String projectName) {
        if (projectName == null || projectName.length() < 3) {
            throw new DomainValidationException(R.string.error_project_name_short);
        }
    }

    public static void validate(String projectName) {
        checkProjectName(projectName);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return Objects.equals(globalId, project.globalId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(globalId);
    }

    @NonNull
    @Override
    public String toString() {
        return projectName;
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}
