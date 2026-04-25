package com.example.questify.data.remote.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.PropertyName;

import java.util.HashMap;
import java.util.Map;

public class UserRemote {

    @PropertyName("globalId")
    public String globalId;

    @PropertyName("username")
    public String username;

    @PropertyName("passwordHash")
    public String passwordHash;

    @PropertyName("level")
    public int level;

    @PropertyName("coins")
    public long coins;

    @PropertyName("updatedAt")
    public Timestamp updatedAt;

    @PropertyName("isDeleted")
    public boolean isDeleted;

    public UserRemote() {
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("globalId", globalId);
        map.put("username", username);
        map.put("passwordHash", passwordHash);
        map.put("level", level);
        map.put("coins", coins);
        map.put("updatedAt", updatedAt);
        map.put("isDeleted", isDeleted);
        return map;
    }
}