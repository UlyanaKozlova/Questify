package com.example.questify.data.mapper;

import com.example.questify.data.local.entity.UserEntity;
import com.example.questify.domain.model.User;

public class UserMapper {

    public static User toDomain(UserEntity entity) {
        return entity == null
                ? null
                : new User(entity.globalId,
                entity.username,
                entity.passwordHash,
                entity.level,
                entity.coins,
                entity.updatedAt);
    }

    public static UserEntity toEntity(User model) {
        if (model == null) {
            return null;
        }

        UserEntity entity = new UserEntity();
        entity.globalId = model.getGlobalId();
        entity.username = model.getUsername();
        entity.passwordHash = model.getPasswordHash();
        entity.level = model.getLevel();
        entity.coins = model.getCoins();
        entity.updatedAt = model.getUpdatedAt();

        return entity;
    }
}
