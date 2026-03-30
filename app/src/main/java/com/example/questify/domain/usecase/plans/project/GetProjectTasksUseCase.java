package com.example.questify.domain.usecase.plans.project;

import com.example.questify.data.repository.TaskRepository;
import com.example.questify.domain.model.Task;

import java.util.List;

import javax.inject.Inject;

public class GetProjectTasksUseCase {
    private final TaskRepository taskRepository;

    @Inject
    public GetProjectTasksUseCase(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> execute(String projectGlobalId) {
        return taskRepository.getTasksByProject(projectGlobalId);
    }
}