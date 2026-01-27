package com.example.questify.domain.usecase;

import com.example.questify.data.repository.TaskRepository;
import com.example.questify.domain.model.Task;

public class CompleteTaskUseCase {

    private final TaskRepository taskRepository;

    public CompleteTaskUseCase(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void execute(Task task) {
        task.setDone(true);
        taskRepository.update(task);
    }
}
