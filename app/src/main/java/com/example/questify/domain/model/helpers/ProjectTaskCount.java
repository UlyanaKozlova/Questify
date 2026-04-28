package com.example.questify.domain.model.helpers;

public class ProjectTaskCount {
    public final String projectName;
    public final String color;
    public final int taskCount;

    public ProjectTaskCount(String projectName, String color, int taskCount) {
        this.projectName = projectName;
        this.color = color;
        this.taskCount = taskCount;
    }
}
