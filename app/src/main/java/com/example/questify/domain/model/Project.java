package com.example.questify.domain.model;


import android.content.Context;

import androidx.annotation.NonNull;

import com.example.questify.R;

import java.util.Objects;
import java.util.UUID;

public class Project {

    private static final String DEFAULT_COLOR = "#FF6200EE"; // todo
    private String globalId;
    private String userGlobalId;
    private String projectName;
    private String color;
    private long updatedAt;

    public Project(String globalId,
                   String userGlobalId,
                   String projectName,
                   String color,
                   long updatedAt,
                   Context context) {
        checkProjectName(projectName, context);
        this.globalId = globalId;
        this.userGlobalId = userGlobalId;
        this.projectName = projectName;
        this.color = color;
        this.updatedAt = updatedAt;
    }

    public Project(String globalId, String userGlobalId, String projectName, String color, long updatedAt) {
        this.globalId = globalId;
        this.userGlobalId = userGlobalId;
        this.projectName = projectName;
        this.color = color;
        this.updatedAt = updatedAt;
    }

    public Project(String projectName,
                   String color,
                   Context context) {
        checkProjectName(projectName, context);
        this.globalId = UUID.randomUUID().toString();
        this.projectName = projectName;
        this.color = color;
    }
    public Project(String projectName,
                   String color) {
        this.globalId = UUID.randomUUID().toString();
        this.projectName = projectName;
        this.color = color;
    }

    public Project(String projectName,
                   Context context) {
        this(projectName, DEFAULT_COLOR, context);
    }

    private void checkProjectName(String projectName,
                                  Context context) {
        if (projectName.length() < 3) {
            throw new IllegalArgumentException(context.getString(R.string.error_project_name_short));
        }
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

    public void setProjectName(String projectName,
                               Context context) {
        checkProjectName(projectName, context);
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