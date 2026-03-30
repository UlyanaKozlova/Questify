package com.example.questify.domain.usecase.plans.tasks.exp;

import android.content.Context;
import android.net.Uri;

import com.example.questify.R;

import java.io.OutputStream;

import javax.inject.Inject;

public class ExportStatisticsToJsonUseCase {
    @Inject
    public ExportStatisticsToJsonUseCase() {
    }

    public void execute(Context context, Uri uri) {
        try (OutputStream os = context.getContentResolver().openOutputStream(uri)) {
            if (os == null) {
                throw new IllegalStateException(context.getString(R.string.error_os_closed));
            }
            String fake = "{ \"a\": \"a\" }";
            os.write(fake.getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}