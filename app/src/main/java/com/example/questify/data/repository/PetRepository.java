package com.example.questify.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.example.questify.data.local.dao.PetDao;
import com.example.questify.data.local.entity.PetEntity;
import com.example.questify.data.mapper.PetMapper;
import com.example.questify.domain.model.Pet;

import java.util.List;

import javax.inject.Inject;

public class PetRepository {

    private final PetDao petDao;

    @Inject
    public PetRepository(PetDao petDao) {
        this.petDao = petDao;
    }

    public LiveData<Pet> getPetForUser(String userGlobalId) {
        return Transformations.map(petDao.getPetForUser(userGlobalId), PetMapper::toDomain);
    }

    public void save(Pet pet) {
        PetEntity entity = PetMapper.toEntity(pet);
        entity.updatedAt = System.currentTimeMillis();
        entity.needsSync = true;
        petDao.insert(entity);
    }

    public void update(Pet pet) {
        PetEntity entity = PetMapper.toEntity(pet);
        entity.updatedAt = System.currentTimeMillis();
        entity.needsSync = true;
        petDao.update(entity);
    }

    public Pet getByGlobalId(String globalId) {
        return PetMapper.toDomain(petDao.getByGlobalId(globalId));
    }

    public List<PetEntity> getNeedingSync() {
        return petDao.getNeedingSync();
    }
}
