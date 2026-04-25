package com.example.questify.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.example.questify.UserSession;
import com.example.questify.data.local.dao.TaskDao;
import com.example.questify.data.local.entity.TaskEntity;
import com.example.questify.data.mapper.TaskMapper;
import com.example.questify.domain.model.Task;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class TaskRepository {

    private final TaskDao taskDao;
    private final UserSession userSession;

    @Inject
    public TaskRepository(TaskDao taskDao, UserSession userSession) {
        this.taskDao = taskDao;
        this.userSession = userSession;
    }

    public void save(Task task) {
        TaskEntity entity = TaskMapper.toEntity(task);
        entity.userGlobalId = userSession.getUserGlobalId();
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

    public List<Task> getTasksByProject(String projectGlobalId) {
        return taskDao.getTasksByProject(projectGlobalId)
                .stream()
                .map(TaskMapper::toDomain)
                .collect(Collectors.toList());
    }

    public void moveTasksToProject(String fromProjectId, String toProjectId) {
        taskDao.moveTasksToProject(fromProjectId, toProjectId);
    }


    public List<Task> getNeedingSync() {
        return taskDao.getNeedingSync()
                .stream()
                .map(TaskMapper::toDomain)
                .collect(Collectors.toList());
    }

    public void saveOrUpdateFromSync(Task task) {
        TaskEntity existing = taskDao.getByGlobalId(task.getGlobalId());
        TaskEntity entity = TaskMapper.toEntity(task);
        entity.userGlobalId = userSession.getUserGlobalId();
        entity.updatedAt = System.currentTimeMillis();
        entity.needsSync = false;
        entity.isDeleted = false;

        if (existing != null) {
            entity.localId = existing.localId;
            taskDao.update(entity);
        } else {
            taskDao.insert(entity);
        }
    }

    public void clearSyncFlag(String globalId) {
        TaskEntity entity = taskDao.getByGlobalId(globalId);
        if (entity != null) {
            entity.needsSync = false;
            taskDao.update(entity);
        }
    }
}