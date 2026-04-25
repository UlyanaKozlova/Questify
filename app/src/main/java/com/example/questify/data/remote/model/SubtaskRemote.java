package com.example.questify.data.remote.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.PropertyName;

import java.util.HashMap;
import java.util.Map;

public class SubtaskRemote {

    @PropertyName("globalId")
    public String globalId;

    @PropertyName("taskGlobalId")
    public String taskGlobalId;

    @PropertyName("isDone")
    public boolean isDone;

    @PropertyName("subtaskName")
    public String subtaskName;

    @PropertyName("updatedAt")
    public Timestamp updatedAt;

    @PropertyName("isDeleted")
    public boolean isDeleted;

    public SubtaskRemote() {
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("globalId", globalId);
        map.put("taskGlobalId", taskGlobalId);
        map.put("isDone", isDone);
        map.put("subtaskName", subtaskName);
        map.put("updatedAt", updatedAt);
        map.put("isDeleted", isDeleted);
        return map;
    }
}