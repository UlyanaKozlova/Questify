package com.example.questify.domain.usecase.plans.tasks.task;

import com.example.questify.data.repository.TaskRepository;
import com.example.questify.domain.model.Task;

import javax.inject.Inject;

public class DeleteTaskUseCase {

    private final TaskRepository taskRepository;

    @Inject
    public DeleteTaskUseCase(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void execute(Task task) {
        taskRepository.delete(task);
    }
}
