package com.example.questify.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.questify.data.local.entity.UserEntity;


@Dao
public interface UserDao {
    @Insert
    void insert(UserEntity entity);

    @Update
    void update(UserEntity entity);


    @Query("SELECT * FROM users LIMIT 1")
    UserEntity getUser();

    @Query("SELECT * FROM users WHERE needsSync = 1  LIMIT 1")
    UserEntity getUserToSync();

    @Query("SELECT * FROM users LIMIT 1")
    LiveData<UserEntity> getUserLive();
}
