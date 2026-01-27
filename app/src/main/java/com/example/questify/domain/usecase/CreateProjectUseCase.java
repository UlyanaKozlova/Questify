package com.example.questify.domain.usecase;

import com.example.questify.data.repository.ProjectRepository;
import com.example.questify.domain.model.Project;

public class CreateProjectUseCase {

    private final ProjectRepository projectRepository;

    public CreateProjectUseCase(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public void execute(Project project) {
        projectRepository.save(project);
    }
}
