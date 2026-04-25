package com.example.questify.data.remote.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.PropertyName;

import java.util.HashMap;
import java.util.Map;

public class PetRemote {

    @PropertyName("globalId")
    public String globalId;

    @PropertyName("userGlobalId")
    public String userGlobalId;

    @PropertyName("currentClothingGlobalId")
    public String currentClothingGlobalId;

    @PropertyName("updatedAt")
    public Timestamp updatedAt;

    @PropertyName("isDeleted")
    public boolean isDeleted;

    public PetRemote() {
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("globalId", globalId);
        map.put("userGlobalId", userGlobalId);
        map.put("currentClothingGlobalId", currentClothingGlobalId);
        map.put("updatedAt", updatedAt);
        map.put("isDeleted", isDeleted);
        return map;
    }
}