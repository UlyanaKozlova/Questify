package com.example.questify.data.repository;

import com.example.questify.data.local.dao.PetClothingRefDao;
import com.example.questify.data.local.dao.PetDao;
import com.example.questify.data.local.entity.PetClothingRefEntity;
import com.example.questify.data.local.entity.PetEntity;
import com.example.questify.data.mapper.PetClothingRefMapper;
import com.example.questify.domain.model.PetClothingRef;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class PetClothingRefRepository {

    private final PetClothingRefDao petClothingRefDao;
    private final PetDao petDao;
    private final ClothingRepository clothingRepository;

    @Inject
    public PetClothingRefRepository(PetClothingRefDao petClothingRefDao,
                                    PetDao petDao,
                                    ClothingRepository clothingRepository) {
        this.petClothingRefDao = petClothingRefDao;
        this.petDao = petDao;
        this.clothingRepository = clothingRepository;
    }

    public void save(PetClothingRef petClothingRef) {
        petClothingRefDao.insert(PetClothingRefMapper.toEntity(petClothingRef));
    }

    public void delete(PetClothingRefEntity petClothingRefEntity) {
        petClothingRefDao.delete(petClothingRefEntity);
    }

    public List<PetClothingRef> getAll() {
        PetEntity pet = petDao.getPet();
        if (pet == null) {
            return java.util.Collections.emptyList();
        }
        return petClothingRefDao.getAllForPet(pet.globalId)
                .stream()
                .map(PetClothingRefMapper::toDomain)
                .collect(Collectors.toList());
    }

    public void deleteAll() {
        PetEntity pet = petDao.getPet();
        if (pet == null) {
            return;
        }
        String defaultGlobalId = clothingRepository.getDefaultGlobalId();
        for (PetClothingRefEntity petClothingRefEntity : petClothingRefDao.getAllForPet(pet.globalId)) {
            if (!petClothingRefEntity.clothingGlobalId.equals(defaultGlobalId)) {
                petClothingRefDao.delete(petClothingRefEntity);
            }
        }
    }

    public void purgeForeignRefs() {
        PetEntity pet = petDao.getPet();
        if (pet == null) {
            return;
        }
        petClothingRefDao.deleteForeignRefs(pet.globalId);
    }

    public void ensureLocalClothingExists() {
        if (petClothingRefDao.getAll().isEmpty()) {
            PetEntity pet = petDao.getPet();
            if (pet != null) {
                PetClothingRefEntity petClothingRefEntity = new PetClothingRefEntity();
                petClothingRefEntity.petGlobalId = pet.globalId;
                petClothingRefEntity.clothingGlobalId = clothingRepository.getDefaultGlobalId();
                petClothingRefDao.insert(petClothingRefEntity);
            }
        }
    }

    public void resetProgress() {
        deleteAll();
        ensureLocalClothingExists();
    }

    public List<PetClothingRef> getPetClothingRefForSync() {
        PetEntity pet = petDao.getPet();
        if (pet == null) {
            return java.util.Collections.emptyList();
        }
        return petClothingRefDao.getAllForPet(pet.globalId)
                .stream()
                .map(PetClothingRefMapper::toDomain)
                .collect(Collectors.toList());
    }

    public void saveFromSync(PetClothingRef ref) {
        PetEntity pet = petDao.getPet();
        if (pet == null || !pet.globalId.equals(ref.getPetGlobalId())) {
            return;
        }
        PetClothingRefEntity existing = petClothingRefDao.getByPetAndClothing(
                ref.getPetGlobalId(), ref.getClothingGlobalId());
        if (existing == null) {
            PetClothingRefEntity entity = PetClothingRefMapper.toEntity(ref);
            petClothingRefDao.insert(entity);
        }
    }
}