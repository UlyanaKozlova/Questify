package com.example.questify.domain.usecase.plans.tasks.imp;

import android.content.Context;
import android.net.Uri;

import com.example.questify.R;
import com.example.questify.domain.usecase.plans.tasks.task.CreateTaskUseCase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public abstract class ImportTasksUseCase {
    private static final String LINE_BREAK = "\n";
    protected final CreateTaskUseCase createTaskUseCase;
    private final Executor executor = Executors.newSingleThreadExecutor();
    public ImportTasksUseCase(CreateTaskUseCase createTaskUseCase) {
        this.createTaskUseCase = createTaskUseCase;
    }

    public void execute(Context context, Uri uri) {
        executor.execute(() -> {
            try {
                String content = readFile(context, uri);
                saveTasks(content, context);
            } catch (Exception e) {
                throw new IllegalStateException(context.getString(R.string.error_reading_file));
            }
        });
    }

    protected abstract void saveTasks(String content, Context context);

    private String readFile(Context context, Uri uri) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            sb.append(line).append(LINE_BREAK);
        }

        return sb.toString();
    }

}
