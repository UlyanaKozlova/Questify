package com.example.questify.data.mapper;

import com.example.questify.data.local.entity.ProjectEntity;
import com.example.questify.domain.model.Project;

public class ProjectMapper {

    private static final String DEFAULT_COLOR = "#FF6200EE";

    public static Project toDomain(ProjectEntity entity) {
        return entity == null
                ? null
                : new Project(
                entity.globalId,
                entity.userGlobalId,
                entity.projectName,
                entity.color != null ? entity.color : DEFAULT_COLOR,
                entity.updatedAt
        );
    }

    public static ProjectEntity toEntity(Project model) {
        if (model == null) {
            return null;
        }

        ProjectEntity entity = new ProjectEntity();
        entity.globalId = model.getGlobalId();
        entity.userGlobalId = model.getUserGlobalId();
        entity.projectName = model.getProjectName();
        entity.color = model.getColor();
        entity.updatedAt = model.getUpdatedAt();
        entity.isDeleted = false;
        entity.needsSync = true;

        return entity;
    }
}