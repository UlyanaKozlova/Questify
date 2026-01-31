package com.example.questify.domain.usecase.plans.tasks.task;

import com.example.questify.data.repository.TaskRepository;
import com.example.questify.domain.model.Task;

import javax.inject.Inject;

public class DeleteTaskUseCase {

    private final TaskRepository repository;

    @Inject
    public DeleteTaskUseCase(TaskRepository repository) {
        this.repository = repository;
    }

    public void execute(Task task) {
        repository.delete(task);
    }
}
