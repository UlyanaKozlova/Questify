package com.example.questify.domain.usecase.plans.tasks.exp;

import android.content.Context;
import android.net.Uri;

import com.example.questify.domain.model.Task;
import com.example.questify.domain.usecase.plans.tasks.task.GetAllTasksUseCase;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.inject.Inject;

public class ExportToIcsUseCase {

    private final GetAllTasksUseCase getAllTasksUseCase;

    @Inject
    public ExportToIcsUseCase(GetAllTasksUseCase getAllTasksUseCase) {
        this.getAllTasksUseCase = getAllTasksUseCase;
    }

    public void execute(Context context, Uri uri) {
        try {
            List<Task> tasks = getAllTasksUseCase.execute();

            StringBuilder builder = new StringBuilder();
            builder.append("BEGIN:VCALENDAR\n");
            builder.append("VERSION:2.0\n");

            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'", Locale.getDefault());
            format.setTimeZone(TimeZone.getTimeZone("UTC"));

            for (Task task : tasks) {
                builder.append("BEGIN:VEVENT\n");
                builder.append("SUMMARY:").append(task.getTaskName()).append("\n");

                String date = format.format(task.getDeadline());

                builder.append("DTSTART:").append(date).append("\n");
                builder.append("DTEND:").append(date).append("\n");

                builder.append("END:VEVENT\n");
            }

            builder.append("END:VCALENDAR");

            try (OutputStream os = context.getContentResolver().openOutputStream(uri)) {
                if (os == null) {
                    throw new IllegalStateException("Не удалось открыть поток для записи");
                }
                os.write(builder.toString().getBytes());
                os.flush();
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}