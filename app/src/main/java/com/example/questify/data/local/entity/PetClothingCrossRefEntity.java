package com.example.questify.data.local.entity;


import androidx.room.Entity;
import androidx.annotation.NonNull;

@Entity(
        tableName = "pet_clothing_cross_ref",
        primaryKeys = {"petGlobalId", "clothingGlobalId"}
)
public class PetClothingCrossRefEntity {

    @NonNull
    public String petGlobalId;

    @NonNull
    public String clothingGlobalId;
}
