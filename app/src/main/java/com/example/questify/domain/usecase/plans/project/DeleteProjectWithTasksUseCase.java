package com.example.questify.domain.usecase.plans.project;

import com.example.questify.data.repository.ProjectRepository;
import com.example.questify.data.repository.TaskRepository;
import com.example.questify.domain.model.Project;

import javax.inject.Inject;

public class DeleteProjectWithTasksUseCase {
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    @Inject
    public DeleteProjectWithTasksUseCase(ProjectRepository projectRepository,
                                         TaskRepository taskRepository) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
    }

    public void execute(Project project) {
        Project defaultProject = projectRepository.getDefaultProject();
        taskRepository.moveTasksToProject(project.getGlobalId(), defaultProject.getGlobalId());
        projectRepository.delete(project);
    }
}