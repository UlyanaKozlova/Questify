package com.example.questify.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.questify.data.local.entity.UserEntity;

import java.util.List;


@Dao
public interface UserDao {
    @Insert
    void insert(UserEntity entity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrReplace(UserEntity entity);

    @Delete
    void delete(UserEntity entity);

    @Update
    void update(UserEntity entity);


    @Query("SELECT * FROM users LIMIT 1")
    UserEntity getUser();

    @Query("SELECT * FROM users WHERE needsSync = 1  LIMIT 1")
    UserEntity getUserToSync();

    @Query("SELECT * FROM users LIMIT 1")
    LiveData<UserEntity> getUserLive();

    @Query("UPDATE users SET isDeleted = 1, needsSync = 1, updatedAt = :updatedAt WHERE globalId = :globalId")
    void softDelete(String globalId, long updatedAt);


    @Query("SELECT * FROM users WHERE isDeleted = 1 AND needsSync = 1")
    List<UserEntity> getSoftDeletedNeedingSync();


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<UserEntity> entities);

    @Update
    void updateAll(List<UserEntity> entities);
}
