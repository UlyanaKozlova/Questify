package com.example.questify.domain.usecase.statistics;

import com.example.questify.data.ai.GeminiAdviceGenerator;
import com.example.questify.domain.model.helpers.TaskStatistics;

import javax.inject.Inject;

public class GetAdviceUseCase {

    private final GeminiAdviceGenerator geminiAdviceGenerator;

    @Inject
    public GetAdviceUseCase(GeminiAdviceGenerator geminiAdviceGenerator) {
        this.geminiAdviceGenerator = geminiAdviceGenerator;
    }

    public String execute(TaskStatistics stats) {
        return geminiAdviceGenerator.generateAdvice(stats);
    }
}
