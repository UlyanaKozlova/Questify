package com.example.questify.domain.usecase.plans.project;

import com.example.questify.data.repository.ProjectRepository;
import com.example.questify.domain.model.Project;

import javax.inject.Inject;

public class CreateProjectUseCase {

    private final ProjectRepository projectRepository;

    @Inject
    public CreateProjectUseCase(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public boolean execute(String projectName, String color) {
        return execute(new Project(projectName, color));
    }

    public boolean execute(Project project) {
        for (Project existing : projectRepository.getAll()) {
            if (existing.getProjectName().equals(project.getProjectName())) {
                return false;
            }
        }
        projectRepository.save(project);
        return true;
    }
}
