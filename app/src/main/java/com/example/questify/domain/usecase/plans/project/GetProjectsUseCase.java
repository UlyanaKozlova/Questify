package com.example.questify.domain.usecase.plans.project;

import com.example.questify.data.repository.ProjectRepository;
import com.example.questify.domain.model.Project;

import java.util.List;

import javax.inject.Inject;

public class GetProjectsUseCase {

    private final ProjectRepository projectRepository;

    @Inject
    public GetProjectsUseCase(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public List<Project> execute() {
        return projectRepository.getAll();
    }
}
