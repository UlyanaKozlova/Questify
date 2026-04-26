package com.example.questify.domain.usecase.plans.subtask;

import com.example.questify.data.ai.GeminiSubtaskGenerator;

import java.util.List;

import javax.inject.Inject;

public class GenerateSubtasksUseCase {

    private final GeminiSubtaskGenerator generator;
    private final CreateSubtaskUseCase createSubtaskUseCase;

    @Inject
    public GenerateSubtasksUseCase(GeminiSubtaskGenerator generator,
                                   CreateSubtaskUseCase createSubtaskUseCase) {
        this.generator = generator;
        this.createSubtaskUseCase = createSubtaskUseCase;
    }

    public void execute(String taskGlobalId, String taskName, String description) {
        List<String> names = generator.generateSubtaskNames(taskName, description);
        for (String name : names) {
            if (name != null && !name.trim().isEmpty()) {
                createSubtaskUseCase.execute(taskGlobalId, name.trim());
            }
        }
    }
}
