package com.example.questify.data.local.entity;


import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "subtasks")
public class SubtaskEntity {

    @PrimaryKey(autoGenerate = true)
    public long localId;

    @NonNull
    public String globalId = "";

    public String taskGlobalId;
    public String userGlobalId;

    public boolean isDone;
    public String subtaskName;

    public long updatedAt;
    public boolean isDeleted;
    public boolean needsSync;
}
