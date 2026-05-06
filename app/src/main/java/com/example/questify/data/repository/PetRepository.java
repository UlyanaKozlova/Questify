package com.example.questify.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.example.questify.UserSession;
import com.example.questify.data.local.dao.PetDao;
import com.example.questify.data.local.entity.PetEntity;
import com.example.questify.data.mapper.PetMapper;
import com.example.questify.domain.model.Pet;

import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PetRepository {
    private final PetDao petDao;
    private final UserSession userSession;
    private final ClothingRepository clothingRepository;

    private final MediatorLiveData<Pet> petLive = new MediatorLiveData<>();

    @Inject
    public PetRepository(PetDao petDao,
                         UserSession userSession,
                         ClothingRepository clothingRepository) {
        this.petDao = petDao;
        this.userSession = userSession;
        this.clothingRepository = clothingRepository;
        petLive.addSource(
                androidx.lifecycle.Transformations.map(petDao.getPetLive(), PetMapper::toDomain),
                petLive::setValue
        );
    }

    public Pet getPet() {
        return PetMapper.toDomain(petDao.getPet());
    }

    public LiveData<Pet> getPetLive() {
        return petLive;
    }

    public void save(Pet pet) {
        PetEntity entity = PetMapper.toEntity(pet);
        entity.updatedAt = System.currentTimeMillis();
        entity.needsSync = true;
        petDao.insert(entity);
        petLive.postValue(PetMapper.toDomain(entity));
    }

    public void update(Pet pet) {
        PetEntity existing = petDao.getPet();
        if (existing == null) return;
        existing.currentClothingGlobalId = pet.getCurrentClothingGlobalId();
        existing.updatedAt = System.currentTimeMillis();
        existing.needsSync = true;
        petDao.update(existing);
        petLive.postValue(PetMapper.toDomain(existing));
    }

    public Pet getByGlobalId(String globalId) {
        return PetMapper.toDomain(petDao.getPetByGlobalId(globalId));
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
            petLive.postValue(PetMapper.toDomain(petEntity));
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
            petLive.postValue(PetMapper.toDomain(petEntity));
        }
    }

    public Pet getNeedingSync() {
        return PetMapper.toDomain(petDao.getPetToSync());
    }

    public void saveOrUpdateFromSync(Pet pet) {
        PetEntity existing = petDao.getPet();
        PetEntity entity = PetMapper.toEntity(pet);
        entity.userGlobalId = userSession.getUserGlobalId();
        entity.updatedAt = System.currentTimeMillis();
        entity.needsSync = false;
        entity.isDeleted = false;

        if (existing != null) {
            entity.localId = existing.localId;
            petDao.update(entity);
        } else {
            petDao.insert(entity);
        }
        petLive.postValue(PetMapper.toDomain(entity));
    }

    public void clearSyncFlag() {
        PetEntity entity = petDao.getPet();
        if (entity != null) {
            entity.needsSync = false;
            petDao.update(entity);
        }
    }
}
