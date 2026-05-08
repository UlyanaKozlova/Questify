package com.example.questify.domain.usecase.plans.subtask;

import com.example.questify.data.repository.SubtaskRepository;
import com.example.questify.data.repository.TaskRepository;
import com.example.questify.domain.model.Subtask;
import com.example.questify.domain.model.Task;
import com.example.questify.domain.usecase.plans.tasks.reward.RewardEngine;

import javax.inject.Inject;

public class DeleteSubtaskUseCase {
    private final SubtaskRepository subtaskRepository;
    private final TaskRepository taskRepository;
    private final RewardEngine rewardEngine;

    @Inject
    public DeleteSubtaskUseCase(SubtaskRepository subtaskRepository,
                                TaskRepository taskRepository,
                                RewardEngine rewardEngine) {
        this.subtaskRepository = subtaskRepository;
        this.taskRepository = taskRepository;
        this.rewardEngine = rewardEngine;
    }

    public void execute(Subtask subtask) {
        String parentId = subtask.getTaskGlobalId();
        subtaskRepository.delete(subtask);

        if (parentId != null) {
            Task parent = taskRepository.getByGlobalId(parentId);
            if (parent != null) {
                rewardEngine.applyAfterChange(parent);
            }
        }
    }
}
