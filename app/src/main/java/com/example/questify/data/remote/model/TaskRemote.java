package com.example.questify.data.remote.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.PropertyName;

import java.util.HashMap;
import java.util.Map;

public class TaskRemote {

    @PropertyName("globalId")
    public String globalId;

    @PropertyName("projectGlobalId")
    public String projectGlobalId;

    @PropertyName("userGlobalId")
    public String userGlobalId;

    @PropertyName("isDone")
    public boolean isDone;

    @PropertyName("taskName")
    public String taskName;

    @PropertyName("description")
    public String description;

    @PropertyName("priority")
    public String priority;

    @PropertyName("difficulty")
    public String difficulty;

    @PropertyName("deadline")
    public Timestamp deadline;

    @PropertyName("updatedAt")
    public Timestamp updatedAt;

    @PropertyName("isDeleted")
    public boolean isDeleted;

    public TaskRemote() {
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("globalId", globalId);
        map.put("projectGlobalId", projectGlobalId);
        map.put("userGlobalId", userGlobalId);
        map.put("isDone", isDone);
        map.put("taskName", taskName);
        map.put("description", description);
        map.put("priority", priority);
        map.put("difficulty", difficulty);
        map.put("deadline", deadline);
        map.put("updatedAt", updatedAt);
        map.put("isDeleted", isDeleted);
        return map;
    }
}