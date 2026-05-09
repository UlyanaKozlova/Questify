package com.example.questify.domain.usecase.plans.tasks.task;

import com.example.questify.R;
import com.example.questify.data.repository.ProjectRepository;
import com.example.questify.data.repository.TaskRepository;
import com.example.questify.util.exception.DomainValidationException;
import com.example.questify.domain.model.Project;
import com.example.questify.domain.model.Task;
import com.example.questify.domain.model.enums.Difficulty;
import com.example.questify.domain.model.enums.Priority;

import javax.inject.Inject;

public class CreateTaskUseCase {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    @Inject
    public CreateTaskUseCase(TaskRepository taskRepository,
                             ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
    }

    public Task execute(String taskName,
                        String description,
                        long deadline,
                        String projectName,
                        Difficulty difficulty,
                        Priority priority) {
        return create(taskName, description, deadline, projectName, difficulty, priority, true);
    }


    public void executeFromImport(String taskName,
                                  String description,
                                  long deadline,
                                  String projectName,
                                  Difficulty difficulty,
                                  Priority priority) {
        String uniqueName = makeUniqueName(taskName);
        create(uniqueName, description, deadline, projectName, difficulty, priority, false);
    }

    private String makeUniqueName(String baseName) {
        if (baseName == null) {
            return null;
        }
        if (taskRepository.getByTaskName(baseName) == null) {
            return baseName;
        }
        int n = 1;
        String candidate;
        do {
            candidate = baseName + " (" + n + ")";
            n++;
        } while (taskRepository.getByTaskName(candidate) != null);
        return candidate;
    }

    private Task create(String taskName,
                        String description,
                        long deadline,
                        String projectName,
                        Difficulty difficulty,
                        Priority priority,
                        boolean validateDeadline) {
        if (taskRepository.getByTaskName(taskName) != null) {
            throw new DomainValidationException(R.string.error_task_name_exists);
        }

        Project project = projectRepository.getByProjectName(projectName);
        if (project == null) {
            project = new Project(projectName);
            projectRepository.save(project);
        }

        Task newTask = validateDeadline
                ? new Task(project.getGlobalId(), taskName, description, priority, difficulty, deadline)
                : Task.forImport(project.getGlobalId(), taskName, description, priority, difficulty, deadline);
        taskRepository.save(newTask);
        return newTask;
    }
}
