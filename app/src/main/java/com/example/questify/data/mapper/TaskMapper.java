package com.example.questify.data.mapper;

import com.example.questify.data.local.entity.TaskEntity;
import com.example.questify.domain.model.Task;
import com.example.questify.domain.model.Priority;
import com.example.questify.domain.model.Difficulty;

public class TaskMapper {

    public static Task toDomain(TaskEntity entity) {
        return entity == null
                ? null
                : new Task(entity.globalId,
                entity.projectGlobalId,
                entity.userGlobalId,
                entity.isDone,
                entity.taskName,
                entity.description,
                Priority.valueOf(entity.priority),
                Difficulty.valueOf(entity.difficulty),
                entity.deadline, entity.updatedAt);
    }

    public static TaskEntity toEntity(Task model) {
        if (model == null) {
            return null;
        }

        TaskEntity entity = new TaskEntity();
        entity.globalId = model.getGlobalId();
        entity.projectGlobalId = model.getProjectGlobalId();
        entity.userGlobalId = model.getUserGlobalId();
        entity.isDone = model.isDone();
        entity.taskName = model.getTaskName();
        entity.description = model.getDescription();
        entity.deadline = model.getDeadline();
        entity.updatedAt = model.getUpdatedAt();

        entity.priority = model.getPriority().name();
        entity.difficulty = model.getDifficulty().name();

        return entity;
    }
}
