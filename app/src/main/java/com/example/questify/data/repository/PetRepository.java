package com.example.questify.data.repository;

import android.util.Log;

import com.example.questify.UserSession;
import com.example.questify.data.local.dao.PetDao;
import com.example.questify.data.local.entity.PetEntity;
import com.example.questify.data.mapper.PetMapper;
import com.example.questify.domain.model.Pet;

import java.util.UUID;

import javax.inject.Inject;

public class PetRepository {
    private final PetDao petDao;
    private final UserSession userSession;
    private final ClothingRepository clothingRepository;

    @Inject
    public PetRepository(PetDao petDao,
                         UserSession userSession,
                         ClothingRepository clothingRepository) {
        this.petDao = petDao;
        this.userSession = userSession;
        this.clothingRepository = clothingRepository;
    }

    public Pet getPet() {
        return PetMapper.toDomain(petDao.getPet());
    }

    public void save(Pet pet) {
        PetEntity entity = PetMapper.toEntity(pet);
        entity.updatedAt = System.currentTimeMillis();
        entity.needsSync = true;
        petDao.insert(entity);
    }

    public void update(Pet pet) {
        PetEntity existing = petDao.getPet();
        if (existing == null) {
            return;
        }
        existing.currentClothingGlobalId = pet.getCurrentClothingGlobalId();
        existing.updatedAt = System.currentTimeMillis();
        existing.needsSync = true;
        petDao.update(existing);
    }

    public Pet getByGlobalId(String globalId) {
        return PetMapper.toDomain(petDao.getPetByGlobalId(globalId));
    }

    public Pet getPetToSync() {
        return PetMapper.toDomain(petDao.getPetToSync());
    }

    public String getCurrentClothingGlobalId() {
        PetEntity pet = petDao.getPet();
        return pet != null ? pet.currentClothingGlobalId : null;
    }

    public void ensureLocalPetExists() {
        if (petDao.getPet() == null) {
            PetEntity petEntity = new PetEntity();
            petEntity.globalId = UUID.randomUUID().toString();
            petEntity.userGlobalId = userSession.getUserGlobalId();
            petEntity.currentClothingGlobalId = clothingRepository.getDefaultGlobalId();
            petEntity.updatedAt = System.currentTimeMillis();
            petEntity.isDeleted = false;
            petEntity.needsSync = true;
            petDao.insert(petEntity);
        }
    }

    public void resetProgress() {
        PetEntity petEntity = petDao.getPet();
        if (petEntity != null) {
            petEntity.currentClothingGlobalId = clothingRepository.getDefaultGlobalId();
            petEntity.updatedAt = System.currentTimeMillis();
            petEntity.isDeleted = false;
            petEntity.needsSync = true;
            petDao.update(petEntity);
        }
    }
}