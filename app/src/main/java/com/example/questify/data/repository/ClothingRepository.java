package com.example.questify.data.repository;

import com.example.questify.data.local.dao.ClothingDao;
import com.example.questify.data.local.entity.ClothingEntity;
import com.example.questify.data.mapper.ClothingMapper;
import com.example.questify.domain.model.Clothing;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class ClothingRepository {

    private final ClothingDao clothingDao;
    private static final String DEFAULT = "default";

    @Inject
    public ClothingRepository(ClothingDao clothingDao) {
        this.clothingDao = clothingDao;
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

    public void delete(Clothing clothing) {
        clothingDao.delete(ClothingMapper.toEntity(clothing));
    }

    public List<Clothing> getAll() {
        return clothingDao.getAll()
                .stream()
                .map(ClothingMapper::toDomain)
                .collect(Collectors.toList());
    }

    public Clothing getByGlobalId(String globalId) {
        return ClothingMapper.toDomain(clothingDao.getByGlobalId(globalId));
    }

    public String getDefaultGlobalId() {
        return clothingDao.getDefaultGlobalId();
    }

    public void ensureLocalClothingExists() {
        if (clothingDao.getAll().isEmpty()) {
            ClothingEntity clothingEntity = new ClothingEntity();
            clothingEntity.globalId = UUID.randomUUID().toString();
            clothingEntity.name = DEFAULT;
            clothingEntity.price = 0;
            clothingEntity.imageResId = 0;
            clothingEntity.updatedAt = System.currentTimeMillis();
            clothingEntity.isDeleted = false;
            clothingEntity.needsSync = true;
            clothingDao.insert(clothingEntity);
        }
    }

    public List<Clothing> getNeedingSync() {
        return clothingDao.getNeedingSync()
                .stream()
                .map(ClothingMapper::toDomain)
                .collect(Collectors.toList());
    }
    public void saveOrUpdateFromSync(Clothing clothing) {
        ClothingEntity existing = clothingDao.getByGlobalId(clothing.getGlobalId());
        ClothingEntity entity = ClothingMapper.toEntity(clothing);
        entity.updatedAt = System.currentTimeMillis();
        entity.needsSync = false;
        entity.isDeleted = false;
        if (existing != null) {
            entity.localId = existing.localId;
            clothingDao.update(entity);
        } else {
            clothingDao.insert(entity);
        }
    }
    public void clearSyncFlag(String globalId) {
        ClothingEntity entity = clothingDao.getByGlobalId(globalId);
        if (entity != null) {
            entity.needsSync = false;
            clothingDao.update(entity);
        }
    }
}