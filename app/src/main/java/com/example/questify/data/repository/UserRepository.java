package com.example.questify.data.repository;

import androidx.lifecycle.LiveData;

import com.example.questify.UserSession;
import com.example.questify.data.local.dao.UserDao;
import com.example.questify.data.local.entity.UserEntity;
import com.example.questify.data.mapper.UserMapper;
import com.example.questify.domain.model.User;

import javax.inject.Inject;

public class UserRepository {

    private final UserDao userDao;
    private final UserSession session;

    @Inject
    public UserRepository(UserDao userDao, UserSession session) {
        this.userDao = userDao;
        this.session = session;
    }

    public void update(User user) {
        UserEntity userEntity = UserMapper.toEntity(user);
        userEntity.updatedAt = System.currentTimeMillis();
        userEntity.needsSync = true;
        userDao.update(userEntity);
    }

    public User getUser() {
        return UserMapper.toDomain(userDao.getUser());
    }

    public LiveData<User> getUserLive() {
        return androidx.lifecycle.Transformations.map(
                userDao.getUserLive(),
                UserMapper::toDomain
        );
    }

    public void ensureLocalUserExists() {
        if (userDao.getUser() == null) {
            UserEntity userEntity = new UserEntity();
            userEntity.globalId = session.getUserGlobalId();
            userEntity.username = "LocalUser";
            userEntity.passwordHash = "12345";
            userEntity.level = 0;
            userEntity.coins = 0;
            userEntity.updatedAt = System.currentTimeMillis();
            userEntity.isDeleted = false;
            userEntity.needsSync = true;
            userDao.insert(userEntity);
        }
    }

    public void resetProgress() {
        User user = getUser();
        if (user != null) {
            user.setLevel(1);
            user.setCoins(0);
            user.setUpdatedAt(System.currentTimeMillis());
            update(user);
        }
    }


    public User getNeedingSync() {
        return UserMapper.toDomain(userDao.getUserToSync());
    }

    public void saveOrUpdateFromSync(User user) {
        UserEntity existing = userDao.getUser();
        UserEntity entity = UserMapper.toEntity(user);
        entity.updatedAt = System.currentTimeMillis();
        entity.needsSync = false;
        entity.isDeleted = false;

        if (existing != null) {
            entity.globalId = existing.globalId;
            userDao.update(entity);
        } else {
            userDao.insert(entity);
        }
    }

    public void clearSyncFlag() {
        UserEntity entity = userDao.getUser();
        if (entity != null) {
            entity.needsSync = false;
            userDao.update(entity);
        }
    }
}