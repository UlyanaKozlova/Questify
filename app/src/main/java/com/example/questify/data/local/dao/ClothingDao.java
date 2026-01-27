package com.example.questify.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.questify.data.local.entity.ClothingEntity;
import java.util.List;

@Dao
public interface ClothingDao {

    @Insert
    long insert(ClothingEntity entity);

    @Update
    void update(ClothingEntity entity);

    @Query("SELECT * FROM clothing WHERE isDeleted = 0")
    LiveData<List<ClothingEntity>> getAllActive();

    @Query("SELECT * FROM clothing WHERE globalId = :globalId LIMIT 1")
    ClothingEntity getByGlobalId(String globalId);

    @Query("SELECT * FROM clothing WHERE needsSync = 1")
    List<ClothingEntity> getNeedingSync();
}
