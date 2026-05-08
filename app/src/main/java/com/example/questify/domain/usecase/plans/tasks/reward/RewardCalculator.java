package com.example.questify.domain.usecase.plans.tasks.reward;

import com.example.questify.domain.model.Task;
import com.example.questify.domain.model.enums.Difficulty;
import com.example.questify.domain.model.enums.Priority;

public final class RewardCalculator {

    private RewardCalculator() {
    }

    public static long taskTotalCoins(Task task) {
        if (task == null) {
            return 0;
        }
        Difficulty difficulty = task.getDifficulty();
        Priority priority = task.getPriority();
        long difficultyWeight = difficulty != null ? difficulty.getWeight() : 0;
        long priorityWeight = priority != null ? priority.getWeight() : 0;
        return difficultyWeight * 3L + priorityWeight;
    }

    public static long perSubtaskCoins(long total, int totalSubtaskCount) {
        if (totalSubtaskCount <= 0) {
            return 0;
        }
        return total / totalSubtaskCount;
    }

    public static long expectedAwarded(Task task, int doneSubtaskCount, int totalSubtaskCount) {
        if (task == null) {
            return 0;
        }
        long total = taskTotalCoins(task);
        if (task.isDone()) {
            return total;
        }
        long perSubtask = perSubtaskCoins(total, totalSubtaskCount);
        long doneCount = Math.max(0, Math.min(doneSubtaskCount, totalSubtaskCount));
        return perSubtask * doneCount;
    }
}
