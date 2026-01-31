package com.example.questify.domain.usecase.plans.tasks.imp;

import android.util.Log;

import com.example.questify.domain.model.Difficulty;
import com.example.questify.domain.model.Priority;
import com.example.questify.domain.usecase.plans.tasks.task.CreateTaskUseCase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import javax.inject.Inject;

public class ImportTasksFromJsonUseCase extends ImportTasksUseCase {
    @Inject
    public ImportTasksFromJsonUseCase(CreateTaskUseCase createTaskUseCase) {
        super(createTaskUseCase);
    }

    @Override
    protected void saveTasks(String content) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<ImportedTaskDto>>() {
        }.getType();
        List<ImportedTaskDto> dtos = gson.fromJson(content, type);
        for (ImportedTaskDto dto : dtos) {
            try {
                createTaskUseCase.execute(
                        dto.name,
                        dto.description,
                        dto.deadlineMillis(),
                        dto.projectGlobalId,
                        Difficulty.valueOf(dto.difficulty.toUpperCase()),
                        Priority.valueOf(dto.priority.toUpperCase())
                );
            } catch (Exception e) {
                Log.e("IMPORT", "Error creating task from JSON: " + dto.name, e);
            }
        }
    }


    static class ImportedTaskDto {
        public String name;
        public String description;
        public String priority;
        public String difficulty;
        public String deadline;
        public String projectGlobalId;

        public long deadlineMillis() {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                return Objects.requireNonNull(sdf.parse(deadline)).getTime();
            } catch (Exception e) {
                return 0;
            }
        }
    }
}
