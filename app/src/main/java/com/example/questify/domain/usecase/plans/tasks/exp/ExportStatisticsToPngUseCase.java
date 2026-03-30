package com.example.questify.domain.usecase.plans.tasks.exp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;

import com.example.questify.R;

import java.io.OutputStream;

import javax.inject.Inject;

public class ExportStatisticsToPngUseCase {
    @Inject
    public ExportStatisticsToPngUseCase() {
    }

    public void execute(Context context, Uri uri) {
        try (OutputStream os = context.getContentResolver().openOutputStream(uri)) {
            if (os == null) {
                throw new IllegalStateException(context.getString(R.string.error_os_closed));
            }

            Bitmap bitmap = Bitmap.createBitmap(400, 200, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);

            canvas.drawColor(Color.WHITE);

            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setTextSize(32);

            canvas.drawText("Statistics", 50, 100, paint);

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}