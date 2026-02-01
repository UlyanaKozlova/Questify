package com.example.questify.domain.usecase.plans.tasks.task;

import com.example.questify.data.repository.TaskRepository;
import com.example.questify.domain.model.Difficulty;
import com.example.questify.domain.model.Priority;
import com.example.questify.domain.model.Task;

import javax.inject.Inject;

public class CreateTaskUseCase {
    private final TaskRepository taskRepository;

    @Inject
    public CreateTaskUseCase(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public boolean execute(String taskName,
                           String description,
                           Long deadline,
                           String projectGlobalId,
                           Difficulty difficulty,
                           Priority priority) {
        Task newTask = new Task(
                projectGlobalId,
                taskName,
                description,
                priority,
                difficulty,
                deadline);
        for (Task task : taskRepository.getAll()) {
            if (task.equals(newTask)) {
                return false;
            }
        }
        taskRepository.save(newTask);
        return true;
    }
}