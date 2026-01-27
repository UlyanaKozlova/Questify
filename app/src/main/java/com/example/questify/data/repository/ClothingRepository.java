package com.example.questify.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.example.questify.data.local.dao.ClothingDao;
import com.example.questify.data.local.entity.ClothingEntity;
import com.example.questify.data.mapper.ClothingMapper;
import com.example.questify.domain.model.Clothing;

import java.util.List;
import java.util.stream.Collectors;

public class ClothingRepository {

    private final ClothingDao clothingDao;

    public ClothingRepository(ClothingDao clothingDao) {
        this.clothingDao = clothingDao;
    }

    public LiveData<List<Clothing>> getAllActive() {
        return Transformations.map(clothingDao.getAllActive(),
                entities -> entities
                        .stream()
                        .map(ClothingMapper::toDomain)
                        .collect(Collectors.toList())
        );
    }

    public void save(Clothing clothing) {
        ClothingEntity entity = ClothingMapper.toEntity(clothing);
        entity.updatedAt = System.currentTimeMillis();
        entity.needsSync = true;
        clothingDao.insert(entity);
    }

    public void update(Clothing clothing) {
        ClothingEntity entity = ClothingMapper.toEntity(clothing);
        entity.updatedAt = System.currentTimeMillis();
        entity.needsSync = true;
        clothingDao.update(entity);
    }

    public Clothing getByGlobalId(String globalId) {
        return ClothingMapper.toDomain(clothingDao.getByGlobalId(globalId));
    }

    public List<ClothingEntity> getNeedingSync() {
        return clothingDao.getNeedingSync();
    }
}
