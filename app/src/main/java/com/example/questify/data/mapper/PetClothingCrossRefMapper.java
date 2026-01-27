package com.example.questify.data.mapper;

import com.example.questify.data.local.entity.PetClothingCrossRefEntity;
import com.example.questify.domain.model.PetClothingLink;

public class PetClothingCrossRefMapper {

    public static PetClothingLink toDomain(PetClothingCrossRefEntity entity) {
        return entity == null
                ? null
                : new PetClothingLink(entity.petGlobalId, entity.clothingGlobalId);
    }

    public static PetClothingCrossRefEntity toEntity(PetClothingLink model) {
        if (model == null) {
            return null;
        }

        PetClothingCrossRefEntity entity = new PetClothingCrossRefEntity();
        entity.petGlobalId = model.getPetGlobalId();
        entity.clothingGlobalId = model.getClothingGlobalId();

        return entity;
    }
}
