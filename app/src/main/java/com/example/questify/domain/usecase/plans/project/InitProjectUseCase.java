package com.example.questify.domain.usecase.plans.project;

import com.example.questify.data.repository.ProjectRepository;

import javax.inject.Inject;

public class InitProjectUseCase {
    private final ProjectRepository projectRepository;

    @Inject
    public InitProjectUseCase(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public void execute() {
        projectRepository.ensureDefaultProjectExists();
    }
}