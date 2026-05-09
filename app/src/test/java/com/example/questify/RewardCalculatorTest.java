package com.example.questify;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.example.questify.domain.model.Task;
import com.example.questify.domain.model.enums.Difficulty;
import com.example.questify.domain.model.enums.Priority;
import com.example.questify.domain.usecase.plans.tasks.reward.RewardCalculator;

import org.junit.Test;

public class RewardCalculatorTest {

    private static Task taskWith(Priority priority, Difficulty difficulty) {
        Task task = new Task();
        task.setPriority(priority);
        task.setDifficulty(difficulty);
        return task;
    }

    @Test
    public void coins_lowestDifficultyAndPriority_returnsMinimum() {
        Task task = taskWith(Priority.VERY_LOW, Difficulty.VERY_EASY);
        assertEquals((long) 3 + 1, RewardCalculator.taskTotalCoins(task));
    }

    @Test
    public void coins_mediumDifficultyAndPriority_returnsMedium() {
        Task task = taskWith(Priority.MEDIUM, Difficulty.MEDIUM);
        assertEquals(3L * 3 + 3, RewardCalculator.taskTotalCoins(task));
    }

    @Test
    public void coins_highestDifficultyAndPriority_returnsMaximum() {
        Task task = taskWith(Priority.VERY_HIGH, Difficulty.VERY_DIFFICULT);
        assertEquals(5L * 3 + 5, RewardCalculator.taskTotalCoins(task));
    }

    @Test
    public void coins_highDifficultyLowPriority_combined() {
        Task task = taskWith(Priority.VERY_LOW, Difficulty.VERY_DIFFICULT);
        assertEquals(5L * 3 + 1, RewardCalculator.taskTotalCoins(task));
    }

    @Test
    public void coins_lowDifficultyHighPriority_combined() {
        Task task = taskWith(Priority.VERY_HIGH, Difficulty.VERY_EASY);
        assertEquals((long) 3 + 5, RewardCalculator.taskTotalCoins(task));
    }

    @Test
    public void coins_harderTaskRewardsMoreThanEasier() {
        Task hard = taskWith(Priority.MEDIUM, Difficulty.VERY_DIFFICULT);
        Task easy = taskWith(Priority.MEDIUM, Difficulty.VERY_EASY);
        assertTrue(RewardCalculator.taskTotalCoins(hard) > RewardCalculator.taskTotalCoins(easy));
    }
}
