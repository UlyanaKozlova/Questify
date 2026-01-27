package com.example.questify.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.example.questify.data.local.dao.TaskDao;
import com.example.questify.data.local.entity.TaskEntity;
import com.example.questify.data.mapper.TaskMapper;
import com.example.questify.domain.model.Task;

import java.util.List;
import java.util.stream.Collectors;

public class TaskRepository {

    private final TaskDao taskDao;

    public TaskRepository(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    public LiveData<List<Task>> getTasksForUser(String userGlobalId) {
        return Transformations.map(taskDao.getTasksForUser(userGlobalId),
                entities -> entities
                        .stream()
                        .map(TaskMapper::toDomain)
                        .collect(Collectors.toList())
        );
    }

    public void save(Task task) {
        TaskEntity entity = TaskMapper.toEntity(task);
        entity.updatedAt = System.currentTimeMillis();
        entity.needsSync = true;
        taskDao.insert(entity);
    }

    public void update(Task task) {
        TaskEntity entity = TaskMapper.toEntity(task);
        entity.updatedAt = System.currentTimeMillis();
        entity.needsSync = true;
        taskDao.update(entity);
    }

    public Task getByGlobalId(String globalId) {
        return TaskMapper.toDomain(taskDao.getByGlobalId(globalId));
    }

    public List<TaskEntity> getNeedingSync() {
        return taskDao.getNeedingSync();
    }
}
