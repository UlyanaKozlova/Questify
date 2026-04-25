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
        return petClothingRefDao.getAll()
                .stream()
                .map(PetClothingRefMapper::toDomain)
                .collect(Collectors.toList());
    }

    public void deleteAll() {
        String defaultGlobalId = clothingRepository.getDefaultGlobalId();
        for (PetClothingRefEntity petClothingRefEntity : petClothingRefDao.getAll()) {
            if (!petClothingRefEntity.clothingGlobalId.equals(defaultGlobalId)) {
                petClothingRefDao.delete(petClothingRefEntity);
            }
        }
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
        return petClothingRefDao.getAll()
                .stream()
                .map(PetClothingRefMapper::toDomain)
                .collect(Collectors.toList());
    }

    public void saveFromSync(PetClothingRef ref) {
        PetClothingRefEntity existing = petClothingRefDao.getByPetAndClothing(
                ref.getPetGlobalId(), ref.getClothingGlobalId());
        if (existing == null) {
            PetClothingRefEntity entity = PetClothingRefMapper.toEntity(ref);
            petClothingRefDao.insert(entity);
        }
    }
}