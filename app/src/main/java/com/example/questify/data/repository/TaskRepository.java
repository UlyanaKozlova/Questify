package com.example.questify.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.example.questify.data.local.dao.TaskDao;
import com.example.questify.data.local.entity.TaskEntity;
import com.example.questify.data.mapper.TaskMapper;
import com.example.questify.domain.model.Task;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class TaskRepository {

    private final TaskDao taskDao;

    @Inject
    public TaskRepository(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    public void save(Task task) {
        TaskEntity entity = TaskMapper.toEntity(task);
        entity.updatedAt = System.currentTimeMillis();
        entity.needsSync = true;
        entity.isDeleted = false;

        taskDao.insert(entity);
    }

    public void update(Task task) {
        TaskEntity entity = TaskMapper.toEntity(task);
        entity.updatedAt = System.currentTimeMillis();
        entity.needsSync = true;
        taskDao.update(entity);
    }

    public void delete(Task task) {
        taskDao.delete(TaskMapper.toEntity(task));
    }

    public void deleteAll() {
        for (TaskEntity taskEntity : taskDao.getAll()) {
            taskDao.delete(taskEntity);
        }
    }

    public List<Task> getAll() {
        return taskDao.getAll()
                .stream()
                .map(TaskMapper::toDomain)
                .collect(Collectors.toList());
    }
    public LiveData<List<Task>> getAllLive() {
        return Transformations.map(taskDao.getAllLive(),
                list -> list.stream()
                        .map(TaskMapper::toDomain)
                        .collect(Collectors.toList()));
    }


    public Task getByGlobalId(String globalId) {
        return TaskMapper.toDomain(taskDao.getByGlobalId(globalId));
    }

    public List<Task> getNeedingSync() {
        return taskDao.getNeedingSync()
                .stream()
                .map(TaskMapper::toDomain)
                .collect(Collectors.toList());
    }
}
