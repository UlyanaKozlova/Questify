package com.example.questify.data.repository;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.example.questify.UserSession;
import com.example.questify.data.local.dao.ProjectDao;
import com.example.questify.data.local.entity.ProjectEntity;
import com.example.questify.data.mapper.ProjectMapper;
import com.example.questify.domain.model.Project;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class ProjectRepository {
    private final static String DEFAULT = "default";

    private final ProjectDao projectDao;
    private final UserSession userSession;

    @Inject
    public ProjectRepository(ProjectDao projectDao,
                             UserSession userSession) {
        this.projectDao = projectDao;
        this.userSession = userSession;
    }

    public void save(Project project) {
        ProjectEntity entity = ProjectMapper.toEntity(project);
        entity.updatedAt = System.currentTimeMillis();
        entity.userGlobalId = userSession.getUserGlobalId();
        entity.needsSync = true;
        projectDao.insert(entity);
    }

    public void update(Project project) {
        ProjectEntity entity = ProjectMapper.toEntity(project);
        entity.updatedAt = System.currentTimeMillis();
        entity.needsSync = true;
        projectDao.update(entity);
    }

    public void delete(Project project) {
        projectDao.delete(ProjectMapper.toEntity(project));
    }

    public void deleteAll() {
        List<ProjectEntity> projectEntities = projectDao.getAll();
        for (int i = 1; i < projectEntities.size(); i++) {
            projectDao.delete(projectEntities.get(i));
        }
    }

    public List<Project> getAll() {
        return projectDao.getAll()
                .stream()
                .map(ProjectMapper::toDomain)
                .collect(Collectors.toList());
    }

    public LiveData<List<Project>> getAllLive() {
        return Transformations.map(projectDao.getAllLive(),
                list -> list.stream()
                        .map(ProjectMapper::toDomain)
                        .collect(Collectors.toList()));
    }

    public Project getByGlobalId(String globalId) {
        return ProjectMapper.toDomain(projectDao.getByGlobalId(globalId));
    }

    public List<Project> getNeedingSync() {
        return projectDao.getNeedingSync()
                .stream()
                .map(ProjectMapper::toDomain)
                .collect(Collectors.toList());
    }

    public void ensureLocalProjectExists() {
        if (projectDao.getAll().isEmpty()) {
            ProjectEntity projectEntity = new ProjectEntity();
            projectEntity.globalId = UUID.randomUUID().toString();
            projectEntity.projectName = DEFAULT;
            projectEntity.userGlobalId = userSession.getUserGlobalId();
            projectEntity.updatedAt = System.currentTimeMillis();
            projectEntity.isDeleted = false;
            projectEntity.needsSync = true;

            projectDao.insert(projectEntity);
        }
    }

    public Project getByProjectName(String projectName) {
        return ProjectMapper.toDomain(projectDao.getByProjectName(projectName));
    }
}
