package com.example.questify.data.local.entity;


import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "clothing")
public class ClothingEntity {

    @PrimaryKey(autoGenerate = true)
    public long localId;

    @NonNull
    public String globalId;

    public String name;
    public int price;
    public int imageResId;

    public long updatedAt;
    public boolean isDeleted;
    public boolean needsSync;
}
