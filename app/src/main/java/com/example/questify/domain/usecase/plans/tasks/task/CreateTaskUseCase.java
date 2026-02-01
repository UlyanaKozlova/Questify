package com.example.questify.domain.usecase.plans.tasks.task;

import com.example.questify.data.repository.ProjectRepository;
import com.example.questify.data.repository.TaskRepository;
import com.example.questify.domain.model.Difficulty;
import com.example.questify.domain.model.Priority;
import com.example.questify.domain.model.Task;
import com.example.questify.domain.usecase.plans.project.CreateProjectUseCase;

import javax.inject.Inject;

public class CreateTaskUseCase {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final CreateProjectUseCase createProjectUseCase;

    @Inject
    public CreateTaskUseCase(TaskRepository taskRepository,
                             ProjectRepository projectRepository,
                             CreateProjectUseCase createProjectUseCase) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.createProjectUseCase = createProjectUseCase;
    }

    public boolean execute(String taskName,
                           String description,
                           Long deadline,
                           String projectName,
                           Difficulty difficulty,
                           Priority priority) {
        if (projectRepository.getByProjectName(projectName) == null) {
            createProjectUseCase.execute(projectName);
        }
        String projectGlobalId = projectRepository.getByProjectName(projectName).getGlobalId();
        Task newTask = new Task(projectGlobalId,
                taskName,
                description,
                priority,
                difficulty,
                deadline);
        for (Task task : taskRepository.getAll()) {
            if (task.equals(newTask)) {
                return false;
            }
        }

        taskRepository.save(newTask);
        return true;
    }
}