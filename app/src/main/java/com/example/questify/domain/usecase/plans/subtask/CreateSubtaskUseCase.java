package com.example.questify.domain.usecase.plans.subtask;

import com.example.questify.data.repository.SubtaskRepository;
import com.example.questify.data.repository.TaskRepository;
import com.example.questify.domain.model.Subtask;
import com.example.questify.domain.model.Task;
import com.example.questify.domain.usecase.plans.tasks.reward.RewardEngine;

import java.util.UUID;

import javax.inject.Inject;

public class CreateSubtaskUseCase {
    private final SubtaskRepository subtaskRepository;
    private final TaskRepository taskRepository;
    private final RewardEngine rewardEngine;

    @Inject
    public CreateSubtaskUseCase(SubtaskRepository subtaskRepository,
                                TaskRepository taskRepository,
                                RewardEngine rewardEngine) {
        this.subtaskRepository = subtaskRepository;
        this.taskRepository = taskRepository;
        this.rewardEngine = rewardEngine;
    }

    public void execute(String taskGlobalId, String subtaskName) {
        Subtask subtask = new Subtask(
                UUID.randomUUID().toString(),
                taskGlobalId,
                false,
                subtaskName.trim(),
                System.currentTimeMillis()
        );
        subtaskRepository.save(subtask);

        Task parent = taskRepository.getByGlobalId(taskGlobalId);
        if (parent != null) {
            rewardEngine.applyAfterChange(parent);
        }
    }
}
