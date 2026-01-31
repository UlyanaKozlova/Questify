package com.example.questify.domain.usecase.plans.tasks.task;

import com.example.questify.data.repository.TaskRepository;
import com.example.questify.domain.model.Difficulty;
import com.example.questify.domain.model.Priority;
import com.example.questify.domain.model.Task;

import javax.inject.Inject;

public class UpdateTaskUseCase {

    private final TaskRepository repository;

    @Inject
    public UpdateTaskUseCase(TaskRepository repository) {
        this.repository = repository;
    }

    public void execute(Task task,
                        String name,
                        String description,
                        long deadline,
                        String projectId,
                        Priority priority,
                        Difficulty difficulty,
                        boolean isDone) {

        task.setTaskName(name);
        task.setDescription(description);
        task.setDeadline(deadline);
        task.setProjectGlobalId(projectId);
        task.setPriority(priority);
        task.setDifficulty(difficulty);
        task.setDone(isDone);
        task.setUpdatedAt(System.currentTimeMillis());

        repository.update(task);
    }
}
