package com.example.questify.domain.usecase.plans.tasks.task;

import com.example.questify.data.repository.ProjectRepository;
import com.example.questify.data.repository.TaskRepository;
import com.example.questify.domain.model.Difficulty;
import com.example.questify.domain.model.Priority;
import com.example.questify.domain.model.Task;

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

    public void execute(Task task,
                        String name,
                        String description,
                        long deadline,
                        String projectName,
                        Priority priority,
                        Difficulty difficulty,
                        boolean isDone) {
        task.setTaskName(name);
        task.setDescription(description);
        task.setDeadline(deadline);
        task.setProjectGlobalId(projectRepository.getByProjectName(projectName).getGlobalId());
        task.setPriority(priority);
        task.setDifficulty(difficulty);
        task.setDone(isDone);

        taskRepository.update(task);
    }
}
