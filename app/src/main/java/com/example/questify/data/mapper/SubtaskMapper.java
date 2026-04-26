package com.example.questify.data.mapper;

import com.example.questify.data.local.entity.SubtaskEntity;
import com.example.questify.domain.model.Subtask;

public class SubtaskMapper {

    public static Subtask toDomain(SubtaskEntity entity) {
        if (entity == null) {
            return null;
        }
        Subtask subtask = new Subtask(entity.globalId,
                entity.taskGlobalId,
                entity.isDone,
                entity.subtaskName,
                entity.updatedAt);
        subtask.setUserGlobalId(entity.userGlobalId);
        return subtask;
    }

    public static SubtaskEntity toEntity(Subtask model) {
        if (model == null) {
            return null;
        }

        SubtaskEntity entity = new SubtaskEntity();
        entity.globalId = model.getGlobalId();
        entity.taskGlobalId = model.getTaskGlobalId();
        entity.userGlobalId = model.getUserGlobalId();
        entity.isDone = model.isDone();
        entity.subtaskName = model.getSubtaskName();
        entity.updatedAt = model.getUpdatedAt();

        return entity;
    }
}
