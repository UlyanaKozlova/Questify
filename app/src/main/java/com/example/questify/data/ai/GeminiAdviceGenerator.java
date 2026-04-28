package com.example.questify.data.ai;

import android.util.Log;

import com.example.questify.domain.model.helpers.TaskStatistics;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.ai.FirebaseAI;
import com.google.firebase.ai.GenerativeModel;
import com.google.firebase.ai.java.GenerativeModelFutures;
import com.google.firebase.ai.type.Content;
import com.google.firebase.ai.type.GenerateContentResponse;
import com.google.firebase.ai.type.GenerativeBackend;
import com.google.firebase.ai.type.TextPart;

import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class GeminiAdviceGenerator {

    private static final String TAG = "GeminiAdvice";

    private final GenerativeModelFutures model;

    @Inject
    public GeminiAdviceGenerator() {
        GenerativeModel ai = FirebaseAI.getInstance(GenerativeBackend.googleAI())
                .generativeModel(GeminiSubtaskGenerator.MODEL_NAME);
        this.model = GenerativeModelFutures.from(ai);
    }

    public String generateAdvice(TaskStatistics stats) {
        String promptText = buildPrompt(stats);
        Content content = new Content(Collections.singletonList(new TextPart(promptText)));
        try {
            ListenableFuture<GenerateContentResponse> future = model.generateContent(content);
            GenerateContentResponse response = future.get(30, TimeUnit.SECONDS);
            String text = response.getText();
            return text != null ? text.trim() : "";
        } catch (ExecutionException e) {
            Log.e(TAG, "Ошибка Gemini API", e);
            return "";
        } catch (TimeoutException e) {
            Log.e(TAG, "Gemini API: таймаут 30 секунд");
            return "";
        } catch (InterruptedException e) {
            Log.e(TAG, "Gemini API: поток прерван", e);
            Thread.currentThread().interrupt();
            return "";
        }
    }

    private String buildPrompt(TaskStatistics stats) {
        return "Ты — помощник по продуктивности. Вот статистика задач пользователя: " +
                "всего задач — " + stats.total + ", " +
                "выполнено — " + stats.completed + " (" + stats.getCompletionPercent() + "%), " +
                "просрочено — " + stats.overdue + " (" + stats.getOverduePercent() + "%). " +
                "Сформулируй один конкретный и полезный совет по улучшению продуктивности на основе этих данных. " +
                "Совет должен быть на русском языке, 2-3 предложения, без заголовков и списков, только текст.";
    }
}
