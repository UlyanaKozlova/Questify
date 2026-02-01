package com.example.questify.domain.usecase.plans.subtask;

import com.example.questify.data.repository.SubtaskRepository;
import com.example.questify.domain.model.Subtask;

import javax.inject.Inject;

public class DeleteSubtaskUseCase {
    private final SubtaskRepository subtaskRepository;

    @Inject
    public DeleteSubtaskUseCase(SubtaskRepository subtaskRepository) {
        this.subtaskRepository = subtaskRepository;
    }

    public void execute(Subtask subtask) {
        subtaskRepository.delete(subtask);
    }
}
