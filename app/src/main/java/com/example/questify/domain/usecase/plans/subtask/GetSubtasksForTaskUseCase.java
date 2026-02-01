package com.example.questify.domain.usecase.plans.subtask;

import com.example.questify.data.repository.SubtaskRepository;
import com.example.questify.domain.model.Subtask;

import java.util.List;

import javax.inject.Inject;

public class GetSubtasksForTaskUseCase {
    private final SubtaskRepository subtaskRepository;

    @Inject
    public GetSubtasksForTaskUseCase(SubtaskRepository subtaskRepository) {
        this.subtaskRepository = subtaskRepository;
    }

    public List<Subtask> execute(String taskGlobalId) {
        return subtaskRepository.getSubtasksForTask(taskGlobalId);
    }
}
