package com.example.questify.data.local.entity;


import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "pet")
public class PetEntity {

    @PrimaryKey(autoGenerate = true)
    public long localId;
    @NonNull
    public String globalId;

    public String userGlobalId;
    public String currentClothingGlobalId;

    public long updatedAt;
    public boolean isDeleted;
    public boolean needsSync;
}
