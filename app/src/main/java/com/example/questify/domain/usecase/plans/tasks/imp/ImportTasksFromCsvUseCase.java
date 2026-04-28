package com.example.questify.domain.usecase.plans.tasks.imp;

import android.content.Context;
import android.util.Log;

import com.example.questify.domain.model.enums.Difficulty;
import com.example.questify.domain.model.enums.Priority;
import com.example.questify.domain.usecase.plans.tasks.task.CreateTaskUseCase;


import javax.inject.Inject;

public class ImportTasksFromCsvUseCase extends ImportTasksUseCase {
    @Inject
    public ImportTasksFromCsvUseCase(CreateTaskUseCase createTaskUseCase) {
        super(createTaskUseCase);
    }

    @Override
    protected void saveTasks(String content, Context context) {
        String[] lines = content.split("\n");
        for (int i = 1; i < lines.length; i++) {
            String[] parts = lines[i].split(",");
            try {
                createTaskUseCase.execute(
                        parts[0],
                        parts[1],
                        Long.parseLong(parts[2]),
                        parts[3],
                        Difficulty.valueOf(parts[4]),
                        Priority.valueOf(parts[5])
                );
            } catch (Exception e) {
                Log.e("IMPORT", "Error parsing CSV line: " + lines[i], e);
            }
        }
    }
}
// todo автоообновление статистики