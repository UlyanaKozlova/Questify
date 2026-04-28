package com.example.questify.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.example.questify.UserSession;
import com.example.questify.data.local.dao.ProjectDao;
import com.example.questify.data.local.entity.ProjectEntity;
import com.example.questify.data.mapper.ProjectMapper;
import com.example.questify.domain.model.Project;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ProjectRepository {
    public static final String DEFAULT_PROJECT_NAME = "Без проекта";
    public static final String DEFAULT_PROJECT_COLOR = "#FF6200EE";

    private final ProjectDao projectDao;
    private final UserSession userSession;

    @Inject
    public ProjectRepository(ProjectDao projectDao, UserSession userSession) {
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
        ProjectEntity existing = projectDao.getByGlobalId(project.getGlobalId());
        if (existing == null) {
            save(project);
            return;
        }
        existing.projectName = project.getProjectName();
        existing.color = project.getColor();
        existing.updatedAt = System.currentTimeMillis();
        existing.needsSync = true;
        projectDao.update(existing);
    }

    public void delete(Project project) {
        Project defaultProject = getDefaultProject();
        if (defaultProject != null && defaultProject.getGlobalId().equals(project.getGlobalId())) {
            return;
        }
        projectDao.softDelete(project.getGlobalId(), System.currentTimeMillis());
    }

    public List<Project> getDeletedNeedingSync() {
        return projectDao.getSoftDeletedNeedingSync()
                .stream()
                .map(ProjectMapper::toDomain)
                .collect(Collectors.toList());
    }

    public void deleteByGlobalId(String globalId) {
        projectDao.deleteByGlobalId(globalId);
    }

    public List<Project> getAll() {
        return projectDao.getAll(userSession.getUserGlobalId())
                .stream()
                .map(ProjectMapper::toDomain)
                .collect(Collectors.toList());
    }

    public LiveData<List<Project>> getAllLive() {
        return Transformations.map(projectDao.getAllLive(userSession.getUserGlobalId()),
                list -> list.stream()
                        .map(ProjectMapper::toDomain)
                        .collect(Collectors.toList()));
    }

    public Project getByGlobalId(String globalId) {
        return ProjectMapper.toDomain(projectDao.getByGlobalId(globalId));
    }

    public Project getByProjectName(String projectName) {
        return ProjectMapper.toDomain(projectDao.getByProjectName(projectName, userSession.getUserGlobalId()));
    }

    public Project getDefaultProject() {
        Project defaultProject = getByProjectName(DEFAULT_PROJECT_NAME);
        if (defaultProject != null) {
            return defaultProject;
        }
        Project toCreate = new Project(DEFAULT_PROJECT_NAME, DEFAULT_PROJECT_COLOR);
        save(toCreate);
        return toCreate;
    }

    public void ensureDefaultProjectExists() {
        getDefaultProject();
    }

    public boolean isDefaultProject(Project project) {
        if (project == null) return false;
        return DEFAULT_PROJECT_NAME.equals(project.getProjectName());
    }


    public List<Project> getNeedingSync() {
        return projectDao.getNeedingSync()
                .stream()
                .map(ProjectMapper::toDomain)
                .collect(Collectors.toList());
    }

    public void saveOrUpdateFromSync(Project project) {
        ProjectEntity existing = projectDao.getByGlobalId(project.getGlobalId());
        ProjectEntity entity = ProjectMapper.toEntity(project);
        entity.userGlobalId = userSession.getUserGlobalId();
        entity.needsSync = false;
        entity.isDeleted = false;

        if (existing != null) {
            entity.localId = existing.localId;
            projectDao.update(entity);
        } else {
            projectDao.insert(entity);
        }
    }

    public void clearSyncFlag(String globalId) {
        ProjectEntity entity = projectDao.getByGlobalId(globalId);
        if (entity != null) {
            entity.needsSync = false;
            projectDao.update(entity);
        }
    }
}