package com.example.questify.data.repository;

import com.example.questify.data.local.dao.PetClothingRefDao;
import com.example.questify.data.local.entity.PetClothingRefEntity;
import com.example.questify.data.mapper.PetClothingRefMapper;
import com.example.questify.domain.model.PetClothingRef;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class PetClothingRefRepository {

    private final PetClothingRefDao petClothingRefDao;
    private final PetRepository petRepository;
    private final ClothingRepository clothingRepository;

    @Inject
    public PetClothingRefRepository(PetClothingRefDao petClothingRefDao,
                                    PetRepository petRepository,
                                    ClothingRepository clothingRepository) {
        this.petClothingRefDao = petClothingRefDao;
        this.petRepository = petRepository;
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
            PetClothingRefEntity petClothingRefEntity = new PetClothingRefEntity();
            petClothingRefEntity.petGlobalId = petRepository.getPetForUser().getGlobalId();
            petClothingRefEntity.clothingGlobalId = clothingRepository.getDefaultGlobalId();
            petClothingRefDao.insert(petClothingRefEntity);
        }
    }
}
