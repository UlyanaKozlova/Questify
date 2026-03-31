package com.example.questify.data.mapper;

import com.example.questify.data.local.entity.ClothingEntity;
import com.example.questify.domain.model.Clothing;

public class ClothingMapper {

    public static Clothing toDomain(ClothingEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Clothing(
                entity.globalId,
                entity.name,
                entity.price,
                entity.imageResId,
                entity.updatedAt
        );
    }

    public static ClothingEntity toEntity(Clothing model) {
        if (model == null) {
            return null;
        }
        ClothingEntity entity = new ClothingEntity();
        entity.globalId = model.getGlobalId();
        entity.name = model.getName();
        entity.price = model.getPrice();
        entity.imageResId = model.getImageResId();
        entity.updatedAt = model.getUpdatedAt();
        return entity;
    }
}