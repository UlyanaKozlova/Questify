package com.example.questify.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import com.example.questify.data.local.dao.UserDao;
import com.example.questify.data.local.entity.UserEntity;
import com.example.questify.data.mapper.UserMapper;
import com.example.questify.domain.model.User;
import java.util.List;

public class UserRepository {

    private final UserDao userDao;

    public UserRepository(UserDao userDao) {
        this.userDao = userDao;
    }

    public LiveData<User> getUser(String globalId) {
        return Transformations.map(userDao.getUser(globalId), UserMapper::toDomain);
    }

    public void save(User user) {
        UserEntity entity = UserMapper.toEntity(user);
        entity.updatedAt = System.currentTimeMillis();
        entity.needsSync = true;
        userDao.insert(entity);
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
