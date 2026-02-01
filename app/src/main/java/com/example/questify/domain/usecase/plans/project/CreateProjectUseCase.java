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

    public boolean execute(String projectName) {
        Project newProject = new Project(projectName);
        for (Project project : projectRepository.getAll()) {
            if (project.equals(newProject)) {
                return false;
            }
        }
        projectRepository.save(newProject);
        return true;
    }
}
