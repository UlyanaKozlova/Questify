package com.example.questify.data.repository;

import com.example.questify.data.local.dao.SubtaskDao;
import com.example.questify.data.local.entity.SubtaskEntity;
import com.example.questify.data.mapper.SubtaskMapper;
import com.example.questify.domain.model.Subtask;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class SubtaskRepository {

    private final SubtaskDao subtaskDao;

    @Inject
    public SubtaskRepository(SubtaskDao subtaskDao) {
        this.subtaskDao = subtaskDao;
    }

    public void save(Subtask subtask) {
        SubtaskEntity entity = SubtaskMapper.toEntity(subtask);
        entity.updatedAt = System.currentTimeMillis();
        entity.needsSync = true;
        subtaskDao.insert(entity);
    }

    public void update(Subtask subtask) {
        SubtaskEntity entity = SubtaskMapper.toEntity(subtask);
        entity.updatedAt = System.currentTimeMillis();
        entity.needsSync = true;
        subtaskDao.update(entity);
    }

    public void delete(Subtask subtask) {
        subtaskDao.delete(SubtaskMapper.toEntity(subtask));
    }


    public Subtask getByGlobalId(String globalId) {
        return SubtaskMapper.toDomain(subtaskDao.getByGlobalId(globalId));
    }

    public List<Subtask> getSubtasksForTask(String taskGlobalId) {
        return subtaskDao.getSubtasksForTask(taskGlobalId)
                .stream()
                .map(SubtaskMapper::toDomain)
                .collect(Collectors.toList());
    }


    public List<Subtask> getNeedingSync() {
        return subtaskDao.getNeedingSync()
                .stream()
                .map(SubtaskMapper::toDomain)
                .collect(Collectors.toList());
    }

    public void saveOrUpdateFromSync(Subtask subtask) {
        SubtaskEntity existing = subtaskDao.getByGlobalId(subtask.getGlobalId());
        SubtaskEntity entity = SubtaskMapper.toEntity(subtask);
        entity.updatedAt = System.currentTimeMillis();
        entity.needsSync = false;
        entity.isDeleted = false;

        if (existing != null) {
            entity.localId = existing.localId;
            subtaskDao.update(entity);
        } else {
            subtaskDao.insert(entity);
        }
    }

    public void clearSyncFlag(String globalId) {
        SubtaskEntity entity = subtaskDao.getByGlobalId(globalId);
        if (entity != null) {
            entity.needsSync = false;
            subtaskDao.update(entity);
        }
    }
}