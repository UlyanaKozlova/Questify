package com.example.questify.domain.usecase.plans.subtask;

import com.example.questify.data.repository.SubtaskRepository;
import com.example.questify.domain.model.Subtask;

import javax.inject.Inject;

public class UpdateSubtaskUseCase {
    private final SubtaskRepository subtaskRepository;

    @Inject
    public UpdateSubtaskUseCase(SubtaskRepository subtaskRepository) {
        this.subtaskRepository = subtaskRepository;
    }

    public void execute(Subtask subtask,
                        String subtaskName) {
        subtask.setSubtaskName(subtaskName);

        subtaskRepository.update(subtask);
    }
}
