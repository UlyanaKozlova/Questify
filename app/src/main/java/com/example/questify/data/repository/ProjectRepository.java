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
        ProjectEntity existing = projectDao.getByGlobalId(project.getGlobalId());
        if (existing != null) {
            projectDao.delete(existing);
        }
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

    public Project getByProjectName(String projectName) {
        return ProjectMapper.toDomain(projectDao.getByProjectName(projectName));
    }

    public Project getDefaultProject() {
        Project defaultProject = getByProjectName(DEFAULT_PROJECT_NAME);
        if (defaultProject == null) {
            defaultProject = new Project(DEFAULT_PROJECT_NAME, DEFAULT_PROJECT_COLOR);
            save(defaultProject);
            defaultProject = getByProjectName(DEFAULT_PROJECT_NAME);
        }
        return defaultProject;
    }

    public void ensureDefaultProjectExists() {
        getDefaultProject();
    }
}