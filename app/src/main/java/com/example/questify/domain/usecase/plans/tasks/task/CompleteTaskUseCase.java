package com.example.questify.domain.usecase.plans.tasks.task;

import com.example.questify.domain.model.Task;
import com.example.questify.domain.usecase.plans.tasks.reward.RewardEngine;

import javax.inject.Inject;

public class CompleteTaskUseCase {
    private final RewardEngine rewardEngine;

    @Inject
    public CompleteTaskUseCase(RewardEngine rewardEngine) {
        this.rewardEngine = rewardEngine;
    }

    public void execute(Task task, boolean isDone) {
        task.setDone(isDone);
        rewardEngine.applyAfterChange(task);
    }
}
