package com.example.questify.data.mapper;

import com.example.questify.data.local.entity.PetEntity;
import com.example.questify.domain.model.Pet;

public class PetMapper {

    public static Pet toDomain(PetEntity entity) {
        return entity == null
                ? null
                : new Pet(entity.globalId, entity.userGlobalId, entity.currentClothingGlobalId, entity.updatedAt);
    }

    public static PetEntity toEntity(Pet model) {
        if (model == null) {
            return null;
        }

        PetEntity entity = new PetEntity();
        entity.globalId = model.getGlobalId();
        entity.userGlobalId = model.getUserGlobalId();
        entity.currentClothingGlobalId = model.getCurrentClothingGlobalId();
        entity.updatedAt = model.getUpdatedAt();

        return entity;
    }
}
