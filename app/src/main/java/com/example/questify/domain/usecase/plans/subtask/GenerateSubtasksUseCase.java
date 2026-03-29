package com.example.questify.domain.usecase.plans.subtask;

import com.example.questify.data.repository.SubtaskRepository;

import javax.inject.Inject;

public class GenerateSubtasksUseCase {
    private final SubtaskRepository subtaskRepository;

    @Inject
    public GenerateSubtasksUseCase(SubtaskRepository subtaskRepository) {
        this.subtaskRepository = subtaskRepository;
    }
    //  как-то для задач добавить поле isSubtasksGenerated и создать
    // менеджера которй их будет изредка проверять и если нужно генерировать
}
