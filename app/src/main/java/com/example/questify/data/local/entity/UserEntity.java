package com.example.questify.data.local.entity;


import androidx.room.Entity;
import androidx.annotation.NonNull;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class UserEntity {

    @PrimaryKey
    @NonNull
    public String globalId;

    public String username;
    public String passwordHash;

    public int level;
    public long coins;

    public long updatedAt;
    public boolean isDeleted;
    public boolean needsSync;
}
