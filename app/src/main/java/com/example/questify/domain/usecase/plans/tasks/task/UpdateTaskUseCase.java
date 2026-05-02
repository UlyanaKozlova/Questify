package com.example.questify.domain.usecase.plans.tasks.task;

import com.example.questify.R;
import com.example.questify.data.repository.ProjectRepository;
import com.example.questify.data.repository.TaskRepository;
import com.example.questify.domain.model.DomainValidationException;
import com.example.questify.domain.model.Project;
import com.example.questify.domain.model.Task;
import com.example.questify.domain.model.enums.Difficulty;
import com.example.questify.domain.model.enums.Priority;

import javax.inject.Inject;

public class UpdateTaskUseCase {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    @Inject
    public UpdateTaskUseCase(TaskRepository taskRepository,
                             ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
    }

    public void execute(Task taskToEdit,
                        String name,
                        String description,
                        long deadline,
                        String projectName,
                        Priority priority,
                        Difficulty difficulty,
                        boolean isDone) {
        Task existing = taskRepository.getByTaskName(name);
        if (existing != null && !existing.getGlobalId().equals(taskToEdit.getGlobalId())) {
            throw new DomainValidationException(R.string.error_task_name_exists);
        }

        Project project = projectRepository.getByProjectName(projectName);
        if (project == null) {
            project = new Project(projectName);
            projectRepository.save(project);
        }
        taskToEdit.setTaskName(name);
        taskToEdit.setDescription(description);
        taskToEdit.setDeadline(deadline);
        taskToEdit.setProjectGlobalId(project.getGlobalId());
        taskToEdit.setPriority(priority);
        taskToEdit.setDifficulty(difficulty);
        taskToEdit.setDone(isDone);
        taskToEdit.setUpdatedAt(System.currentTimeMillis());
        taskRepository.update(taskToEdit);
    }

    public void updateDoneStatus(Task taskToEdit, boolean isDone) {
        taskToEdit.setDone(isDone);
        taskToEdit.setUpdatedAt(System.currentTimeMillis());
        taskRepository.update(taskToEdit);
    }
}
