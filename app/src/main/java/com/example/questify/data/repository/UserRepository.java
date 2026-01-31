package com.example.questify.data.repository;

import com.example.questify.UserSession;
import com.example.questify.data.local.dao.UserDao;
import com.example.questify.data.local.entity.UserEntity;
import com.example.questify.data.mapper.ClothingMapper;
import com.example.questify.data.mapper.UserMapper;
import com.example.questify.domain.model.User;

import java.util.List;
import java.util.stream.Collectors;

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


    public User getUserByGlobalId(String globalId) {
        return UserMapper.toDomain(userDao.getUser(globalId));
    }

    public List<User> getNeedingSync() {
        return userDao.getNeedingSync()
                .stream()
                .map(UserMapper::toDomain)
                .collect(Collectors.toList());
    }

    public void deleteProgress() {
        UserEntity userEntity = userDao.getUser(session.getUserGlobalId());
        userEntity.level = 0;
        userEntity.coins = 0;
        userEntity.updatedAt = System.currentTimeMillis();
        userEntity.isDeleted = false;
        userEntity.needsSync = true;
        userDao.update(userEntity);
    }

    public void ensureLocalUserExists() {
        if (userDao.getUser(session.getUserGlobalId()) == null) {
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
}
