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
}
