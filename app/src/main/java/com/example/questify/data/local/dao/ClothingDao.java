package com.example.questify.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.questify.data.local.entity.ClothingEntity;

import java.util.List;

@Dao
public interface ClothingDao {

    @Insert
    long insert(ClothingEntity clothingEntity);

    @Update
    void update(ClothingEntity clothingEntity);

    @Delete
    void delete(ClothingEntity clothingEntity);

    @Query("SELECT * FROM clothing WHERE isDeleted = 0")
    List<ClothingEntity> getAll();

    @Query("SELECT * FROM clothing WHERE globalId = :globalId LIMIT 1")
    ClothingEntity getByGlobalId(String globalId);

    @Query("SELECT * FROM clothing WHERE needsSync = 1")
    List<ClothingEntity> getNeedingSync();

    @Query("SELECT globalId FROM clothing WHERE name = 'default' LIMIT 1")
    String getDefaultGlobalId();

    @Query("DELETE FROM clothing")
    void deleteAll();
}