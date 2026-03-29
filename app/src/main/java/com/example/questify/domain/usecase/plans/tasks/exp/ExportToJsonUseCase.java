package com.example.questify.domain.usecase.plans.tasks.exp;

import android.content.Context;
import android.net.Uri;

import com.example.questify.domain.model.Task;
import com.example.questify.domain.usecase.plans.tasks.task.GetAllTasksUseCase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.inject.Inject;

public class ExportToJsonUseCase {

    private final GetAllTasksUseCase getAllTasksUseCase;

    @Inject
    public ExportToJsonUseCase(GetAllTasksUseCase getAllTasksUseCase) {
        this.getAllTasksUseCase = getAllTasksUseCase;
    }

    public void execute(Context context, Uri uri) {
        try {
            List<Task> tasks = getAllTasksUseCase.execute();
            JSONArray array = new JSONArray();

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
            format.setTimeZone(TimeZone.getTimeZone("UTC"));

            for (Task task : tasks) {
                JSONObject obj = new JSONObject();

                obj.put("id", task.getGlobalId());
                obj.put("name", task.getTaskName());
                obj.put("description", task.getDescription());
                obj.put("priority", task.getPriority().name());
                obj.put("difficulty", task.getDifficulty().name());
                obj.put("isDone", task.isDone());
                obj.put("deadline", format.format(task.getDeadline()));

                array.put(obj);
            }
            try (OutputStream os = context.getContentResolver().openOutputStream(uri)) {
                if (os == null) {
                    throw new IllegalStateException("Не удалось открыть файл для записи");
                }
                os.write(array.toString(2).getBytes());
                os.flush();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}