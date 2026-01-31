package com.example.questify.data.mapper;

import com.example.questify.data.local.entity.PetClothingRefEntity;
import com.example.questify.domain.model.PetClothingRef;

public class PetClothingRefMapper {

    public static PetClothingRef toDomain(PetClothingRefEntity entity) {
        return entity == null
                ? null
                : new PetClothingRef(entity.petGlobalId, entity.clothingGlobalId);
    }

    public static PetClothingRefEntity toEntity(PetClothingRef model) {
        if (model == null) {
            return null;
        }

        PetClothingRefEntity entity = new PetClothingRefEntity();
        entity.petGlobalId = model.getPetGlobalId();
        entity.clothingGlobalId = model.getClothingGlobalId();

        return entity;
    }
}
