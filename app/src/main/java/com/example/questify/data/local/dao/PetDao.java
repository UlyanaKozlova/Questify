package com.example.questify.data.local.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.questify.data.local.entity.PetEntity;

import java.util.List;

@Dao
public interface PetDao {

    @Insert
    long insert(PetEntity entity);

    @Update
    void update(PetEntity entity);


    @Query("SELECT * FROM pet LIMIT 1")
    PetEntity getPet();

    @Query("SELECT * FROM pet LIMIT 1")
    LiveData<PetEntity> getPetLive();

    @Query("SELECT * FROM pet WHERE globalId = :globalId LIMIT 1")
    PetEntity getPetByGlobalId(String globalId);

    @Query("SELECT * FROM pet WHERE needsSync = 1 LIMIT 1")
    PetEntity getPetToSync();


    @Query("UPDATE pet SET isDeleted = 1, needsSync = 1, updatedAt = :updatedAt WHERE globalId = :globalId")
    void softDelete(String globalId, long updatedAt);

    @Query("SELECT * FROM pet WHERE isDeleted = 1 AND needsSync = 1")
    List<PetEntity> getSoftDeletedNeedingSync();


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<PetEntity> entities);

    @Update
    void updateAll(List<PetEntity> entities);
}
