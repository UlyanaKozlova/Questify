package com.example.questify.data.local.entity;


import androidx.room.Entity;
import androidx.annotation.NonNull;

@Entity(
        tableName = "pet_clothing_ref",
        primaryKeys = {"petGlobalId", "clothingGlobalId"}
)
public class PetClothingRefEntity {
    @NonNull
    public String petGlobalId;

    @NonNull
    public String clothingGlobalId;
}
