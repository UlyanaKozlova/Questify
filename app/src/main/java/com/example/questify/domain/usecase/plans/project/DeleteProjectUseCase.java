package com.example.questify.domain.usecase.plans.project;

import com.example.questify.data.repository.ProjectRepository;
import com.example.questify.domain.model.Project;

import javax.inject.Inject;

public class DeleteProjectUseCase {
    private final ProjectRepository projectRepository;

    @Inject
    public DeleteProjectUseCase(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public void execute(Project project) {
        projectRepository.delete(project);
    }
}