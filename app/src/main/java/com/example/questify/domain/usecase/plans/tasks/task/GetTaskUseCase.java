package com.example.questify.domain.usecase.plans.tasks.task;

import com.example.questify.data.repository.TaskRepository;
import com.example.questify.domain.model.Task;

import javax.inject.Inject;

public class GetTaskUseCase {

    private final TaskRepository taskRepository;

    @Inject
    public GetTaskUseCase(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task execute(String globalId) {
        return taskRepository.getByGlobalId(globalId);
    }
}
