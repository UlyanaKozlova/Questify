package com.example.questify.domain.usecase.plans.project;

import androidx.lifecycle.LiveData;

import com.example.questify.data.repository.ProjectRepository;
import com.example.questify.domain.model.Project;

import java.util.List;

import javax.inject.Inject;

public class GetAllProjectsUseCase {

    private final ProjectRepository projectRepository;

    @Inject
    public GetAllProjectsUseCase(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public List<Project> execute() {
        return projectRepository.getAll();
    }
    public LiveData<List<Project>> executeLive() {
        return projectRepository.getAllLive();
    }
}
