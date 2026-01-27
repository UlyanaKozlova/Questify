package com.example.questify.data.local.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
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

    @Query("SELECT * FROM pet WHERE userGlobalId = :userGlobalId LIMIT 1")
    LiveData<PetEntity> getPetForUser(String userGlobalId);

    @Query("SELECT * FROM pet WHERE globalId = :globalId LIMIT 1")
    PetEntity getByGlobalId(String globalId);

    @Query("SELECT * FROM pet WHERE needsSync = 1")
    List<PetEntity> getNeedingSync();
}
