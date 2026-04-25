package com.example.questify.data.remote.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.PropertyName;

import java.util.HashMap;
import java.util.Map;

public class ProjectRemote {

    @PropertyName("globalId")
    public String globalId;

    @PropertyName("userGlobalId")
    public String userGlobalId;

    @PropertyName("projectName")
    public String projectName;

    @PropertyName("color")
    public String color;

    @PropertyName("updatedAt")
    public Timestamp updatedAt;

    @PropertyName("isDeleted")
    public boolean isDeleted;

    public ProjectRemote() {
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("globalId", globalId);
        map.put("userGlobalId", userGlobalId);
        map.put("projectName", projectName);
        map.put("color", color);
        map.put("updatedAt", updatedAt);
        map.put("isDeleted", isDeleted);
        return map;
    }
}