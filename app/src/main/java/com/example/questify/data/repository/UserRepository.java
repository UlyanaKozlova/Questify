package com.example.questify.data.repository;

import com.example.questify.UserSession;
import com.example.questify.data.local.dao.UserDao;
import com.example.questify.data.local.entity.UserEntity;
import com.example.questify.data.mapper.UserMapper;
import com.example.questify.domain.model.User;

import java.util.List;

import javax.inject.Inject;

public class UserRepository {

    private final UserDao userDao;
    private final UserSession session;
    @Inject
    public UserRepository(UserDao userDao, UserSession session) {
        this.userDao = userDao;
        this.session = session;
    }
    public void ensureLocalUserExists() {
        UserEntity userEntity = userDao.getUserSync(session.getUserGlobalId());
        if (userEntity == null) {
            UserEntity user = new UserEntity();
            user.globalId = session.getUserGlobalId();
            user.username = "LocalUser";
            user.passwordHash = "12345";
            user.level = 0;
            user.coins = 0;
            user.updatedAt = System.currentTimeMillis();
            user.isDeleted = false;
            user.needsSync = false;
            userDao.insert(user);
        }
    }

    public void deleteProgress() {
        UserEntity userEntity = userDao.getUserSync(session.getUserGlobalId());
        userEntity.level = 0;
        userEntity.coins = 0;
        userEntity.updatedAt = System.currentTimeMillis();
        userEntity.isDeleted = false;
        userEntity.needsSync = true;
        userDao.update(userEntity);
    }
    public void update(User user) {
        UserEntity userEntity = UserMapper.toEntity(user);
        userEntity.updatedAt = System.currentTimeMillis();
        userEntity.needsSync = true;
        userDao.update(userEntity);
    }

    public User getUserSync(String globalId) {
        return UserMapper.toDomain(userDao.getUserSync(globalId));
    }

    public List<UserEntity> getNeedingSync() {
        return userDao.getNeedingSync();
    }
}
