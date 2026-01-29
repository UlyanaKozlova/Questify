package com.example.questify.domain.usecase.plans.task;

import com.example.questify.data.repository.TaskRepository;
import com.example.questify.domain.model.Task;

import javax.inject.Inject;

public class CompleteTaskUseCase {

    private final TaskRepository taskRepository;
    @Inject
    public CompleteTaskUseCase(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void execute(Task task) {
        task.setDone(true);
        taskRepository.update(task);
        // todo обновлять монеты, уровень
    }
}
