package com.example.questify.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.questify.data.local.entity.UserEntity;
import java.util.List;


@Dao
public interface UserDao {
    @Insert
    void insert(UserEntity entity);
    @Update
    void update(UserEntity entity);


    @Query("SELECT * FROM users WHERE globalId = :globalId LIMIT 1")
    UserEntity getUser(String globalId);
    @Query("SELECT * FROM users WHERE needsSync = 1")
    List<UserEntity> getNeedingSync();
}
