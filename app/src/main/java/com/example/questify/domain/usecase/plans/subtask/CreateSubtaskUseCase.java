package com.example.questify.domain.usecase.plans.subtask;

import com.example.questify.data.repository.SubtaskRepository;
import com.example.questify.domain.model.Subtask;

import java.util.UUID;

import javax.inject.Inject;

public class CreateSubtaskUseCase {
    private final SubtaskRepository subtaskRepository;

    @Inject
    public CreateSubtaskUseCase(SubtaskRepository subtaskRepository) {
        this.subtaskRepository = subtaskRepository;
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
    }
}
