package com.example.questify.domain.usecase;

import com.example.questify.data.repository.TaskRepository;
import com.example.questify.domain.model.Task;

public class CreateTaskUseCase {

    private final TaskRepository taskRepository;

    public CreateTaskUseCase(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void execute(Task task) {
        taskRepository.save(task);
    }
}
