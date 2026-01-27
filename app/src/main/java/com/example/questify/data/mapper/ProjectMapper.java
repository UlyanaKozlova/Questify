package com.example.questify.data.mapper;

import com.example.questify.data.local.entity.ProjectEntity;
import com.example.questify.domain.model.Project;

public class ProjectMapper {

    public static Project toDomain(ProjectEntity entity) {
        return entity == null
                ? null
                : new Project(entity.globalId, entity.userGlobalId, entity.projectName, entity.updatedAt);
    }

    public static ProjectEntity toEntity(Project model) {
        if (model == null) {
            return null;
        }

        ProjectEntity entity = new ProjectEntity();
        entity.globalId = model.getGlobalId();
        entity.userGlobalId = model.getUserGlobalId();
        entity.projectName = model.getProjectName();
        entity.updatedAt = model.getUpdatedAt();

        return entity;
    }
}
