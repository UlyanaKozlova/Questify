package com.example.questify.data.mapper;

import com.example.questify.data.local.entity.ClothingEntity;
import com.example.questify.domain.model.Clothing;

public class ClothingMapper {

    public static Clothing toDomain(ClothingEntity entity) {
        return entity == null
                ? null
                : new Clothing(entity.globalId, entity.name, entity.price, entity.updatedAt);
    }

    public static ClothingEntity toEntity(Clothing model) {
        if (model == null) {
            return null;
        }

        ClothingEntity entity = new ClothingEntity();
        entity.globalId = model.getGlobalId();
        entity.name = model.getName();
        entity.price = model.getPrice();
        entity.updatedAt = model.getUpdatedAt();

        return entity;
    }
}
