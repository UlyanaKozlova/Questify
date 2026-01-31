package com.example.questify.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

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
}
