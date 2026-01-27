package com.example.questify.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.questify.data.local.entity.PetClothingCrossRefEntity;
import java.util.List;


@Dao
public interface PetClothingCrossRefDao {
    @Insert
    void insert(PetClothingCrossRefEntity entity);

    @Query("DELETE FROM pet_clothing_cross_ref WHERE petGlobalId = :petGlobalId AND clothingGlobalId = :clothingGlobalId")
    void delete(String petGlobalId, String clothingGlobalId);

    @Query("SELECT clothingGlobalId FROM pet_clothing_cross_ref WHERE petGlobalId = :petGlobalId")
    List<String> getClothingForPet(String petGlobalId);
}
