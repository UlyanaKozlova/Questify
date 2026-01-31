package com.example.questify.domain.usecase.plans.tasks.task;

import com.example.questify.data.repository.TaskRepository;
import com.example.questify.domain.model.Task;

import javax.inject.Inject;

public class GetTaskUseCase {

    private final TaskRepository repository;

    @Inject
    public GetTaskUseCase(TaskRepository repository) {
        this.repository = repository;
    }

    public Task execute(String globalId) {
        return repository.getByGlobalId(globalId);
    }
}
