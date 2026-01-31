package com.example.questify.data.repository;


import com.example.questify.data.local.dao.ProjectDao;
import com.example.questify.data.local.entity.ProjectEntity;
import com.example.questify.data.mapper.ProjectMapper;
import com.example.questify.domain.model.Project;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class ProjectRepository {

    private final ProjectDao projectDao;

    @Inject
    public ProjectRepository(ProjectDao projectDao) {
        this.projectDao = projectDao;
    }

    public void save(Project project) {
        ProjectEntity entity = ProjectMapper.toEntity(project);
        entity.updatedAt = System.currentTimeMillis();
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
        for (ProjectEntity projectEntity : projectDao.getAll()) {
            projectDao.delete(projectEntity);
        }
    }

    public List<Project> getAll() {
        return projectDao.getAll()
                .stream()
                .map(ProjectMapper::toDomain)
                .collect(Collectors.toList());
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
}
