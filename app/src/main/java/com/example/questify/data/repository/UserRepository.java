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
        UserEntity existing = userDao.getUserSync(session.getUserGlobalId());
        if (existing == null) {
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
    public void update(User user) {
        UserEntity entity = UserMapper.toEntity(user);
        entity.updatedAt = System.currentTimeMillis();
        entity.needsSync = true;
        userDao.update(entity);
    }

    public User getUserSync(String globalId) {
        return UserMapper.toDomain(userDao.getUserSync(globalId));
    }

    public List<UserEntity> getNeedingSync() {
        return userDao.getNeedingSync();
    }
}
