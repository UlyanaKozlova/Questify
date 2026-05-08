package com.example.questify.data.local.entity;


import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "tasks")
public class TaskEntity {

    @PrimaryKey(autoGenerate = true)
    public long localId;

    @NonNull
    public String globalId;

    public String projectGlobalId;
    public String userGlobalId;

    public boolean isDone;
    public String taskName;
    public String description;

    public String priority;
    public String difficulty;

    public long deadline;

    public long coinsAwarded;

    public long updatedAt;
    public boolean isDeleted;
    public boolean needsSync;
}
