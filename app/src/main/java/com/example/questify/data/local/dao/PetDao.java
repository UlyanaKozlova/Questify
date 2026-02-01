package com.example.questify.data.local.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.questify.data.local.entity.PetEntity;

@Dao
public interface PetDao {

    @Insert
    long insert(PetEntity entity);

    @Update
    void update(PetEntity entity);


    @Query("SELECT * FROM pet LIMIT 1")
    PetEntity getPet();

    @Query("SELECT * FROM pet WHERE globalId = :globalId LIMIT 1")
    PetEntity getPetByGlobalId(String globalId);

    @Query("SELECT * FROM pet WHERE needsSync = 1 LIMIT 1")
    PetEntity getPetToSync();
}
