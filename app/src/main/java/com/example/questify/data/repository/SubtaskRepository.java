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

    public void deleteAll() {
        for (SubtaskEntity subtaskEntity : subtaskDao.getAllSubtasks()) {
            subtaskDao.delete(subtaskEntity);
        }
    }

    public Subtask getByGlobalId(String globalId) {
        return SubtaskMapper.toDomain(subtaskDao.getByGlobalId(globalId));
    }

    public List<Subtask> getNeedingSync() {
        return subtaskDao.getNeedingSync()
                .stream()
                .map(SubtaskMapper::toDomain)
                .collect(Collectors.toList());
    }

    public List<Subtask> getSubtasksForTask(String taskGlobalId) {
        return subtaskDao.getSubtasksForTask(taskGlobalId)
                .stream()
                .map(SubtaskMapper::toDomain)
                .collect(Collectors.toList());
    }
}
