package com.example.questify.domain.usecase;

import androidx.lifecycle.LiveData;

import com.example.questify.data.repository.ProjectRepository;
import com.example.questify.domain.model.Project;

import java.util.List;

public class GetProjectsUseCase {

    private final ProjectRepository projectRepository;

    public GetProjectsUseCase(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public LiveData<List<Project>> execute(String userGlobalId) {
        return projectRepository.getProjectsForUser(userGlobalId);
    }
}
