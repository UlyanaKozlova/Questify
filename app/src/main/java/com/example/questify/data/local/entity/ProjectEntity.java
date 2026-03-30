package com.example.questify.data.local.entity;


import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "projects")
public class ProjectEntity {

    @PrimaryKey(autoGenerate = true)
    public long localId;

    @NonNull
    public String globalId;

    public String userGlobalId;
    public String projectName;
    public String color;

    public long updatedAt;
    public boolean isDeleted;
    public boolean needsSync;
}
