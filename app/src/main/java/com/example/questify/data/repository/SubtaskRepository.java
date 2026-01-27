package com.example.questify.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import com.example.questify.data.local.dao.SubtaskDao;
import com.example.questify.data.local.entity.SubtaskEntity;
import com.example.questify.data.mapper.SubtaskMapper;
import com.example.questify.domain.model.Subtask;
import java.util.List;
import java.util.stream.Collectors;

public class SubtaskRepository {

    private final SubtaskDao subtaskDao;

    public SubtaskRepository(SubtaskDao subtaskDao) {
        this.subtaskDao = subtaskDao;
    }

    public LiveData<List<Subtask>> getSubtasksForTask(String taskGlobalId) {
        return Transformations.map(subtaskDao.getSubtasksForTask(taskGlobalId),
                entities -> entities
                        .stream()
                        .map(SubtaskMapper::toDomain)
                        .collect(Collectors.toList())
        );
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

    public Subtask getByGlobalId(String globalId) {
        return SubtaskMapper.toDomain(subtaskDao.getByGlobalId(globalId));
    }

    public List<SubtaskEntity> getNeedingSync() {
        return subtaskDao.getNeedingSync();
    }
}
