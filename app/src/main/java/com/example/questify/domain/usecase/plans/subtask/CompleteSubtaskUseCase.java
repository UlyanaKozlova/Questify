package com.example.questify.domain.usecase.plans.subtask;

import com.example.questify.data.repository.SubtaskRepository;
import com.example.questify.domain.model.Subtask;

import javax.inject.Inject;

public class CompleteSubtaskUseCase {
    private final SubtaskRepository subtaskRepository;

    @Inject
    public CompleteSubtaskUseCase(SubtaskRepository subtaskRepository) {
        this.subtaskRepository = subtaskRepository;
    }

    public void execute(Subtask subtask, boolean isDone) {
        subtask.setDone(isDone);
        // todo монеты и уровни для субтасков
        subtaskRepository.update(subtask);
    }
}
