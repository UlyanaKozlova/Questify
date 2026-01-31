package com.example.questify.domain.usecase.plans.tasks.task;

import com.example.questify.data.repository.TaskRepository;
import com.example.questify.domain.model.Task;
import com.example.questify.domain.model.User;
import com.example.questify.domain.usecase.user.GetUserUseCase;
import com.example.questify.domain.usecase.user.UpdateUserUseCase;

import javax.inject.Inject;

public class CompleteTaskUseCase {
    private final TaskRepository taskRepository;
    private final GetUserUseCase getUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final static int LEVEL = 35;

    @Inject
    public CompleteTaskUseCase(TaskRepository taskRepository,
                               GetUserUseCase getUserUseCase,
                               UpdateUserUseCase updateUserUseCase) {
        this.taskRepository = taskRepository;
        this.getUserUseCase = getUserUseCase;
        this.updateUserUseCase = updateUserUseCase;
    }

    public void execute(Task task, boolean isDone) {
        task.setDone(isDone);
        User user = getUserUseCase.execute();
        long coins = task.getDifficulty().getWeight() * 3L + task.getPriority().getWeight();
        long newCoins = user.getCoins() + (isDone ? coins : -coins);
        user.setLevel((int) newCoins / LEVEL);
        user.setCoins(newCoins);

        updateUserUseCase.execute(user);
        taskRepository.update(task);
    }
}
