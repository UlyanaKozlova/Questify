package com.example.questify.data.repository;

import com.example.questify.R;
import com.example.questify.data.local.dao.ClothingDao;
import com.example.questify.data.local.dao.PetClothingRefDao;
import com.example.questify.data.local.dao.PetDao;
import com.example.questify.data.local.entity.ClothingEntity;
import com.example.questify.data.mapper.ClothingMapper;
import com.example.questify.domain.model.Clothing;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class ClothingRepository {

    private final ClothingDao clothingDao;
    private final PetClothingRefDao petClothingRefDao;
    private final PetDao petDao;

    private static final String DEFAULT = "default";
    public static final String DEFAULT_GLOBAL_ID = "clothing_default";

    @Inject
    public ClothingRepository(ClothingDao clothingDao,
                              PetClothingRefDao petClothingRefDao,
                              PetDao petDao) {
        this.clothingDao = clothingDao;
        this.petClothingRefDao = petClothingRefDao;
        this.petDao = petDao;
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
        if (clothing == null || clothing.getGlobalId() == null) {
            return;
        }
        clothingDao.softDelete(clothing.getGlobalId(), System.currentTimeMillis());
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
        List<ClothingEntity> defaults = clothingDao.getAllByName(DEFAULT);
        long now = System.currentTimeMillis();

        ClothingEntity canonical = null;
        for (ClothingEntity e : defaults) {
            if (DEFAULT_GLOBAL_ID.equals(e.globalId)) {
                canonical = e;
                break;
            }
        }

        if (canonical == null) {
            ClothingEntity entity = new ClothingEntity();
            entity.globalId = DEFAULT_GLOBAL_ID;
            entity.name = DEFAULT;
            entity.price = 0;
            entity.imageResId = R.drawable.pet_default;
            entity.updatedAt = now;
            entity.isDeleted = false;
            entity.needsSync = true;
            clothingDao.insert(entity);
        } else if (canonical.imageResId != R.drawable.pet_default) {
            canonical.imageResId = R.drawable.pet_default;
            canonical.updatedAt = now;
            canonical.needsSync = true;
            clothingDao.update(canonical);
        }

        for (ClothingEntity e : defaults) {
            if (DEFAULT_GLOBAL_ID.equals(e.globalId)) {
                continue;
            }
            petClothingRefDao.repointClothing(e.globalId, DEFAULT_GLOBAL_ID);
            petDao.repointCurrentClothing(e.globalId, DEFAULT_GLOBAL_ID, now);
            clothingDao.softDelete(e.globalId, now);
        }
    }

    public List<Clothing> getNeedingSync() {
        return clothingDao.getNeedingSync()
                .stream()
                .filter(e -> !e.isDeleted)
                .map(ClothingMapper::toDomain)
                .collect(Collectors.toList());
    }

    public List<Clothing> getDeletedNeedingSync() {
        return clothingDao.getSoftDeletedNeedingSync()
                .stream()
                .map(ClothingMapper::toDomain)
                .collect(Collectors.toList());
    }

    public void deleteByGlobalId(String globalId) {
        ClothingEntity entity = clothingDao.getByGlobalId(globalId);
        if (entity != null) {
            clothingDao.delete(entity);
        }
    }

    public void saveOrUpdateFromSync(Clothing clothing) {
        ClothingEntity existing = clothingDao.getByGlobalId(clothing.getGlobalId());
        if (existing != null && existing.isDeleted && existing.needsSync) {
            return;
        }
        if (existing != null && existing.updatedAt > clothing.getUpdatedAt()) {
            return;
        }
        ClothingEntity entity = ClothingMapper.toEntity(clothing);
        entity.updatedAt = clothing.getUpdatedAt();
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
