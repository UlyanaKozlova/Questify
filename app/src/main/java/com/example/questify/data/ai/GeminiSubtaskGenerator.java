package com.example.questify.data.ai;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.ai.FirebaseAI;
import com.google.firebase.ai.GenerativeModel;
import com.google.firebase.ai.java.GenerativeModelFutures;
import com.google.firebase.ai.type.Content;
import com.google.firebase.ai.type.GenerateContentResponse;
import com.google.firebase.ai.type.GenerativeBackend;
import com.google.firebase.ai.type.TextPart;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.util.Log;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class GeminiSubtaskGenerator {

    private static final String TAG = "GeminiSubtasks";
    public static final String MODEL_NAME = "gemini-3.1-flash-lite-preview";

    private final GenerativeModelFutures model;
    private final Gson gson = new Gson();

    @Inject
    public GeminiSubtaskGenerator() {
        GenerativeModel ai = FirebaseAI.getInstance(GenerativeBackend.googleAI())
                .generativeModel(MODEL_NAME);
        this.model = GenerativeModelFutures.from(ai);
    }

    public List<String> generateSubtaskNames(String taskName, String description) {
        String promptText = buildPrompt(taskName, description);
        Content content = new Content(Collections.singletonList(new TextPart(promptText)));
        try {
            ListenableFuture<GenerateContentResponse> future = model.generateContent(content);
            GenerateContentResponse response = future.get(30, TimeUnit.SECONDS);
            String rawText = response.getText();
            return parseJsonArray(rawText);
        } catch (ExecutionException e) {
            return new ArrayList<>();
        } catch (TimeoutException e) {
            Log.e(TAG, "Gemini API: таймаут 30 секунд");
            return new ArrayList<>();
        } catch (InterruptedException e) {
            Log.e(TAG, "Gemini API: поток прерван", e);
            Thread.currentThread().interrupt();
            return new ArrayList<>();
        }
    }

    private String buildPrompt(String taskName, String description) {
        StringBuilder sb = new StringBuilder();
        sb.append("Разбей следующую задачу на от 3 до 7 чётких и конкретных подзадач. ")
                .append("Название задачи: \"")
                .append(taskName).append("\". ");
        if (description != null && !description.trim().isEmpty()) {
            sb.append("Описание: \"").append(description).append("\". ");
        }
        sb.append("Верни ТОЛЬКО JSON-массив строк без объяснений и markdown. ")
                .append("Пример: [\"Первый шаг\",\"Второй шаг\",\"Третий шаг\"]");
        return sb.toString();
    }

    private List<String> parseJsonArray(String text) {
        if (text == null) return new ArrayList<>();
        String cleaned = text.trim();
        int start = cleaned.indexOf('[');
        int end = cleaned.lastIndexOf(']');
        if (start >= 0 && end > start) {
            cleaned = cleaned.substring(start, end + 1);
        }
        try {
            Type listType = new TypeToken<List<String>>() {
            }.getType();
            List<String> result = gson.fromJson(cleaned, listType);
            return result != null ? result : new ArrayList<>();
        } catch (Exception e) {
            Log.e(TAG, "Ошибка парсинга JSON: \"" + cleaned + "\"", e);
            return new ArrayList<>();
        }
    }
}
