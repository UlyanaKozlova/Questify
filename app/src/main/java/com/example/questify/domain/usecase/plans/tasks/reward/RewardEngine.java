package com.example.questify.domain.usecase.plans.tasks.reward;

import com.example.questify.data.repository.SubtaskRepository;
import com.example.questify.data.repository.TaskRepository;
import com.example.questify.data.repository.UserRepository;
import com.example.questify.domain.model.Subtask;
import com.example.questify.domain.model.Task;
import com.example.questify.domain.model.User;

import java.util.List;

import javax.inject.Inject;

public class RewardEngine {

    private static final int COINS_PER_LEVEL = 35;

    private final SubtaskRepository subtaskRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    @Inject
    public RewardEngine(SubtaskRepository subtaskRepository,
                        UserRepository userRepository,
                        TaskRepository taskRepository) {
        this.subtaskRepository = subtaskRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    public void applyAfterChange(Task task) {
        if (task == null || task.getGlobalId() == null) {
            return;
        }

        List<Subtask> subtasks = subtaskRepository.getSubtasksForTask(task.getGlobalId());
        int totalSubtasks = subtasks.size();
        int doneSubtasks = (int) subtasks.stream().filter(Subtask::isDone).count();

        if (!task.isDone() && totalSubtasks > 0 && doneSubtasks == totalSubtasks) {
            task.setDone(true);
        }

        long expected = RewardCalculator.expectedAwarded(task, doneSubtasks, totalSubtasks);
        long currentlyAwarded = task.getCoinsAwarded();
        long delta = expected - currentlyAwarded;

        if (delta != 0) {
            User user = userRepository.getUser();
            if (user != null) {
                long newCoins = Math.max(0, user.getCoins() + delta);
                long newEarned = Math.max(0, user.getEarnedCoins() + delta);
                user.setCoins(newCoins);
                user.setEarnedCoins(newEarned);
                user.setLevel((int) (user.getEarnedCoins() / COINS_PER_LEVEL));
                userRepository.update(user);
            }
            task.setCoinsAwarded(expected);
        }

        task.setUpdatedAt(System.currentTimeMillis());
        taskRepository.update(task);
    }
}
