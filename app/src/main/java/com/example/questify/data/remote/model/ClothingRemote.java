package com.example.questify.data.remote.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.PropertyName;

import java.util.HashMap;
import java.util.Map;

public class ClothingRemote {

    @PropertyName("globalId")
    public String globalId;

    @PropertyName("name")
    public String name;

    @PropertyName("price")
    public int price;

    @PropertyName("imageResId")
    public int imageResId;

    @PropertyName("updatedAt")
    public Timestamp updatedAt;

    @PropertyName("isDeleted")
    public boolean isDeleted;

    public ClothingRemote() {
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("globalId", globalId);
        map.put("name", name);
        map.put("price", price);
        map.put("imageResId", imageResId);
        map.put("updatedAt", updatedAt);
        map.put("isDeleted", isDeleted);
        return map;
    }
}