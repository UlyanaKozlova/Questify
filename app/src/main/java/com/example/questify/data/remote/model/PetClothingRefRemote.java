package com.example.questify.data.remote.model;

import com.google.firebase.firestore.PropertyName;

import java.util.HashMap;
import java.util.Map;

public class PetClothingRefRemote {

    @PropertyName("petGlobalId")
    public String petGlobalId;

    @PropertyName("clothingGlobalId")
    public String clothingGlobalId;

    public PetClothingRefRemote() {
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("petGlobalId", petGlobalId);
        map.put("clothingGlobalId", clothingGlobalId);
        return map;
    }
}