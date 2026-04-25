package com.example.questify.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.questify.data.local.entity.PetClothingRefEntity;

import java.util.List;


@Dao
public interface PetClothingRefDao {
    @Insert
    void insert(PetClothingRefEntity entity);

    @Delete
    void delete(PetClothingRefEntity petClothingRefEntity);


    @Query("SELECT * FROM pet_clothing_ref")
    List<PetClothingRefEntity> getAll();

    @Query("SELECT * FROM pet_clothing_ref WHERE petGlobalId = :petGlobalId AND clothingGlobalId = :clothingGlobalId LIMIT 1")
    PetClothingRefEntity getByPetAndClothing(String petGlobalId, String clothingGlobalId);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<PetClothingRefEntity> entities);

    @Update
    void updateAll(List<PetClothingRefEntity> entities);
}
