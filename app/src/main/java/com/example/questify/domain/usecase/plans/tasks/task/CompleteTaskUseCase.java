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
        long taskCoins = task.getDifficulty().getWeight() * 3L + task.getPriority().getWeight();
        if (isDone) {
            user.setCoins(user.getCoins() + taskCoins);
            user.setEarnedCoins(user.getEarnedCoins() + taskCoins);
        } else {
            user.setCoins(Math.max(0, user.getCoins() - taskCoins));
            user.setEarnedCoins(Math.max(0, user.getEarnedCoins() - taskCoins));
        }
        user.setLevel((int) (user.getEarnedCoins() / LEVEL));
        updateUserUseCase.execute(user);
        taskRepository.update(task);
    }
}
